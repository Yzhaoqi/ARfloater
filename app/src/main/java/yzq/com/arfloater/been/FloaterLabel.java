package yzq.com.arfloater.been;

import android.location.Location;

import java.text.DecimalFormat;

/**
 * Created by YZQ on 2018/1/16.
 */

public class FloaterLabel {
    private double longitude;
    private double latitude;
    private String title;
    private static DecimalFormat df = new DecimalFormat("#.##");

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLatitude(double latitude) {
        this.latitude = Double.parseDouble(df.format(latitude));
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = Double.parseDouble(df.format(longitude));
    }

    public double getLongitude() {
        return longitude;
    }
}
