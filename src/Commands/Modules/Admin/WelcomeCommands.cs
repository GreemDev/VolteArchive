﻿using System.Threading.Tasks;
using Discord.WebSocket;
using Qmmands;
using Volte.Commands.Preconditions;
using Volte.Data;
using Volte.Data.Models.EventArgs;
using Volte.Extensions;
using Volte.Services;
using Gommon;

namespace Volte.Commands.Modules.Admin
{
    public partial class AdminModule : VolteModule
    {
        public ImageWelcomeService ImageWelcomeService { get; set; }
        public DefaultWelcomeService DefaultWelcomeService { get; set; }

        [Command("WelcomeChannel", "Wc")]
        [Description("Sets the channel used for welcoming new users for this guild.")]
        [Remarks("Usage: |prefix|welcomechannel {#channel}")]
        [RequireGuildAdmin]
        public async Task WelcomeChannelAsync([Remainder] SocketTextChannel channel)
        {
            var data = Db.GetData(Context.Guild);
            config.WelcomeOptions.WelcomeChannel = channel.Id;
            Db.UpdateData(config);
            await Context.CreateEmbed($"Set this server's welcome channel to {channel.Mention}.")
                .SendToAsync(Context.Channel);
        }

        [Command("WelcomeMessage", "Wmsg")]
        [Description(
            "Sets or shows the welcome message used to welcome new users for this guild. Only in effect when the bot isn't using the welcome image generating API.")]
        [Remarks("Usage: |prefix|welcomemessage [message]")]
        [RequireGuildAdmin]
        public async Task WelcomeMessageAsync([Remainder] string message = null)
        {
            var data = Db.GetData(Context.Guild);

            if (message is null)
            {
                await Context
                    .CreateEmbed(
                        $"The current welcome message for this server is ```\n{config.WelcomeOptions.WelcomeMessage}```")
                    .SendToAsync(Context.Channel);
            }
            else
            {
                config.WelcomeOptions.WelcomeMessage = message;
                Db.UpdateData(config);
                var welcomeChannel = await Context.Guild.GetTextChannelAsync(config.WelcomeOptions.WelcomeChannel);
                var sendingTest = config.WelcomeOptions.WelcomeChannel == 0 || welcomeChannel is null
                    ? "Not sending a test message as you do not have a welcome channel set." +
                      "Set a welcome channel to fully complete the setup!"
                    : $"Sending a test message to {welcomeChannel.Mention}.";
                await Context.CreateEmbed($"Set this server's welcome message to ```{message}```\n\n{sendingTest}")
                    .SendToAsync(Context.Channel);
                if (welcomeChannel is null) return;
                var args = new UserJoinedEventArgs(Context.User);
                if (Config.WelcomeApiKey.IsNullOrWhitespace())
                    await DefaultWelcomeService.JoinAsync(args);
                else
                    await ImageWelcomeService.JoinAsync(args);
            }
        }

        [Command("WelcomeColor", "WelcomeColour", "Wcl")]
        [Description("Sets the color used for welcome embeds for this guild.")]
        [Remarks("Usage: |prefix|welcomecolor {r} {g} {b}")]
        [RequireGuildAdmin]
        public async Task WelcomeColorAsync(int r, int g, int b)
        {
            if (r > 255 || g > 255 || b > 255)
            {
                await Context
                    .CreateEmbed(
                        "You cannot have an RGB value greater than 255. Either the R, G, or B value you entered exceeded 255 in value.")
                    .SendToAsync(Context.Channel);
                return;
            }

            var data = Db.GetData(Context.Guild);
            config.WelcomeOptions.WelcomeColorR = r;
            config.WelcomeOptions.WelcomeColorG = g;
            config.WelcomeOptions.WelcomeColorB = b;
            Db.UpdateData(config);
            await Context
                .CreateEmbed($"Successfully set this server's welcome message embed colour to `{r}, {g}, {b}`!")
                .SendToAsync(Context.Channel);
        }

        [Command("LeavingMessage", "Lmsg")]
        [Description("Sets or shows the leaving message used to say bye for this guild.")]
        [Remarks("Usage: |prefix|leavingmessage [message]")]
        [RequireGuildAdmin]
        public async Task LeavingMessageAsync([Remainder] string message = null)
        {
            var data = Db.GetData(Context.Guild);

            if (message is null)
            {
                await Context
                    .CreateEmbed(
                        $"The current leaving message for this server is ```\n{config.WelcomeOptions.WelcomeMessage}```")
                    .SendToAsync(Context.Channel);
            }
            else
            {
                config.WelcomeOptions.LeavingMessage = message;
                Db.UpdateData(config);
                var welcomeChannel = await Context.Guild.GetTextChannelAsync(config.WelcomeOptions.WelcomeChannel);
                var sendingTest = config.WelcomeOptions.WelcomeChannel == 0 || welcomeChannel is null
                    ? "Not sending a test message, as you do not have a welcome channel set. " +
                      "Set a welcome channel to fully complete the setup!"
                    : $"Sending a test message to {welcomeChannel.Mention}.";
                await Context.CreateEmbed($"Set this server's leaving message to ```{message}```\n\n{sendingTest}")
                    .SendToAsync(Context.Channel);
                if (welcomeChannel is null) return;

                var args = new UserLeftEventArgs(Context.User);
                await DefaultWelcomeService.LeaveAsync(args);
            }
        }
    }
}