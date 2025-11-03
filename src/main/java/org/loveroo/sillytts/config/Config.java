package org.loveroo.sillytts.config;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.json.JSONObject;
import org.loveroo.sillytts.config.mods.ColorModifier;
import org.loveroo.sillytts.config.mods.DefaultModifier;
import org.loveroo.sillytts.config.mods.EnumModifier;
import org.loveroo.sillytts.config.mods.HashMapModifier;
import org.loveroo.sillytts.config.mods.KeybindModifier;
import org.loveroo.sillytts.config.mods.ListModifier;
import org.loveroo.sillytts.keybind.KeyBinding;

public class Config {

    private static final String CONFIG_PATH = "config.json";

    private static final HashMap<Class<?>, FieldModifier> FIELD_MODS = new HashMap<>();
    private static final FieldModifier DEFAULT_MOD = new DefaultModifier();

    static {
        FIELD_MODS.put(Enum.class, new EnumModifier());
        FIELD_MODS.put(Color.class, new ColorModifier());
        FIELD_MODS.put(KeyBinding.class, new KeybindModifier());
        FIELD_MODS.put(List.class, new ListModifier());
        FIELD_MODS.put(HashMap.class, new HashMapModifier());
    }

    public String WINDOW_NAME = "tts :3";

    public int FONT_SIZE = 16;

    public HashMap<String, String> WORD_REPLACEMENTS = new HashMap<>();
    public HashMap<String, String> CONST_REPLACEMENTS = new HashMap<>();

    public String PIPER_PATH = "/home/roo/bin/piper/piper";
    public String VOICE_MODEL = "/home/roo/tts/en_US-libritts_r-medium.onnx";

    public List<Integer> SEND_TTS_KEYBIND = List.of(KeyEvent.VK_SHIFT, KeyEvent.VK_ENTER);
    public List<Integer> MINIMIZE_TTS_KEYBIND = List.of(KeyEvent.VK_ESCAPE);
    public List<Integer> CLOSE_TTS_KEYBIND = List.of(KeyEvent.VK_ALT, KeyEvent.VK_F4);
    public List<Integer> OPEN_TTS_WINDOW_KEYBIND = List.of(125, NativeKeyEvent.VC_F9);

    public Color CARET_COLOR = new Color(1.0f, 0.46f, 0.82f);
    public Color SELECTION_COLOR = new Color(1.0f, 0.46f, 0.82f);
    public Color OUTLINE_COLOR = new Color(1.0f, 0.46f, 0.82f);

    public Color TEXT_COLOR = new Color(0.75f, 0.75f, 0.75f);

    public int AUDIO_BUFFER_SIZE = 4096;

    public Config() {
        WORD_REPLACEMENTS.put("tts", "text to speech");
        WORD_REPLACEMENTS.put("psychis", "psy kiss");
        WORD_REPLACEMENTS.put("nvm", "nevermind");
        WORD_REPLACEMENTS.put("wdym", "what do you mean");
        WORD_REPLACEMENTS.put("rooxchicken", "roo ecks chicken");
        WORD_REPLACEMENTS.put("omg", "oh my god");
        WORD_REPLACEMENTS.put("omgg", "oh my golly gosh");
        WORD_REPLACEMENTS.put("omggg", "oh my good golly gosh");
        WORD_REPLACEMENTS.put("smp", "ess em pee");
        WORD_REPLACEMENTS.put("mc", "minecraft");
        WORD_REPLACEMENTS.put(":c", "sad");
        WORD_REPLACEMENTS.put(":\\(", "sad");
        WORD_REPLACEMENTS.put(":\\)", "happy");
        WORD_REPLACEMENTS.put(":\\>", "happy");
        WORD_REPLACEMENTS.put(":D", "happy");
        WORD_REPLACEMENTS.put(":P", "silly");
        WORD_REPLACEMENTS.put(":3", "silly");
        WORD_REPLACEMENTS.put("tts", "text to speech");
        WORD_REPLACEMENTS.put("btw", "by the way");
        WORD_REPLACEMENTS.put("ikr", "i know right");
        WORD_REPLACEMENTS.put("smth", "something");
        WORD_REPLACEMENTS.put("kms", "kay emm ess");
        WORD_REPLACEMENTS.put("fr", "for real");
        WORD_REPLACEMENTS.put("rn", "right now");

        CONST_REPLACEMENTS.put("\\_", " underscore ");
        CONST_REPLACEMENTS.put("\\-", " dash ");
        CONST_REPLACEMENTS.put("\\/", " slash ");
    }

    public void load() {
        try {
            var config = new File(CONFIG_PATH);
            
            var data = "{}";
            if(!config.exists()) {
                save();
            }

            if(config.exists()) {
                data = Files.readString(Paths.get(CONFIG_PATH));
            }
    
            var json = new JSONObject(data);
    
            var clazz = this.getClass();
            for(var field : clazz.getDeclaredFields()) {
                if(Modifier.isStatic(field.getModifiers()) || !field.canAccess(this)) {
                    continue;
                }
    
                var value = json.opt(field.getName().toLowerCase());
                if(value == null) {
                    value = getValueFromField(field);
                }
    
                loadValueFromField(field, value);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            var json = new JSONObject();
    
            var clazz = this.getClass();
            for(var field : clazz.getDeclaredFields()) {
                if(Modifier.isStatic(field.getModifiers()) || !field.canAccess(this)) {
                    continue;
                }
    
                var value = getValueFromField(field);
                json.put(field.getName().toLowerCase(), value);
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

    private void loadValueFromField(Field field, Object value) throws Exception {
        FIELD_MODS.getOrDefault(field.getType(), DEFAULT_MOD).load(field, this, value);
    }

    private Object getValueFromField(Field field) throws Exception {
        return FIELD_MODS.getOrDefault(field.getType(), DEFAULT_MOD).save(field, this);
    }

    public static interface FieldModifier {

        void load(Field field, Config instance, Object value) throws Exception;
        Object save(Field field, Config instance) throws Exception;
    }
}