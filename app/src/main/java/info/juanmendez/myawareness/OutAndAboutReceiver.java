package info.juanmendez.myawareness;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.google.android.gms.awareness.fence.FenceState;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;

import info.juanmendez.myawareness.dependencies.ComboFence;
import info.juanmendez.myawareness.ui.MainActivity_;
import info.juanmendez.myawareness.utils.ComboFenceUtils;


/**
 * Created by Juan Mendez on 9/9/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EReceiver
public class OutAndAboutReceiver extends WakefulBroadcastReceiver {
    @SystemService
    NotificationManager notificationManager;

    public static final String FENCE_INTENT_FILTER = "info.juanmendez.myawareness.BACK_COMBO_RECEIVER_ACTION";
    public static final String FENCE_KEY = "BackComboFenceKey";
    public static final int NOTIFICATION_KEY = 86038603;

    @Bean
    ComboFence comboFence;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if( TextUtils.equals( action, FENCE_INTENT_FILTER) ){
            notify( context, intent );
        }else if( TextUtils.equals( action, Intent.ACTION_REBOOT) ){

            /*ComboFenceUtils.toComboFence( comboFence, PreferenceManager.getDefaultSharedPreferences(context));
            ComboFenceUtils.generateFence( comboFence );

            PendingIntent fenceIntent = PendingIntent.getBroadcast(rootContext, 0, new Intent(FENCE_INTENT_FILTER), 0);

            Awareness.FenceApi.updateFences(connection.getClient(), new FenceUpdateRequest.Builder()
                    .addFence(OutAndAboutReceiver.FENCE_KEY, comboFence.getFence(), fenceIntent)
                    .build());*/
        }
    }

    private void notify( Context context, Intent intent ) {

        FenceState fenceState = FenceState.extract(intent);
        if( !TextUtils.equals(fenceState.getFenceKey(), FENCE_KEY)) return;

        ComboFenceUtils.toComboFence( comboFence, PreferenceManager.getDefaultSharedPreferences(context));

        ComboFenceUtils.toPreferences(PreferenceManager.getDefaultSharedPreferences(context), comboFence );

        String message = "";
        switch (fenceState.getCurrentState()) {
            case FenceState.TRUE:
                comboFence.setRunning( true );
                message = "TRUE!";
                break;
            case FenceState.FALSE:
                comboFence.setRunning( false );
                message = "FALSE!";
                break;
            default:
            case FenceState.UNKNOWN:
                comboFence.setRunning( false );
                message = "UNKNOWN!";
                break;
        }

        String content = String.format( "result(%s), headphones(%s), location(%s)",
                message,
                comboFence.isHeadphones()?"yes":"no",
                comboFence.isLocation()?"yes meters(" + comboFence.getMeters() + ")":"no");


        intent = new Intent(context, MainActivity_.class);
        intent.putExtra( FENCE_KEY, true );

        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID, intent, flags);


        Notification notification = new NotificationCompat.Builder(context)
                .setContentText("Back Combo Fence")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText( content )
                .setContentInfo( content )
                .setContentIntent( pendingIntent )
                .build();

        notificationManager.notify( NOTIFICATION_KEY,  notification );
    }
}