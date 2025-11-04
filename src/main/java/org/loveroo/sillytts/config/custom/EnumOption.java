package org.loveroo.sillytts.config.custom;

import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;

public class EnumOption<T extends Enum<T>> extends ConfigOption<T> {

    public EnumOption(ConfigElement configElement, T defaultValue) {
        super(configElement, defaultValue);
    }

    @Override
    public void load(Object value) {
        set(getDefaultValue().getDeclaringClass().getEnumConstants()[(Integer) value]);
    }

    @Override
    public Object save() {
        return get().ordinal();
    }
}