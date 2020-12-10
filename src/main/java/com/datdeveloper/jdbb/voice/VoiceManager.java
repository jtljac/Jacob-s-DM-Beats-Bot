package com.datdeveloper.jdbb.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
    AudioHandler audioHandler = new AudioHandler();

    public boolean connect(VoiceChannel channel, Guild guild) {
        if (audioMan == null){
            audioMan = guild.getAudioManager();
            audioMan.setSendingHandler(audioHandler);
            // audioHandler.audioPlayer.setVolume(3);
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

    public void stop(){
        audioHandler.audioPlayer.stopTrack();
    }

    public boolean isConnected(){
        return audioMan != null && audioMan.isConnected();
    }
    @Nullable
    public VoiceChannel getChannel(){
        return audioMan.getConnectedChannel();
    }

    public void playTrack(String path, int loopCount, boolean forcePlay){
        audioHandler.playerManager.loadItem(path, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (forcePlay) {
                    audioHandler.scheduler.forcePlay(audioHandler.audioPlayer, track, loopCount, true);
                } else audioHandler.scheduler.queueTrack(track, loopCount);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {
                System.out.println("Could not find " + path);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                System.out.println("Failure");
            }
        });
    }

    public boolean skip() {
        return audioHandler.scheduler.skip(audioHandler.audioPlayer);
    }

    public int seek(float position) {
        return audioHandler.scheduler.seek(position);
    }
}
