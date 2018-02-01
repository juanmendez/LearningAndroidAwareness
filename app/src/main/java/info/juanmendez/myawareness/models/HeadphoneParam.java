package info.juanmendez.myawareness.models;

import com.google.android.gms.awareness.state.HeadphoneState;

/**
 * Created by juan on 1/31/18.
 */

public class HeadphoneParam {
    public boolean mMustPlugIn = false;

    public HeadphoneParam(boolean mustPlugIn) {
        mMustPlugIn = mustPlugIn;
    }

    public boolean mustPlugin() {
        return mMustPlugIn;
    }

    public void setMustPlugIn(boolean mustPlugIn) {
        mMustPlugIn = mustPlugIn;
    }

    public int getHeadphoneState(){
        return mMustPlugIn ? HeadphoneState.PLUGGED_IN: HeadphoneState.UNPLUGGED;
    }
}
