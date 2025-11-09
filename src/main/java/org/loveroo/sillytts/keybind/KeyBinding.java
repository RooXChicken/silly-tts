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

    private RebindStartEvent rebindStartEvent;
    private RebindUpdateEvent rebindUpdateEvent;
    private RebindEndEvent rebindEndEvent;

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
            if(reboundKeys.contains(keyCode)) {
                return;
            }

            reboundKeys.add(keyCode);
            rebindUpdateEvent.update();

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
        if(state != KeybindState.ACTIVE) {
            return;
        }

        state = KeybindState.REBINDING;
        reboundKeys = new ArrayList<>();

        rebindStartEvent.start();
    }

    public void endKeyRebind() {
        if(state != KeybindState.REBINDING) {
            return;
        }

        state = KeybindState.ACTIVE;

        getOption().set(reboundKeys);
        loadKeys(reboundKeys);

        reboundKeys = null;
        rebindEndEvent.end();
    }

    public void setRebindStartEvent(RebindStartEvent rebindStartEvent) {
        this.rebindStartEvent = rebindStartEvent;
    }

    public void setRebindUpdateEvent(RebindUpdateEvent rebindUpdateEvent) {
        this.rebindUpdateEvent = rebindUpdateEvent;
    }

    public void setRebindEndEvent(RebindEndEvent rebindEndEvent) {
        this.rebindEndEvent = rebindEndEvent;
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
        var keys = (state == KeybindState.ACTIVE) ? getOption().get() : reboundKeys;
        if(keys.isEmpty() && state == KeybindState.ACTIVE) {
            return "Unbound";
        }

        var builder = new StringBuilder();

        for(var i = 0; i < keys.size(); i++) {
            var key = keys.get(i);
            builder.append((keybind.isNative() ? NativeKeyEvent.getKeyText(key) : KeyEvent.getKeyText(key)));

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

    public static interface RebindStartEvent {

        public void start();
    }

    public static interface RebindUpdateEvent {

        public void update();
    }

    public static interface RebindEndEvent {

        public void end();
    }

    public static interface KeybindEvent {

        public void activate();
    }
}
