package org.loveroo.sillytts.config.mods;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.loveroo.sillytts.config.Config;
import org.loveroo.sillytts.config.Config.FieldModifier;

public class ListModifier implements FieldModifier {
    
    @Override
    public void load(Field field, Config instance, Object value) throws Exception {
        var jsonArray = (JSONArray) value;
        var array = new ArrayList<Object>();

        for(var i = 0; i < jsonArray.length(); i++){
            array.add(jsonArray.opt(i));
        }

        field.set(instance, array);
    }

    @Override
    public Object save(Field field, Config instance) throws Exception {
        return new JSONArray((List<?>) field.get(instance));
    }
}
