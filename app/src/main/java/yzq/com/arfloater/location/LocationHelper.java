package yzq.com.arfloater.location;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by YZQ on 2018/1/16.
 */

public class LocationHelper {
    private LocationManager locationManager;
    private BDLocation mLocation;
    private Context context;

    public LocationHelper(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
    }

    private LocationClient mLocationClient = null;
    private BDAbstractLocationListener myListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mLocation = bdLocation;
            Log.i("location", bdLocation.getLocTypeDescription());
        }
    };

    public void registerListener() {
        mLocationClient = new LocationClient(context.getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(false);
        option.setScanSpan(0);//设置发起定位请求的间隔时间为5000ms
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
    }

    public BDLocation getLocation() {
        return mLocation;
    }
}
