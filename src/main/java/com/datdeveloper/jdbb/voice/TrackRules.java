package com.datdeveloper.jdbb.voice;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class TrackRules {
    AudioTrack track;
    int loopcount;

    public TrackRules(AudioTrack track, int loopcount) {
        this.track = track;
        this.loopcount = loopcount;
    }
}
