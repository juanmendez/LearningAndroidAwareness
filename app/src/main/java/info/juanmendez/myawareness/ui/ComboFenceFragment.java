package info.juanmendez.myawareness.ui;

import android.support.v4.app.Fragment;
import android.widget.CheckBox;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;
import info.juanmendez.myawareness.dependencies.ComboFence;


/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EFragment(R.layout.fragment_combo_fence)
public class ComboFenceFragment extends Fragment {
    @ViewById(R.id.comboFence_checkHeadphones)
    CheckBox chechHeadphones;

    @ViewById(R.id.comboFence_checkLocation)
    CheckBox checkLocation;

    @ViewById(R.id.comboFence_messageText)
    TextView messageText;

    @Bean
    AwarenessConnection connection;

    @Bean
    ComboFence comboFence;

    @Override
    public void onResume(){
        super.onResume();
        FragmentUtils.setHomeEnabled( this, true );
        connection.connect();
        uiSetup();
    }

    private void uiSetup(){

    }

    @Override
    public void onPause(){
        super.onPause();
        FragmentUtils.setHomeEnabled( this, false );
        connection.disconnect();
    }
}
