package org.loveroo.sillytts.window.gui.element;

import java.awt.Graphics;

import javax.swing.JComboBox;

public class ComboBox<T> extends JComboBox<T> {

    public ComboBox(T[] type) {
        super(type);
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        BackgroundDrawer.drawBackground(graphics, this);

        super.paintComponent(graphics);
    }
}
