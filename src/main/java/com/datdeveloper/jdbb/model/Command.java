package com.datdeveloper.jdbb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Command {
    public boolean random = false;
    public boolean shuffle = false;
    public List<Track> tracks = new ArrayList<>();
    public String category;


    public Command() {}

    public Command(boolean random) {
        this.random = random;
    }

    public void addTrack(String path, int loops) {
        tracks.add(new Track(path, loops));
    }

    public List<Track> getTracks() {
        if (random) {
            if (shuffle) {
                Collections.shuffle(tracks);
                shuffle = false;
            }
            Track track = tracks.remove(0);
            tracks.add(track);

            return Collections.singletonList(track);
        }

        return tracks;
    }
}
