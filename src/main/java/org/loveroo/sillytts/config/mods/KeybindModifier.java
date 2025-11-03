package org.loveroo.sillytts.config.mods;

import java.lang.reflect.Field;
import java.util.List;

import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.FieldModifier;
import org.loveroo.sillytts.keybind.KeyBinding;

public class KeybindModifier implements FieldModifier {
    
    @SuppressWarnings("unchecked")
    @Override
    public void load(Field field, Config instance, Object value) throws Exception {
        var keybind = (KeyBinding) field.get(instance);
        keybind.loadKeys((List<Integer>) value);
    }

    @Override
    public Object save(Field field, Config instance) throws Exception {
        return ((KeyBinding) field.get(instance)).getKeys();
    }
}
