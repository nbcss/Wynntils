package cf.wynntils.core.framework.settings;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import cf.wynntils.core.framework.settings.annotations.Setting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 23/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class SettingsContainer {

    SettingsHolder holder;
    ArrayList<Field> fields = new ArrayList<>();
    ModuleContainer m;

    public SettingsContainer(ModuleContainer m, SettingsHolder holder) {
        this.holder = holder;
        this.m = m;

        for(Field f : holder.getClass().getDeclaredFields()) {
            if(f.getAnnotation(Setting.class) == null) {
                continue;
            }
            fields.add(f);
        }

        try{
            tryToLoad();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void tryToLoad() throws Exception {
        updateValues(SettingsManager.getSettings(m, holder));
    }

    public HashMap<Field, Object> getValues() throws Exception {
        HashMap<Field, Object> values = new HashMap<>();

        for(Field f : fields) {
            values.put(f, f.get(holder));
        }

        return values;
    }

    public void saveSettings() throws Exception {
        SettingsManager.saveSettings(m, holder);
    }

    public void setValue(Field f, Object value) throws Exception {
        f.set(holder, value);
        holder.onSettingChanged(f.getName());

        SettingsManager.saveSettings(m, holder);
    }

    public void setValue(Field f, Object value, boolean save) throws Exception {
        f.set(holder, value);
        holder.onSettingChanged(f.getName());

        if(save)
            SettingsManager.saveSettings(m, holder);
    }

    public void updateValues(SettingsHolder newH) throws Exception {
        for(Field f : newH.getClass().getDeclaredFields()) {
            if(f.getAnnotation(Setting.class) == null) {
                continue;
            }

            setValue(f, f.get(newH), false);
        }
    }

}