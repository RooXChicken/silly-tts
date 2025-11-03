package org.loveroo.sillytts.util;

import java.lang.ProcessBuilder.Redirect;
import java.util.List;

import org.loveroo.sillytts.Main;

public class TTSSystem {
    
    public static void sendTTSString(String input) {
        sendRawTTSString(replaceText(input));
    }

    public static void sendRawTTSString(String input) {
        var thread = new TTSThread(input);
        thread.start();
    }

    private static String replaceText(String text) {
        var newText = " " + text.toLowerCase() + " ";

        for(var entry : Main.getConfig().WORD_REPLACEMENTS.entrySet()) {
            var key = String.format("\\s%s\\s", entry.getKey());
            var word = String.format(" %s ", entry.getValue());

            newText = newText.replaceAll(key, word);
        }

        for(var entry : Main.getConfig().CONST_REPLACEMENTS.entrySet()) {
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
                var processes = ProcessBuilder.startPipeline(List.of(
                    new ProcessBuilder(new String[] { "echo", input })
                        .inheritIO()
                        .redirectOutput(Redirect.PIPE),
                    new ProcessBuilder(new String[] { Main.getConfig().PIPER_PATH, "--model", Main.getConfig().VOICE_MODEL, "--output-raw" })
                        .redirectError(Redirect.DISCARD)
                ));

                AudioSystem.playSoundFromStream(processes.get(processes.size()-1).getInputStream());
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
