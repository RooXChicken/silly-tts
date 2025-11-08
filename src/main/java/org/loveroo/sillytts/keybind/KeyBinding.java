package org.loveroo.sillytts.keybind;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.loveroo.sillytts.config.custom.KeybindOption;

public class KeyBinding {

    private final HashMap<Integer, KeyState> keys = new HashMap<>();
    private List<Integer> reboundKeys = null;
    
    private final KeybindOption option;
    private final KeybindEvent event;

    private Keybind keybind;
    private KeybindState state = KeybindState.ACTIVE;

    public KeyBinding(KeybindOption option, KeybindEvent event) {
        this.option = option;
        this.event = event;

        loadKeys(option.get());
        option.registerChangeAction(this::loadKeys);
    }

    public void setKeybind(Keybind keybind) {
        this.keybind = keybind;
    }
    
    protected void onKeyPress(int keyCode) {
        if(state == KeybindState.REBINDING) {
            reboundKeys.add(keyCode);
            return;
        }

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
        if(state == KeybindState.REBINDING) {
            endKeyRebind();
            return;
        }

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

    public void startKeyRebind() {
        state = KeybindState.REBINDING;
        reboundKeys = new ArrayList<>();
    }

    protected void endKeyRebind() {
        state = KeybindState.ACTIVE;
        loadKeys(reboundKeys);

        reboundKeys = null;
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

    public String getDisplayString() {
        var builder = new StringBuilder();

        var keys = (state == KeybindState.ACTIVE) ? getOption().get() : reboundKeys;

        for(var i = 0; i < keys.size(); i++) {
            var key = keys.get(i);
            builder.append((keybind.isAWT() ? NativeKeyEvent.getKeyText(key) : KeyEvent.getKeyText(key)));

            if(i < keys.size()-1) {
                builder.append(" + ");
            }
        }

        if(state == KeybindState.REBINDING) {
            builder.append("...");
        }

        return builder.toString();
    }

    public KeybindOption getOption() {
        return option;
    }

    public static enum KeyState {

        PRESSED,
        RELEASED,
    }

    public static enum KeybindState {

        ACTIVE,
        REBINDING,
    }

    public static interface KeybindEvent {

        public void activate();
    }
}
