package info.juanmendez.myawareness;

import android.app.Application;

import org.androidannotations.annotations.EApplication;

import timber.log.Timber;


/**
 * Created by Juan Mendez on 9/8/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EApplication
public class AwarenessApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        handleTimber();
    }

    private void handleTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant( new Timber.DebugTree());
        } else {
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable t) {
                    return;
                }
            });
        }
    }
}