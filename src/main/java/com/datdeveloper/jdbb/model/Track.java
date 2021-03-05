package com.datdeveloper.jdbb.model;

import java.nio.file.Path;

public class Track {
    public String path;
    public int loop;
    public boolean link;

    public Track() {}

    public Track(String path, int loops) {
        this.path = path;
        this.loop = loops;
    }

    public String getPath(Path basePath) {
        if (link) return path;
        return basePath.resolve(path).toString();
    }
}
