package yzq.com.arfloater.message;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.List;

/**
 * Created by YZQ on 2018/1/16.
 */

public class LocationHelper {
    private LocationManager locationManager;
    private BDLocation mLocation;
    private Context context;

    private static LocationHelper locationHelper = null;

    public static LocationHelper getInstance(Context context) {
        if (locationHelper == null) {
            locationHelper = new LocationHelper(context);
        }
        return locationHelper;
    }

    private LocationHelper(Context context) {
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
