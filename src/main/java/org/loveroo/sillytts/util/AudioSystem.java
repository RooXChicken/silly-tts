package org.loveroo.sillytts.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.loveroo.sillytts.config.Config;
import org.urish.openal.Device;
import org.urish.openal.OpenAL;
import org.urish.openal.jna.ALFactory;

public class AudioSystem {

    private static OpenAL openal;
    private static ALFactory factory;

    public static void init() {
        try {
            factory = new ALFactory();

            registerOpenAL(Config.OUTPUT_DEVICE.get());
            Config.OUTPUT_DEVICE.registerChangeAction(AudioSystem::registerOpenAL);
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
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
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
    
    /**
     * Plays the audio from the stream on a new thread
     * @param stream The audio stream
     */
    public static void playSoundFromStream(InputStream stream) {
        try {
            final var thread = new AudioThread(stream.readAllBytes());
            thread.start();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    static class AudioThread extends Thread {

        private static AudioFormat format;
        private final byte[] data;

        public AudioThread(byte[] data) {
            this.data = data;

            loadFormat();

            Config.AUDIO_SAMPLE_RATE.registerChangeAction((channels) -> { loadFormat(); });
            Config.AUDIO_CHANNELS.registerChangeAction((channels) -> { loadFormat(); });
        }

        private static void loadFormat() {
            format = new AudioFormat(Config.AUDIO_SAMPLE_RATE.get(), 16, Config.AUDIO_CHANNELS.get(), true, false);
        }

        @Override
        public void run() {
            try {
                var stream = new ByteArrayInputStream(data);
                
                var source = openal.createSource(new AudioInputStream(stream, format, data.length));
                source.play();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
