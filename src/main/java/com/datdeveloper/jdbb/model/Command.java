package com.datdeveloper.jdbb.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Command {
    public boolean random;
    public List<Track> tracks = new ArrayList<>();
    private transient boolean shuffled = false;

    public Command() {}

    public Command(boolean random) {
        this.random = random;
    }

    public void addTrack(String path, int loops) {
        tracks.add(new Track(path, loops));
    }

    public List<Track> getTracks() {
        if (random) {
            if (!shuffled) {
                Collections.shuffle(tracks);
                shuffled = true;
            }
            Track track = tracks.remove(0);
            tracks.add(track);

            return Collections.singletonList(track);
        }

        return tracks;
    }
}
