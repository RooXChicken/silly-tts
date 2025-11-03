package org.loveroo.sillytts.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import org.loveroo.sillytts.Main;
import org.urish.openal.Device;
import org.urish.openal.OpenAL;
import org.urish.openal.jna.ALFactory;

public class AudioSystem {

    private static OpenAL openal;
    private static ALFactory factory;

    public static void init() {
        try {
            factory = new ALFactory();
            openal = new OpenAL(factory, Main.getConfig().OUTPUT_DEVICE);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        openal.close();
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

        private final byte[] data;

        public AudioThread(byte[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                var format = new AudioFormat(Main.getConfig().AUDIO_SAMPLE_RATE, 16, Main.getConfig().AUDIO_CHANNELS, true, false);

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
