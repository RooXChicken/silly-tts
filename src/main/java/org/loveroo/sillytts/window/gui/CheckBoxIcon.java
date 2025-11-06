package org.loveroo.sillytts.window.gui;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.Serializable;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

public class CheckBoxIcon implements Icon, UIResource, Serializable {

    protected int getControlSize() {
        return 20;
    }

    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        JCheckBox checkBox = (JCheckBox)component;
        ButtonModel model = checkBox.getModel();

        var border = UIManager.getBorder("TextPane.border");
        border.paintBorder(component, graphics, x - 4, y - 6, 32, 32);

        if(model.isSelected()) {
            drawCheck(component, graphics, x + 2, y);
        }
    }

    protected void drawCheck(Component component, Graphics graphics, int x, int y) {
        var g2d = (Graphics2D) graphics.create();
        int controlSize = getControlSize();

        g2d.setColor(UIManager.getColor("TextPane.selectionBackground"));
        g2d.setStroke(new BasicStroke(2.8f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawLine(x + 4, y + (controlSize/2 + 2), x + (controlSize/2 - 2), y + (controlSize - 4));
        g2d.drawLine(x + (controlSize/2 - 1), y + (controlSize - 4), x + (controlSize - 6), y + 6);

        g2d.dispose();
    }

    public int getIconWidth() {
        return getControlSize();
    }

    public int getIconHeight() {
        return getControlSize();
    }
 }
