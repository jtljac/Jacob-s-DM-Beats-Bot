package com.datdeveloper.jdbb.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.*;

public class TrackScheduler extends AudioEventAdapter {
    Deque<TrackRules> trackQueue = new ArrayDeque<>();
    AudioPlayer player;
    TrackRules currentTrack = null;

    // Force the current track to loop
    boolean forceLoop = false;
    boolean queueLoop = false;

    public TrackScheduler (AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);
        if (endReason == AudioTrackEndReason.REPLACED || endReason == AudioTrackEndReason.STOPPED) return;

        if (forceLoop || currentTrack.loopcount != 0) {
            if (!forceLoop) {
                currentTrack.loopcount -= 1;
            }
            currentTrack.track = track.makeClone();
        } else {
            if (queueLoop) {
                currentTrack.track = track.makeClone();
                trackQueue.add(currentTrack);
            }

            if (!trackQueue.isEmpty()) currentTrack = trackQueue.remove();
            else currentTrack = null;
        }
        if (currentTrack != null) player.playTrack(currentTrack.track);
    }

    /**
     * Queue the given track to play
     * @param track The track to queue
     */
    public void queueTrack(TrackRules track) {
        if (currentTrack == null || player.isPaused()) {
            currentTrack = track;
            player.playTrack(currentTrack.track);
            if (player.isPaused()) player.setPaused(false);
        } else {
            trackQueue.add(track);
        }
    }

    /**
     * Play the passed track immediately, rather than queueing it
     * @param track The track to play
     * @param clear Should the queue of upcoming tracks be cleared?
     */
    public void forcePlay(TrackRules track, boolean clear){
        if (clear) trackQueue.clear();
        currentTrack = track;
        player.playTrack(currentTrack.track);
    }

    public void clearQueue() {
        trackQueue.clear();
    }

    public void shuffle() {
        List<TrackRules> temp = new ArrayList<>(trackQueue);
        Collections.shuffle(temp);
        trackQueue = new ArrayDeque<>(temp);
    }

    public List<TrackRules> getQueue() {
        return new ArrayList<>(trackQueue);
    }

    /**
     * Skips to the next track
     * @return If theres any songs left
     */
    public boolean skip() {
        if (queueLoop) trackQueue.add(new TrackRules(currentTrack.track.makeClone(), currentTrack.loopcount));
        if (!trackQueue.isEmpty()) {
            currentTrack = trackQueue.remove();
            player.playTrack(currentTrack.track);
            return true;
        } else {
            currentTrack = null;
            player.stopTrack();
            return false;
        }
    }

    public int stop() {
        if (currentTrack == null) return 1;
        else if (player.isPaused()) return 2;

        player.setPaused(true);
        player.stopTrack();
        return 0;
    }

    public int resume() {
        if (currentTrack == null) return 1;
        else if (!player.isPaused()) return 2;

        player.setPaused(false);

        return 0;
    }

    /**
     * Seeks to a place in the current track
     * @param seconds Seconds to seek to
     * @return A return code, 0: success, 1: cannot seek, 2: invalid time, 3: no track
     */
    public int seek(float seconds) {
        if (currentTrack == null) return 3;
        long time = (long)(seconds * 1000);
        if (!currentTrack.track.isSeekable()) return 1;
        else if (time > currentTrack.track.getDuration() || time < 0) return 2;

        currentTrack.track.setPosition(time);
        return 0;
    }

    /**
     * Switches the scheduler to loop the current current song or not depending on it's current state
     * @return True if the scheduler is now looping the current song
     */
    public boolean loop() {
        forceLoop = !forceLoop;
        return forceLoop;
    }

    /**
     * Switches the scheduler to loop the current queue (songs will be requeued after they finish
     * @return True if the scheduler is now looping the queue
     */
    public boolean loopQueue() {
        queueLoop = !queueLoop;
        return queueLoop;
    }

    /**
     * Gets the currently playing track
     */
    public TrackRules getCurrentTrack() {
        return currentTrack;
    }
}
