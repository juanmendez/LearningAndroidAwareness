package info.juanmendez.myawareness.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;
import info.juanmendez.myawareness.dependencies.ComboFence;
import info.juanmendez.myawareness.dependencies.SnackMePlease;


/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EFragment(R.layout.fragment_combo_fence)
public class ComboFenceFragment extends Fragment {
    @Bean
    AwarenessConnection connection;

    @Bean
    ComboFence comboFence;

    @Bean
    SnackMePlease snackMePlease;

    @ViewById(R.id.comboFence_checkHeadphones)
    CheckBox checkboxHeadphones;

    @ViewById(R.id.comboFence_checkLocation)
    CheckBox checkboxLocation;

    @ViewById(R.id.comboFence_meterText)
    EditText meterText;

    @ViewById(R.id.comboFence_messageText)
    TextView messageText;

    @ViewById(R.id.comboFence_toggleButton)
    ToggleButton toggleButton;

    private static final String FENCE_INTENT_FILTER = "COMBO_RECEIVER_ACTION";
    private static final String FENCE_KEY = "MyHeadphonesOnWalkingKey";

    @Override
    public void onResume(){
        super.onResume();
        FragmentUtils.setHomeEnabled( this, true );
        connection.connect();

        checkboxLocation.setChecked( comboFence.isLocation());
        checkboxHeadphones.setChecked( comboFence.isHeadphones());

        meterText.setText( String.valueOf(comboFence.getMeters()) );
        showMeterText( comboFence.isLocation() );
    }

    @CheckedChange(R.id.comboFence_checkLocation)
    void onCheckedLocation( boolean isChecked ){
        comboFence.setLocation( isChecked );
        showMeterText( isChecked );
    }

    void showMeterText(Boolean show ){

        //reset if meterText is not displayed
        if( !show ){
            comboFence.setMeters( 0 );
            meterText.setText("");
        }

        meterText.setVisibility( show? View.VISIBLE:View.GONE );
    }

    @CheckedChange(R.id.comboFence_checkHeadphones)
    void onCheckedHeadphones( boolean isChecked ){
        comboFence.setHeadphones( isChecked );
    }

    @AfterTextChange(R.id.comboFence_meterText)
    void onMeterText(Editable meters){

        String strMeters = meters.toString();

        if( strMeters.isEmpty() )
            strMeters = "0";

        comboFence.setMeters( Integer.parseInt(strMeters) );
    }

    @CheckedChange(R.id.comboFence_toggleButton)
    void onToggleButton( boolean isChecked ){
        if( comboFence.validate() ){

        }else{
            toggleButton.setChecked(false);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        FragmentUtils.setHomeEnabled( this, false );
        connection.disconnect();
    }

    @Receiver(actions = FENCE_INTENT_FILTER )
    public void onBroadcastReceiver(Context context, Intent intent) {

    }
}
