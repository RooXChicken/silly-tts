package org.loveroo.sillytts.config.custom;

import java.awt.Color;
import java.awt.Frame;

import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;
import org.loveroo.sillytts.util.ATWValueModifier;

public class ColorOption extends ConfigOption<Color> {

    public ColorOption(ConfigElement configElement, Color defaultValue) {
        super(configElement, new Color(defaultValue.getRGB()));
        forceSet(defaultValue);
    }
    
    @Override
    public void load(Object value) {
        ATWValueModifier.setColor(get(), new Color((Integer) value));
        set(get());
    }

    @Override
    public void set(Object value) {
        ATWValueModifier.setColor(get(), (Color) value);
        repaintAll();
        
        super.set(get());
    }

    private static void repaintAll() {
        System.out.println("Forcing repaint");
        for(var frame : Frame.getFrames()) {
            frame.repaint();
        }
    }

    @Override
    public Object save() {
        return get().getRGB();
    }
}
