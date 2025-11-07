package org.loveroo.sillytts.config.custom;

import java.util.List;

import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.keybind.Keybind;

public class KeybindOption extends ListOption<Integer> {

    private Keybind keybind;

    public KeybindOption(ConfigElement configElement, List<Integer> defaultValue) {
        super(configElement, defaultValue);
    }

    public Keybind getKeybind() {
        return keybind;
    }

    public void setKeybind(Keybind keybind) {
        this.keybind = keybind;
    }
}