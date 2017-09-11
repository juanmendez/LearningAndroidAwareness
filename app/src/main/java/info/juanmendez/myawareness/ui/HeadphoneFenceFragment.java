package info.juanmendez.myawareness.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import java.util.Date;

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
    AwarenessConnection connection;

    @Bean
    SnackMePlease snackMePlease;

    @ViewById
    TextView headphoneFenceTextMessage;

    @InstanceState
    String mMessage;

    private static final String FENCE_INTENT_FILTER = "FENCE_RECEIVER_ACTION";
    private static final String FENCE_KEY = "MyHeadphoneFenceKey";

    @Override
    public void onResume(){
        super.onResume();
        FragmentUtils.setHomeEnabled( this, true );

        lastMessage();
        connection.connect();
        turnOnFence();
    }

    @Override
    public void onPause(){
        super.onPause();
        FragmentUtils.setHomeEnabled( this, false );
        turnOffFence();
    }

    /**
     * Awareness requires us to instruct our petition to listen to headphone activity.
     * Therefore we create a dynamic receiver in which we include a pending intent
     * which Awareness will later use to ping our receiver. Our receiver is also filtering
     * based on what's on the pendingIntent. Because there can be more than one fence with the same
     * parameters, we pass a key to identify our fence and that's going to be detected in the new receiver
     */
    private void turnOnFence() {

        AwarenessFence fencePluggedIn = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
        PendingIntent fenceIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent(FENCE_INTENT_FILTER), 0);

        Awareness.FenceApi.updateFences(connection.getClient(), new FenceUpdateRequest.Builder()
                .addFence(FENCE_KEY, fencePluggedIn, fenceIntent)
                .build()).setResultCallback(status -> {
            if (status.isSuccess()) {
                snackMePlease.i("Fence for headphones in registered");
            } else {
                snackMePlease.i("Fence for headphones in NOT registered");
            }
        });

        Awareness.FenceApi.queryFences(connection.getClient(),
                FenceQueryRequest.forFences(FENCE_KEY))
                .setResultCallback(fenceQueryResult -> {
                    if (!fenceQueryResult.getStatus().isSuccess()) {
                        writeMessage("Could not query fences: ");
                        return;
                    }
                    FenceStateMap map = fenceQueryResult.getFenceStateMap();
                    for (String fenceKey : map.getFenceKeys()) {
                        FenceState fenceState = map.getFenceState(fenceKey);
                        writeMessage("Fence " + fenceKey + ": "
                                + fenceState.getCurrentState()
                                + ", was="
                                + fenceState.getPreviousState()
                                + ", lastUpdateTime="
                                + new java.text.SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(
                                new Date(fenceState.getLastFenceUpdateTimeMillis())));
                    }
                });
    }

    private void turnOffFence() {
        connection.disconnect();
    }

    private void lastMessage(){
        if( mMessage!= null )
            writeMessage( mMessage );
        else{
            writeMessage( "Plug or unplug your headphones. Fragment only handles fence changes.");
        }
    }

    private void writeMessage( String message ){
        headphoneFenceTextMessage.setText( mMessage=message );
    }

    @Receiver(actions = FENCE_INTENT_FILTER )
    public void onBroadcastReceiver(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);
        if (TextUtils.equals(fenceState.getFenceKey(), FENCE_KEY)) {

            snackMePlease.i( "Receiver has being pinge");
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
}
