package org.loveroo.sillytts.keybind;

import java.awt.event.KeyListener;

import org.jnativehook.keyboard.NativeKeyListener;
import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.custom.KeybindOption;
import org.loveroo.sillytts.window.SettingsWindow;
import org.loveroo.sillytts.window.TTSInputWindow;

public enum Keybind {
    
    SEND_TTS_KEYBIND(false, new KeyBinding(Config.SEND_TTS_KEYBIND, () -> { Main.getTTSWindow().sendTTS(); })),
    MINIMIZE_TTS_KEYBIND(false, new KeyBinding(Config.MINIMIZE_TTS_KEYBIND, () -> { Main.setEnabled(false); })),
    CLOSE_TTS_KEYBIND(false, new KeyBinding(Config.CLOSE_TTS_KEYBIND, () -> { System.exit(0); })),
    OPEN_TTS_WINDOW_KEYBIND(true, new KeyBinding(Config.OPEN_TTS_WINDOW_KEYBIND, () -> {
            var lock = new TTSInputWindow.UpdateTextLock();
            lock.start();

            Main.setEnabled(true);
        })),

    OPEN_SETTINGS(false, new KeyBinding(Config.OPEN_SETTINGS, () -> { new SettingsWindow(); }));

    private final KeyBinding keyBinding;
    private final boolean isAWT;

    Keybind(boolean isAWT, KeyBinding keyBinding) {
        this.isAWT = isAWT;

        this.keyBinding = keyBinding;
        this.keyBinding.setKeybind(this);
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

    public boolean isAWT() {
        return isAWT;
    }

    static {
        for(var keybind : values()) {
            var option = Config.ConfigElement.valueOf(keybind.name());
            ((KeybindOption) Config.getConfig(option)).setKeybind(keybind);
        }
    }
}
