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

    private static final String isLOCATION = "location";
    private static final String isHEADPHONES = "headphones";
    private static final String METERS = "meters";
    private static final String COMPLETED = "completed";

    /**
     * This method fills in data from preference into a ComboFence object.
     * We make use of this so we have less to write on our components
     * @param comboFence
     * @param preference
     */
    public static void toComboFence( ComboFence comboFence, SharedPreferences preference ){

        comboFence.setHeadphones( preference.getBoolean(isHEADPHONES, false));
        comboFence.setLocation( preference.getBoolean(isLOCATION, false));
        comboFence.setMeters( preference.getInt(METERS, 0));
        comboFence.setCompleted( preference.getBoolean(COMPLETED, false));
    }

    /**
     * This method copes data from ComboFence object into preferences.
     * @param preference
     * @param comboFence
     */
    public static void toPreferences(SharedPreferences preference, ComboFence comboFence){
        SharedPreferences.Editor edit = preference.edit();
        edit.putBoolean( isHEADPHONES, comboFence.isHeadphones() );
        edit.putBoolean( isLOCATION, comboFence.isLocation() );
        edit.putInt( METERS, comboFence.getMeters() );
        edit.putBoolean( COMPLETED, comboFence.getCompleted() );
        edit.commit();
    }
}
