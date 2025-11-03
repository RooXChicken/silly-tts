package org.loveroo.sillytts.util;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class AudioSystem {
    
    public static void playSoundFromStream(InputStream stream) throws Exception {
        final var thread = new AudioThread(stream);
        thread.start();

        // // final var fileStream = new FileInputStream(fileName);
        // final var bufferedStream = new BufferedInputStream(stream);

        // final var audioStream = AudioSystem.getAudioInputStream(bufferedStream);
        // final var clip = (Clip) AudioSystem.getLine(new Line.Info());

        // clip.addLineListener((event) -> {
        //     if(event.getType() == LineEvent.Type.STOP) {
        //         try {
        //             clip.close();
        //             stream.close();

        //             bufferedStream.close();
        //             audioStream.close();
        //         }
        //         catch(Exception e) { }
        //     }
        // });

        // clip.open(audioStream);
        // clip.start();
    }

    static class AudioThread extends Thread {

        private final InputStream stream;

        public AudioThread(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            try {
                final var format = new AudioFormat(22050, 16, 1, true, false);
                final var info = new DataLine.Info(SourceDataLine.class, format);
                final var line = (SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
    
                final var BUFFER_SIZE = 4096;
    
                line.open(format, BUFFER_SIZE);
                line.start();
    
                var bytesRead = 0;
                final var buffer = new byte[BUFFER_SIZE];
    
                while(bytesRead != -1) {
                    bytesRead = stream.read(buffer, 0, BUFFER_SIZE);
    
                    if(bytesRead < 0) {
                        continue;
                    }
                    
                    line.write(buffer, 0, bytesRead);
                }
    
                line.drain();
                line.stop();
    
                line.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
