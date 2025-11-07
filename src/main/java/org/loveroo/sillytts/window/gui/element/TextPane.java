package org.loveroo.sillytts.window.gui.element;

import java.awt.Graphics;

import javax.swing.JTextPane;

public class TextPane extends JTextPane {

    public TextPane() {
        super();
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        BackgroundDrawer.drawBackground(graphics, this);

        super.paintComponent(graphics);
    }
}
