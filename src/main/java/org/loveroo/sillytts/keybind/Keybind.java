package org.loveroo.sillytts.keybind;

import java.awt.event.KeyListener;

import org.jnativehook.keyboard.NativeKeyListener;
import org.loveroo.sillytts.Main;

public enum Keybind {
    
    SEND_TTS_KEYBIND(new KeyBinding(() -> { Main.getTTSWindow().sendTTS(); }, Main.getConfig().SEND_TTS_KEYBIND)),
    MINIMIZE_TTS_KEYBIND(new KeyBinding(() -> { Main.setEnabled(false); }, Main.getConfig().MINIMIZE_TTS_KEYBIND)),
    CLOSE_TTS_KEYBIND(new KeyBinding(() -> { System.exit(0); }, Main.getConfig().CLOSE_TTS_KEYBIND)),
    OPEN_TTS_WINDOW_KEYBIND(new KeyBinding(() -> { Main.setEnabled(true); }, Main.getConfig().OPEN_TTS_WINDOW_KEYBIND));

    private final KeyBinding keyBinding;

    Keybind(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public NativeKeyListener getNativeListener() {
        return getKeyBinding().getNativeListener();
    }

    public KeyListener getATWListener() {
        return getKeyBinding().getATWListener();
    }
}
