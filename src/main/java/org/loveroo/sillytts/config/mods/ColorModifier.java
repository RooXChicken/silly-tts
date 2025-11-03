package org.loveroo.sillytts.config.mods;

import java.awt.Color;
import java.lang.reflect.Field;

import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.FieldModifier;

public class ColorModifier implements FieldModifier {
    
    @Override
    public void load(Field field, Config instance, Object value) throws Exception {
        field.set(instance, new Color((Integer) value));
    }

    @Override
    public Object save(Field field, Config instance) throws Exception {
        return ((Color) field.get(instance)).getRGB();
    }
}
