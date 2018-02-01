package info.juanmendez.myawareness.models;

import com.google.android.gms.awareness.state.HeadphoneState;

/**
 * Created by juan on 1/31/18.
 */

public class HeadphoneParam {
    public boolean mIsOn = false;

    public HeadphoneParam(boolean isOn) {
        mIsOn = isOn;
    }

    public boolean isOn() {
        return mIsOn;
    }

    public void setOn(boolean on) {
        mIsOn = on;
    }

    public int getHeadphoneState(){
        return mIsOn ? HeadphoneState.PLUGGED_IN: HeadphoneState.UNPLUGGED;
    }
}
