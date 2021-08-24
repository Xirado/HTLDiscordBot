package at.Xirado.htl.bot.listeners;

import at.Xirado.htl.bot.misc.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

public class OnGuildMemberJoin extends ListenerAdapter
{
    public static final Button VERIFY_BUTTON = Button.primary("verify", "Verifizieren");
    public static final Button RULES_BUTTON = Button.link(Util.RULE_CHANNEL_JUMP_URL, "Hausordnung").withEmoji(Emoji.fromUnicode("\uD83D\uDCDC"));
    public static final MessageEmbed DM_EMBED = new EmbedBuilder()
                .setAuthor("Verifizierung | HTL Discord", null, Util.LOGO_URL)
                .setColor(0x3EB489)
                .setDescription("**Servus und willkommen auf dem HTL Discord-Server!**\n\nUm Zugriff auf alle Kanäle zu haben, musst du dich noch verifizieren!\nDafür brauchst du nur auf den **Verifizieren** Button drücken!")
                .setFooter("Mit deiner Verifizierung bestätigst du, unsere Hausordnung gelesen und verstanden zu haben!")
                .build();

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event)
    {
        if (event.getUser().isBot()) return;
        Member member = event.getMember();
        User user = event.getUser();
        user.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(DM_EMBED).setActionRow(VERIFY_BUTTON, RULES_BUTTON))
                .queue(s -> {}, e -> {});
    }
}
