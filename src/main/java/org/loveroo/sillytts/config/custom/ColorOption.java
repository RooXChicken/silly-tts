package org.loveroo.sillytts.config.custom;

import java.awt.Color;

import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;

public class ColorOption extends ConfigOption<Color> {

    public ColorOption(ConfigElement configElement, Color defaultValue) {
        super(configElement, defaultValue);
    }
    
    @Override
    public void load(Object value) {
        set(new Color((Integer) value));
    }

    @Override
    public Object save() {
        return get().getRGB();
    }
}
