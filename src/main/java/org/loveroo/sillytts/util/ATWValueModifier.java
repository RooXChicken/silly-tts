package org.loveroo.sillytts.util;

import java.awt.Color;
import java.awt.Font;

public class ATWValueModifier {

    public static void setFontSize(Font font, int size) {
        try {
            final var setSizeField = Font.class.getDeclaredField("size");
            setSizeField.setAccessible(true);
            setSizeField.set(font, size);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Modified the values of the color directly to allow hot swapping of colors where it normally isn't supported. Uses reflection
     * Ensure the following is "add-open": {@code java.desktop/java.awt}
     * @param color The target to modify
     * @param newColor The color to set the target to
     */
    public static void setColor(Color color, Color newColor) {
        try {
            final var setValueField = Color.class.getDeclaredField("value");
            setValueField.setAccessible(true);
            setValueField.set(color, newColor.getRGB());

            final var setFRGBField = Color.class.getDeclaredField("frgbvalue");
            setFRGBField.setAccessible(true);
            setFRGBField.set(color, newColor.getRGBColorComponents(null));

            final var setFValueField = Color.class.getDeclaredField("fvalue");
            setFValueField.setAccessible(true);
            setFValueField.set(color, newColor.getComponents(null));

            final var setFAlphaField = Color.class.getDeclaredField("falpha");
            setFAlphaField.setAccessible(true);
            setFAlphaField.set(color, newColor.getRGBComponents(null)[3]);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
