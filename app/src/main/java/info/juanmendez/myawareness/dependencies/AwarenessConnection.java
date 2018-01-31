package info.juanmendez.myawareness.dependencies;

import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import info.juanmendez.myawareness.events.ShortResponse;

/**
 * Created by Juan Mendez on 9/9/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean
public class AwarenessConnection {
    @Bean
    AwarenessClient mAwarenessClient;

    private GoogleApiClient.OnConnectionFailedListener mFailedListener;
    private GoogleApiClient mGoogleApiClient;

    @AfterInject
    void afterInject(){
        mGoogleApiClient = mAwarenessClient.getClient();
    }

    public void connect(@NonNull ShortResponse<ConnectionResult> failedResponse){
        disconnect();

        mGoogleApiClient.registerConnectionFailedListener( mFailedListener = failedResponse::onResult);
        connect();
    }

    public void connect(){
        if( !mGoogleApiClient.isConnected() ){
            mGoogleApiClient.connect();
            mFailedListener = null;
        }
    }

    public void disconnect(){
        if( mGoogleApiClient.isConnected() ){
            mGoogleApiClient.disconnect();

            if( mFailedListener != null ){
                mGoogleApiClient.unregisterConnectionFailedListener( mFailedListener );
                mFailedListener = null;
            }
        }
    }

    public GoogleApiClient getAwarenessClient(){
        return mGoogleApiClient;
    }

    public boolean isConnected(){
        return mGoogleApiClient.isConnected();
    }
}