package at.Xirado.htl.bot.commands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;

public class VoiceTargetCommand implements Command
{
    public static final Route ROUTE = Route.Invites.CREATE_INVITE;

    @Override
    public void executeCommand(SlashCommandEvent event)
    {
        String appId = event.getOption("application").getAsString();
        Member member = event.getMember();
        if (member == null)
        {
            event.reply("Dieser Befehl kann nur auf dem Server ausgeführt werden!").queue();
            return;
        }
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || voiceState.getChannel() == null)
        {
            event.reply("Dazu musst du in einem Sprachkanal sein!").setEphemeral(true).queue();
            return;
        }
        createApplicationInviteURL(voiceState.getChannel(), appId)
                .flatMap(link -> event.reply("Klicke hier: <"+link+">").setEphemeral(true))
                .queue();
    }


    public static RestAction<String> createApplicationInviteURL(VoiceChannel channel, String appId)
    {
        DataObject requestBody = DataObject.empty()
                .put("max_age", 3600)
                .put("max_uses", 0)
                .put("unique", true)
                .put("target_type", 2)
                .put("target_application_id", Long.parseUnsignedLong(appId));
        return new RestActionImpl<>(
                        channel.getJDA(),
                        ROUTE.compile(channel.getId()),
                        requestBody,
                        (response, request) ->
                                "https://discord.gg/"+response.getObject().getString("code"));
    }

}
