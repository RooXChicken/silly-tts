package org.loveroo.sillytts.window;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextPane;

import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;
import org.loveroo.sillytts.util.AudioSystem;
import org.loveroo.sillytts.window.gui.BorderlessComboBox;
import org.loveroo.sillytts.window.gui.SimpleDocumentListener;

public class SettingsWindow extends JFrame {
    
    public SettingsWindow() {
        setTitle("Settings");
        setIconImage(Main.getWindowIcon().getImage());

        setLayout(null);
        setSize(Window.SETTINGS.getWidth(), Window.SETTINGS.getHeight());

        var frame = new JLayeredPane();
        frame.setBounds(0, 0, getWidth(), getHeight());

        frame.add(createLabel(ConfigElement.OUTPUT_DEVICE.getShownName(), 14), JLayeredPane.PALETTE_LAYER);
        
        var audioDevice = new JComboBox<>(new String[0]);
        
        audioDevice.addActionListener((action) -> {
            var selection = audioDevice.getSelectedItem();
            if(selection == null) {
                return;
            }
            
            Config.OUTPUT_DEVICE.set(selection);
        });
        
        audioDevice.setBounds(0, 14, Window.SETTINGS.getWidth() - 42, 35);
        audioDevice.setUI(new BorderlessComboBox());
        audioDevice.setMaximumRowCount(8);
        
        Config.TEXT_COLOR.registerChangeAction((color) -> {
            audioDevice.setForeground(color);
        });
        
        updateAudioDevices(audioDevice);
        audioDevice.addMouseListener(new RefreshAudioListener(audioDevice));

        createTextOption(frame, Config.PIPER_COMMAND, 62);
        createTextOption(frame, Config.VOICE_MODEL, 110);

        frame.add(audioDevice, JLayeredPane.DEFAULT_LAYER);

        add(frame);

        frame.setVisible(true);
        setVisible(true);
    }

    private void createTextOption(JLayeredPane frame, ConfigOption<String> option, int y) {
        var textBox = new JTextPane();
        textBox.setBounds(0, y, Window.SETTINGS.getWidth() - 4, 35);

        textBox.getDocument().addDocumentListener(new SimpleDocumentListener((event) -> {
            option.set(textBox.getText());
        }));

        textBox.setText(option.get());

        frame.add(createLabel(option.getConfigElement().getShownName(), y), JLayeredPane.PALETTE_LAYER);
        frame.add(textBox, JLayeredPane.DEFAULT_LAYER);
    }

    private JLabel createLabel(String text, int y) {
        var label = new JLabel(" " + text);
        label.setBounds(12, y - 14, (int) label.getPreferredSize().getWidth() + 12, (int) label.getPreferredSize().getHeight());
        label.setOpaque(true);

        return label;
    }

    private static void updateAudioDevices(JComboBox<String> box) {
        var model = (DefaultComboBoxModel<String>) box.getModel();
        model.removeAllElements();

        var devices = AudioSystem.getAudioDevices();
        var element = devices.stream().filter((it) -> it.equals(Config.OUTPUT_DEVICE.get())).findFirst().orElse(null);

        model.addAll(devices);
        model.setSelectedItem(element);
    }

    static class RefreshAudioListener implements MouseListener {

        private final JComboBox<String> box;

        public RefreshAudioListener(JComboBox<String> box) {
            this.box = box;
        }

        @Override
        public void mouseClicked(MouseEvent event) { }

        @Override
        public void mousePressed(MouseEvent event) {
            SettingsWindow.updateAudioDevices(box);
        }

        @Override
        public void mouseReleased(MouseEvent event) { }

        @Override
        public void mouseEntered(MouseEvent event) { }

        @Override
        public void mouseExited(MouseEvent event) { }
    }
}
