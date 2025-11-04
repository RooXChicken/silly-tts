package org.loveroo.sillytts.window.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.Border;

import org.loveroo.sillytts.config.Config.ConfigOption;

public class RoundedBorder implements Border {

    private int radius;
    private ConfigOption<Color> color;

    public RoundedBorder(int radius, ConfigOption<Color> color) {
        this.radius = radius;
        this.color = color;
    }

    public int getRadius() {
        return radius;
    }

    public Color getColor() {
        return color.get();
    }

    @Override
    public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
        var g2d = (Graphics2D) graphics.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getColor());
        g2d.setStroke(new BasicStroke(2.8f));

        final int padding = 1;
        g2d.draw(new RoundRectangle2D.Double(x + padding, y + padding, width - 1 - padding*2, height - 1 - padding*2, getRadius(), getRadius()));
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component component) {
        int value = getRadius() / 2;
        return new Insets(value, value, value, value);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
