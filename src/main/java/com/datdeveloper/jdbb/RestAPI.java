package com.datdeveloper.jdbb;

import com.datdeveloper.jdbb.exception.ArgumentException;
import com.datdeveloper.jdbb.voice.AudioHandler;
import com.datdeveloper.jdbb.voice.TrackRules;
import com.datdeveloper.jdbb.voice.VoiceManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import spark.Spark;

import java.util.List;

public class RestAPI {
    Gson gson = new Gson();
    Long serverID;
    AudioHandler handler;

    public RestAPI (){
        Spark.port(8080);
        Spark.path("/api", () -> {
            Spark.before("/*", (req, res) -> {
                Bot.logger.info("Received api call: " + req.url());

                serverID = req.queryMap("serverid").longValue();

                handler = VoiceManager.getInstance().getHandler(serverID);

                if (serverID == null) {
                    throw new ArgumentException("01 - You must provide a server ID as a query parameter");
                }

                if (handler == null) {
                    throw new ArgumentException("02 - That server does not have an active bot in a voice channel");
                }
            });

            // Play
            Spark.get("/play", (req, res) -> {
                String command = req.queryParams("command");
                Boolean forcePlay = req.queryMap("forceplay").booleanValue();

                if (command == null) {
                    throw new ArgumentException("03 - You must provide a command as a query parameter");
                }

                if (!handler.playTrack(command, (forcePlay != null && forcePlay))) {
                    throw new ArgumentException("04 - Unknown command");
                } else {
                    res.status(200);
                    return "00 - Success";
                }
            });

            // Skip
            Spark.get("/skip", (req, res) -> {
                handler.scheduler.skip();

                return "00 - Success";
            });

            // Get Commands
            Spark.get("/listcommands", (req, res) -> handler.commands, gson::toJson);

            // Reload
            Spark.get("/reload", (req, res) -> {
                handler.loadCommands();
                res.redirect("/api/listcommands?serverid=" + serverID);
                return "moved";
            });

            // Queue
            Spark.get("/queue", (req, res) -> {
                List<TrackRules> queue = handler.scheduler.getQueue();
                JsonObject writer = new JsonObject();
                {
                    TrackRules track = handler.scheduler.getCurrentTrack();
                    JsonObject theTrack = new JsonObject();
                    theTrack.addProperty("track", track.track.getIdentifier());
                    theTrack.addProperty("loops", track.loopcount);
                    writer.add("currentTrack", theTrack);
                }

                {
                    JsonArray theQueue = new JsonArray();
                    for (TrackRules track : queue) {
                        JsonObject theTrack = new JsonObject();
                        theTrack.addProperty("track", track.track.getIdentifier());
                        theTrack.addProperty("loops", track.loopcount);
                        theQueue.add(theTrack);
                    }
                    writer.add("queue", theQueue);
                }

                return writer.toString();
            });

            // ClearQueue
            Spark.get("/clearqueue", (req, res) -> {
                handler.scheduler.clearQueue();
                return "00 - success";
            });

            // Shuffle
            Spark.get("/shuffle", (req, res) -> {
                handler.scheduler.shuffle();
                return "00 - success";
            });

            // loop
            Spark.get("/loop", (req, res) -> {
                if (handler.scheduler.loop()) {
                    return "{\"looping\": true}";
                } else {
                    return "{\"looping\": false}";
                }
            });

            // Queue Loop
            Spark.get("/loopqueue", (req, res) -> {
                if (handler.scheduler.loopQueue()) {
                    return "{\"looping\": true}";
                } else {
                    return "{\"looping\": false}";
                }
            });

            // Seek
            Spark.get("/seek", (req, res) -> {
                try {
                    Float time = req.queryMap("time").floatValue();

                    if (time == null) {
                        throw new ArgumentException("03 - You must provide a time as a query parameter");
                    }

                    switch (handler.scheduler.seek(time)) {
                        case 1:
                            res.status(400);
                            return "04 -You cannot seek through this track";
                        case 2:
                            res.status(400);
                            return "05 - You need to provide a valid time, between 0 and " + handler.scheduler.getCurrentTrack().track.getDuration();
                        case 3:
                            res.status(400);
                            return "06 - There isn't a track to seek";
                    }
                    return "00 - Success";
                } catch (NumberFormatException e) {
                    throw new ArgumentException("07 - Time must be a valid number");
                }
            });

            // Get Track Length
            Spark.get("/tracktiming", (req, res) -> {
                TrackRules currentTrack = handler.scheduler.getCurrentTrack();

                if (currentTrack == null) {
                    res.status(400);
                    return "03 - No track is playing right now";
                } else if (!currentTrack.track.isSeekable()) {
                    res.status(400);
                    return "04 - You cannot seek through this track";
                }

                return "{" +
                        "\"trackLength\": " + ((float) handler.scheduler.getCurrentTrack().track.getDuration()) / 1000.f + "," +
                        "\"trackTime\": " + ((float) handler.scheduler.getCurrentTrack().track.getPosition()) / 1000.f +
                        "}";
            });

            Spark.after("/*", (req, res) -> {
                serverID = null;
                handler = null;
            });

            Spark.exception(ArgumentException.class, (exception, req, res) -> {
                res.status(400);
                res.body(exception.getMessage());
            });
        });
    }
}
