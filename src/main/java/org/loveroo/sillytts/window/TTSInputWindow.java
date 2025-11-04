package org.loveroo.sillytts.window;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.StyleConstants;

import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.keybind.Keybind;
import org.loveroo.sillytts.util.TTSSystem;
import org.loveroo.sillytts.window.gui.RoundedBorder;

public class TTSInputWindow extends JFrame {

    private final JTextPane textBox;
    
    public TTSInputWindow() {
        setTitle(Config.WINDOW_NAME.get());
        Config.WINDOW_NAME.registerChangeAction(this::setTitle);

        var icon = new ImageIcon(Main.class.getResource("/icon.png"));
        setIconImage(icon.getImage());

        setBounds(960+1920 - (Window.TTS_INPUT.getWidth() / 2), 540 - (Window.TTS_INPUT.getHeight() / 2), Window.TTS_INPUT.getWidth(), Window.TTS_INPUT.getHeight());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        textBox = new JTextPane();

        textBox.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new RoundedBorder(15, Config.OUTLINE_COLOR)));
        textBox.setBackground(new Color(20, 20, 20));

        var textStyle = textBox.addStyle("text_color", null);
        StyleConstants.setForeground(textStyle, Config.TEXT_COLOR.get());
        StyleConstants.setFontSize(textStyle, Config.FONT_SIZE.get());
        StyleConstants.setFontFamily(textStyle, Main.getFont().getFontName());

        Config.TEXT_COLOR.registerChangeAction((color) -> {
            StyleConstants.setForeground(textStyle, color);
        });

        Config.FONT_SIZE.registerChangeAction((color) -> {
            StyleConstants.setFontSize(textStyle, color);
        });

        textBox.setLogicalStyle(textStyle);

        textBox.setSelectionColor(Config.SELECTION_COLOR.get());
        textBox.setCaretColor(Config.CARET_COLOR.get());

        Config.SELECTION_COLOR.registerChangeAction(textBox::setSelectionColor);
        Config.SELECTION_COLOR.registerChangeAction(textBox::setCaretColor);

        textBox.addKeyListener(Keybind.SEND_TTS_KEYBIND.getATWListener());
        textBox.addKeyListener(Keybind.MINIMIZE_TTS_KEYBIND.getATWListener());
        textBox.addKeyListener(Keybind.CLOSE_TTS_KEYBIND.getATWListener());

        addWindowListener(new WindowListener());

        add(textBox);
        setVisible(true);
    }

    public void sendTTS() {
        var text = textBox.getText();

        textBox.setText("");
        setState(Frame.ICONIFIED);

        TTSSystem.sendTTSString(text);
    }

    public void minimize() {
        setState(Frame.ICONIFIED);
        textBox.setText("");
    }

    class WindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent event) {
            minimize();
        }
    }
}
