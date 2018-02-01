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
 *
 * shows how to get a snapshot https://developers.google.com/awareness/android-api/snapshot-api-overview if headphones are plugged or not
 */
@EFragment(R.layout.fragment_snapshot)
public class HeadphoneSnapshotFragment extends Fragment {

    @Bean
    AwarenessConnection mConnection;

    @ViewById(R.id.snapshotTextMessage)
    TextView mTextMessage;

    @InstanceState
    String mMessage;

    @Override
    public void onResume() {
        super.onResume();

        lastMessage();
        FragmentUtils.setHomeEnabled( this, true );
        mConnection.connect();
    }

    @Override
    public void onPause(){
        super.onPause();
        FragmentUtils.setHomeEnabled( this, false );
        mConnection.disconnect();
    }

    @Click(R.id.snapshotBtn)
    public void getSnapshot(){
        Awareness.SnapshotApi.getHeadphoneState( mConnection.getAwarenessClient() ).setResultCallback(headphoneStateResult -> {
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
        mTextMessage.setText( mMessage =message );
    }
}
