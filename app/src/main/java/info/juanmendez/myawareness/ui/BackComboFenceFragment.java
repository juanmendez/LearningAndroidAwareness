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
    CheckBox mCheckboxHeadphones;

    @ViewById(R.id.comboFence_checkLocation)
    CheckBox mCheckboxLocation;

    @ViewById(R.id.comboFence_meterText)
    EditText mMeterText;

    @ViewById(R.id.comboFence_messageText)
    TextView mMessageText;

    @ViewById(R.id.comboFence_toggleButton)
    ToggleButton mToggleButton;

    @SystemService
    NotificationManager mNotificationManager;

    @Bean
    AwarenessConnection mConnection;

    @Bean
    ComboFence mComboFence;

    @Bean
    SnackMePlease mSnackmePlease;

    @Override
    public void onResume(){
        super.onResume();
        FragmentUtils.setHomeEnabled( this, true );
        mConnection.connect();
        turnOnFence();
        updateView();
    }

    //<editor-fold desc="Event-Listeners">
        @CheckedChange(R.id.comboFence_checkLocation)
        void onCheckedLocation( boolean isChecked ){
            mComboFence.setLocation( isChecked );
            showMeterText( isChecked );
        }

        @CheckedChange(R.id.comboFence_checkHeadphones)
        void onCheckedHeadphones( boolean isChecked ){
            mComboFence.setHeadphones( isChecked );
        }

        @AfterTextChange(R.id.comboFence_meterText)
        void onMeterText(Editable meters){

            String strMeters = meters.toString();

            if( strMeters.isEmpty() )
                strMeters = "0";

            mComboFence.setMeters( Integer.parseInt(strMeters) );
        }

        @CheckedChange(R.id.comboFence_toggleButton)
        void onToggleButton( boolean isChecked ){
            if( isChecked && mComboFence.validate() ){
                buildUpFences();
            }else{
                mToggleButton.setChecked(false);
                turnOffFence();
                mSnackmePlease.e( mComboFence.getErrorMessage() );
            }
        }
    //</editor-fold>

    void updateView(){

        syncFromPreferences();
        mCheckboxLocation.setChecked( mComboFence.isLocation());
        mCheckboxHeadphones.setChecked( mComboFence.isHeadphones());

        mMeterText.setText( String.valueOf(mComboFence.getMeters()) );
        showMeterText( mComboFence.isLocation() );
        mToggleButton.setChecked( mComboFence.getRunning());
    }

    void showMeterText(Boolean show ){

        //reset if mMeterText is not displayed
        if( !show ){
            mComboFence.setMeters( 0 );
            mMeterText.setText("");
        }

        mMeterText.setVisibility( show? View.VISIBLE:View.GONE );
    }

    private void buildUpFences(){
        List<AwarenessFence> fences = new ArrayList<>();

        ShortResponse<AwarenessFence> snapshotResponse = fence -> {
            fences.add( fence );

            if( fences.size() == mComboFence.getFencesNeeded() ){
                AwarenessFence awarenessFence;

                if( fences.size() == 1 )
                    awarenessFence = fences.get(0);
                else
                    awarenessFence = AwarenessFence.and( fences );

                mComboFence.setFence( awarenessFence );
                turnOnFence();
            }
        };

        if( mComboFence.isLocation() ){
            //getSnapshot ensures to have permission granted, so we can suppress it during onResult
            LocationSnapshotService.build(getActivity(), mConnection).getSnapshot(new Response<Location>() {

                @SuppressLint("MissingPermission")
                @Override
                public void onResult(Location location) {
                    mComboFence.setLat( location.getLatitude() );
                    mComboFence.setLon( location.getLongitude() );
                    snapshotResponse.onResult( AwarenessFence.not(LocationFence.in( location.getLatitude(), location.getLongitude(), mComboFence.getMeters(), mComboFence.getMeters() )) );
                }

                @Override
                public void onError(Exception exception) {
                    mSnackmePlease.e( exception.getMessage() );
                }
            });
        }

        if( mComboFence.isHeadphones() ){
            snapshotResponse.onResult(HeadphoneFence.during(HeadphoneState.PLUGGED_IN));
        }
    }

    //runs fence only if it is available.
    private void turnOnFence() {

        if( mComboFence.getFence() != null && !mComboFence.getRunning() ){
            mComboFence.setRunning(true);
            saveChangesToPreferences();

            PendingIntent fenceIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(OutAndAboutReceiver.FENCE_INTENT_FILTER), 0);

            Awareness.FenceApi.updateFences(mConnection.getAwarenessClient(), new FenceUpdateRequest.Builder()
                    .addFence(OutAndAboutReceiver.FENCE_KEY, mComboFence.getFence(), fenceIntent)
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

        if( mComboFence.getRunning() ){
            mComboFence.setFence(null);
            mComboFence.setRunning(false);
            saveChangesToPreferences();

            //clear any notifications
            mNotificationManager.cancel( OutAndAboutReceiver.NOTIFICATION_KEY);

            Awareness.FenceApi.updateFences(mConnection.getAwarenessClient(), new FenceUpdateRequest.Builder()
                    .removeFence(OutAndAboutReceiver.FENCE_KEY).build())
                    .setResultCallback(status -> {
                        if( status.isSuccess() ){
                            mSnackmePlease.i( "our fence successfully disconnected");
                        }else{
                            mSnackmePlease.e( "there was a problem disconnecting " + status.getStatusMessage() );
                        }
                    });
        }

    }

    //<editor-fold desc="Preferences">
    private void saveChangesToPreferences(){
        ComboFenceUtils.toPreferences( PreferenceManager.getDefaultSharedPreferences(getContext()), mComboFence);
    }

    private void syncFromPreferences() {
        if( !mComboFence.getXfer() ){
            ComboFenceUtils.toComboFence(mComboFence, PreferenceManager.getDefaultSharedPreferences(getContext()));
        }
    }
    //</editor-fold>

    @Override
    public void onPause(){
        super.onPause();
        FragmentUtils.setHomeEnabled( this, false );
        mConnection.disconnect();
    }
}
