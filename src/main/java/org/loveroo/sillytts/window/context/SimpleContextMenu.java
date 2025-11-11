package org.loveroo.sillytts.window.context;

import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.window.SettingsWindow;
import org.loveroo.sillytts.window.gui.element.BackgroundDrawer;

public class SimpleContextMenu extends JPopupMenu {
    
    public SimpleContextMenu(ContextMenu menu) {
        super();
        
        var items = menu.getContextItems();

        for(var item : items) {
            var context = new JMenuItem(item.name());
            context.addActionListener(item.event());

            add(context);
        }

        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics graphics) {
        BackgroundDrawer.drawBackground(graphics, this);

        super.paintComponent(graphics);
    }

    public static record ContextItem(String name, ActionListener event) { }

    public static enum ContextMenu {

        MAIN_TTS(List.of(
            new ContextItem("Send TTS", (event) -> {
                Main.getTTSWindow().sendTTS();
            }),
            new ContextItem("Settings", (event) -> {
                new SettingsWindow();
            }),
            new ContextItem("Quit", (event) -> {
                System.exit(0);
            })
        ));

        private final List<ContextItem> contextItems;

        ContextMenu(List<ContextItem> contextItems) {
            this.contextItems = contextItems;
        }

        public List<ContextItem> getContextItems() {
            return contextItems;
        }
    }
}
