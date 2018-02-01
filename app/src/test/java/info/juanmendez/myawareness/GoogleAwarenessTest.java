package info.juanmendez.myawareness;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;

import info.juanmendez.myawareness.models.ComboParam;
import info.juanmendez.myawareness.models.HeadphoneParam;
import info.juanmendez.myawareness.models.LocationParam;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GoogleAwarenessTest {

    /**
     * lets place ComboParam in a json
     * @throws Exception
     */
    @Test
    public void testJson() throws Exception {
        ComboParam comboParam = new ComboParam();
        LocationParam locationParam = new LocationParam();
        locationParam.setMeters( 10 );
        locationParam.setLat( 2);
        locationParam.setLon( 2 );
        comboParam.setLocationParam( locationParam );

        HeadphoneParam headphoneParam = new HeadphoneParam(true);
        comboParam.setHeadphoneParam( headphoneParam );

        String json = new Gson().toJson( comboParam );
        Assert.assertFalse( json.isEmpty() );

        comboParam = new Gson().fromJson( json, ComboParam.class );
        Assert.assertEquals( comboParam.getLocationParam().getMeters(), 10 );


        comboParam = new Gson().fromJson( "", ComboParam.class );
        Assert.assertNull( comboParam );
    }
}