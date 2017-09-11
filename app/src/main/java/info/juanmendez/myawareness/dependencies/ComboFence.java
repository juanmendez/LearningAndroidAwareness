package info.juanmendez.myawareness.dependencies;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean(scope= EBean.Scope.Singleton)
public class ComboFence {
    @Bean
    SnackMePlease snackMePlease;
    
    private boolean location;
    private boolean headphones;
    private int meters;

    public boolean isLocation() {
        return location;
    }

    public void setLocation(boolean location) {
        this.location = location;
    }

    public boolean isHeadphones() {
        return headphones;
    }

    public void setHeadphones(boolean headphones) {
        this.headphones = headphones;
    }

    public int getMeters() {
        return meters;
    }

    public void setMeters(int meters) {
        this.meters = meters;
    }

    /**
     * test if we can start the fence or not
     * for simplicity we are already showing error message with snackMePlease
     * @return true if valid, else not valid
     */
    public boolean validate(){
        if( isHeadphones() || isLocation() ){

            if( isLocation() && getMeters() == 0 ){
                snackMePlease.showMessage( "Please enter distance!");
                return false;
            }

            return true;
        }else{
            snackMePlease.showMessage( "Please select at least one fence!");
            return false;
        }
    }
}