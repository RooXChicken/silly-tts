package org.loveroo.sillytts;

import java.awt.Color;
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

        initTheme();

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

    private static void repaintAll() {
        for(var frame : Frame.getFrames()) {
            frame.repaint();
        }
    }

    private static Color textColor;
    private static Color caretColor;
    private static Color selectionColor;
    private static Color outlineColor;
    private static Color backgroundColor;

    private static void initTheme() {
        UIManager.put("Caret.width", 2);

        final var border = new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new RoundedBorder(15, Config.OUTLINE_COLOR));

        textColor = Config.TEXT_COLOR.get();
        caretColor = Config.CARET_COLOR.get();
        selectionColor = Config.SELECTION_COLOR.get();
        outlineColor = Config.OUTLINE_COLOR.get();
        backgroundColor = Config.BACKGROUND_COLOR.get();

        UIManager.put("TextPane.border", border);
        UIManager.put("ListUI.border", border);
        UIManager.put("ToolTip.border", border);

        UIManager.put("Label.font", getFont());
        UIManager.put("TextPane.font", getFont());
        UIManager.put("ComboBox.font", getFont());
        UIManager.put("ToolTip.font", getFont());
        UIManager.put("TabbedPane.font", getFont());

        UIManager.put("Label.foreground", textColor);
        UIManager.put("TextPane.foreground", textColor);
        UIManager.put("ComboBox.foreground", textColor);
        UIManager.put("ToolTip.foreground", textColor);
        UIManager.put("TabbedPane.foreground", textColor);

        UIManager.put("TextPane.caretForeground", caretColor);

        UIManager.put("ComboBox.selectionBackground", selectionColor);
        
        UIManager.put("ComboBox.buttonHighlight", outlineColor);
        UIManager.put("ComboBox.buttonShadow", outlineColor);
        UIManager.put("ComboBox.buttonDarkShadow", outlineColor);
        UIManager.put("TabbedPane.borderHightlightColor", outlineColor);
        UIManager.put("TabbedPane.selectHighlight", outlineColor);
        UIManager.put("TabbedPane.selected", outlineColor);
        UIManager.put("TabbedPane.light", outlineColor);
        UIManager.put("TabbedPane.highlight", outlineColor);
        UIManager.put("TabbedPane.shadow", outlineColor);
        UIManager.put("TabbedPane.darkShadow", outlineColor);
        UIManager.put("TabbedPane.focus", outlineColor);
        UIManager.put("TextPane.selectionBackground", outlineColor);
        
        UIManager.put("TextPane.background", backgroundColor);
        UIManager.put("TextPane.selectionForeground", backgroundColor);
        UIManager.put("Label.background", backgroundColor);
        UIManager.put("Panel.background", backgroundColor);
        UIManager.put("ComboBox.background", backgroundColor);
        UIManager.put("ComboBox.buttonBackground", backgroundColor);
        UIManager.put("CheckBox.background", backgroundColor);
        UIManager.put("ToolTip.background", backgroundColor);
        UIManager.put("TabbedPane.background", backgroundColor);
        UIManager.put("TabbedPane.tabAreaBackground", backgroundColor);
        UIManager.put("TabbedPane.contentAreaColor", backgroundColor);

        Config.TEXT_COLOR.registerChangeAction((value) -> {
            repaintAll();
        });

        Config.OUTLINE_COLOR.registerChangeAction((value) -> {
            repaintAll();
        });

        Config.CARET_COLOR.registerChangeAction((value) -> {
            repaintAll();
        });

        Config.SELECTION_COLOR.registerChangeAction((value) -> {
            repaintAll();
        });

        Config.BACKGROUND_COLOR.registerChangeAction((value) -> {
            repaintAll();
        });
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