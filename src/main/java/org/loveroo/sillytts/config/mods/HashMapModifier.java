package org.loveroo.sillytts.config.mods;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.json.JSONObject;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.FieldModifier;

public class HashMapModifier implements FieldModifier {
    
    @Override
    public void load(Field field, Config instance, Object value) throws Exception {
        var jsonObject = (JSONObject) value;
        var map = new HashMap<Object, Object>();

        var iter = jsonObject.keys();
        while(iter.hasNext()) {
            var key = (String) iter.next();
            map.put(key, jsonObject.opt(key));
        }

        field.set(instance, map);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object save(Field field, Config instance) throws Exception {
        var jsonObject = new JSONObject();
        var map = (HashMap<Object, Object>) field.get(instance);
        
        for(var entry : map.entrySet()) {
            jsonObject.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        return jsonObject;
    }
}
