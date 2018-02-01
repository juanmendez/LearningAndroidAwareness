package info.juanmendez.myawareness.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;

import info.juanmendez.myawareness.dependencies.AwarenessPref;
import info.juanmendez.myawareness.dependencies.AwarenessPref_;
import info.juanmendez.myawareness.dependencies.FenceRepo;
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
     * test if we can start the mAwarenessFence or not
     * @return true if valid, else not valid
     */
    public static String areThereErrors( FenceRepo fenceRepo){
        String errorMessage = "";

        if( getFencesTotal( fenceRepo.getComboParam()) > 0 ){

            if( fenceRepo.getComboParam().hasLocation() && fenceRepo.getComboParam().getLocationParam().getMeters() == 0 ){
                errorMessage = "Please enter distance!";
            }
        }else{
            errorMessage = "Please select at least one mAwarenessFence!";
        }

        return errorMessage;
    }

    public static int getFencesTotal( ComboParam comboParam ) {
        return (comboParam.hasLocation() ?1:0)+( comboParam.hasHeadphones() ?1:0);
    }

    /**
     * This method fills in data from preference into a ComboParam object.
     * We make use of this so we have less to write on our components
     * @param comboParam object being updated
     * @param preference default preferences to pull data from
     */
    public static void toComboFence(ComboParam comboParam, AwarenessPref_ preference ){
        ComboParam jsonComboParam = new Gson().fromJson( preference.comboParamJson().getOr(""), ComboParam.class );

        if( jsonComboParam != null ){
            comboParam.setHeadphoneParam( jsonComboParam.getHeadphoneParam() );
            comboParam.setLocationParam( jsonComboParam.getLocationParam() );
            comboParam.setXfer( jsonComboParam.getXfer() );
            comboParam.setRunning( jsonComboParam.getRunning() );
        }
    }

    /**
     * This method copes data from ComboParam object into preferences.
     * @param preference object being updated
     * @param comboParam object copied from
     */
    public static void toPreferences(AwarenessPref_ preference, ComboParam comboParam){
        preference.comboParamJson().put( new Gson().toJson( comboParam ));
    }
}