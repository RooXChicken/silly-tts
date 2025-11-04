package org.loveroo.sillytts.keybind;

import java.awt.event.KeyListener;

import org.jnativehook.keyboard.NativeKeyListener;
import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.config.Config;

public enum Keybind {
    
    SEND_TTS_KEYBIND(new KeyBinding(() -> { Main.getTTSWindow().sendTTS(); }, Config.SEND_TTS_KEYBIND)),
    MINIMIZE_TTS_KEYBIND(new KeyBinding(() -> { Main.setEnabled(false); }, Config.MINIMIZE_TTS_KEYBIND)),
    CLOSE_TTS_KEYBIND(new KeyBinding(() -> { System.exit(0); }, Config.CLOSE_TTS_KEYBIND)),
    OPEN_TTS_WINDOW_KEYBIND(new KeyBinding(() -> { Main.setEnabled(true); }, Config.OPEN_TTS_WINDOW_KEYBIND));

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
