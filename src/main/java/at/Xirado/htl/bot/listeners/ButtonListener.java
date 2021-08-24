package at.Xirado.htl.bot.listeners;

import at.Xirado.htl.bot.Main;
import at.Xirado.htl.bot.misc.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static at.Xirado.htl.bot.listeners.OnGuildMemberJoin.*;

public class ButtonListener extends ListenerAdapter
{
    public static final Button JOIN_SERVER = Button.link(Util.INVITE_URL, "HTL-Discord beitreten");

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event)
    {
        Role role = event.getJDA().getRolesByName("Verifiziert", false).get(0);
        switch (event.getInteraction().getComponentId())
        {
            case "send_again" -> {
                Member member = event.getMember();
                if (member == null) return;
                if (member.getRoles().contains(role))
                {
                    MessageEmbed embed = new EmbedBuilder()
                            .setDescription("Du bist bereits verifiziert!")
                            .setAuthor("Verifizierung | HTL Discord", null, Util.LOGO_URL)
                            .setColor(0xCF1020)
                            .build();
                    event.replyEmbeds(embed).setEphemeral(true).queue();
                    return;
                }
                User user = event.getUser();
                event.deferReply(true)
                        .flatMap(x -> user.openPrivateChannel())
                        .flatMap(channel -> channel.sendMessageEmbeds(DM_EMBED).setActionRow(VERIFY_BUTTON, RULES_BUTTON))
                        .mapToResult()
                        .flatMap(result -> {
                            if (result.isFailure())
                            {
                                EmbedBuilder builder = new EmbedBuilder()
                                        .setColor(0xCF1020)
                                        .setDescription("Ich konnte dir keine Nachricht schicken, da du Privatnachrichten deaktiviert hast!\nWenn du dir sicher bist, dass es nicht an dir liegt, sende bitte eine DM an <@184654964122058752>")
                                        .setAuthor("Verifizierung | HTL Discord", null, Util.LOGO_URL);
                                return event.getHook().sendMessageEmbeds(builder.build());
                            } else
                            {
                                EmbedBuilder builder = new EmbedBuilder()
                                        .setColor(0x3EB489)
                                        .setAuthor("Verifizierung | HTL Discord", null, Util.LOGO_URL)
                                        .setDescription("Du hast eine Nachricht von mir erhalten!");
                                return event.getHook().sendMessageEmbeds(builder.build());
                            }
                        }).queue();
            }
            case "verify" -> {
                if (role != null)
                {
                    event.deferReply().queue(x -> {
                        Guild guild = role.getGuild();
                        guild.retrieveMember(event.getUser()).queue(member -> {
                            if (member.getRoles().contains(role))
                            {
                                MessageEmbed embed = new EmbedBuilder()
                                        .setDescription("Du bist bereits verifiziert!")
                                        .setAuthor("Verifizierung | HTL Discord", null, Util.LOGO_URL)
                                        .setColor(0xCF1020)
                                        .build();
                                x.sendMessageEmbeds(embed).queue();
                                return;
                            }
                            guild.addRoleToMember(member, role)
                                    .flatMap(y -> {
                                        MessageEmbed embed = new EmbedBuilder()
                                                .setDescription("Danke, du wurdest erfolgreich verifiziert. **Wir wünschen dir viel Spaß!**")
                                                .setColor(0x3EB489)
                                                .setAuthor("Verifizierung | HTL Discord", null, Util.LOGO_URL)
                                                .build();
                                        return x.sendMessageEmbeds(embed);
                                    }).queue();

                        }, new ErrorHandler().handle(ErrorResponse.UNKNOWN_MEMBER, (e) -> x.sendMessage("Du bist nicht auf dem Server!").queue()));
                    });
                }
            }
        }
    }
}
