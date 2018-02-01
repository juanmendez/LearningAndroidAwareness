package info.juanmendez.myawareness.models;

import com.google.android.gms.awareness.state.HeadphoneState;

/**
 * Created by juan on 1/31/18.
 */

public class HeadphoneParam {
    private boolean mIsTurnedOn = false;
    private boolean mMustPlugIn = false;

    public HeadphoneParam(boolean mustPlugIn) {
        mMustPlugIn = mustPlugIn;
    }

    public boolean mustPlugin() {
        return mMustPlugIn;
    }

    public void setMustPlugIn(boolean mustPlugIn) {
        mMustPlugIn = mustPlugIn;
    }

    public boolean isTurnedOn() {
        return mIsTurnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        mIsTurnedOn = turnedOn;
    }

    public boolean isMustPlugIn() {
        return mMustPlugIn;
    }

    public int getHeadphoneState(){
        return mMustPlugIn ? HeadphoneState.PLUGGED_IN: HeadphoneState.UNPLUGGED;
    }
}
