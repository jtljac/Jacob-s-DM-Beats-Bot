package com.datdeveloper.jdbb.voice;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayDeque;
import java.util.Deque;

public class TrackScheduler extends AudioEventAdapter {
    Deque<TrackRules> trackQueue = new ArrayDeque<>();
    TrackRules currentTrack = null;

    // Force the current track to loop
    boolean forceLoop = false;


    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);
        if (endReason == AudioTrackEndReason.REPLACED) return;

        if (forceLoop || currentTrack.loopcount != 0) {
            if (forceLoop) {
                currentTrack.loopcount -= 1;
            }
            currentTrack.track = track.makeClone();
        } else {
            if (!trackQueue.isEmpty()) currentTrack = trackQueue.remove();
            else currentTrack = null;
        }
        if (currentTrack != null) player.playTrack(currentTrack.track);
    }

    public void queueTrack(AudioTrack track, int loopCount) {
        trackQueue.add(new TrackRules(track, loopCount));
    }

    /**
     * Play the passed track immediately, rather than queueing it
     * @param player The Audio player
     * @param track The track to play
     * @param loopCount The amount of times to loop the track
     * @param clear Should the queue of upcoming tracks be cleared?
     */
    public void forcePlay(AudioPlayer player, AudioTrack track, int loopCount, boolean clear){
        if (clear) trackQueue.clear();
        currentTrack = new TrackRules(track, loopCount);
        player.playTrack(currentTrack.track);
    }

    /**
     * Skips to the next track
     * @param player The audio player
     * @return If theres any songs left
     */
    public boolean skip(AudioPlayer player) {
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

    public boolean stop(AudioPlayer player) {
        return false;
    }

    public boolean resume(AudioPlayer player) {
        return false;
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
}
