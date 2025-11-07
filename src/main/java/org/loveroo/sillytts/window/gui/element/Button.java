package org.loveroo.sillytts.window.gui.element;

import java.awt.Graphics;

import javax.swing.JButton;

public class Button extends JButton {

    public Button(String name) {
        super(name);
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        BackgroundDrawer.drawBackground(graphics, this);

        super.paintComponent(graphics);
    }
}
