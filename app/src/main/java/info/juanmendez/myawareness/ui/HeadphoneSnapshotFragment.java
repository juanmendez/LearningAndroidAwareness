package info.juanmendez.myawareness.ui;

import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.google.android.gms.awareness.Awareness;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import info.juanmendez.myawareness.FragmentUtils;
import info.juanmendez.myawareness.R;
import info.juanmendez.myawareness.dependencies.AwarenessConnection;


/**
 * Created by Juan Mendez on 9/9/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EFragment(R.layout.fragment_snapshot)
public class HeadphoneSnapshotFragment extends Fragment {

    @Bean
    AwarenessConnection connection;

    @ViewById
    TextView snapshotTextMessage;

    @InstanceState
    String mMessage;

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
        Awareness.SnapshotApi.getHeadphoneState( connection.getClient() ).setResultCallback(headphoneStateResult -> {
            if( !headphoneStateResult.getStatus().isSuccess() )
                return;
            writeMessage( headphoneStateResult.getHeadphoneState().toString() );
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
