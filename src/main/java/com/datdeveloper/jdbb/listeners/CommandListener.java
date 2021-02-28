package com.datdeveloper.jdbb.listeners;

import com.datdeveloper.jdbb.Bot;
import com.datdeveloper.jdbb.voice.AudioHandler;
import com.datdeveloper.jdbb.voice.TrackRules;
import com.datdeveloper.jdbb.voice.VoiceManager;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.*;

public class CommandListener extends ListenerAdapter {

    private final String commandPrefix = "!jdbb";

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String content = event.getMessage().getContentRaw();

        if (content.startsWith(commandPrefix)) {
            if (content.length() > commandPrefix.length() + 1) {
                Bot.logger.info("Received command: " + content + ", from " + event.getAuthor().getAsTag());

                // Split command into parts
                String[] command = content.substring(commandPrefix.length() + 1).toLowerCase().split(" ");
                switch (command[0]) {
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

                    case "reload":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) handler.loadCommands();
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "p":
                    case "play":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) {
                                if (command.length > 1) {
                                    if (!handler.playTrack(command[1], false)) {
                                        event.getChannel().sendMessage("Unknown track").queue();
                                    }
                                } else {
                                    event.getChannel().sendMessage("You need to provide a song").queue();
                                }
                            }
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "fp":
                    case "forceplay":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) {
                                if (command.length > 1) {
                                    if (!handler.playTrack(command[1], true)) {
                                        event.getChannel().sendMessage("Unknown track").queue();
                                    }
                                } else {
                                    event.getChannel().sendMessage("You need to provide a song").queue();
                                }
                            }
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "queue":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) {
                                List<TrackRules> queue = handler.scheduler.getQueue();
                                StringBuilder message = new StringBuilder();
                                message.append("Queued songs:\n```\n");
                                for (int i = 0; i < queue.size(); i++) {
                                    message.append(i + 1).append(".) ").append(queue.get(i).track.getIdentifier()).append(": ").append(queue.get(i).loopcount);
                                }
                                message.append("```");
                            }
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "clearqueue":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) handler.scheduler.clearQueue();
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "shuffle":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) handler.scheduler.shuffle();
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "skip":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) handler.scheduler.skip();
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "resume":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) {
                                switch(handler.scheduler.resume()) {
                                    case 1:
                                        event.getChannel().sendMessage("The bot does not currently have a track loaded to resume").queue();
                                        break;
                                    case 2:
                                        event.getChannel().sendMessage("The track is already playing").queue();
                                        break;
                                }
                            }
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "stop":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) {
                                switch(handler.scheduler.stop()) {
                                    case 1:
                                        event.getChannel().sendMessage("The bot is not currently playing a track to pause").queue();
                                        break;
                                    case 2:
                                        event.getChannel().sendMessage("The track is already paused").queue();
                                        break;
                                }
                            }
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "seek":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler();
                            if (handler != null) {
                                if (command.length > 1) {
                                    try {
                                        switch (VoiceManager.getInstance().audioHandler.scheduler.seek(Float.parseFloat(command[1]))) {
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
                            }
                            else event.getChannel().sendMessage("The bot must be connected to an audio channel").queue();
                        } else {
                            event.getChannel().sendMessage("That is a dm only command").queue();
                        }
                        break;

                    case "quit":
                        if (Bot.DMs.contains(event.getMember().getId())) {
                            if (VoiceManager.getInstance().isConnected()) {
                                VoiceManager.getInstance().quit();
                            } else {
                                event.getChannel().sendMessage("The bot is not connected").queue();
                            }
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
