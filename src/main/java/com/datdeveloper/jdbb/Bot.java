package com.datdeveloper.jdbb;

import com.datdeveloper.jdbb.listeners.CommandListener;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Bot {
    static final Activity[] activities = {Activity.playing("with the ship's navigation system"),Activity.playing("with the DM's notes"), Activity.playing("with the protocol droids"), Activity.playing("with the airlock"), Activity.listening("the hum of the engine"), Activity.listening("the DM's inner demons"), Activity.listening("the bickering of the droids"), Activity.listening("David Bowie - Space Oddity"), Activity.listening("John Williams - Duel of the Fates"), Activity.listening("John Williams - Battle of the Heroes"), Activity.watching("space porn"), Activity.watching("Star Wars"),  Activity.watching("the starry sky"), Activity.watching("the stars go by")};
    public static final List<String> DMs = Arrays.asList("713447797491368007", "160836068931928064");
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA api = JDABuilder.createDefault(SecretStuff.DISCORDBOTKEY)
                .enableCache(CacheFlag.VOICE_STATE)
                .setAudioSendFactory(new NativeAudioSendFactory())
                .build();
        api.addEventListener(new CommandListener());
        Activity nextActivityStatus;
        System.out.println(new File("").getAbsolutePath());
        while(true){
            nextActivityStatus = activities[new Random().nextInt(activities.length)];
            System.out.println("Next activity: " + nextActivityStatus.getType().toString() + " " + nextActivityStatus.toString());
            api.getPresence().setActivity(nextActivityStatus);
            Thread.sleep(60000L);
        }
    }
}
