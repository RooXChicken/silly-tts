package org.loveroo.sillytts.config.custom;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;

public class ListOption<T> extends ConfigOption<List<T>> {

    public ListOption(ConfigElement configElement, List<T> defaultValue) {
        super(configElement, defaultValue);
    }
    
    @Override
    public void load(Object value) {
        set(toList((JSONArray) value));
    }

    @Override
    public Object save() {
        return toArray(get());
    }

    private JSONArray toArray(List<T> list) {
        return new JSONArray(list);
    }

    @SuppressWarnings("unchecked")
    private List<T> toList(JSONArray json) {
        var array = new ArrayList<Object>();

        for(var i = 0; i < json.length(); i++){
            array.add(json.opt(i));
        }

        return (List<T>) array;
    }
}
