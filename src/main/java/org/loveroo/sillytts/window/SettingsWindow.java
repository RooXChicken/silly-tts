package org.loveroo.sillytts.window;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;
import org.loveroo.sillytts.config.Config.FilePath;
import org.loveroo.sillytts.util.AudioSystem;
import org.loveroo.sillytts.window.gui.BorderlessComboBox;
import org.loveroo.sillytts.window.gui.CheckBoxIcon;
import org.loveroo.sillytts.window.gui.CustomTabbedPane;
import org.loveroo.sillytts.window.gui.SimpleDocumentListener;

public class SettingsWindow extends JFrame {
    
    public SettingsWindow() {
        setTitle("Settings");
        setIconImage(Main.getWindowIcon().getImage());

        setLayout(null);
        setSize(Window.SETTINGS.getWidth(), Window.SETTINGS.getHeight());

        final var tabPane = new JTabbedPane();
        tabPane.setUI(new CustomTabbedPane());
        tabPane.setBounds(0, 0, getWidth() - 3, getHeight() - 31);

        final var piperPane = new SettingsPane(
            Config.PIPER_COMMAND,
            Config.VOICE_MODEL
        );
        
        final var audioPane = new SettingsPane(
            null,
            Config.AUDIO_SAMPLE_RATE,
            Config.AUDIO_CHANNELS
        );

        final var visualPane = new SettingsPane(
            // Config.FONT_SIZE,
            Config.BACKGROUND_COLOR,
            Config.OUTLINE_COLOR,
            Config.CARET_COLOR,
            Config.SELECTION_COLOR
        );
        
        final var appPane = new SettingsPane(
            Config.WINDOW_NAME,
            Config.TTS_WINDOW_OPEN_DELAY,
            Config.MINIMIZE_ON_X
        );
        
        final var audioDeviceLabel = SettingsPane.createLabel(Config.OUTPUT_DEVICE.getConfigElement(), 14);
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
        audioDevice.setToolTipText(ConfigElement.OUTPUT_DEVICE.getDescription());
        
        Config.TEXT_COLOR.registerChangeAction((color) -> {
            audioDevice.setForeground(color);
        });
        
        updateAudioDevices(audioDevice);
        audioDevice.addMouseListener(new RefreshAudioListener(audioDevice));

        audioPane.add(audioDevice, JLayeredPane.DEFAULT_LAYER);
        audioPane.add(audioDeviceLabel, JLayeredPane.PALETTE_LAYER);

        tabPane.addTab("Audio", audioPane);
        tabPane.addTab("Piper", piperPane);
        tabPane.addTab("Visual", visualPane);
        tabPane.addTab("App", appPane);

        add(tabPane);
        tabPane.setVisible(true);
        
        setVisible(true);
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

    static class SettingsPane extends JLayeredPane {

        private static final int ELEMENT_START = 14;
        private static final int ELEMENT_HEIGHT = 48;

        @SuppressWarnings("unchecked")
        public SettingsPane(ConfigOption<?> ... options) {
            setBounds(0, 32, getWidth(), getHeight() - 32);

            var index = 0;

            for(var option : options) {
                if(option == null) {
                    index++;
                    continue;
                }

                JComponent optionComponent = null;
                var y = ELEMENT_START + (ELEMENT_HEIGHT * index);
                
                if(option.getDefaultValue().getClass() == String.class) {
                    optionComponent = createTextOption((ConfigOption<String>) option, y);
                }
                if(option.getDefaultValue().getClass() == FilePath.class) {
                    optionComponent = createFilePathOption((ConfigOption<FilePath>) option, y);
                }
                else if(option.getDefaultValue().getClass() == Integer.class) {
                    optionComponent = createIntegerOption((ConfigOption<Integer>) option, y);
                }
                else if(option.getDefaultValue().getClass() == Boolean.class) {
                    optionComponent = createCheckOption((ConfigOption<Boolean>) option, y);
                }
                else if(option.getDefaultValue().getClass() == Color.class) {
                    optionComponent = createColorPickerOption((ConfigOption<Color>) option, y);
                }
                
                if(optionComponent != null) {
                    add(optionComponent, JLayeredPane.DEFAULT_LAYER);
                }

                var label = createLabel(option.getConfigElement(), y);
                add(label, JLayeredPane.PALETTE_LAYER);

                index++;
            }
        }

        private JComponent createTextOption(ConfigOption<String> option, int y) {
            var textBox = createTextPane(option, y, (pane, event) -> {
                option.set(pane.getText());
            });

            return textBox;
        }

        private JComponent createIntegerOption(ConfigOption<Integer> option, int y) {
            var textBox = createTextPane(option, y, (pane, event) -> {
                try {
                    var value = (int) Double.parseDouble(pane.getText());
                    option.set(value);
                }
                catch(Exception e) { }
            });

            textBox.setSize(96, textBox.getHeight());

            return textBox;
        }

        private JComponent createCheckOption(ConfigOption<Boolean> option, int y) {
            var checkBox = new JCheckBox();
            checkBox.setIcon(new CheckBoxIcon());
            checkBox.setBounds(0, y, 32, 32);
            
            checkBox.addItemListener((event) -> {
                option.set(checkBox.isSelected());
            });

            checkBox.setSelected(option.get());
            checkBox.setToolTipText(option.getConfigElement().getDescription());

            return checkBox;
        }

        private JComponent createColorPickerOption(ConfigOption<Color> option, int y) {
            var colorPicker = createTextPane(option, y, (pane, event) -> {
                try {
                    var value = (int) Long.parseLong(pane.getText().trim(), 16);
                    option.set(new Color(value));
                }
                catch(Exception e) { }
            });

            colorPicker.setSize(128, colorPicker.getHeight());
            colorPicker.setText(toRGBHex(option.get()));

            var button = createButton("Reset", new Rectangle(colorPicker.getWidth() + 16, y, 96, 35), (event) -> {
                colorPicker.setText(toRGBHex(option.getDefaultValue()));
            });

            add(button);

            return colorPicker;
        }

        public String toRGBHex(Color color) {
            var rgb = color.getRGB();

            var r = (rgb >> 16 & 0xff);
            var g = (rgb >> 8 & 0xff);
            var b = (rgb & 0xff);

            var rText = getColorHexComponent(r);
            var gText = getColorHexComponent(g);
            var bText = getColorHexComponent(b);

            return (rText + gText + bText).toUpperCase();
        }

        private String getColorHexComponent(int component) {
            var text = Integer.toHexString(component);
            return "0".repeat(2 - text.length()) + text;
        }

        private JComponent createFilePathOption(ConfigOption<FilePath> option, int y) {
            var path = createTextPane(option, y, (pane, event) -> {
                option.get().setPath(pane.getText());
                option.set(option.get());
            });

            path.setSize(path.getWidth() - 64, path.getHeight());

            var filePicker = createButton("...", new Rectangle(path.getWidth() + 16, y, 48, 35), (event) -> {
                var picker = new JFileChooser();
                var filter = new FileNameExtensionFilter("Piper TTS File", "onnx");

                picker.setFileFilter(filter);
                if(picker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    path.setText(picker.getSelectedFile().getAbsolutePath());
                }
            });

            add(filePicker);

            return path;
        }

        private JTextPane createTextPane(ConfigOption<?> option, int y, TextPaneOnChange event) {
            var textBox = new JTextPane();
            textBox.setBounds(0, y, Window.SETTINGS.getWidth() - 42, 35);

            textBox.setText(String.valueOf(option.get()));
            textBox.setToolTipText(option.getConfigElement().getDescription());

            textBox.getDocument().addDocumentListener(new SimpleDocumentListener((documentEvent) -> {
                event.onChange(textBox, documentEvent);
            }));

            return textBox;
        }

        private JButton createButton(String name, Rectangle bounds, ActionListener event) {
            var button = new JButton(name);
            button.setBounds(bounds);

            button.addActionListener(event);

            return button;
        }

        private static JLabel createLabel(ConfigElement option, int y) {
            var label = new JLabel(" " + option.getShownName());
            label.setBounds(12, y - 14, (int) label.getPreferredSize().getWidth() + 12, (int) label.getPreferredSize().getHeight());
            label.setOpaque(true);
            label.setToolTipText(option.getDescription());

            return label;
        }

        public static interface TextPaneOnChange {

            public void onChange(JTextPane pane, DocumentEvent event);
        }
    }
}
