package com.datdeveloper.jdbb.listeners;

import com.datdeveloper.jdbb.Bot;
import com.datdeveloper.jdbb.voice.AudioHandler;
import com.datdeveloper.jdbb.voice.VoiceManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getContentRaw();
        if (content.startsWith("!jdbb")) {
            if (content.length() > 5) {
                System.out.println("Received command: " + content + ", from " + event.getAuthor().getAsTag());
                // check commands
                ArrayList<String> command = new ArrayList<>(Arrays.asList(content.substring(6).split(" ")));
                switch (command.get(0)) {
                    case "join":
                        VoiceChannel playerChannel = event.getMember().getVoiceState().getChannel();
                        if (playerChannel != null) {
                            if (!VoiceManager.getInstance().connect(playerChannel, event.getGuild())) {
                                event.getChannel().sendMessage("Unable to join voice channel").queue();
                            }
                        } else {
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You must be in a voice channel to do that").queue();
                        }
                        break;
                    case "skip":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            VoiceManager.getInstance().skip();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;
                    case "seek":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            if (command.size() > 1) {
                                try {
                                    switch (VoiceManager.getInstance().seek(Float.parseFloat(command.get(1)))) {
                                        case 1:
                                            event.getChannel().sendMessage("You cannot seek through this track").queue();
                                            break;
                                        case 2:
                                            event.getChannel().sendMessage("You need to provide a valid time").queue();
                                            break;
                                        case 3:
                                            event.getChannel().sendMessage("There isn't a track to seek").queue();
                                    }
                                } catch (NumberFormatException e) {
                                    event.getChannel().sendMessage("You need to provide a valid time").queue();
                                }
                            } else {
                                event.getChannel().sendMessage("You need to provide a time").queue();
                            }
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;
                    case "finale":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            VoiceManager.getInstance().playTrack("sound/finale.mp3",  0, true);
                            VoiceManager.getInstance().playTrack("sound/finale loop.mp3",  -1, false);
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;
                    case "sad":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            VoiceManager.getInstance().playTrack("sound/sad.mp3",  0, true);
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;
                    case "hope":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            VoiceManager.getInstance().playTrack("sound/hope.mp3",  -1, true);
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;
                    case "victory":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            VoiceManager.getInstance().playTrack("sound/victory.mp3", 0, true);
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;
                    default:
                        event.getChannel().sendMessage("I don't know that command").queue();
                }
            } else {
                event.getChannel().sendMessage("Please enter a subcommand").queue();
            }
        }
    }
}
