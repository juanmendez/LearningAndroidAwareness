package info.juanmendez.myawareness.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;
import info.juanmendez.myawareness.dependencies.SnackMePlease;


/**
 * Created by Juan Mendez on 9/9/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EFragment(R.layout.fragment_snapshot)
public class LocationSnapshotFragment extends Fragment {

    @Bean
    AwarenessConnection connection;

    @ViewById
    TextView snapshotTextMessage;

    @InstanceState
    String mMessage;

    @Bean
    SnackMePlease snackMePlease;
    RxPermissions rxPermissions;

    @Override
    public void onResume() {
        super.onResume();

        rxPermissions  = new RxPermissions(getActivity());
        lastMessage();
        FragmentUtils.setHomeEnabled( this, true );
        connection.connect();
    }

    @Override
    public void onPause(){
        super.onPause();
        FragmentUtils.setHomeEnabled( this, false );
        connection.disconnect();

    }

    @Click(R.id.snapshotBtn)
    public void getSnapshot(){

        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        getLocationSnapshot();
                    } else {
                        snackMePlease.showMessage( "ACCESS_FINE_LOCATION was denied");
                    }
                });
    }

    @SuppressLint("MissingPermission") //RxPermissions takes care
    private void getLocationSnapshot(){
        Awareness.SnapshotApi.getLocation( connection.getClient() ).setResultCallback(locationStateResult -> {
            if( !locationStateResult.getStatus().isSuccess() )
                return;

            Location location = locationStateResult.getLocation();
            writeMessage( String.format("You are @ (%s,%s)", location.getLatitude(), location.getLongitude() ));
        });
    }

    private void lastMessage(){
        if( mMessage!= null )
            writeMessage( mMessage );
    }

    private void writeMessage( String message ){
        snapshotTextMessage.setText( mMessage =message );
    }
}
