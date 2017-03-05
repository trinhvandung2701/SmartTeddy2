package iot.tdmu.edu.vn.smartteddy.intro;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    private int PRIVATE_MODE  = 0;

    private static final String PREF_NAME = "snow-intro-slider";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    public Session(Context context){
        this.context = context;
        preferences = this.context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
