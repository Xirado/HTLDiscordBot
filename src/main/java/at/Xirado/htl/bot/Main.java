package at.Xirado.htl.bot;

import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import at.Xirado.htl.bot.listeners.OneWordStory;
import at.Xirado.htl.bot.misc.Util;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main
{
	public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	public static int memberCount = 0, onlineCount = 0;
	public static String path = null;
	private static final String TOKEN = "NzE5MTcyMDMwMjk3Mjc2NDE2.XtzjJQ.O3btU0F_w9DsE-8cQrJfml8YgKk";
	private static JDA jda = null;
	public static JDA getJDA()
	{
		return jda;
	}
	
	public static void main(String[] args)
	{
		try
		{
			path = Util.getPath();
			jda = JDABuilder.create(TOKEN, EnumSet.allOf(GatewayIntent.class)).build();
			jda.awaitReady();
			jda.getPresence().setPresence(OnlineStatus.ONLINE, false);
			System.out.println("HTLAustria.eu » "+"Erfolgreich als @"+jda.getSelfUser().getAsTag()+" angemeldet!");
			Util.addListeners();
			Util.initMemberCount();
			OneWordStory.firstStart = true;
			Guild server = jda.getGuildById(713469621532885002L);
			if(server != null)
			{
				TextChannel oneWordStory = server.getTextChannelById(OneWordStory.CHANNEL_ID);
				if(oneWordStory != null)
				{
					oneWordStory.sendMessage("\uD83E\uDD16 Bip Bop \uD83E\uDD16 Ich wurde neugestartet und die letzte laufende Geschichte wurde gelöscht.").queue();
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
