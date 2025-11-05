package org.loveroo.sillytts.keybind;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.List;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.loveroo.sillytts.config.Config.ConfigOption;

public class KeyBinding {

    private final HashMap<Integer, KeyState> keys = new HashMap<>();
    private final KeybindEvent event;

    public KeyBinding(KeybindEvent event, ConfigOption<List<Integer>> keys) {
        this.event = event;

        loadKeys(keys.get());
        keys.registerChangeAction(this::loadKeys);
    }
    
    protected void onKeyPress(int keyCode) {
        if(!keys.containsKey(keyCode)) {
            return;
        }

        keys.put(keyCode, KeyState.PRESSED);

        if(allKeysPressed()) {
            event.activate();
            clearKeys();
        }
    }

    protected void onKeyRelease(int keyCode) {
        if(!keys.containsKey(keyCode)) {
            return;
        }

        keys.put(keyCode, KeyState.RELEASED);
    }

    private boolean allKeysPressed() {
        return keys.values().stream().allMatch((state) -> state == KeyState.PRESSED);
    }

    private void clearKeys() {
        loadKeys(getKeys());
    }

    public void loadKeys(List<Integer> newKeys) {
        keys.clear();

        for(var key : newKeys) {
            keys.put(key, KeyState.RELEASED);
        }
    }

    public List<Integer> getKeys() {
        return keys.keySet().stream().toList();
    }

    public NativeKeyListener getNativeListener() {
        return new NativeKeyListener() {

            @Override
            public void nativeKeyTyped(NativeKeyEvent event) { }

            @Override
            public void nativeKeyPressed(NativeKeyEvent event) {
                onKeyPress(event.getKeyCode());
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent event) {
                onKeyRelease(event.getKeyCode());
            }
        };
    }

    public KeyListener getATWListener() {
        return new KeyListener() {

            @Override
            public void keyTyped(KeyEvent event) { }

            @Override
            public void keyPressed(KeyEvent event) {
                onKeyPress(event.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent event) {
                onKeyRelease(event.getKeyCode());
            }
        };
    }

    public static enum KeyState {

        PRESSED,
        RELEASED,
    }

    public static interface KeybindEvent {

        public void activate();
    }
}
