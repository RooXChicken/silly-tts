package org.loveroo.sillytts.window.gui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;

import org.loveroo.sillytts.config.Config;

public class BorderlessComboBox extends BasicComboBoxUI {

    @Override
    protected void installDefaults() {
        super.installDefaults();

        comboBox.setRenderer(new CellRenderer(comboBox));
    }

    // @Override
    // protected JButton createArrowButton() {
    //     var button = super.createArrowButton();
        

    //     button.setSize(48, 48);

    //     return button;
    // }

    static class CellRenderer extends DefaultListCellRenderer {

        private final JComboBox<?> box;

        public CellRenderer(JComboBox<?> box) {
            super();

            this.box = box;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if(!box.isPopupVisible()) {
                list.setSelectionBackground(Config.BACKGROUND_COLOR.get());
                list.setSelectionForeground(Config.TEXT_COLOR.get());
            }
            else {
                list.setSelectionBackground(Config.OUTLINE_COLOR.get());
                list.setSelectionForeground(Config.BACKGROUND_COLOR.get());
            }

            if(list.getBorder() == null) {
                list.setBorder(UIManager.getBorder("TextPane.border"));
            }

            // TODO: remove scroll bar (when not needed)
            // list.setSize((int) list.getPreferredSize().getWidth() + 30, (int) list.getPreferredSize().getHeight() + 30);

            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}