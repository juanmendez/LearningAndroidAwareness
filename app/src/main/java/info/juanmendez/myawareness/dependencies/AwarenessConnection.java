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
    AwarenessClient awarenessClient;

    private GoogleApiClient.OnConnectionFailedListener mFailedListener;
    private GoogleApiClient client;

    @AfterInject
    public void afterInject(){
        client = awarenessClient.getClient();
    }

    public void connect(@NonNull ShortResponse<ConnectionResult> response){
        disconnect();

        client.registerConnectionFailedListener( mFailedListener = connectionResult ->{response.onResult(connectionResult);});
        connect();
    }

    public void connect(){
        if( !client.isConnected() ){
            client.connect();
            mFailedListener = null;
        }
    }

    public void disconnect(){
        if( client.isConnected() ){
            client.disconnect();

            if( mFailedListener != null ){
                client.unregisterConnectionFailedListener( mFailedListener );
                mFailedListener = null;
            }
        }
    }

    public GoogleApiClient getClient(){
        return client;
    }
}
