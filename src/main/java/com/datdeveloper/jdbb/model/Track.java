package com.datdeveloper.jdbb.model;

public class Track {
    public String path;
    public int loop;

    public Track() {}

    public Track(String path, int loops) {
        this.path = path;
        this.loop = loops;
    }
}
