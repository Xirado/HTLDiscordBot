package at.Xirado.htl.bot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface Command
{
    void executeCommand(SlashCommandEvent event);
}