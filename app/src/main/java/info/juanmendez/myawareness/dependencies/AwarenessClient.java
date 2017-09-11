package info.juanmendez.myawareness.dependencies;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;

import info.juanmendez.myawareness.AwarenessApp;

/**
 * Created by Juan Mendez on 9/8/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean(scope= EBean.Scope.Singleton)
public class AwarenessClient {
    @App
    AwarenessApp app;

    private GoogleApiClient client;

    @AfterInject
    public void afterInject(){
        client = new GoogleApiClient.Builder(app)
                .addApi(Awareness.API)
                .build();
    }

    public GoogleApiClient getClient() {
        return client;
    }
}