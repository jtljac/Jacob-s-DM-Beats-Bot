package com.datdeveloper.jdbb.voice;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nullable;
import java.util.*;

public class VoiceManager {
    // Singleton
    private static VoiceManager instance = null;
    public static VoiceManager getInstance(){
        if (instance == null){
            instance = new VoiceManager();
        }
        return instance;
    }


    final Map<Long, AudioHandler> audioHandlers = new HashMap<>();

    /**
     * Connect the bot to the given voice channel
     * @param channel The channel to join
     * @param guild The server to join on
     * @return True if successful connection
     */
    public int connect(VoiceChannel channel, Guild guild) {
        AudioManager audioMan = guild.getAudioManager();

        if (!audioHandlers.containsKey(guild.getIdLong())) {
            AudioHandler handler = new AudioHandler();
            audioMan.setSendingHandler(handler);
            audioHandlers.put(guild.getIdLong(), handler);
        }

        if (audioMan.isConnected() && audioMan.getConnectedChannel().equals(channel)) return 1;

        if (!guild.getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)){
            return 2;
        }

        audioMan.openAudioConnection(channel);
        return 0;
    }

    public boolean quit(Guild guild){
        if (isConnected(guild)) {
            guild.getAudioManager().closeAudioConnection();
            audioHandlers.remove(guild.getIdLong());
            return true;
        } else {
            return false;
        }
    }

    public AudioHandler getHandler(Guild guild){
        return audioHandlers.get(guild.getIdLong());
    }

    public AudioHandler getHandler(Long id) {
        return audioHandlers.get(id);
    }

    public boolean isConnected(Guild guild){
        return guild.getAudioManager().isConnected();
    }

    @Nullable
    public VoiceChannel getChannel(Guild guild){
        return guild.getAudioManager().getConnectedChannel();
    }
}
