package info.juanmendez.myawareness.dependencies;

import com.google.android.gms.awareness.fence.AwarenessFence;

import org.androidannotations.annotations.EBean;

/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean
public class ComboFence {
    
    private boolean mLocation;
    private double mLat;
    private double mLon;
    private boolean mHeadphones;
    private int mMeters;
    private AwarenessFence mFence;
    private String mErrorMessage ="";
    private Boolean mRunning = false;
    private Boolean mXfer = false;

    //<editor-fold desc="Getter-Setter">
    public Boolean getRunning() {
        return mRunning;
    }

    public void setRunning(Boolean running) {
        mRunning = running;
    }

    public boolean isLocation() {
        return mLocation;
    }

    public void setLocation(boolean location) {
        mLocation = location;
    }

    public boolean isHeadphones() {
        return mHeadphones;
    }

    public void setHeadphones(boolean headphones) {
        mHeadphones = headphones;
    }

    public int getMeters() {
        return mMeters;
    }

    public void setMeters(int meters) {
        mMeters = meters;
    }

    public AwarenessFence getFence() {
        return mFence;
    }

    public void setFence(AwarenessFence fence) {
        mFence = fence;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public Boolean getXfer() {
        return mXfer;
    }

    public void setXfer(Boolean xfer) {
        mXfer = xfer;
    }

    //</editor-fold>

    /**
     * test if we can start the mFence or not
     * @return true if valid, else not valid
     */
    public boolean validate(){
        mErrorMessage = "";
        if( isHeadphones() || isLocation() ){

            if( isLocation() && getMeters() == 0 ){
                mErrorMessage = "Please enter distance!";
                return false;
            }

            return true;
        }else{
            mErrorMessage = "Please select at least one mFence!";
            return false;
        }
    }

    public int getFencesNeeded() {
        return (mLocation ?1:0)+(mHeadphones ?1:0);
    }
}