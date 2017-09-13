package info.juanmendez.myawareness.utils;

import android.content.SharedPreferences;

import info.juanmendez.myawareness.dependencies.ComboFence;

/**
 * Created by Juan Mendez on 9/12/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 * Takes care of synching ComboFence with SharedPreferences
 */
public class ComboFenceUtils {

    public static final String PREF_NAME = "Combo-Fence-Preferences";
    private static final String isLOCATION = "location";
    private static final String isHEADPHONES = "headphones";
    private static final String METERS = "meters";
    private static final String RUNNING = "running";
    private static final String LAT = "latitude";
    private static final String LON = "longitude";

    /**
     * This method fills in data from preference into a ComboFence object.
     * We make use of this so we have less to write on our components
     * @param comboFence object being updated
     * @param preference default preferences to pull data from
     */
    public static void toComboFence( ComboFence comboFence, SharedPreferences preference ){

        comboFence.setHeadphones( preference.getBoolean(isHEADPHONES, false));
        comboFence.setLocation( preference.getBoolean(isLOCATION, false));
        comboFence.setLat( preference.getLong(LAT, 0));
        comboFence.setLon( preference.getLong(LON, 0));
        comboFence.setMeters( preference.getInt(METERS, 0));
        comboFence.setRunning( preference.getBoolean(RUNNING, false));
        comboFence.setXfer(true);
    }

    /**
     * This method copes data from ComboFence object into preferences.
     * @param preference object being updated
     * @param comboFence object copied from
     */
    public static void toPreferences(SharedPreferences preference, ComboFence comboFence){
        SharedPreferences.Editor edit = preference.edit();
        edit.putBoolean( isHEADPHONES, comboFence.isHeadphones() );
        edit.putBoolean( isLOCATION, comboFence.isLocation() );
        edit.putLong( LAT, (long) comboFence.getLat());
        edit.putLong( LON, (long) comboFence.getLon());
        edit.putInt( METERS, comboFence.getMeters() );
        edit.putBoolean(RUNNING, comboFence.getRunning() );
        edit.apply();
    }
}
