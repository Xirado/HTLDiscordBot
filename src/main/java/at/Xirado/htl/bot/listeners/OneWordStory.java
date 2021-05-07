package at.Xirado.htl.bot.listeners;

import at.Xirado.htl.bot.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class OneWordStory extends ListenerAdapter
{
    public static final List<Long> CURRENT_STORY = Collections.synchronizedList(new ArrayList<>());
    public static final long CHANNEL_ID = 793082837070381056L;
    public static OffsetDateTime lastStart = OffsetDateTime.now();
    public static boolean firstStart;

    @Override
    public void onGuildMessageReceived(final @NotNull GuildMessageReceivedEvent event)
    {
        Main.SCHEDULED_EXECUTOR_SERVICE.submit(() -> {
            TextChannel c = event.getChannel();
            if(c.getIdLong() != CHANNEL_ID) return;
            if(event.getAuthor().getIdLong() == Main.getJDA().getSelfUser().getIdLong()) return;
            if(event.isWebhookMessage() || event.getAuthor().isBot())
            {
                event.getMessage().delete().queue(s -> {}, e -> {});
                return;
            }
            String content = event.getMessage().getContentRaw();
            User sender = event.getAuthor();
            Member member = event.getMember();
            if(content.equalsIgnoreCase("!geschichte"))
            {
                event.getMessage().delete().queue();
                sendHistory(true,sender.getIdLong());
                return;
            }
            if(content.equalsIgnoreCase("!ende"))
            {
                if(hasRole(member, 749958593113227275L) || hasRole(member, 713478147634626583L) || member.hasPermission(Permission.ADMINISTRATOR))
                {
                    sendHistory(false, 0L);
                }else
                {
                    event.getMessage().delete().queue();
                }
                return;

            }
            if(content.startsWith("!"))
            {
                event.getMessage().delete().queue(s -> {}, e -> {});
                return;
            }
            if(content.startsWith("//")) return;

            Message m = getLastMessage(event.getChannel(), event.getMessage().getTimeCreated());
            if(m.getTimeCreated().isAfter(lastStart))
            {
                if(m.getAuthor().getIdLong() == event.getAuthor().getIdLong())
                {
                    event.getMessage().delete().queue();
                    return;
                }
            }
            if(content.contains("\n") || content.contains("\r\n") || content.contains("\r") || content.contains("\u200B") || content.contains("\0"))
            {
                event.getMessage().delete().queue();
                return;
            }
            String[] args = content.split(" ");
            if(args.length > 1)
            {
                if(args.length > 2)
                {
                    event.getMessage().delete().queue();
                    return;
                }
                if(content.startsWith(", ") || content.startsWith(". "))
                {
                    CURRENT_STORY.add(event.getMessageIdLong());
                }else
                {
                    event.getMessage().delete().queue();
                }
                return;
            }
            CURRENT_STORY.add(event.getMessageIdLong());
        });

    }

    public static void sendHistory(boolean inPrivateMessage, long userID)
    {
        Main.SCHEDULED_EXECUTOR_SERVICE.execute(() ->
        {
            TextChannel textChannel = Main.getJDA().getTextChannelById(CHANNEL_ID);
            if(textChannel == null) return;
            if(CURRENT_STORY.size() == 0)
            {
                if(inPrivateMessage)
                {

                    Main.getJDA().retrieveUserById(userID).queue(
                            (user) -> user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("**Es gibt noch keine Geschichte...**").queue(s -> {}, e -> {}))
                    );
                }else
                {
                    textChannel.sendMessage("**Bruh, die Geschichte hat noch nicht einmal angefangen...**").queue();
                }
                return;
            }

            MessagePaginationAction history = textChannel.getIterableHistory();
            history.takeWhileAsync(
                    (message) ->
                            message.getTimeCreated().isAfter(lastStart)).thenAcceptAsync((messages ->
            {
                StringBuilder builder = new StringBuilder();
                ArrayList<String> messagesContent = new ArrayList<>();
                for (Message message : messages)
                {
                    String content = message.getContentRaw();
                    String[] arguments = content.split(" ");
                    if(content.startsWith("//") || content.startsWith("!") || message.getAuthor().isBot() || message.isWebhookMessage()) continue;
                    if(content.contains("\n") || content.contains("\r\n") || content.contains("\r") || content.contains("\u200B"))
                    {
                        continue;
                    }
                    if(arguments.length > 1)
                    {
                        if(arguments.length == 2)
                        {
                            if(content.startsWith(", ") || content.startsWith(". "))
                            {
                                messagesContent.add(content);
                                continue;
                            }
                        }
                        continue;
                    }
                    if(arguments.length == 1)
                    {
                        if(arguments[0].startsWith(","))
                        {
                            long count = arguments[0].chars().filter(ch -> ch == ',').count();
                            if(count == 1)
                            {
                                String word = arguments[0].substring(1);
                                messagesContent.add(", "+word);
                            }else {
                                messagesContent.add(content);
                            }

                            continue;
                        }else if(arguments[0].startsWith("."))
                        {
                            long count = arguments[0].chars().filter(ch -> ch == '.').count();
                            if(count == 1)
                            {
                                String word = arguments[0].substring(1);
                                messagesContent.add(". "+word);
                            }else {
                                messagesContent.add(content);
                            }
                            continue;
                        }
                        messagesContent.add(content);
                    }


                }
                if(messagesContent.size() == 0)
                {
                    if(inPrivateMessage)
                    {

                        Main.getJDA().retrieveUserById(userID).queue(
                                (user) ->
                                {
                                    user.openPrivateChannel().queue(
                                            (privateChannel -> {
                                                privateChannel.sendMessage("**Es gibt noch keine Geschichte...**").queue(s -> {}, e -> {});
                                            })
                                    );
                                }
                        );
                    }else
                    {
                        textChannel.sendMessage("**Bruh, die Geschichte hat noch nicht einmal angefangen...**").queue();
                    }
                    return;
                }
                Collections.reverse(messagesContent);
                for(int i = 0; i < messagesContent.size(); i++)
                {
                    String currentMessage = messagesContent.get(i);
                    String nextMessage = i+1 >= messagesContent.size() ? null : messagesContent.get(i+1);
                    builder.append(currentMessage);
                    if(nextMessage != null)
                    {
                        if(!nextMessage.startsWith(",") && !nextMessage.startsWith("."))
                        {
                            builder.append(" ");
                        }

                    }
                }

                String parsedString = builder.toString().trim().replaceAll(" -", "").replaceAll("- ", "");
                if(inPrivateMessage)
                {
                    Main.getJDA().retrieveUserById(userID).queue(user -> user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("**Zusammenfassung der laufenden Geschichte:**\n\n"+ parsedString).allowedMentions(EnumSet.noneOf(Message.MentionType.class)).queue(success -> {}, error -> {})));
                }else
                {
                    textChannel.sendMessage("**Eure Geschichte:**\n\n"+parsedString).allowedMentions(EnumSet.noneOf(Message.MentionType.class)).queue();
                    CURRENT_STORY.clear();
                    lastStart = OffsetDateTime.now();
                }
            }));

        });

    }

    public boolean hasRole(Member member, long ID)
    {
        Role role = member.getGuild().getRoleById(ID);
        if(role == null) return false;
        return member.getRoles().contains(role);
    }
    public Role findRole(Member member, String name) {
        List<Role> roles = member.getRoles();
        return roles.stream()
                .filter(role -> role.getName().equalsIgnoreCase(name)) // filter by role name
                .findFirst() // take first result
                .orElse(null); // else return null
    }

    /**
     * Get last message not from Bot
     * @param channel The Channel where to get the last message from
     * @return The last message
     */
    public static Message getLastMessage(TextChannel channel, OffsetDateTime before)
    {
        return channel.getIterableHistory()
                .stream()
                .filter(x -> !x.isWebhookMessage())
                .filter(x -> !x.getAuthor().isBot())
                .filter(x -> !x.getContentRaw().startsWith("!"))
                .filter(x -> !x.getContentRaw().startsWith("//"))
                .filter(x -> x.getTimeCreated().isBefore(before))
                .filter(x -> x.getContentRaw().split(" ").length <= 2)
                .findFirst()
                .orElse(null);
    }
}
