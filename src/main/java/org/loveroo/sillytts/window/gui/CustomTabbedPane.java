package org.loveroo.sillytts.window.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class CustomTabbedPane extends BasicTabbedPaneUI {
    
    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
        if(isSelected) {
            tabPane.setForegroundAt(tabIndex, UIManager.getColor("TabbedPane.contentAreaColor"));
        }
        else {
            tabPane.setForegroundAt(tabIndex, UIManager.getColor("Label.foreground"));
        }

        super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
    }
}
