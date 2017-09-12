package info.juanmendez.myawareness.dependencies;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import org.androidannotations.annotations.EBean;

import info.juanmendez.myawareness.R;

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

    public void i(String message ){
        if( snackView!=null && !message.isEmpty() ){
            Snackbar snackbar = Snackbar.make(snackView, message, Snackbar.LENGTH_LONG );
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(snackView.getContext(), android.R.color.black ));
            snackbar.show();
        }
    }

    public void e(String message) {
        if( snackView!=null && !message.isEmpty() ){
            Snackbar snackbar = Snackbar.make(snackView, message, Snackbar.LENGTH_LONG );
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(snackView.getContext(), R.color.colorAccent ));
            snackbar.show();
        }
    }
}