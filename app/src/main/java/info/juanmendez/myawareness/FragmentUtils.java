package info.juanmendez.myawareness;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Juan Mendez on 8/1/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public class FragmentUtils {

    /**
     * display or hide home back button. works only in parent fragment.
     * @param topLevelFragment must be a top fragment
     * @param show visibility of home button
     */
    public static void setHomeEnabled(@NonNull Fragment topLevelFragment, Boolean show ){

        if( topLevelFragment.getActivity().getActionBar() != null ){
            topLevelFragment.getActivity().getActionBar().setDisplayShowHomeEnabled( show );
        }else if( topLevelFragment.getActivity() instanceof AppCompatActivity){
            AppCompatActivity appCompatActivity = (AppCompatActivity) topLevelFragment.getActivity();

            if( appCompatActivity.getSupportActionBar() != null ){
                appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled( show );
            }
        }
    }
}