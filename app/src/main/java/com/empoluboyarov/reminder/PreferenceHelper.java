package com.empoluboyarov.reminder;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceHelper {

    private static PreferenceHelper instance;
    private Context context;
    private SharedPreferences preferences;

    public static final String SPLASH_IS_INVISIBLE = "splash_is_invisible";

    private PreferenceHelper(){
    }

    public static PreferenceHelper getInstance(){
        if (instance == null)
            instance = new PreferenceHelper();
        return instance;
    }

    public void init(Context context){
        this.context = context;
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE); //создаем файл настроек с именем 'preferences' и доступом только из нашего приложения
    }

    public void putBoolean(String key, boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key){
        return preferences.getBoolean(key, false);
    }
}
