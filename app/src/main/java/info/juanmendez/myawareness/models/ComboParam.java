package info.juanmendez.myawareness.models;

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

    private HeadphoneParam mHeadphoneParam;
    private LocationParam mLocationParam;

    //<editor-fold desc="Getter-Setter">
    public boolean hasLocation() {
        return mLocationParam != null;
    }

    public LocationParam getLocationParam() {
        return mLocationParam;
    }

    public void setLocationParam(LocationParam location) {
        mLocationParam = location;
    }

    public boolean hasHeadphones() {
        return mHeadphoneParam != null;
    }

    public void setHeadphoneParam(HeadphoneParam headphones) {
        mHeadphoneParam = headphones;
    }

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