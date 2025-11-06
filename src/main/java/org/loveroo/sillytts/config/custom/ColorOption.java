package org.loveroo.sillytts.config.custom;

import java.awt.Color;

import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;
import org.loveroo.sillytts.util.ColorModifier;

public class ColorOption extends ConfigOption<Color> {

    public ColorOption(ConfigElement configElement, Color defaultValue) {
        super(configElement, defaultValue);
    }
    
    @Override
    public void load(Object value) {
        ColorModifier.setColor(get(), new Color((Integer) value));
        set(get());
    }

    @Override
    public void set(Object value) {
        ColorModifier.setColor(get(), (Color) value);
        super.set(get());
    }

    @Override
    public Object save() {
        return get().getRGB();
    }
}
