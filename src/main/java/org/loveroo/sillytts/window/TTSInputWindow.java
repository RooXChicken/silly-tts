package org.loveroo.sillytts.window;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.keybind.Keybind;
import org.loveroo.sillytts.util.TTSSystem;
import org.loveroo.sillytts.window.context.SimpleContextMenu;
import org.loveroo.sillytts.window.context.SimpleContextMenu.ContextMenu;
import org.loveroo.sillytts.window.gui.element.TextPane;

public class TTSInputWindow extends JFrame {

    private final JTextPane textBox;
    
    public TTSInputWindow() {
        setTitle(Config.WINDOW_NAME.get());
        Config.WINDOW_NAME.registerChangeAction(this::setTitle);

        setIconImage(Main.getWindowIcon().getImage());

        setSize(Window.TTS_INPUT.getWidth(), Window.TTS_INPUT.getHeight());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        var context = new SimpleContextMenu(ContextMenu.MAIN_TTS);

        textBox = new TextPane();
        textBox.setComponentPopupMenu(context);

        textBox.addKeyListener(Keybind.SEND_TTS.getATWListener());
        textBox.addKeyListener(Keybind.MINIMIZE_TTS.getATWListener());
        textBox.addKeyListener(Keybind.CLOSE_TTS.getATWListener());
        textBox.addKeyListener(Keybind.OPEN_SETTINGS.getATWListener());

        UpdateTextLock.setTextPane(textBox);

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
            if(Config.MINIMIZE_ON_X.get()) {
                minimize();
            }
            else {
                System.exit(0);
            }
        }
    }

    public static class UpdateTextLock extends Thread {

        private static JTextPane text;

        private static void setTextPane(JTextPane text) {
            UpdateTextLock.text = text;
        }

        @Override
        public void run() {
            text.setEditable(false);

            try {
                Thread.sleep(Config.TTS_WINDOW_OPEN_DELAY.get());
            }
            catch (InterruptedException e) { }

            text.setEditable(true);
        }
    }
}
