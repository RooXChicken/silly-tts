package org.loveroo.sillytts.window.gui.element;

import java.awt.Graphics;

import javax.swing.JCheckBox;

public class CheckBox extends JCheckBox {

    public CheckBox() {
        super();
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        BackgroundDrawer.drawBackground(graphics, this);

        super.paintComponent(graphics);
    }
}
