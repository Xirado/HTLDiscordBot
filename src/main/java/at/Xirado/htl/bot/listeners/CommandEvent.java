package at.Xirado.htl.bot.listeners;

import at.Xirado.htl.bot.misc.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

public class CommandEvent extends ListenerAdapter
{
    public static final Button SEND_AGAIN = Button.secondary("send_again", "Erneut senden");
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event)
    {
        Member member = event.getMember();
        if (!member.hasPermission(Permission.ADMINISTRATOR))
            return;
        String content = event.getMessage().getContentRaw();
        if (content.equalsIgnoreCase("$sendverifymessage"))
        {
            event.getMessage().delete().queue(s -> {}, e -> {});
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(0x3EB489)
                    .setAuthor("Verifizierung | HTL Discord", null, Util.LOGO_URL)
                    .setDescription("Servus und willkommen auf dem HTL Discord!\nDu solltest nach dem Beitreten eine Privatnachricht erhalten haben.\nIst das nicht der Fall, drücke bitte auf den **Erneut senden** Button.\nWenn du immernoch keine Nachricht erhalten hast, vergewissere dich, dass du DMs eingeschalten hast!\n\nFalls die Verifizierung nicht funktioniert, schreibe bitte eine DM an <@184654964122058752>");
            event.getChannel().sendMessageEmbeds(builder.build())
                    .setActionRow(SEND_AGAIN).queue();
            return;
        }
    }
}
