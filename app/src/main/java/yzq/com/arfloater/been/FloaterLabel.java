package yzq.com.arfloater.been;

import android.location.Location;

/**
 * Created by YZQ on 2018/1/16.
 */

public class FloaterLabel {
    private double longitude;
    private double latitude;
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
