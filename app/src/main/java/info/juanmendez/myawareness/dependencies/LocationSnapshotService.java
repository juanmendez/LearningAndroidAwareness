package info.juanmendez.myawareness.dependencies;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;

import com.google.android.gms.awareness.Awareness;
import com.tbruyelle.rxpermissions2.RxPermissions;

import info.juanmendez.myawareness.events.Response;

/**
 * Created by Juan Mendez on 9/11/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
public class LocationSnapshotService {

    RxPermissions rxPermissions;
    AwarenessConnection connection;

    public LocationSnapshotService(Activity activity, AwarenessConnection connection ){
        this.connection = connection;
        rxPermissions  = new RxPermissions(activity);
    }

    @SuppressLint("MissingPermission")
    public void getSnapshot(Response<Location> response){

        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        Awareness.SnapshotApi.getLocation( connection.getClient() ).setResultCallback(locationStateResult -> {
                            if( !locationStateResult.getStatus().isSuccess() ){
                                response.onError( new Exception("Snapshot failed getting locationsnapshot"));
                                return;
                            }

                            response.onResult( locationStateResult.getLocation() );
                        });
                    } else {
                        response.onError( new Exception("Permission wasn't granted"));
                    }
                });
    }
}
