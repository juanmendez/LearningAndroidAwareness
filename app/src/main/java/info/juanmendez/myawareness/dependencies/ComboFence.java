package info.juanmendez.myawareness.dependencies;

import org.androidannotations.annotations.EBean;

/**
 * Created by Juan Mendez on 9/10/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */
@EBean(scope= EBean.Scope.Singleton)
public class ComboFence {
    private boolean location;
    private boolean headphones;

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
}
