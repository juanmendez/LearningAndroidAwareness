package info.juanmendez.myawareness.ui;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessClient;
import info.juanmendez.myawareness.dependencies.SnackMePlease;

/**
 * This is a very simple demo showing how to obtain the most current state of headphones!
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @Bean
    AwarenessClient awarenessClient;

    @Bean
    SnackMePlease snackMePlease;

    @AfterViews
    public void afterViews(){
        snackMePlease.setSnackView( findViewById(R.id.main_coordinatorLayout) );
    }

    @Click
    public void mainShowHeadphoneSnapshot(){
        showFragment( HeadphoneSnapshotFragment_.builder().build() );
    }

    @Click
    public void mainShowHeadphoneFence(){
        showFragment( HeadphoneFenceFragment_.builder().build() );
    }

    @Click
    public void mainShowLocationSnapshot(){
        showFragment( LocationSnapshotFragment_.builder().build() );
    }

    @Click
    public void mainComboFence(){
        showFragment( ComboFenceFragment_.builder().build() );
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_content, fragment)
                .addToBackStack(null)
                .commit();
    }
}