package org.loveroo.sillytts.keybind;

import java.awt.event.KeyListener;

import org.jnativehook.keyboard.NativeKeyListener;
import org.loveroo.sillytts.Main;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.custom.KeybindOption;
import org.loveroo.sillytts.util.AudioSystem;
import org.loveroo.sillytts.window.SettingsWindow;
import org.loveroo.sillytts.window.TTSInputWindow;

public enum Keybind {
    
    SEND_TTS(false, new KeyBinding(Config.SEND_TTS_KEYBIND, () -> { Main.getTTSWindow().sendTTS(); })),
    MINIMIZE_TTS(false, new KeyBinding(Config.MINIMIZE_TTS_KEYBIND, () -> { Main.setEnabled(false); })),
    CLOSE_TTS(false, new KeyBinding(Config.CLOSE_TTS_KEYBIND, () -> { System.exit(0); })),
    OPEN_TTS_WINDOW(true, new KeyBinding(Config.OPEN_TTS_WINDOW_KEYBIND, () -> {
            var lock = new TTSInputWindow.UpdateTextLock();
            lock.start();

            Main.setEnabled(true);
        })),

    STOP_TTS(true, new KeyBinding(Config.STOP_TTS_KEYBIND, () -> { AudioSystem.stopAll(); })),
    OPEN_SETTINGS(false, new KeyBinding(Config.OPEN_SETTINGS_KEYBIND, () -> { new SettingsWindow(); }));

    private final KeyBinding keyBinding;
    private final boolean isNative;

    Keybind(boolean isNative, KeyBinding keyBinding) {
        this.isNative = isNative;

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

    public boolean isNative() {
        return isNative;
    }

    static {
        for(var keybind : values()) {
            var option = Config.ConfigElement.valueOf(keybind.name() + "_KEYBIND");
            ((KeybindOption) Config.getConfig(option)).setKeybind(keybind);
        }
    }
}
