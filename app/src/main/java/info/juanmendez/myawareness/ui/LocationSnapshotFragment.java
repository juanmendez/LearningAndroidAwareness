package info.juanmendez.myawareness.ui;

import android.location.Location;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;
import info.juanmendez.myawareness.dependencies.LocationSnapshotService;
import info.juanmendez.myawareness.dependencies.SnackMePlease;
import info.juanmendez.myawareness.events.Response;


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

    @Override
    public void onResume() {
        super.onResume();

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

        LocationSnapshotService snapshotService = new LocationSnapshotService(getActivity(), connection );
        snapshotService.getSnapshot(new Response<Location>() {
            @Override
            public void onResult(Location location) {
                writeMessage( String.format("You are @ (%s,%s)", location.getLatitude(), location.getLongitude() ));
            }

            @Override
            public void onError(Exception exception) {
                writeMessage( exception.getMessage() );
            }
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
