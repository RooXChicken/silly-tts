package org.loveroo.sillytts;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import org.jnativehook.GlobalScreen;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.ConfigOption;
import org.loveroo.sillytts.keybind.Keybind;
import org.loveroo.sillytts.util.AudioSystem;
import org.loveroo.sillytts.window.TTSInputWindow;
import org.loveroo.sillytts.window.gui.RoundedBorder;

public class Main {

    private static Font font;
    private static TTSInputWindow ttsInputWindow;

    private static ImageIcon windowIcon;

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

            font = Font.createFonts(fontStream)[0].deriveFont((float) Config.FONT_SIZE.get());
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
        }
        catch(Exception e) {
            System.out.println("Failed to load font! " + e);
            return;
        }

        windowIcon = new ImageIcon(Main.class.getResource("/icon.png"));

        Config.load();
        Config.save();

        // javax.swing.UIManager.getDefaults().keySet()
        //     .stream()
        //     .map((it) -> it.toString())
        //     .sorted()
        //     .forEach((it) -> System.out.println(it));

        initTheme();
        // UIManager.put("ComboBox.selectionBackground", new ColorUIResource(Config.SELECTION_COLOR.get()));
        // UIManager.put("ComboBox.selectionBackground", new ColorUIResource(Config.SELECTION_COLOR.get()));

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

    private static void initTheme() {
        UIManager.put("Caret.width", 2);

        final var border = new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new RoundedBorder(15, Config.OUTLINE_COLOR));

        UIManager.put("TextPane.border", border);
        UIManager.put("ListUI.border", border);

        UIManager.put("Label.font", getFont());
        UIManager.put("TextPane.font", getFont());
        UIManager.put("ComboBox.font", getFont());

        // UIManager.put("ComboBox.borderPaintsFocus", true);

        registerUpdater("Label.foreground", Config.TEXT_COLOR);
        registerUpdater("TextPane.foreground", Config.TEXT_COLOR);
        registerUpdater("ComboBox.foreground", Config.TEXT_COLOR);

        registerUpdater("TextPane.caretForeground", Config.SELECTION_COLOR);
        registerUpdater("TextPane.selectionBackground", Config.SELECTION_COLOR);
        registerUpdater("ComboBox.selectionBackground", Config.SELECTION_COLOR);
        registerUpdater("ComboBox.buttonHighlight", Config.SELECTION_COLOR);
        registerUpdater("ComboBox.buttonShadow", Config.SELECTION_COLOR);
        registerUpdater("ComboBox.buttonDarkShadow", Config.SELECTION_COLOR);
        
        registerUpdater("TextPane.background", Config.BACKGROUND_COLOR);
        registerUpdater("TextPane.selectionForeground", Config.BACKGROUND_COLOR);
        registerUpdater("Label.background", Config.BACKGROUND_COLOR);
        registerUpdater("Panel.background", Config.BACKGROUND_COLOR);
        registerUpdater("ComboBox.background", Config.BACKGROUND_COLOR);
        registerUpdater("ComboBox.buttonBackground", Config.BACKGROUND_COLOR);
    }

    private static void registerUpdater(String key, ConfigOption<?> option) {
        option.registerChangeAction((value) -> {
            UIManager.put(key, value);
        });

        UIManager.put(key, option.get());
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

    public static ImageIcon getWindowIcon() {
        return windowIcon;
    }

    public static void styleWindow(Style style) {
        StyleConstants.setForeground(style, Config.TEXT_COLOR.get());
        StyleConstants.setFontSize(style, Config.FONT_SIZE.get());
        StyleConstants.setFontFamily(style, Main.getFont().getFontName());

        Config.TEXT_COLOR.registerChangeAction((color) -> {
            StyleConstants.setForeground(style, color);
        });

        Config.FONT_SIZE.registerChangeAction((color) -> {
            StyleConstants.setFontSize(style, color);
        });
    }
}