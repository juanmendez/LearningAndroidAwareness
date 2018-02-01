package info.juanmendez.myawareness.dependencies;

import com.google.android.gms.awareness.fence.AwarenessFence;

import org.androidannotations.annotations.EBean;

import info.juanmendez.myawareness.models.ComboParam;
import io.reactivex.annotations.NonNull;

/**
 * Created by juan on 1/31/18.
 * Keeps a hold of comboParam, and awarenessFence objects
 */
@EBean(scope = EBean.Scope.Singleton)
public class FenceRepo {

    private AwarenessFence mAwarenessFence;
    private ComboParam mComboParam = new ComboParam();

    public ComboParam getComboParam() {
        return mComboParam;
    }

    public AwarenessFence getAwarenessFence() {
        return mAwarenessFence;
    }

    public void setAwarenessFence(AwarenessFence awarenessFence) {
        mAwarenessFence = awarenessFence;
    }
}