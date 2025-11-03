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
import org.loveroo.sillytts.keybind.Keybind;
import org.loveroo.sillytts.util.TTSSystem;
import org.loveroo.sillytts.window.gui.RoundedBorder;

public class TTSInputWindow extends JFrame {

    private final JTextPane textBox;
    
    public TTSInputWindow() {
        setTitle(Main.getConfig().WINDOW_NAME);

        var icon = new ImageIcon(Main.class.getResource("/icon.png"));
        setIconImage(icon.getImage());

        setBounds(960+1920 - (Window.TTS_INPUT.getWidth() / 2), 540 - (Window.TTS_INPUT.getHeight() / 2), Window.TTS_INPUT.getWidth(), Window.TTS_INPUT.getHeight());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        textBox = new JTextPane();

        textBox.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new RoundedBorder(15, Main.getConfig().OUTLINE_COLOR)));
        textBox.setBackground(new Color(20, 20, 20));

        var textStyle = textBox.addStyle("text_color", null);
        StyleConstants.setForeground(textStyle, Main.getConfig().TEXT_COLOR);
        StyleConstants.setFontSize(textStyle, Main.getConfig().FONT_SIZE);
        StyleConstants.setFontFamily(textStyle, Main.getFont().getFontName());

        textBox.setLogicalStyle(textStyle);

        textBox.setSelectionColor(Main.getConfig().SELECTION_COLOR);
        textBox.setCaretColor(Main.getConfig().CARET_COLOR);

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
