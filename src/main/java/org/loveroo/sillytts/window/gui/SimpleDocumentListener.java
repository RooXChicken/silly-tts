package org.loveroo.sillytts.window.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SimpleDocumentListener implements DocumentListener {

        private final Event documentEvent;

        public SimpleDocumentListener(Event documentEvent) {
            this.documentEvent = documentEvent;
        }

        @Override
        public void insertUpdate(DocumentEvent event) {
            documentEvent.trigger(event);
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            documentEvent.trigger(event);
        }

        @Override
        public void changedUpdate(DocumentEvent event) {
            documentEvent.trigger(event);
        }

        public static interface Event {

            public void trigger(DocumentEvent event);
        }
    }