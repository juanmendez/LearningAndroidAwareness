package info.juanmendez.myawareness;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;

import com.google.android.gms.awareness.fence.FenceState;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import info.juanmendez.myawareness.dependencies.AwarenessPref_;
import info.juanmendez.myawareness.dependencies.FenceRepo;
import info.juanmendez.myawareness.models.ComboParam;
import info.juanmendez.myawareness.models.HeadphoneParam;
import info.juanmendez.myawareness.models.LocationParam;
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
    NotificationManager mNotificationManager;

    public static final String FENCE_INTENT_FILTER = "info.juanmendez.myawareness.BACK_COMBO_RECEIVER_ACTION";
    public static final String FENCE_KEY = "BackComboFenceKey";
    public static final int NOTIFICATION_KEY = 86038603;

    @Bean
    FenceRepo mFenceRepo;

    @Pref
    AwarenessPref_ mAwarenessPref;

    ComboParam mComboParam;

    @AfterInject
    void afterInject(){
        mComboParam = mFenceRepo.getComboParam();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if( TextUtils.equals( action, FENCE_INTENT_FILTER) ){
            notify( context, intent );
        }
    }

    private void notify( Context context, Intent intent ) {

        FenceState fenceState = FenceState.extract(intent);
        if( !TextUtils.equals(fenceState.getFenceKey(), FENCE_KEY)) return;

        ComboFenceUtils.toComboFence(mComboParam, mAwarenessPref);

        ComboFenceUtils.toPreferences(mAwarenessPref, mComboParam);

        String message;

        switch (fenceState.getCurrentState()) {
            case FenceState.TRUE:
                mComboParam.setFenceRunning( true );
                message = "TRUE!";
                break;
            case FenceState.FALSE:
                mComboParam.setFenceRunning( false );
                message = "FALSE!";
                break;
            case FenceState.UNKNOWN:
                mComboParam.setFenceRunning( false );
                message = "UNKNOWN!";
                break;
            default: message = "";
        }


        HeadphoneParam headphoneParam = mComboParam.getHeadphoneParam();
        LocationParam locationParam = mComboParam.getLocationParam();
        int meters = 0;

        if( locationParam.isTurnedOn() ){
            meters = mComboParam.getLocationParam().getMeters();
        }

        String content = String.format( "result(%s), headphones(%s), location(%s)",
                message,
                headphoneParam.isTurnedOn()?"yes":"no",
                locationParam.isTurnedOn()?"yes meters(" + meters + ")":"no");


        intent = new Intent(context, MainActivity_.class);
        intent.putExtra( FENCE_KEY, true );

        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID, intent, flags);



        Notification notification = new Notification.Builder(context)
                .setContentText("Back Combo FenceRepo")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText( content )
                .setContentInfo( content )
                .setContentIntent( pendingIntent )
                .build();

        mNotificationManager.notify( NOTIFICATION_KEY,  notification );
    }
}