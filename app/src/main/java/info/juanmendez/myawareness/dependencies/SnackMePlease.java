package info.juanmendez.myawareness.dependencies;

import android.support.design.widget.Snackbar;
import android.view.View;

import org.androidannotations.annotations.EBean;

/**
 * Created by Juan Mendez on 9/9/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

@EBean(scope=EBean.Scope.Singleton)
public class SnackMePlease {
    View snackView;

    public void setSnackView(View snackView) {
        this.snackView = snackView;
    }

    public void showMessage( String message ){
        if( snackView!=null ){
            Snackbar.make(snackView, message, Snackbar.LENGTH_LONG ).show();
        }
    }
}
