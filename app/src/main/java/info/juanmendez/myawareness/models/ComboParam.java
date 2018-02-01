package info.juanmendez.myawareness.models;

import com.google.android.gms.awareness.fence.AwarenessFence;

import org.androidannotations.annotations.EBean;

import info.juanmendez.myawareness.models.HeadphoneParam;
import info.juanmendez.myawareness.models.LocationParam;

/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
public class ComboParam {

    private Boolean mRunning = false;
    private Boolean mXfer = false;

    private HeadphoneParam mHeadphoneParam;
    private LocationParam mLocationParam;

    //<editor-fold desc="Getter-Setter">
    public Boolean getRunning() {
        return mRunning;
    }

    public void setRunning(Boolean running) {
        mRunning = running;
    }

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

    public Boolean getXfer() {
        return mXfer;
    }

    public void setXfer(Boolean xfer) {
        mXfer = xfer;
    }
    //</editor-fold>
}