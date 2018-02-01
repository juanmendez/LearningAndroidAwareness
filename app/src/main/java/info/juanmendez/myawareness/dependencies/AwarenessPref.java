package info.juanmendez.myawareness.dependencies;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by juan on 1/31/18.
 * SharedPreference saves ComboParam object as json.
 * On reboot, it's easier now to recreate the object having several attributes
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface AwarenessPref {

    @DefaultString("")
    String comboParamJson();
}
