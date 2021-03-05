package com.datdeveloper.jdbb.voice;

import com.datdeveloper.jdbb.Bot;
import com.datdeveloper.jdbb.model.Command;
import com.datdeveloper.jdbb.model.Track;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class AudioHandler implements AudioSendHandler {
    public static final Path basePath = Path.of("./sound/");

    public final AudioPlayer audioPlayer;
    public AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    public TrackScheduler scheduler;

    private AudioFrame lastFrame;

    public HashMap<String, Command> commands;

    public AudioHandler() {
        AudioSourceManagers.registerRemoteSources(playerManager);
        audioPlayer = playerManager.createPlayer();
        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);

        scheduler = new TrackScheduler(audioPlayer);

        audioPlayer.addListener(scheduler);
        audioPlayer.setVolume(15);

        loadCommands();
    }

    public void loadCommands() {
        try(FileReader reader = new FileReader(basePath.resolve("sounds.json").toFile())) {

            Type listType = new TypeToken<HashMap<String, Command>>(){}.getType();
            commands = new Gson().fromJson(reader, listType);
        } catch (FileNotFoundException e) {
            Bot.logger.error("Failed to find sounds.json, this file must be created in order to be able to play tracks");
            e.printStackTrace();
        } catch (IOException e) {
            Bot.logger.error("Failed to load sounds.json, this file must be available in order to be able to play tracks");
            e.printStackTrace();
        }
    }

    public boolean playTrack(String command, boolean forcePlay) {
        Command theCommand = commands.get(command);

        if (theCommand == null) return false;

        List<Track> trackList = theCommand.getTracks();

        int i = 0;

        if (forcePlay) {
            Track first = trackList.get(0);
            playTrack(basePath.resolve(first.path).toString(), first.loop, true);
            ++i;
        }

        for (;i < trackList.size(); ++i) {
            Track track = trackList.get(i);
            playTrack(track.getPath(basePath), track.loop, false);
        }

        return true;
    }

    public void playTrack(String path, int loopCount, boolean forcePlay){
        playerManager.loadItemOrdered(scheduler, path, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                if (forcePlay) scheduler.forcePlay(new TrackRules(track, loopCount), true);
                else scheduler.queueTrack(new TrackRules(track, loopCount));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack selectedTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();

                tracks.remove(selectedTrack);

                if (forcePlay) scheduler.forcePlay(new TrackRules(selectedTrack, loopCount), true);
                else scheduler.queueTrack(new TrackRules(selectedTrack, loopCount));

                for (AudioTrack track : tracks) {
                    scheduler.queueTrack(new TrackRules(track, loopCount));
                }
            }

            @Override
            public void noMatches() {
                Bot.logger.warn("Could not find match for: " + path);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                Bot.logger.warn("Failed to load track");
            }
        });
    }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        byte[] data = lastFrame.getData();
        lastFrame = null;
        return ByteBuffer.wrap(data);
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
