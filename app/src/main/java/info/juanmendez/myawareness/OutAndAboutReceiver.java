package info.juanmendez.myawareness;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
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
public class OutAndAboutReceiver extends BroadcastReceiver {
    @SystemService
    NotificationManager notificationManager;

    public static final String FENCE_INTENT_FILTER = "info.juanmendez.myawareness.BACK_COMBO_RECEIVER_ACTION";
    public static final String FENCE_KEY = "BackComboFenceKey";
    public static final int NOTIFICATION_KEY = 86038603;

    @Bean
    ComboFence comboFence;

    @Override
    public void onReceive(Context context, Intent intent) {

        ComboFenceUtils.toComboFence( comboFence, PreferenceManager.getDefaultSharedPreferences(context));

        FenceState fenceState = FenceState.extract(intent);

        if(TextUtils.equals(fenceState.getFenceKey(), FENCE_KEY)) {
            ComboFenceUtils.toPreferences(PreferenceManager.getDefaultSharedPreferences(context), comboFence );

            String message = "";
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    comboFence.setRunning( true );
                    notify( context,"TRUE!");
                    break;
                case FenceState.FALSE:
                    comboFence.setRunning( false );
                    notify( context,"FALSE!");
                    break;
                default:
                case FenceState.UNKNOWN:
                    comboFence.setRunning( false );
                    notify( context, "UNKNOWN!");
                    break;
            }
        }
    }

    private void notify( Context context, String message) {

        String content = String.format( "result(%s), headphones(%s), location(%s)",
                                        message,
                                        comboFence.isHeadphones()?"yes":"no",
                                        comboFence.isLocation()?"yes meters(" + comboFence.getMeters() + ")":"no");


        Intent intent = new Intent(context, MainActivity_.class);
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