package com.datdeveloper.jdbb.voice;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nullable;

public class VoiceManager {
    // Singleton
    private static VoiceManager instance = null;
    public static VoiceManager getInstance(){
        if (instance == null){
            instance = new VoiceManager();
        }
        return instance;
    }

    AudioManager audioMan = null;
    public AudioHandler audioHandler = new AudioHandler();

    /**
     * Connect the bot to the given voice channel
     * @param channel The channel to join
     * @param guild The server to join on
     * @return True if successful connection
     */
    public boolean connect(VoiceChannel channel, Guild guild) {
        // TODO: Extend to support multiple servers
        if (audioMan == null){
            audioMan = guild.getAudioManager();
            audioMan.setSendingHandler(audioHandler);
        }

        if (audioMan.isConnected()) {
            if (audioMan.getConnectedChannel().equals(channel)) {
                return false;
            }
        }

        if (!guild.getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)){
            return false;
        }

        audioMan.openAudioConnection(channel);
        return true;
    }

    public boolean quit(){
        if (audioMan != null && audioMan.isConnected()) {
            audioMan.closeAudioConnection();
            return true;
        } else {
            return false;
        }
    }

    public AudioHandler getHandler(){
        if (isConnected()) {
            return audioHandler;
        }
        return null;
    }

    public boolean isConnected(){
        return audioMan != null && audioMan.isConnected();
    }

    @Nullable
    public VoiceChannel getChannel(){
        return audioMan.getConnectedChannel();
    }
}
