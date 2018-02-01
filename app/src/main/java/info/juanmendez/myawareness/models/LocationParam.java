package info.juanmendez.myawareness.models;

/**
 * Created by juan on 1/31/18.
 */

public class LocationParam {
    private boolean mIsTurnedOn = false;
    private double mLon = 0;
    private double mLat = 0;
    private int mMeters = 0;

    public boolean isTurnedOn() {
        return mIsTurnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        mIsTurnedOn = turnedOn;
    }

    public double getLon() {
        return mLon;
    }

    public void setLon(double lon) {
        mLon = lon;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public int getMeters() {
        return mMeters;
    }

    public void setMeters(int meters) {
        mMeters = meters;
    }
}
