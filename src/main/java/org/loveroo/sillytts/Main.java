package org.loveroo.sillytts;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;

import org.jnativehook.GlobalScreen;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.keybind.Keybind;
import org.loveroo.sillytts.util.AudioSystem;
import org.loveroo.sillytts.window.TTSInputWindow;

public class Main {

    private static Font font;
    private static TTSInputWindow ttsInputWindow;

    public static void main(String[] args) {
        // turns off the logging for the native hook
        final var logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        try {
            var fontStream = Main.class.getResourceAsStream("/CascadiaMono-Bold.ttf");

            if(fontStream == null) {
                System.out.println("Failed to load font! Stream is null!");
                return;
            }

            font = Font.createFonts(fontStream)[0];
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        }
        catch(Exception e) {
            System.out.println("Failed to load font! " + e);
            return;
        }

        Config.load();
        Config.save();

        UIManager.put("Caret.width", 2);

        ttsInputWindow = new TTSInputWindow();
        setEnabled(false);

        AudioSystem.init();
        Runtime.getRuntime().addShutdownHook(new Thread(AudioSystem::shutdown));

        try
        {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(Keybind.OPEN_TTS_WINDOW_KEYBIND.getNativeListener());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Controls whether the window is shown
     * @param enabled The window's visibility
     */
    public static void setEnabled(boolean enabled) {
        if(enabled) {
            ttsInputWindow.setState(Frame.NORMAL);
        }
        else {
            ttsInputWindow.minimize();
        }
    }

    /**
     * Gets the main app font
     * @return The font
     */
    public static Font getFont() {
        return font;
    }

    /**
     * Gets the main TTS window
     * @return The TTS window
     */
    public static TTSInputWindow getTTSWindow() {
        return ttsInputWindow;
    }
}