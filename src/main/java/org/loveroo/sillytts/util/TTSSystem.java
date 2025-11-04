package org.loveroo.sillytts.util;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import org.loveroo.sillytts.config.Config;

public class TTSSystem {
    
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
        var thread = new TTSThread(input);
        thread.start();
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

        public TTSThread(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            try {
                // pipe echo into piper for file-less playback

                var processes = ProcessBuilder.startPipeline(List.of(
                    new ProcessBuilder(new String[] { "echo", input })
                        .inheritIO()
                        .redirectOutput(Redirect.PIPE),
                    new ProcessBuilder(new String[] { Config.PIPER_PATH.get(), "--model", Config.VOICE_MODEL.get(), "--output-raw" })
                        .redirectError(Redirect.DISCARD)
                ));

                // play the audio from the stream
                AudioSystem.playSoundFromStream(processes.get(processes.size()-1).getInputStream());
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
