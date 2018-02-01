package info.juanmendez.myawareness.ui;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.state.HeadphoneState;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.OutAndAboutReceiver;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;
import info.juanmendez.myawareness.dependencies.AwarenessPref_;
import info.juanmendez.myawareness.dependencies.FenceRepo;
import info.juanmendez.myawareness.dependencies.LocationSnapshotService;
import info.juanmendez.myawareness.dependencies.SnackMePlease;
import info.juanmendez.myawareness.events.Response;
import info.juanmendez.myawareness.events.ShortResponse;
import info.juanmendez.myawareness.models.ComboParam;
import info.juanmendez.myawareness.models.HeadphoneParam;
import info.juanmendez.myawareness.models.LocationParam;
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

    @Pref
    AwarenessPref_ mAwarenessPref;

    @Bean
    AwarenessConnection mConnection;

    @Bean
    FenceRepo mFenceRepo;

    ComboParam mComboParam;

    @Bean
    SnackMePlease mSnackmePlease;

    @AfterInject
    void afterInject(){
        mComboParam = mFenceRepo.getComboParam();
    }

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
            mComboParam.setLocationParam( isChecked?new LocationParam():null );
            showMeterText( isChecked );
        }

        @CheckedChange(R.id.comboFence_checkHeadphones)
        void onCheckedHeadphones( boolean isChecked ){
            mComboParam.setHeadphoneParam( isChecked?new HeadphoneParam(true):null);
        }

        @AfterTextChange(R.id.comboFence_meterText)
        void onMeterText(Editable meters){

            String strMeters = meters.toString();

            if( strMeters.isEmpty() )
                strMeters = "0";

            if( mComboParam.hasLocation() ){
                mComboParam.getLocationParam().setMeters( Integer.parseInt(strMeters) );
            }
        }

        @CheckedChange(R.id.comboFence_toggleButton)
        void onToggleButton( boolean isChecked ){

            String errorMessage = ComboFenceUtils.areThereErrors( mFenceRepo );
            if( isChecked && errorMessage.isEmpty() ){
                buildUpFences();
            }else{
                mToggleButton.setChecked(false);
                turnOffFence();
                mSnackmePlease.e( errorMessage );
            }
        }
    //</editor-fold>

    void updateView(){

        syncFromPreferences();
        mCheckboxLocation.setChecked( mComboParam.hasLocation());
        mCheckboxHeadphones.setChecked( mComboParam.hasHeadphones());

        if( mComboParam.hasLocation() ){
            mMeterText.setText( String.valueOf(mComboParam.getLocationParam().getMeters()) );
        }else{
            mMeterText.setText( "0" );
        }

        showMeterText( mComboParam.hasLocation() );
        mToggleButton.setChecked( mComboParam.getRunning());

        showFenceQueries();
    }

    void showMeterText(Boolean show ){

        //reset if mMeterText is not displayed
        if( !show ){
            mComboParam.setLocationParam( null );
            mMeterText.setText("");
        }

        mMeterText.setVisibility( show? View.VISIBLE:View.GONE );
    }

    private void buildUpFences(){
        List<AwarenessFence> fences = new ArrayList<>();

        ShortResponse<AwarenessFence> snapshotResponse = fence -> {
            fences.add( fence );

            if( fences.size() == ComboFenceUtils.getFencesTotal( mComboParam ) ){
                AwarenessFence awarenessFence;

                if( fences.size() == 1 )
                    awarenessFence = fences.get(0);
                else
                    awarenessFence = AwarenessFence.and( fences );

                mFenceRepo.setAwarenessFence( awarenessFence );
                turnOnFence();
            }
        };

        if( mComboParam.hasLocation() ){
            //getSnapshot ensures to have permission granted, so we can suppress it during onResult
            LocationSnapshotService.build(getActivity(), mConnection).getSnapshot(new Response<Location>() {

                @SuppressLint("MissingPermission")
                @Override
                public void onResult(Location location) {

                    if( mComboParam.hasLocation() ){
                        mComboParam.getLocationParam().setLat( location.getLatitude() );
                        mComboParam.getLocationParam().setLon( location.getLongitude() );

                        snapshotResponse.onResult( AwarenessFence.not(LocationFence.in( location.getLatitude(),
                                location.getLongitude(),
                                mComboParam.getLocationParam().getMeters(),
                                mComboParam.getLocationParam().getMeters() )) );
                    }
                }

                @Override
                public void onError(Exception exception) {
                    mSnackmePlease.e( exception.getMessage() );
                }
            });
        }

        if( mComboParam.hasHeadphones() ){
            snapshotResponse.onResult(HeadphoneFence.during(HeadphoneState.PLUGGED_IN));
        }
    }

    //runs fence only if it is available.
    private void turnOnFence() {

        if( mFenceRepo.getAwarenessFence() != null && !mComboParam.getRunning() ){
            mComboParam.setRunning(true);
            saveChangesToPreferences();

            PendingIntent fenceIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(OutAndAboutReceiver.FENCE_INTENT_FILTER), 0);

            Awareness.FenceApi.updateFences(mConnection.getAwarenessClient(), new FenceUpdateRequest.Builder()
                    .addFence(OutAndAboutReceiver.FENCE_KEY, mFenceRepo.getAwarenessFence(), fenceIntent)
                    .build()).setResultCallback(status -> {
                if (status.isSuccess()) {
                    Timber.i("FenceRepo for headphones in registered");
                } else {
                    Timber.i("FenceRepo for headphones in NOT registered");
                }
            });
        }
    }

    private void turnOffFence(){

        if( mComboParam.getRunning() ){
            mFenceRepo.setAwarenessFence(null);
            mComboParam.setRunning(false);
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

    /**
     * As the code shows we get the current status of the fence.
     */
    @Click(R.id.fenceStatusBtn)
    void showFenceQueries(){

        Awareness.FenceApi.queryFences(mConnection.getAwarenessClient(),
                FenceQueryRequest.forFences(Collections.singletonList(OutAndAboutReceiver.FENCE_KEY)))
                .setResultCallback(fenceQueryResult -> {

                    if (!fenceQueryResult.getStatus().isSuccess()) {
                        mMessageText.setText( "Could not query fence: " +  OutAndAboutReceiver.FENCE_KEY );
                        return;
                    }
                    FenceStateMap map = fenceQueryResult.getFenceStateMap();
                    for (String fenceKey : map.getFenceKeys()) {
                        FenceState fenceState = map.getFenceState(fenceKey);

                        mMessageText.append( "FenceRepo " + fenceKey + " is= "
                                + describeStatus(fenceState.getCurrentState())
                                + ", was="
                                + describeStatus(fenceState.getPreviousState())
                                + ", lastUpdateTime="
                                + new Date(fenceState.getLastFenceUpdateTimeMillis()));
                    }
                });

    }

    /**
     * @see //developers.google.com/android/reference/com/google/android/gms/awareness/fence/FenceState
     * @param state
     * @return
     */
    private String describeStatus( int state ){
        if( state == FenceState.TRUE )
            return "ON";
        else if( state == FenceState.FALSE )
            return "OFF";
        else
            return "UNKNOWN";
    }

    //<editor-fold desc="Preferences">
    private void saveChangesToPreferences(){
        ComboFenceUtils.toPreferences( mAwarenessPref, mComboParam);
    }

    private void syncFromPreferences() {
        if( !mComboParam.getXfer() ){
            ComboFenceUtils.toComboFence(mComboParam, mAwarenessPref);
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
