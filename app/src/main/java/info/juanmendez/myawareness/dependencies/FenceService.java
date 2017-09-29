package info.juanmendez.myawareness.dependencies;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.state.HeadphoneState;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

import info.juanmendez.myawareness.OutAndAboutReceiver;
import info.juanmendez.myawareness.utils.ComboFenceUtils;
import timber.log.Timber;

/**
 * Created by Juan Mendez on 9/13/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 *
 * This class is part of RebootReceiver. The whole code could have been part of the receiver but for
 * testing purposes it is detached. I was able to run it directly from BackComboFenceFragment.
 * Then I tested the receiver by adding another action to it and started the broadcast from the same fragment.
 * Once I was able to test it, and see it worked. Then it confirmed it would work from reboot.
 */
@EBean
public class FenceService {
    @RootContext
    Context context;

    @Bean
    ComboFence comboFence;

    @Bean
    AwarenessConnection connection;

    public void rebootFences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        ComboFenceUtils.toComboFence(comboFence, preferences);

        if( comboFence.getRunning() ){
            if( !connection.isConnected() )
                connection.connect();

            inflateFence();
            startAwareness();
        }else{
            Timber.e( "ComboFence wasn't running before reboot" );
        }
    }

    private void inflateFence() {

        List<AwarenessFence> fences = new ArrayList<>();

        if (comboFence.isHeadphones()) {
            fences.add(HeadphoneFence.during(HeadphoneState.PLUGGED_IN));
        }

        if (comboFence.isLocation()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fences.add(AwarenessFence.not(LocationFence.in(comboFence.getLat(), comboFence.getLon(), comboFence.getMeters(), comboFence.getMeters())));
            }
        }

        if( !fences.isEmpty() ){
            comboFence.setFence( fences.size()>1?AwarenessFence.and( fences ):fences.get(0) );
        }else{
            Timber.i( "There are no fences available!" );
        }
    }

    private void startAwareness(){
        if( comboFence.getFence() != null ){
            PendingIntent fenceIntent = PendingIntent.getBroadcast(context, 0,
                    new Intent(OutAndAboutReceiver.FENCE_INTENT_FILTER), 0);

            Awareness.FenceApi.updateFences(connection.getClient(), new FenceUpdateRequest.Builder()
                    .addFence(OutAndAboutReceiver.FENCE_KEY, comboFence.getFence(), fenceIntent)
                    .build()).setResultCallback(status -> {
                if (status.isSuccess()) {
                    Timber.i("Service was able to start combo fence" );
                } else {
                    Timber.e("Service wasn't able to start combo fence");
                }
            });
        }
    }
}
