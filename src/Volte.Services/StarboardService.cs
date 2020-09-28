using System;
using System.Linq;
using System.Threading.Tasks;
using DSharpPlus;
using DSharpPlus.Entities;
using DSharpPlus.EventArgs;
using DSharpPlus.Exceptions;
using Gommon;
using Volte.Core.Helpers;
using Volte.Core.Entities;

namespace Volte.Services
{
    public sealed class StarboardService : VolteEventService
    {
        private readonly DatabaseService _db;

        // Ensures starboard message creations don't happen twice, and edits are atomic. Also ensures dictionary updates
        // don't happen at the same time.
        private readonly AsyncDuplicateLock<ulong> _starboardReadWriteLock;

        private readonly DiscordEmoji _starEmoji = DiscordEmoji.FromUnicode(EmojiHelper.Star);

        public StarboardService(DatabaseService databaseService)
        {
            _db = databaseService;
            _starboardReadWriteLock = new AsyncDuplicateLock<ulong>();
        }


        public override Task DoAsync(EventArgs args)
        {
            return args switch
            {
                MessageReactionAddEventArgs reactionAdd => HandleReactionAddAsync(reactionAdd),
                MessageReactionRemoveEventArgs reactionRemove => HandleReactionRemoveAsync(reactionRemove),
                MessageReactionsClearEventArgs reactionsClear => HandleReactionsClearAsync(reactionsClear),
                _ => Task.CompletedTask
            };
        }

        private async Task HandleReactionAddAsync(MessageReactionAddEventArgs args)
        {
            if (args.Channel is DiscordDmChannel) return;
            if (args.Emoji.Name != EmojiHelper.Star) return;
            if (args.User.IsCurrent) return;

            var guildId = args.Guild.Id;
            var messageId = args.Message.Id;
            var starrerId = args.User.Id;
            
            var data = _db.GetData(guildId);
            var starboard = data.Configuration.Starboard;
            
            var starboardChannel = await args.Client.GetChannelAsync(starboard.StarboardChannel);
            if (starboardChannel is null) return;

            if (_db.TryGetStargazers(guildId, messageId, out var entry))
            {
                using (await _starboardReadWriteLock.LockAsync(entry.StarredMessageId))
                {
                    // Add the star to the database
                    if (entry.Stargazers.TryAdd(starrerId, args.Channel == starboardChannel ? StarTarget.StarboardMessage : StarTarget.OriginalMessage))
                    {
                        // Update message star count
                        await UpdateOrPostToStarboardAsync(starboard, args.Message, entry);

                        _db.UpdateStargazers(entry);
                    }
                    else
                    {
                        // Invalid star! Either the starboard post or the actual message already has a reaction by this user.
                        if (starboard.DeleteInvalidStars)
                        {
                            await args.Message.DeleteReactionAsync(_starEmoji, args.User, "Star reaction is invalid: User has already starred!");
                        }
                    }
                }
            }
            else if (args.Channel != starboardChannel) // Can't make a new starboard message for a post in the starboard channel!
            {
                using (await _starboardReadWriteLock.LockAsync(messageId))
                {
                    if (args.Message.Reactions.FirstOrDefault(e => e.Emoji == _starEmoji)?.Count >= starboard.StarsRequiredToPost)
                    {
                        // Create new star message!
                        entry = new StarboardEntry
                        {
                            GuildId = guildId,
                            StarredMessageId = messageId,
                            StarboardMessageId = 0, // is set in UpdateOrPostToStarboardAsync
                            Stargazers =
                            {
                                [starrerId] = StarTarget.OriginalMessage
                            }
                        };

                        await UpdateOrPostToStarboardAsync(starboard, args.Message, entry);
                    }

                    _db.UpdateStargazers(entry);
                }
            }
        }

        private async Task HandleReactionRemoveAsync(MessageReactionRemoveEventArgs args)
        {
            if (args.Channel is DiscordDmChannel) return;
            if (args.Emoji.Name != EmojiHelper.Star) return;
            if (args.User.IsCurrent) return;
            
            var guildId = args.Guild.Id;
            var messageId = args.Message.Id;
            var starrerId = args.User.Id;

            var data = _db.GetData(guildId);
            var starboard = data.Configuration.Starboard;
            
            var starboardChannel = await args.Client.GetChannelAsync(starboard.StarboardChannel);
            if (starboardChannel is null) return;

            if (_db.TryGetStargazers(guildId, messageId, out var entry))
            {
                using (await _starboardReadWriteLock.LockAsync(entry.StarredMessageId))
                {
                    var removedStarTarget = messageId == entry.StarredMessageId
                        ? StarTarget.OriginalMessage
                        : StarTarget.StarboardMessage;

                    // Remove the star from the database
                    if (entry.Stargazers.TryGetValue(starrerId, out var starTarget) && starTarget == removedStarTarget && entry.Stargazers.Remove(starrerId))
                    {
                        // Update message star count
                        if (entry.StarCount < starboard.StarsRequiredToPost)
                        {
                            _db.RemoveStargazers(entry);
                            await UpdateOrPostToStarboardAsync(starboard, args.Message, entry);
                        }
                    }
                }
            }
        }

        private async Task HandleReactionsClearAsync(MessageReactionsClearEventArgs args)
        {
            if (args.Channel.Type is ChannelType.Private) return;

            var guildId = args.Guild.Id;
            var messageId = args.Message.Id;

            var data = _db.GetData(guildId);
            var starboard = data.Configuration.Starboard;
            
            var starboardChannel = await args.Client.GetChannelAsync(starboard.StarboardChannel);
            if (starboardChannel is null) return;

            if (_db.TryGetStargazers(guildId, messageId, out var entry))
            {
                using (await _starboardReadWriteLock.LockAsync(entry.StarredMessageId))
                {
                    var clearedStarTarget = messageId == entry.StarredMessageId
                        ? StarTarget.OriginalMessage
                        : StarTarget.StarboardMessage;

                    var clearList = entry.Stargazers
                        .Where(x => x.Value == clearedStarTarget)
                        .Select(x => x.Key)
                        .ToArray();

                    // Remove the stars from the database
                    if (clearList.Length > 0)
                    {
                        foreach (var userId in clearList)
                        {
                            entry.Stargazers.Remove(userId);
                        }

                        // Update message star count
                        if (entry.StarCount < starboard.StarsRequiredToPost)
                        {
                            _db.RemoveStargazers(entry);
                            await UpdateOrPostToStarboardAsync(starboard, args.Message, entry);
                        }
                        else
                        {
                            _db.UpdateStargazers(entry);
                        }
                    }
                }
            }
        }
        
        /// <summary>
        ///     Updates or posts a message to the starboard in a guild.
        ///     Calls to this method should be synchronized to _messageWriteLock beforehand!
        /// </summary>
        /// <param name="starboard">The guild's starboard configuration</param>
        /// <param name="message">The message to star</param>
        /// <param name="entry"></param>
        /// <returns></returns>
        private async Task UpdateOrPostToStarboardAsync(StarboardOptions starboard, DiscordMessage message, StarboardEntry entry)
        {
            var starboardChannel = message.Channel.Guild.GetChannel(starboard.StarboardChannel);
            if (starboardChannel is null)
            {
                return;
            }

            if (entry.StarboardMessageId == 0)
            {
                if (entry.StarCount >= starboard.StarsRequiredToPost)
                {
                    // New message just reached star threshold, send it
                    var newMessage = await PostToStarboardAsync(message, entry.StarCount);
                    entry.StarboardMessageId = newMessage.Id;
                }
            }
            else
            {
                DiscordMessage starboardMessage;
                try
                {
                    starboardMessage = await starboardChannel.GetMessageAsync(entry.StarboardMessageId);
                }
                catch (NotFoundException)
                {
                    // Ignore, maybe log to console
                    return;
                }

                if (entry.StarCount >= starboard.StarsRequiredToPost)
                {
                    // Update existing message
                    var targetMessage = $"{EmojiHelper.Star} {entry.StarCount}";
                    if (starboardMessage.Content != targetMessage)
                    {
                        await starboardMessage.ModifyAsync(targetMessage);
                    }
                }
                else
                {
                    // Unstarred below the limit so delete the message if any
                    await starboardMessage.DeleteAsync();
                    entry.StarboardMessageId = 0;
                }
            }
        }

        private async Task<DiscordMessage> PostToStarboardAsync(DiscordMessage message, int starCount)
        {
            var data = _db.GetData(message.Channel.Guild);
            
            var starboardChannel = message.Channel.Guild.GetChannel(data.Configuration.Starboard.StarboardChannel);
            if (starboardChannel is null)
            {
                return null;
            }

            // Discord API limitation: Fetch a full message. The message in OnReactionXXX does not contain an Author
            // field unless it is present in DSharpPlus' message cache.
            message = await message.Channel.GetMessageAsync(message.Id);

            var e = new DiscordEmbedBuilder()
                .WithSuccessColor()
                .WithDescription(message.Content)
                .WithAuthor(message.Author)
                .AddField("Original Message", message.JumpLink);

            var result = await starboardChannel.SendMessageAsync($"{_starEmoji} {starCount}", embed: e.Build());
            await result.CreateReactionAsync(_starEmoji);
            return result;
        }
    }
}