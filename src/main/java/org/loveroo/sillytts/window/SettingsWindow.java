package org.loveroo.sillytts.window;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map.Entry;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;
import org.loveroo.sillytts.config.Config.FilePath;
import org.loveroo.sillytts.config.custom.HashMapOption;
import org.loveroo.sillytts.config.custom.KeybindOption;
import org.loveroo.sillytts.util.AudioSystem;
import org.loveroo.sillytts.window.gui.BorderlessComboBox;
import org.loveroo.sillytts.window.gui.CheckBoxIcon;
import org.loveroo.sillytts.window.gui.CustomTabbedPane;
import org.loveroo.sillytts.window.gui.SimpleDocumentListener;
import org.loveroo.sillytts.window.gui.element.Button;
import org.loveroo.sillytts.window.gui.element.CheckBox;
import org.loveroo.sillytts.window.gui.element.ComboBox;
import org.loveroo.sillytts.window.gui.element.TextPane;

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
            Config.BACKGROUND_COLOR,
            Config.COMPONENT_BACKGROUND_COLOR,
            Config.OUTLINE_COLOR,
            Config.CARET_COLOR,
            Config.SELECTION_COLOR
        );

        final var sfxPane = new SettingsPane(
            Config.SOUND_EFFECTS
        );

        final var keybindPane = new SettingsPane(
            Config.OPEN_TTS_WINDOW_KEYBIND,
            Config.SEND_TTS_KEYBIND,
            Config.MINIMIZE_TTS_KEYBIND,
            Config.CLOSE_TTS_KEYBIND,
            Config.STOP_TTS_KEYBIND,
            Config.OPEN_SETTINGS_KEYBIND
        );
        
        final var appPane = new SettingsPane(
            Config.WINDOW_NAME,
            Config.TTS_WINDOW_OPEN_DELAY,
            Config.MINIMIZE_ON_X
        );
        
        final var audioDeviceLabel = SettingsPane.createLabel(Config.OUTPUT_DEVICE.getConfigElement(), 18);
        var audioDevice = new ComboBox<>(new String[0]);
        
        audioDevice.addActionListener((action) -> {
            var selection = audioDevice.getSelectedItem();
            if(selection == null) {
                return;
            }
            
            Config.OUTPUT_DEVICE.set(selection);
        });
        
        audioDevice.setBounds(0, 18, Window.SETTINGS.getWidth() - 42, 35);
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
        tabPane.addTab("Sounds", sfxPane);
        tabPane.addTab("Visual", visualPane);
        tabPane.addTab("Keybind", keybindPane);
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

        private static final int ELEMENT_START = 18;
        private static final int ELEMENT_HEIGHT = 60;

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
                else if(option.getClass() == KeybindOption.class) {
                    optionComponent = createKeybindOption((KeybindOption) option, y);
                }
                else if(option.getClass() == HashMapOption.class) {
                    optionComponent = createHashMapOption((HashMapOption<?, ?>) option, y);
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
            var checkBox = new CheckBox();
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

            var filePicker = createFilePathButton(path.getWidth() + 16, y, option.get().getFilter(), (text) -> {
                path.setText(text);
            });

            add(filePicker);

            return path;
        }

        private JComponent createFilePathButton(int x, int y, FileNameExtensionFilter filter, OnFilePathChosen event) {
            var filePicker = createButton("...", new Rectangle(x, y, 48, 35), (buttonEvent) -> {
                var picker = new JFileChooser();
                picker.setFileFilter(filter);

                if(picker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    event.selected(picker.getSelectedFile().getAbsolutePath());
                }
            });

            return filePicker;
        }

        public static interface OnFilePathChosen {

            public void selected(String path);
        }

        private JComponent createKeybindOption(KeybindOption option, int y) {
            var keybind = option.getKeybind().getKeyBinding();
            var button = createButton(keybind.getDisplayString(), new Rectangle(0, y, 192, 35), null);
            button.setToolTipText(option.getConfigElement().getDescription());

            button.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent e) { }

                @Override
                public void focusLost(FocusEvent e) {
                    keybind.endKeyRebind();
                }
            });

            button.addActionListener((event) -> {
                option.getKeybind().getKeyBinding().startKeyRebind();
            });

            if(!option.getKeybind().isNative()) {
                var keyListener = keybind.getATWListener();

                keybind.setRebindStartEvent(() -> {
                    button.setText(keybind.getDisplayString());
                    button.addKeyListener(keyListener);
                });

                keybind.setRebindUpdateEvent(() -> {
                    button.setText(keybind.getDisplayString());
                });

                keybind.setRebindEndEvent(() -> {
                    button.setText(keybind.getDisplayString());
                    button.removeKeyListener(keyListener);
                });
            }
            else {
                keybind.setRebindStartEvent(() -> {
                    button.setText(keybind.getDisplayString());
                });

                keybind.setRebindUpdateEvent(() -> {
                    button.setText(keybind.getDisplayString());
                });

                keybind.setRebindEndEvent(() -> {
                    button.setText(keybind.getDisplayString());
                });
            }

            var reset = createButton("Reset", new Rectangle(button.getWidth() + 16, y, 96, 35), (event) -> {
                keybind.endKeyRebind();
                keybind.loadKeys(option.getDefaultValue());
                option.set(option.getDefaultValue());

                button.setText(keybind.getDisplayString());
            });

            add(reset);

            return button;
        }

        private <K, V> JComponent createHashMapOption(HashMapOption<K, V> option, int y) {
            var panel = new JPanel();
            panel.setLayout(null);

            var scroll = new JScrollPane(panel);
            scroll.setBounds(0, y, Window.SETTINGS.getWidth(), Window.SETTINGS.getHeight());

            initHashMapPanel(panel, option);

            return scroll;
        }

        @SuppressWarnings("unchecked")
        private <K, V> void initHashMapPanel(JPanel panel, HashMapOption<K, V> option) {
            final var height = (ELEMENT_HEIGHT - 16);

            panel.removeAll();
            
            var index = 0;
            for(var entry : option.get().entrySet()) {
                addMapElement(panel, option, entry, index * height);
                index++;
            }

            JButton addButton;
            addButton = createButton("Add", new Rectangle(0, index * height, 64, 35), (event) -> {
                option.get().put((K) "", (V) "");
                option.set(option.get());

                initHashMapPanel(panel, option);
                Main.repaintAll();
            });

            panel.add(addButton);
        }

        @SuppressWarnings("unchecked")
        private <K, V> void addMapElement(JPanel panel, HashMapOption<K, V> option, Entry<K, V> entry, int y) {
            final var filter = new FileNameExtensionFilter("Audio files", "wav");

            var keyBox = createTextPane(String.valueOf(entry.getKey()), option.getConfigElement().getDescription(), 192, y, (pane, event) -> {
                var key = pane.getName();

                var value = option.get().get(key);
                option.get().remove(key);

                key = pane.getText();
                pane.setName(key);
                
                option.get().put((K) key, value);
                option.set(option.get());
            });

            keyBox.setName(String.valueOf(entry.getKey()));

            var valueBox = createTextPane(String.valueOf(entry.getValue()), option.getConfigElement().getDescription(), (Window.SETTINGS.getWidth() - 192 - 144), y, (pane, event) -> {
                option.get().put((K) keyBox.getName(), (V) pane.getText());
                option.set(option.get());
            });

            valueBox.setLocation(keyBox.getWidth() + 16, valueBox.getY());

            var filePicker = createFilePathButton(valueBox.getX() + valueBox.getWidth() + 16, valueBox.getY(), filter, (text) -> {
                valueBox.setText(text);
            });

            var remove = createButton("-", new Rectangle(filePicker.getX() + filePicker.getWidth() + 16, filePicker.getY(), 32, 35), (event) -> {
                option.get().remove(keyBox.getName());
                option.set(option.get());

                initHashMapPanel(panel, option);
                Main.repaintAll();
            });

            panel.add(keyBox);
            panel.add(valueBox);
            panel.add(filePicker);
            panel.add(remove);
        }

        private JTextPane createTextPane(ConfigOption<?> option, int y, TextPaneOnChange event) {
            return createTextPane(String.valueOf(option.get()), option.getConfigElement().getDescription(), Window.SETTINGS.getWidth() - 42, y, event);
        }

        private JTextPane createTextPane(String contents, String tooltip, int width, int y, TextPaneOnChange event) {
            var textBox = new TextPane();
            textBox.setBounds(0, y, width, 35);

            textBox.setText(contents);
            textBox.setToolTipText(tooltip);

            textBox.getDocument().addDocumentListener(new SimpleDocumentListener((documentEvent) -> {
                event.onChange(textBox, documentEvent);
            }));

            return textBox;
        }

        private JButton createButton(String name, Rectangle bounds, ActionListener event) {
            var button = new Button(name);
            button.setBounds(bounds);

            if(event != null) {
                button.addActionListener(event);
            }

            return button;
        }

        private static JLabel createLabel(ConfigElement option, int y) {
            var label = new JLabel(option.getShownName());
            label.setBounds(2, y - 18, (int) label.getPreferredSize().getWidth(), (int) label.getPreferredSize().getHeight());
            label.setOpaque(true);
            label.setToolTipText(option.getDescription());

            return label;
        }

        public static interface TextPaneOnChange {

            public void onChange(JTextPane pane, DocumentEvent event);
        }
    }
}
