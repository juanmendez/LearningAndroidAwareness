package info.juanmendez.myawareness.models;
import android.support.annotation.NonNull;

/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 *
 * It keeps a reference of locationParam, and headphoneParam
 * as well as if the object was generated on reboot, and if the fence is running before reboot
 */
public class ComboParam {

    private Boolean mFenceRunning = false;
    private Boolean mBuiltOnReboot = false;

    private HeadphoneParam mHeadphoneParam = new HeadphoneParam(true);
    private LocationParam mLocationParam = new LocationParam();

    //<editor-fold desc="Getter-Setter">
    @NonNull
    public LocationParam getLocationParam() {
        return mLocationParam;
    }

    public void setLocationParam( @NonNull LocationParam location) {
        mLocationParam = location;
    }

    public void setHeadphoneParam( @NonNull HeadphoneParam headphones) {
        mHeadphoneParam = headphones;
    }

    @NonNull
    public HeadphoneParam getHeadphoneParam() {
        return mHeadphoneParam;
    }

    public Boolean getBuiltOnReboot() {
        return mBuiltOnReboot;
    }

    public void setBuiltOnReboot(Boolean builtOnReboot) {
        mBuiltOnReboot = builtOnReboot;
    }

    public Boolean getFenceRunning() {
        return mFenceRunning;
    }

    public void setFenceRunning(Boolean fenceRunning) {
        mFenceRunning = fenceRunning;
    }
    //</editor-fold>
}