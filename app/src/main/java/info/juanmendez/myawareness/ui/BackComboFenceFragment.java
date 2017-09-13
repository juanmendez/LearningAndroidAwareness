package info.juanmendez.myawareness.ui;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.state.HeadphoneState;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.OutAndAboutReceiver;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;
import info.juanmendez.myawareness.dependencies.ComboFence;
import info.juanmendez.myawareness.dependencies.LocationSnapshotService;
import info.juanmendez.myawareness.dependencies.SnackMePlease;
import info.juanmendez.myawareness.events.Response;
import info.juanmendez.myawareness.events.ShortResponse;
import info.juanmendez.myawareness.utils.ComboFenceUtils;
import timber.log.Timber;


/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EFragment(R.layout.fragment_combo_fence)
public class BackComboFenceFragment extends Fragment {
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

    @SystemService
    NotificationManager notificationManager;

    @Bean
    AwarenessConnection connection;

    @Bean
    ComboFence comboFence;

    @Bean
    SnackMePlease snackMePlease;

    @Override
    public void onResume(){
        super.onResume();
        FragmentUtils.setHomeEnabled( this, true );
        connection.connect();
        turnOnFence();
        updateView();
    }

    //<editor-fold desc="Event-Listeners">
        @CheckedChange(R.id.comboFence_checkLocation)
        void onCheckedLocation( boolean isChecked ){
            comboFence.setLocation( isChecked );
            showMeterText( isChecked );
            onToggleButton(false);
        }

        @CheckedChange(R.id.comboFence_checkHeadphones)
        void onCheckedHeadphones( boolean isChecked ){
            comboFence.setHeadphones( isChecked );
            onToggleButton(false);
        }

        @AfterTextChange(R.id.comboFence_meterText)
        void onMeterText(Editable meters){

            String strMeters = meters.toString();

            if( strMeters.isEmpty() )
                strMeters = "0";

            comboFence.setMeters( Integer.parseInt(strMeters) );
            onToggleButton(false);
        }

        @CheckedChange(R.id.comboFence_toggleButton)
        void onToggleButton( boolean isChecked ){
            if( isChecked && comboFence.validate() ){
                buildUpFences();
            }else{
                toggleButton.setChecked(false);
                turnOffFence();
                snackMePlease.e( comboFence.getErrorMessage() );
            }
        }
    //</editor-fold>

    void updateView(){

        syncFromPreferences();
        checkboxLocation.setChecked( comboFence.isLocation());
        checkboxHeadphones.setChecked( comboFence.isHeadphones());

        meterText.setText( String.valueOf(comboFence.getMeters()) );
        showMeterText( comboFence.isLocation() );
        toggleButton.setChecked( comboFence.getRunning());
    }

    void showMeterText(Boolean show ){

        //reset if meterText is not displayed
        if( !show ){
            comboFence.setMeters( 0 );
            meterText.setText("");
        }

        meterText.setVisibility( show? View.VISIBLE:View.GONE );
    }

    private void buildUpFences(){
        List<AwarenessFence> fences = new ArrayList<>();

        ShortResponse<AwarenessFence> snapshotResponse = fence -> {
            fences.add( fence );

            if( fences.size() == comboFence.getFencesNeeded() ){
                AwarenessFence awarenessFence;

                if( fences.size() == 1 )
                    awarenessFence = fences.get(0);
                else
                    awarenessFence = AwarenessFence.and( fences );

                comboFence.setFence( awarenessFence );
                turnOnFence();
            }
        };

        if( comboFence.isLocation() ){
            //getSnapshot ensures to have permission granted, so we can suppress it during onResult
            LocationSnapshotService.build(getActivity(),connection).getSnapshot(new Response<Location>() {

                @SuppressLint("MissingPermission")
                @Override
                public void onResult(Location location) {
                    snapshotResponse.onResult( AwarenessFence.not(LocationFence.in( location.getLatitude(), location.getLongitude(), comboFence.getMeters(), comboFence.getMeters() )) );
                }

                @Override
                public void onError(Exception exception) {
                    snackMePlease.e( exception.getMessage() );
                }
            });
        }

        if( comboFence.isHeadphones() ){
            snapshotResponse.onResult(HeadphoneFence.during(HeadphoneState.PLUGGED_IN));
        }
    }

    //runs fence only if it is available.
    private void turnOnFence() {

        if( comboFence.getFence() != null && !comboFence.getRunning() ){
            comboFence.setRunning(true);
            saveChangesToPreferences();
            PendingIntent fenceIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(OutAndAboutReceiver.FENCE_INTENT_FILTER), 0);

            Awareness.FenceApi.updateFences(connection.getClient(), new FenceUpdateRequest.Builder()
                    .addFence(OutAndAboutReceiver.FENCE_KEY, comboFence.getFence(), fenceIntent)
                    .build()).setResultCallback(status -> {
                if (status.isSuccess()) {
                    Timber.i("Fence for headphones in registered");
                } else {
                    Timber.i("Fence for headphones in NOT registered");
                }
            });
        }
    }

    private void turnOffFence(){

        if( comboFence.getRunning() ){
            comboFence.setFence(null);
            comboFence.setRunning(false);
            saveChangesToPreferences();

            //clear any notifications
            notificationManager.cancel( OutAndAboutReceiver.NOTIFICATION_KEY);

            Awareness.FenceApi.updateFences(connection.getClient(), new FenceUpdateRequest.Builder()
                    .removeFence(OutAndAboutReceiver.FENCE_KEY).build())
                    .setResultCallback(status -> {
                        if( status.isSuccess() ){
                            snackMePlease.i( "our fence successfully disconnected");
                        }else{
                            snackMePlease.e( "there was a problem disconnecting " + status.getStatusMessage() );
                        }
                    });
        }

    }

    //<editor-fold desc="Preferences">
    private void saveChangesToPreferences(){
        ComboFenceUtils.toPreferences( PreferenceManager.getDefaultSharedPreferences(getContext()), comboFence );
    }

    private void syncFromPreferences() {
        if( !comboFence.getXfer() ){
            ComboFenceUtils.toComboFence( comboFence, PreferenceManager.getDefaultSharedPreferences(getContext()));
        }
    }
    //</editor-fold>

    @Override
    public void onPause(){
        super.onPause();
        FragmentUtils.setHomeEnabled( this, false );
        connection.disconnect();
    }
}
