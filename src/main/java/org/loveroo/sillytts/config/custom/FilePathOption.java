package org.loveroo.sillytts.config.custom;

import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;
import org.loveroo.sillytts.config.Config.FilePath;

public class FilePathOption extends ConfigOption<FilePath> {

    public FilePathOption(ConfigElement configElement, FilePath defaultValue) {
        super(configElement, defaultValue);
    }

    @Override
    public void load(Object value) {
        set(new FilePath((String) value, get().getFilter()));
    }

    @Override
    public Object save() {
        return get().getPath();
    }
}