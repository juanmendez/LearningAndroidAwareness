package info.juanmendez.myawareness.utils;

import android.content.SharedPreferences;

import info.juanmendez.myawareness.models.ComboParam;
import info.juanmendez.myawareness.models.HeadphoneParam;
import info.juanmendez.myawareness.models.LocationParam;

/**
 * Created by Juan Mendez on 9/12/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 * Takes care of synching ComboParam with SharedPreferences
 */
public class ComboFenceUtils {

    public static final String PREF_NAME = "Combo-FenceRepo-Preferences";
    private static final String isLOCATION = "location";
    private static final String isHEADPHONES = "headphones";
    private static final String METERS = "meters";
    private static final String RUNNING = "running";
    private static final String LAT = "latitude";
    private static final String LON = "longitude";

    /**
     * This method fills in data from preference into a ComboParam object.
     * We make use of this so we have less to write on our components
     * @param comboParam object being updated
     * @param preference default preferences to pull data from
     */
    public static void toComboFence(ComboParam comboParam, SharedPreferences preference ){

        if( preference.getBoolean(isHEADPHONES, false) ){
            comboParam.setHeadphoneParam( new HeadphoneParam(true));
        }

        if( preference.getBoolean(isLOCATION, false) ){
            LocationParam locationParam = new LocationParam();
            locationParam.setLat(preference.getLong(LAT, 0));
            locationParam.setLon(preference.getLong(LON, 0));
            locationParam.setMeters(preference.getInt(METERS, 0));
        }

        comboParam.setRunning( preference.getBoolean(RUNNING, false));
        comboParam.setXfer(true);
    }

    /**
     * This method copes data from ComboParam object into preferences.
     * @param preference object being updated
     * @param comboParam object copied from
     */
    public static void toPreferences(SharedPreferences preference, ComboParam comboParam){
        SharedPreferences.Editor edit = preference.edit();
        edit.putBoolean( isHEADPHONES, comboParam.hasHeadphones() );
        edit.putBoolean( isLOCATION, comboParam.hasLocation() );

        if( comboParam.hasHeadphones() ){

        }

        if( comboParam.hasLocation() ){
            LocationParam locationParam = comboParam.getLocationParam();
            edit.putLong( LAT, (long) locationParam.getLat());
            edit.putLong( LON, (long) locationParam.getLon());
            edit.putInt( METERS, locationParam.getMeters() );
        }

        edit.putBoolean(RUNNING, comboParam.getRunning() );
        edit.apply();
    }
}