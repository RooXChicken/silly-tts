package org.loveroo.sillytts.util;

import java.io.ByteArrayInputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.util.AudioSystem.AudioData;

public class TTSSystem {
    
    private static final String SOUND_FINDER_REGEX = ":[^\\s:]+?:";
    private static final Pattern SOUND_FINDER_PATTERN = Pattern.compile(SOUND_FINDER_REGEX);

    /**
     * Wraps {@link TTSSystem#sendRawTTSString(String)} in {@link TTSSystem#replaceText(String)} for proper formatting and text replacement
     * @param input The text
     */
    public static void sendTTSString(String input) {
        sendRawTTSString(replaceText(input));
    }

    /**
     * Sends the input over to Piper, which further passes it along to {@link AudioSystem#playSoundFromStream(java.io.InputStream)}
     * @param input The text
     */
    public static void sendRawTTSString(String input) {
        final var split = input.split(SOUND_FINDER_REGEX);
        final var sounds = new ArrayList<String>();

        final var match = SOUND_FINDER_PATTERN.matcher(input);
        while(match.find()) {
            sounds.add(match.group());
        }

        final var streams = new byte[split.length][];

        final var threads = new LinkedHashSet<Thread>();

        for(var i = 0; i < split.length; i++) {
            final var index = i;
            final var line = split[index];

            var thread = new TTSThread(line, (stream) -> {
                streams[index] = (stream);
            });

            threads.add(thread);
            thread.start();
        }

        for(var thread : threads) {
            try {
                thread.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        final var audioData = new ArrayList<AudioData>();

        final var BUFFER_WHITESPACE = 8820;

        var soundIndex = 0;
        for(var i = 0; i < streams.length + sounds.size(); i++) {
            if(i < streams.length) {
                var buf = streams[i];
                audioData.add(new AudioData(AudioSystem.getFormat(), Arrays.copyOf(buf, Math.max(0, buf.length - BUFFER_WHITESPACE))));
            }

            if(soundIndex < sounds.size()) {
                var sound = sounds.get(soundIndex++);
                if(!Config.SOUND_EFFECTS.get().containsKey(sound)) {
                    continue;
                }

                try {
                    var soundData = Files.readAllBytes(Paths.get(Config.SOUND_EFFECTS.get().get(sound)));
    
                    var soundStream = new ByteArrayInputStream(soundData);
                    var sourceStream = javax.sound.sampled.AudioSystem.getAudioInputStream(soundStream);
                    var targetStream = javax.sound.sampled.AudioSystem.getAudioInputStream(AudioSystem.getFormat(), sourceStream);
    
                    audioData.add(new AudioData(targetStream.getFormat(), targetStream.readAllBytes()));
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        AudioSystem.playSoundFromStream(audioData);
    }

    private static String replaceText(String text) {
        var newText = " " + text.toLowerCase() + " ";

        for(var entry : Config.WORD_REPLACEMENTS.get().entrySet()) {
            var key = String.format("\\s%s\\s", entry.getKey());
            var word = String.format(" %s ", entry.getValue());

            newText = newText.replaceAll(key, word);
        }

        for(var entry : Config.CONST_REPLACEMENTS.get().entrySet()) {
            newText = newText.replaceAll(entry.getKey(), entry.getValue());
        }

        var output = newText.replaceAll("[',.!]\\s", "... ");
        return output.trim();
    }

    static class TTSThread extends Thread {

        private final String input;
        private final TTSFinishedEvent event;

        public TTSThread(String input, TTSFinishedEvent event) {
            this.input = input;
            this.event = event;
        }

        @Override
        public void run() {
            var piperArgs = String.format(" --model %s --output-raw", Config.VOICE_MODEL.get().getPath().trim());

            try {
                // pipe echo into piper for file-less playback
                var processes = ProcessBuilder.startPipeline(List.of(
                    new ProcessBuilder(getOsEchoCommand(input))
                        .inheritIO()
                        .redirectOutput(Redirect.PIPE),
                    new ProcessBuilder((Config.PIPER_COMMAND.get().getPath().trim() + piperArgs).split(" "))
                        .redirectError(Redirect.DISCARD)
                ));

                event.onCompletion(processes.get(processes.size()-1).getInputStream().readAllBytes());

                // play the audio from the stream
                // AudioSystem.playSoundFromStream(processes.get(processes.size()-1).getInputStream());
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        private static String[] getOsEchoCommand(String input) {
            return switch(OSUtil.getOs()) {
                case WINDOWS -> new String[] { "cmd.exe", "/C", "echo", input };
                case LINUX -> new String[] { "echo", input };

                default -> new String[0];
            };
        }

        public static interface TTSFinishedEvent {

            public void onCompletion(byte[] stream);
        }
    }
}
