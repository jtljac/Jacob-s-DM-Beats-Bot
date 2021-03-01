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
            String response = null;
            if (content.length() > commandPrefix.length() + 1) {
                Bot.logger.info("Received command: " + content + ", from " + event.getAuthor().getAsTag());

                // Split command into parts
                String[] command = content.substring(commandPrefix.length() + 1).toLowerCase().split(" ");
                String userId = event.getMember().getId();

                switch (command[0]) {
                    case "join":
                        VoiceChannel playerChannel = event.getMember().getVoiceState().getChannel();
                        if (playerChannel != null) {
                            switch (VoiceManager.getInstance().connect(playerChannel, event.getGuild())) {
                                case 1:
                                    response = "Aleady connected to that channel";
                                case 2:
                                    response = "No permission to join that voice channel";
                            }
                        } else {
                            response = event.getAuthor().getAsMention() + " You must be in a voice channel to do that";
                        }
                        break;

                    case "quit":
                        if (Bot.DMs.contains(userId)) {
                            if (!VoiceManager.getInstance().quit(event.getGuild())) {
                                response = "The bot is not connected";
                            }
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "reload":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) handler.loadCommands();
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "p":
                    case "play":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) {
                                if (command.length > 1) {
                                    if (!handler.playTrack(command[1], false)) {
                                        response = "Unknown track";
                                    }
                                } else {
                                    response = "You need to provide a song";
                                }
                            }
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "fp":
                    case "forceplay":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) {
                                if (command.length > 1) {
                                    if (!handler.playTrack(command[1], true)) {
                                        response = "Unknown track";
                                    }
                                } else {
                                    response = "You need to provide a song";
                                }
                            }
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "queue":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) {
                                List<TrackRules> queue = handler.scheduler.getQueue();
                                StringBuilder message = new StringBuilder();
                                message.append("Current Song: ```").append(handler.scheduler.getCurrentTrack().track.getIdentifier()).append("```\n");
                                message.append("Queued songs:\n```\n");
                                for (int i = 0; i < queue.size(); i++) {
                                    message.append(i + 1).append(".) ").append(queue.get(i).track.getIdentifier()).append(": ").append(queue.get(i).loopcount);
                                }
                                message.append("```");
                                response = message.toString();
                            }
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "clearqueue":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) handler.scheduler.clearQueue();
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "shuffle":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) handler.scheduler.shuffle();
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "skip":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) handler.scheduler.skip();
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "resume":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) {
                                switch(handler.scheduler.resume()) {
                                    case 1:
                                        response = "The bot does not currently have a track loaded to resume";
                                        break;
                                    case 2:
                                        response = "The track is already playing";
                                        break;
                                }
                            }
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "stop":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) {
                                switch(handler.scheduler.stop()) {
                                    case 1:
                                        response = "The bot is not currently playing a track to pause";
                                        break;
                                    case 2:
                                        response = "The track is already paused";
                                        break;
                                }
                            }
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "loop":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) {
                                if (handler.scheduler.loop()) {
                                    response = "Looping enabled: Now looping the current song";
                                } else {
                                    response = "Looping disabled: Now continuing through the queue";
                                }
                            }
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "loopqueue":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) {
                                if (handler.scheduler.loopQueue()) {
                                    response = "Queue looping enabled: Now re-queuing songs after they finish";
                                } else {
                                    response = "Queue looping disabled: Now discarding songs after they finish";
                                }
                            }
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    case "seek":
                        if (Bot.DMs.contains(userId)) {
                            AudioHandler handler = VoiceManager.getInstance().getHandler(event.getGuild());
                            if (handler != null) {
                                if (command.length > 1) {
                                    try {
                                        switch (handler.scheduler.seek(Float.parseFloat(command[1]))) {
                                            case 1:
                                                response = "You cannot seek through this track";
                                                break;
                                            case 2:
                                                response = "You need to provide a valid time";
                                                break;
                                            case 3:
                                                response = "There isn't a track to seek";
                                        }
                                    } catch (NumberFormatException e) {
                                        response = "You need to provide a valid time";
                                    }
                                } else {
                                    response = "You need to provide a time";
                                }
                            }
                            else response = "The bot must be connected to an audio channel";
                        } else {
                            response = "That is a dm only command";
                        }
                        break;

                    default:
                        response = "I don't know that command";
                }
            } else {
                response = "Please enter a subcommand";
            }
            if (response != null) event.getChannel().sendMessage(response).queue();
        }
    }
}
