package org.loveroo.sillytts.util;

import java.io.File;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import org.loveroo.sillytts.config.Config;
import org.urish.openal.Device;
import org.urish.openal.OpenAL;
import org.urish.openal.Source;
import org.urish.openal.jna.ALFactory;

public class AudioSystem {

    private static OpenAL openal;
    private static ALFactory factory;

    private static AudioFormat format;

    public static void init() {
        try {
            factory = new ALFactory();

            registerOpenAL(Config.OUTPUT_DEVICE.get());

            loadFormat();

            Config.OUTPUT_DEVICE.registerChangeAction(AudioSystem::registerOpenAL);

            Config.AUDIO_SAMPLE_RATE.registerChangeAction((channels) -> { loadFormat(); });
            Config.AUDIO_CHANNELS.registerChangeAction((channels) -> { loadFormat(); });
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        if(openal != null) {
            openal.close();

            openal = null;
        }
    }

    private static void registerOpenAL(String device) {
        shutdown();

        try {
            openal = new OpenAL(factory, device);
            System.out.println("Created new OpenAL device: " + device);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadFormat() {
        format = new AudioFormat(Config.AUDIO_SAMPLE_RATE.get(), 16, Config.AUDIO_CHANNELS.get(), true, false);
    }

    public static List<String> getAudioDevices() {
        try {
            return Device.availableDevices(factory);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return List.of();
    }

    public static Source loadFile(File file) {
        try {
            return openal.createSource(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static AudioFormat getFormat() {
        return format;
    }

    public static void stopAll() {
        registerOpenAL(Config.OUTPUT_DEVICE.get());
    }
    
    /**
     * Plays the audio from the stream on a new thread
     * @param stream The audio stream
     */
    public static void playSoundFromStream(byte[] buffer) {
        final var thread = new AudioThread(buffer);
        thread.start();
    }

    /**
     * Plays the audio from the stream on a new thread
     * @param stream The audio stream
     */
    public static void playSoundFromStream(List<AudioData> data) {
        final var thread = new AudioThread(data);
        thread.start();
    }

    static class AudioThread extends Thread {

        private final List<AudioData> audioData;

        public AudioThread(List<AudioData> audioData) {
            this.audioData = audioData;
        }

        public AudioThread(byte[] audioData) {
            this(List.of(new AudioData(format, audioData)));
        }

        @Override
        public void run() {
            try {
                final var source = openal.createSource();
                
                for(var data : audioData) {
                    var buffer = openal.createBuffer();
                    buffer.addBufferData(format, data.data());
                    
                    source.queueBuffer(buffer);
                }
                
                source.play();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static record AudioData(AudioFormat format, byte[] data) {

        public AudioData {
            if(format == null) {
                format = getFormat();
            }
        }
    }
}
