package at.Xirado.htl.bot.misc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;


import at.Xirado.htl.bot.Main;
import at.Xirado.htl.bot.listeners.*;
import at.Xirado.htl.bot.misc.JSON;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;

public class Util
{

	public static final String LOGO_URL = "https://i.imgur.com/gO4qbaz.gif";
	public static final String RULE_CHANNEL_JUMP_URL = "https://discord.com/channels/713469621532885002/745264850950946926/802991263325749268";
	public static final String INVITE_URL = "https://discord.gg/Ktt4pbEmeK";
	public static void addListeners()
	{
		JDA jda = Main.getJDA();
		jda.addEventListener(new OneWordStory(), new Zaehlen(), new BinaerZaehlen(), new OnGuildMemberJoin(), new CommandEvent(), new ButtonListener());
	}
	
	public static String getPath()
	{
		try
		{
			String path2 = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path2, StandardCharsets.UTF_8);
			decodedPath = decodedPath.substring(0,decodedPath.lastIndexOf("/"));
			return decodedPath;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static void loadFile(String filename)
	{
		File file = new File(Main.path, filename);
		if (!file.exists()) {
            try {
            	InputStream in = Main.class.getResourceAsStream("/"+filename);
                if(in != null) {
                	Files.copy(in, file.toPath());
                }else {
                	file.createNewFile();
                }
            } catch (IOException e) {
            	e.printStackTrace();
            }
		}
	}
	
	public static void runAsync(Runnable r)
	{
		Main.SCHEDULED_EXECUTOR_SERVICE.submit(r);
	}
	
	public static void initMemberCount()
	{
		Main.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
			Guild g = Main.getJDA().getGuildById(713469621532885002L);
			try
			{
				if(g == null) return;
				int memberCount = g.getMemberCount();
				if(memberCount != Main.memberCount)
				{
					VoiceChannel voice = g.getVoiceChannelById("719276192108249230");
					if(voice != null)
					{
						voice.getManager().setName("『🙋』Mitglieder: "+memberCount).queue(s -> Main.memberCount = memberCount);
						Main.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(memberCount+" Mitglieder"), false);
					}
				}
			}catch (Exception ignored){}

			try
			{
				URL url = new URL("https://discord.com/api/guilds/713469621532885002/widget.json");
				JSON json = JSON.parse(url);
				if(json == null) return;
				Integer onlineCount = json.get("presence_count", Integer.class);
				if(onlineCount != null  && onlineCount != Main.onlineCount)
				{
					VoiceChannel voice = g.getVoiceChannelById("719276235129094174");
					if(voice != null){
						voice.getManager().setName("『💻』Online: "+onlineCount).queue(s -> Main.onlineCount = onlineCount);
					}
				}
			} catch (Exception ignored){}
		}, 0, 5, TimeUnit.MINUTES);
	}
}
