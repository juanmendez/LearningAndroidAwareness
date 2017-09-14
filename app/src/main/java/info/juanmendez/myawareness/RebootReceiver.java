package info.juanmendez.myawareness;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EReceiver;

import info.juanmendez.myawareness.dependencies.FenceService;


/**
 * Created by Juan Mendez on 9/13/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EReceiver
public class RebootReceiver extends WakefulBroadcastReceiver {

    @Bean
    FenceService fenceService;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if( action != null && (action.equals( Intent.ACTION_REBOOT ) || action.equals( Intent.ACTION_BOOT_COMPLETED )) ){
            fenceService.rebootFences();
        }
    }
}