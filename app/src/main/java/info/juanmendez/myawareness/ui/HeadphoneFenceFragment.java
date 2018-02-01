package info.juanmendez.myawareness.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;
import info.juanmendez.myawareness.dependencies.SnackMePlease;

/**
 * Created by Juan Mendez on 9/9/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EFragment(R.layout.fragment_headphone_fence)
public class HeadphoneFenceFragment extends Fragment{
    @Bean
    AwarenessConnection mConnection;

    @Bean
    SnackMePlease mSnackmePlease;

    @ViewById(R.id.headphoneFenceTextMessage)
    TextView mTextMessage;

    @InstanceState
    String mMessage;

    public static final String FENCE_INTENT_FILTER = "FENCE_HEADPHONE_ACTION";
    public static final String FENCE_KEY = "MyHeadphoneFenceKey";

    @Override
    public void onResume(){
        super.onResume();

        lastMessage();
        mConnection.connect();
        turnOnFence();
        FragmentUtils.setHomeEnabled( this, true );
    }

    @Override
    public void onPause(){
        super.onPause();
        turnOffFence();
        FragmentUtils.setHomeEnabled( this, false );
    }

    /**
     * Awareness requires us to instruct our petition to listen to headphone activity.
     * Therefore we create a dynamic receiver in which we include a pending intent
     * which Awareness will later use to ping our receiver. Our receiver is also filtering
     * based on what's on the pendingIntent. Because there can be more than one fence with the same
     * parameters, we pass a NOTIFICATION_KEY to identify our fence and that's going to be detected in the new receiver
     */
    private void turnOnFence() {

        AwarenessFence fencePluggedIn = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
        PendingIntent fenceIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(FENCE_INTENT_FILTER), 0);

        Awareness.FenceApi.updateFences(mConnection.getAwarenessClient(), new FenceUpdateRequest.Builder()
                .addFence(FENCE_KEY, fencePluggedIn, fenceIntent)
                .build()).setResultCallback(status -> {
            if (status.isSuccess()) {
                mSnackmePlease.i("FenceRepo for headphones in registered");
            } else {
                mSnackmePlease.i("FenceRepo for headphones in NOT registered");
            }
        });
    }

    private void turnOffFence() {
        mConnection.disconnect();
    }

    private void lastMessage(){
        if( mMessage!= null )
            writeMessage( mMessage );
        else{
            writeMessage( "Plug or unplug your headphones. Fragment only handles fence changes.");
        }
    }

    private void writeMessage( String message ){
        mTextMessage.setText( mMessage=message );
    }

    @Receiver(actions = FENCE_INTENT_FILTER )
    public void onHeadphonesReceiver(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);
        if (TextUtils.equals(fenceState.getFenceKey(), FENCE_KEY)) {

            mSnackmePlease.i( "Receiver has being pinged");
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    writeMessage( "Headphones are plugged");
                    break;
                case FenceState.FALSE:
                    writeMessage( "Headphones are unplugged");
                    break;
                default:
                case FenceState.UNKNOWN:
                    writeMessage( "Headphones are unknown");
                    break;
            }
        }
    }

    /*
    FIXED: Receiver Issue, I simply commented this out.
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }*/
}
