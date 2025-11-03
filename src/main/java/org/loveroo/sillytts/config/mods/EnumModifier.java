package org.loveroo.sillytts.config.mods;

import java.lang.reflect.Field;

import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.FieldModifier;

public class EnumModifier implements FieldModifier {
    
    @Override
    public void load(Field field, Config instance, Object value) throws Exception {
        field.set(instance, field.getType().getEnumConstants()[(Integer) value]);
    }

    @Override
    public Object save(Field field, Config instance) throws Exception {
        return ((Enum<?>)field.get(instance)).ordinal();
    }
}
