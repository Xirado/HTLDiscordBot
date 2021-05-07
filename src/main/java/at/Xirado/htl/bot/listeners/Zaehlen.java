package at.Xirado.htl.bot.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Zaehlen extends ListenerAdapter
{



    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent e)
    {
        TextChannel tc = e.getChannel();
        if(tc.getIdLong() != 714230681215041607L)
        {
            return;
        }
        if(e.getAuthor().isBot()){
            e.getMessage().delete().queue();
            return;
        }
        Long parsedText;
        try
        {
            parsedText = Long.parseLong(e.getMessage().getContentRaw());
        } catch (NumberFormatException numberFormatException)
        {
            e.getMessage().delete().queue();
            return;
        }
        tc.getHistory().retrievePast(2).queue(
                messages ->
                {
                    Message lastMessage = messages.get(1);
                    long value;
                    try
                    {
                        value = Long.parseLong(lastMessage.getContentRaw());
                        if(value+1 != parsedText)
                        {
                            e.getMessage().delete().queue();
                        }
                    } catch (NumberFormatException ignored)
                    {

                    }

                }
        );
    }
}
