package com.datdeveloper.jdbb;

import com.datdeveloper.jdbb.listeners.CommandListener;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Bot {
    // Activities to display in discord
    static final Activity[] activities = {Activity.playing("with the ship's navigation system"),Activity.playing("with the DM's notes"), Activity.playing("with the protocol droids"), Activity.playing("with the airlock"), Activity.listening("the hum of the engine"), Activity.listening("the DM's inner demons"), Activity.listening("the bickering of the droids"), Activity.listening("David Bowie - Space Oddity"), Activity.listening("John Williams - Duel of the Fates"), Activity.listening("John Williams - Battle of the Heroes"), Activity.watching("space porn"), Activity.watching("Star Wars"),  Activity.watching("the starry sky"), Activity.watching("the stars go by")};
    // The delay before selecting a new activity
    static final long activityDelay = 60000L;

    public static final Logger logger = LoggerFactory.getLogger(Bot.class);

    // Discord ID's for permissions
    public static final List<String> DMs = Arrays.asList("713447797491368007", "160836068931928064");

    public static RestAPI rest;


    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA api = JDABuilder.createDefault(SecretStuff.DISCORDBOTKEY)
                .enableCache(CacheFlag.VOICE_STATE)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .build();
        api.addEventListener(new CommandListener());

        rest = new RestAPI();

        Activity nextActivityStatus;

        while(true){
            nextActivityStatus = activities[new Random().nextInt(activities.length)];
            logger.info("Next activity: " + nextActivityStatus.getType().toString() + " " + nextActivityStatus.toString());
            api.getPresence().setActivity(nextActivityStatus);

            // the event listeners are in another thread, we're safe to put this one to sleep
            Thread.sleep(activityDelay);
        }
    }
}
