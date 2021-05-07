package at.Xirado.htl.bot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class BinaerZaehlen extends ListenerAdapter
{



    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e)
    {
        TextChannel tc = e.getChannel();
        if(tc.getIdLong() != 714837732429660170L)
        {
            return;
        }
        if(e.getAuthor().isBot()){
            e.getMessage().delete().queue();
            return;
        }
        if(!e.getMessage().getContentRaw().matches("[01 ]+"))
        {
            e.getMessage().delete().queue();
            return;
        }
        long value;
        try
        {
            value = Long.parseLong(e.getMessage().getContentRaw().replaceAll(" ", ""), 2);
        } catch (NumberFormatException numberFormatException)
        {
            e.getMessage().delete().queue();
            return;
        }
        tc.getHistory().retrievePast(2).queue(
                messages ->
                {
                    Message lastMessage = messages.get(1);
                    try
                    {
                        long lastMessagevalue = Long.parseLong(lastMessage.getContentRaw().replaceAll(" ", ""), 2);
                        if(lastMessagevalue+1 != value)
                        {
                            e.getMessage().delete().queue();
                        }
                    }
                    catch (NumberFormatException ignored)
                    {

                    }

                }
        );
    }
}
