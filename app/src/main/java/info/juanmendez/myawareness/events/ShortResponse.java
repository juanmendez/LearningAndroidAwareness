package info.juanmendez.myawareness.events;

/**
 * Created by Juan Mendez on 8/22/2017.
 * www.juanmendez.info
 * contact@juanmendez.info
 */

public interface ShortResponse<T> {
    void onResult(T result);
}
