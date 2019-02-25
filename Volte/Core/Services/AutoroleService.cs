﻿using System.Linq;
using System.Threading.Tasks;
using Discord.WebSocket;
using Volte.Core.Discord;
using Volte.Core.Extensions;

namespace Volte.Core.Services
{
    internal class AutoroleService : IService
    {
        internal async Task ApplyRoleAsync(SocketGuildUser user)
        {
            var config = VolteBot.GetRequiredService<DatabaseService>().GetConfig(user.Guild.Id);
            if (!config.Autorole.IsNullOrWhitespace())
            {
                var targetRole = user.Guild.Roles.FirstOrDefault(r => r.Name.EqualsIgnoreCase(config.Autorole));
                await user.AddRoleAsync(targetRole);
            }
        }
    }
}