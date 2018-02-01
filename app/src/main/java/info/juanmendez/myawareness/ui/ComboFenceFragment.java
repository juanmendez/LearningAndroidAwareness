package info.juanmendez.myawareness.ui;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.state.HeadphoneState;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;
import info.juanmendez.myawareness.dependencies.FenceRepo;
import info.juanmendez.myawareness.models.ComboParam;
import info.juanmendez.myawareness.dependencies.LocationSnapshotService;
import info.juanmendez.myawareness.dependencies.SnackMePlease;
import info.juanmendez.myawareness.events.Response;
import info.juanmendez.myawareness.events.ShortResponse;
import info.juanmendez.myawareness.models.HeadphoneParam;
import info.juanmendez.myawareness.models.LocationParam;
import info.juanmendez.myawareness.utils.ComboFenceUtils;
import timber.log.Timber;


/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 *
 * (Similar to HeadphoneFenceFragment but combines fences with AND, OR, NOT clauses)
 * User can include a radius distance to make a awarenessFence based mustPlugIn current location
 * User has also the option to include along with distance, if headphones should be plugged in. It's kind of silly.
 */
@EFragment(R.layout.fragment_combo_fence)
public class ComboFenceFragment extends Fragment {

    public static final String FENCE_INTENT_FILTER = "COMBO_RECEIVER_ACTION";
    public static final String FENCE_KEY = "MyComboFenceKey";

    @Bean
    AwarenessConnection mConnection;

    @ViewById(R.id.comboFence_checkHeadphones)
    CheckBox mCheckboxHeadphones;

    @ViewById(R.id.comboFence_checkLocation)
    CheckBox mCheckboxLocation;

    @ViewById(R.id.comboFence_meterText)
    EditText mMeterText;

    @ViewById(R.id.comboFence_messageText)
    TextView mTextMessage;

    @ViewById(R.id.comboFence_toggleButton)
    ToggleButton mToggleButton;

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
            mComboParam.getLocationParam().setTurnedOn( isChecked );
            showMeterText( isChecked );
        }

        @CheckedChange(R.id.comboFence_checkHeadphones)
        void onCheckedHeadphones( boolean isChecked ){
            mComboParam.getHeadphoneParam().setTurnedOn( isChecked );
        }

        @AfterTextChange(R.id.comboFence_meterText)
        void onMeterText(Editable meters){

            String strMeters = meters.toString();

            if( strMeters.isEmpty() )
                strMeters = "0";

            if( mComboParam.getLocationParam().isTurnedOn() ){
                mComboParam.getLocationParam().setMeters( Integer.parseInt(strMeters) );
            }
        }

        @CheckedChange(R.id.comboFence_toggleButton)
        void onToggleButton( boolean isChecked ){

            String errorMessage = ComboFenceUtils.areThereErrors( mFenceRepo );
            if( isChecked && errorMessage.isEmpty() ){
                buildUpFences();

            }else{
                mFenceRepo.setAwarenessFence(null);
                mToggleButton.setChecked(false);
                mSnackmePlease.e( errorMessage );
            }
        }
    //</editor-fold>

    void updateView(){

        HeadphoneParam headphoneParam = mComboParam.getHeadphoneParam();
        LocationParam locationParam = mComboParam.getLocationParam();

        mCheckboxLocation.setChecked( locationParam.isTurnedOn());
        mCheckboxHeadphones.setChecked( headphoneParam.isTurnedOn());

        int meters = 0;

        if( locationParam.isTurnedOn() ){
            meters = mComboParam.getLocationParam().getMeters();
        }

        mMeterText.setText( String.valueOf(meters) );
        showMeterText( locationParam.isTurnedOn() );
    }


    void showMeterText(Boolean show ){

        //reset if mMeterText is not displayed
        if( !show ){

            if( mComboParam.getLocationParam().isTurnedOn() ){
                mComboParam.getLocationParam().setMeters(0);
            }

            mMeterText.setText("");
        }

        mMeterText.setVisibility( show? View.VISIBLE:View.GONE );
    }


    private void buildUpFences(){
        List<AwarenessFence> fences = new ArrayList<>();

        ShortResponse<AwarenessFence> snapshotResponse = fence -> {
            fences.add( fence );

            if( fences.size() == ComboFenceUtils.getFencesTotal( mComboParam )){
                AwarenessFence awarenessFence;

                if( fences.size() == 1 )
                    awarenessFence = fences.get(0);
                else
                    awarenessFence = AwarenessFence.and( fences );

                mFenceRepo.setAwarenessFence( awarenessFence );

                turnOnFence();
            }
        };

        if( mComboParam.getLocationParam().isTurnedOn() ){

            LocationParam locationParam = mComboParam.getLocationParam();

            //getSnapshot ensures to have permission granted, so we can suppress it during onResult
            LocationSnapshotService.build(getActivity(), mConnection).getSnapshot(new Response<Location>() {

                @SuppressLint("MissingPermission")
                @Override
                public void onResult(Location location) {
                    snapshotResponse.onResult( AwarenessFence.not(LocationFence.in( location.getLatitude(), location.getLongitude(), locationParam.getMeters(), locationParam.getMeters() )) );
                }

                @Override
                public void onError(Exception exception) {
                    mSnackmePlease.e( exception.getMessage() );
                }
            });
        }

        if( mComboParam.getHeadphoneParam().isTurnedOn() ){
            snapshotResponse.onResult(HeadphoneFence.during(HeadphoneState.PLUGGED_IN));
        }
    }

    //runs fence only if it is available.
    private void turnOnFence() {

        if( mFenceRepo.getAwarenessFence() != null ){
            PendingIntent fenceIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(FENCE_INTENT_FILTER), 0);

            Awareness.FenceApi.updateFences(mConnection.getAwarenessClient(), new FenceUpdateRequest.Builder()
                    .addFence(FENCE_KEY, mFenceRepo.getAwarenessFence(), fenceIntent)
                    .build()).setResultCallback(status -> {
                if (status.isSuccess()) {
                    Timber.i("FenceRepo for headphones in registered");
                } else {
                    Timber.i("FenceRepo for headphones in NOT registered");
                }
            });
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        FragmentUtils.setHomeEnabled( this, false );
        mConnection.disconnect();
    }

    @Receiver(actions = FENCE_INTENT_FILTER )
    public void onComboReceiver(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);
        if (TextUtils.equals(fenceState.getFenceKey(), FENCE_KEY)) {

            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    mSnackmePlease.e( "TRUE!");
                    break;
                case FenceState.FALSE:
                    mSnackmePlease.e( "FALSE!");
                    break;
                default:
                case FenceState.UNKNOWN:
                    mSnackmePlease.i( "UNKNOWN!");
                    break;
            }
        }
    }
}