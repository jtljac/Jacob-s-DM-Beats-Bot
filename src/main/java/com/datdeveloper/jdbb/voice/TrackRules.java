package com.datdeveloper.jdbb.voice;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class TrackRules {
    public AudioTrack track;
    public int loopcount;

    public TrackRules(AudioTrack track, int loopcount) {
        this.track = track;
        this.loopcount = loopcount;
    }
}
