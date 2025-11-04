package org.loveroo.sillytts.config.custom;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.loveroo.sillytts.config.Config.ConfigElement;
import org.loveroo.sillytts.config.Config.ConfigOption;

public class HashMapOption<K, V> extends ConfigOption<HashMap<K, V>> {

    public HashMapOption(ConfigElement configElement, HashMap<K, V> defaultValue) {
        super(configElement, defaultValue);
    }
    
    @Override
    public void load(Object value) {
        set(fromJson((JSONObject) value));
    }
    
    @Override
    public Object save() {
        return toJson((HashMap<K, V>) get());
    }

    private JSONObject toJson(HashMap<K, V> map) {
        var json = new JSONObject();
        
        for(var entry : map.entrySet()) {
            try {
                json.put(String.valueOf(entry.getKey()), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return json;
    }

    @SuppressWarnings("unchecked")
    private HashMap<K, V> fromJson(JSONObject json) {
        var map = new HashMap<Object, Object>();

        var iter = json.keys();
        while(iter.hasNext()) {
            var key = (String) iter.next();
            map.put(key, json.opt(key));
        }

        return (HashMap<K, V>) map;
    }
}