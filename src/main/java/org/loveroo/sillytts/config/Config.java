package org.loveroo.sillytts.config;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.json.JSONObject;
import org.loveroo.sillytts.config.custom.ColorOption;
import org.loveroo.sillytts.config.custom.HashMapOption;
import org.loveroo.sillytts.config.custom.ListOption;

public class Config {

    private static final String CONFIG_PATH = "config.json";
    private static final HashMap<ConfigElement, ConfigOption<?>> CONFIGS = new HashMap<>();

    public static final ConfigOption<String> WINDOW_NAME = new ConfigOption<>(ConfigElement.WINDOW_NAME, "tts :3");

    public static final ConfigOption<Integer> FONT_SIZE = new ConfigOption<>(ConfigElement.FONT_SIZE, 16);

    public static final ConfigOption<Boolean> MINIMIZE_ON_X = new ConfigOption<>(ConfigElement.MINIMIZE_ON_X, true);
    public static final ConfigOption<Integer> TTS_WINDOW_OPEN_DELAY = new ConfigOption<>(ConfigElement.TTS_WINDOW_OPEN_DELAY, 10);

    public static final ConfigOption<HashMap<String, String>>
        WORD_REPLACEMENTS = new HashMapOption<>(ConfigElement.WORD_REPLACEMENTS, new HashMap<>()),
        CONST_REPLACEMENTS = new HashMapOption<>(ConfigElement.CONST_REPLACEMENTS, new HashMap<>());

    public static final ConfigOption<String>
        PIPER_COMMAND = new ConfigOption<>(ConfigElement.PIPER_COMMAND, "python3 -m piper"),
        VOICE_MODEL = new ConfigOption<>(ConfigElement.VOICE_MODEL, "");

    public static final ConfigOption<List<Integer>>
        SEND_TTS_KEYBIND = new ListOption<>(ConfigElement.SEND_TTS_KEYBIND, List.of(KeyEvent.VK_SHIFT, KeyEvent.VK_ENTER)),
        MINIMIZE_TTS_KEYBIND = new ListOption<>(ConfigElement.MINIMIZE_TTS_KEYBIND, List.of(KeyEvent.VK_ESCAPE)),
        CLOSE_TTS_KEYBIND = new ListOption<>(ConfigElement.CLOSE_TTS_KEYBIND, List.of(KeyEvent.VK_ALT, KeyEvent.VK_F4)),
        OPEN_TTS_WINDOW_KEYBIND = new ListOption<>(ConfigElement.OPEN_TTS_WINDOW_KEYBIND, List.of(125, NativeKeyEvent.VC_F9)),
        OPEN_SETTINGS = new ListOption<>(ConfigElement.OPEN_SETTINGS, List.of(KeyEvent.VK_CONTROL, KeyEvent.VK_COMMA));

    public static final ConfigOption<Color>
        BACKGROUND_COLOR = new ColorOption(ConfigElement.BACKGROUND_COLOR, new Color(20, 20, 20)),
        CARET_COLOR = new ColorOption(ConfigElement.CARET_COLOR, new Color(1.0f, 0.46f, 0.82f)),
        SELECTION_COLOR = new ColorOption(ConfigElement.SELECTION_COLOR, new Color(1.0f, 0.46f, 0.82f)),
        OUTLINE_COLOR = new ColorOption(ConfigElement.OUTLINE_COLOR, new Color(1.0f, 0.46f, 0.82f)),
        TEXT_COLOR = new ColorOption(ConfigElement.TEXT_COLOR, new Color(0.75f, 0.75f, 0.75f));


    public static final ConfigOption<String> OUTPUT_DEVICE = new ConfigOption<>(ConfigElement.OUTPUT_DEVICE, "VIRTUAL_SRC");
    public static final ConfigOption<Integer> AUDIO_SAMPLE_RATE = new ConfigOption<>(ConfigElement.AUDIO_SAMPLE_RATE, 22050);
    public static final ConfigOption<Integer> AUDIO_CHANNELS = new ConfigOption<>(ConfigElement.AUDIO_CHANNELS, 1);

    static {
        WORD_REPLACEMENTS.getDefaultValue().put("tts", "text to speech");
        WORD_REPLACEMENTS.getDefaultValue().put("psychis", "psy kiss");
        WORD_REPLACEMENTS.getDefaultValue().put("nvm", "nevermind");
        WORD_REPLACEMENTS.getDefaultValue().put("wdym", "what do you mean");
        WORD_REPLACEMENTS.getDefaultValue().put("rooxchicken", "roo ecks chicken");
        WORD_REPLACEMENTS.getDefaultValue().put("omg", "oh my god");
        WORD_REPLACEMENTS.getDefaultValue().put("omgg", "oh my golly gosh");
        WORD_REPLACEMENTS.getDefaultValue().put("omggg", "oh my good golly gosh");
        WORD_REPLACEMENTS.getDefaultValue().put("smp", "ess em pee");
        WORD_REPLACEMENTS.getDefaultValue().put("mc", "minecraft");
        WORD_REPLACEMENTS.getDefaultValue().put(":c", "sad");
        WORD_REPLACEMENTS.getDefaultValue().put(":\\(", "sad");
        WORD_REPLACEMENTS.getDefaultValue().put(":\\)", "happy");
        WORD_REPLACEMENTS.getDefaultValue().put(":\\>", "happy");
        WORD_REPLACEMENTS.getDefaultValue().put(":D", "happy");
        WORD_REPLACEMENTS.getDefaultValue().put(":P", "silly");
        WORD_REPLACEMENTS.getDefaultValue().put(":3", "silly");
        WORD_REPLACEMENTS.getDefaultValue().put("tts", "text to speech");
        WORD_REPLACEMENTS.getDefaultValue().put("btw", "by the way");
        WORD_REPLACEMENTS.getDefaultValue().put("ikr", "i know right");
        WORD_REPLACEMENTS.getDefaultValue().put("smth", "something");
        WORD_REPLACEMENTS.getDefaultValue().put("kms", "kay emm ess");
        WORD_REPLACEMENTS.getDefaultValue().put("fr", "for real");
        WORD_REPLACEMENTS.getDefaultValue().put("rn", "right now");
        WORD_REPLACEMENTS.getDefaultValue().put("u", "you");
        WORD_REPLACEMENTS.getDefaultValue().put("ur", "your");

        CONST_REPLACEMENTS.getDefaultValue().put("\\_", " underscore ");
        CONST_REPLACEMENTS.getDefaultValue().put("\\-", " dash ");
        CONST_REPLACEMENTS.getDefaultValue().put("\\/", " slash ");
    }

    public static void load() {
        try {
            var configFile = new File(CONFIG_PATH);
            
            var data = "{}";
            if(!configFile.exists()) {
                save();
            }

            if(configFile.exists()) {
                data = Files.readString(Paths.get(CONFIG_PATH));
            }
    
            var json = new JSONObject(data);
    
            for(var option : ConfigElement.values()) {
                var config = CONFIGS.get(option);
                var value = json.opt(option.name().toLowerCase());

                if(value == null) {
                    continue;
                }

                config.load(value);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            var json = new JSONObject();
    
            for(var option : ConfigElement.values()) {
                var config = CONFIGS.get(option);
                var value = config.save();

                json.put(option.name().toLowerCase(), value);
            }
    
            var config = new File(CONFIG_PATH);
            var writer = new FileWriter(config);
    
            writer.write(json.toString());
            writer.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public static class ConfigOption<T> {
        
        private final ConfigElement configElement;
        private final T defaultValue;
        private T value;

        private HashSet<OnChange<T>> changeActions = new HashSet<>();

        public ConfigOption(ConfigElement configElement, T defaultValue) {
            this.configElement = configElement;
            this.defaultValue = defaultValue;

            this.value = defaultValue;
            register();
        }

        protected void register() {
            CONFIGS.put(configElement, this);
        }

        public ConfigElement getConfigElement() {
            return configElement;
        }

        public T getDefaultValue() {
            return defaultValue;
        }

        public T get() {
            return value;
        }

        public void registerChangeAction(OnChange<T> changeAction) {
            changeActions.add(changeAction);
        }

        @SuppressWarnings("unchecked")
        public void set(Object value) {
            this.value = (T) value;

            for(var action : changeActions) {
                action.onChange(get());
            }

            Config.save();
        }

        public void load(Object value) {
            set(value);
        }

        public Object save() {
            return get();
        }

        public static interface OnChange<T> {

            public void onChange(T value);
        }
    }

    public static enum ConfigElement {

        WINDOW_NAME("Window Name"),
        FONT_SIZE("Font Size"),
        MINIMIZE_ON_X("Minimize on Close"),
        TTS_WINDOW_OPEN_DELAY("Window Open Lock Delay"),
        WORD_REPLACEMENTS("Word Replacements"),
        CONST_REPLACEMENTS("Constant Replacements"),
        PIPER_COMMAND("Piper Command"),
        VOICE_MODEL("Voice Model Path"),
        SEND_TTS_KEYBIND("Send TTS Keybind"),
        MINIMIZE_TTS_KEYBIND("Minimize Keybind"),
        CLOSE_TTS_KEYBIND("Quit Keybind"),
        OPEN_TTS_WINDOW_KEYBIND("Open TTS Keybind"),
        OPEN_SETTINGS("Open Settings Keybind"),
        BACKGROUND_COLOR("Background Color"),
        CARET_COLOR("Caret Color"),
        SELECTION_COLOR("Selection Color"),
        OUTLINE_COLOR("Outline Color"),
        TEXT_COLOR("Text Color"),
        OUTPUT_DEVICE("Output Device"),
        AUDIO_SAMPLE_RATE("Sample Rate"),
        AUDIO_CHANNELS("Channel Count");

        private final String shownName;

        ConfigElement(String shownName) {
            this.shownName = shownName;
        }

        public String getShownName() {
            return shownName;
        }
    }
}