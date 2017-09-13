package info.juanmendez.myawareness.dependencies;

import com.google.android.gms.awareness.fence.AwarenessFence;

import org.androidannotations.annotations.EBean;

/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean
public class ComboFence {
    
    private boolean location;
    private boolean headphones;
    private int meters;
    private AwarenessFence fence;
    private String errorMessage="";
    private Boolean running = false;
    private Boolean xfer = false;

    //<editor-fold desc="Getter-Setter">
    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }

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

    public AwarenessFence getFence() {
        return fence;
    }

    public void setFence(AwarenessFence fence) {
        this.fence = fence;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Boolean getXfer() {
        return xfer;
    }

    public void setXfer(Boolean xfer) {
        this.xfer = xfer;
    }

    //</editor-fold>

    /**
     * test if we can start the fence or not
     * @return true if valid, else not valid
     */
    public boolean validate(){
        errorMessage = "";
        if( isHeadphones() || isLocation() ){

            if( isLocation() && getMeters() == 0 ){
                errorMessage = "Please enter distance!";
                return false;
            }

            return true;
        }else{
            errorMessage = "Please select at least one fence!";
            return false;
        }
    }

    public int getFencesNeeded() {
        return (location?1:0)+(headphones?1:0);
    }
}