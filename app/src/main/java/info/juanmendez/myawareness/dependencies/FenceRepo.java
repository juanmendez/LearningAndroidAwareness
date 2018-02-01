package info.juanmendez.myawareness.dependencies;

import com.google.android.gms.awareness.fence.AwarenessFence;

import org.androidannotations.annotations.EBean;

import info.juanmendez.myawareness.models.ComboParam;
import io.reactivex.annotations.NonNull;

/**
 * Created by juan on 1/31/18.
 */
@EBean(scope = EBean.Scope.Singleton)
public class FenceRepo {

    private AwarenessFence mAwarenessFence;
    private String mErrorMessage ="";
    private ComboParam mComboParam = new ComboParam();

    public ComboParam getComboParam() {
        return mComboParam;
    }

    /**
     * test if we can start the mAwarenessFence or not
     * @return true if valid, else not valid
     */
    public boolean areFencesValid(){
        mErrorMessage = "";
        if( getFencesTotal() > 0 ){

            if( mComboParam.hasLocation() && mComboParam.getLocationParam().getMeters() == 0 ){
                mErrorMessage = "Please enter distance!";
                return false;
            }

            return true;
        }else{
            mErrorMessage = "Please select at least one mAwarenessFence!";
            return false;
        }
    }

    public int getFencesTotal() {
        return (mComboParam.hasLocation() ?1:0)+( mComboParam.hasHeadphones() ?1:0);
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public AwarenessFence getAwarenessFence() {
        return mAwarenessFence;
    }

    public void setAwarenessFence(AwarenessFence awarenessFence) {
        mAwarenessFence = awarenessFence;
    }
}