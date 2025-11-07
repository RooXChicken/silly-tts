package org.loveroo.sillytts.window.gui.element;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

import org.loveroo.sillytts.config.Config;

public class BackgroundDrawer {
    
    public static void drawBackground(Graphics graphics, JComponent component) {
        var g2d = (Graphics2D) graphics.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Config.COMPONENT_BACKGROUND_COLOR.get());
        g2d.fillRoundRect(3, 3, component.getWidth() - 6, component.getHeight() - 6, 15, 15);
        
        g2d.dispose();
    }
}
