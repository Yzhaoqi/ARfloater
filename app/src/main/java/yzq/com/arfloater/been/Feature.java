package yzq.com.arfloater.been;

import java.text.DecimalFormat;

/**
 * Created by YZQ on 2018/3/27.
 */

public class Feature{
    private String title, hint;
    private double longitude, latitude;
    private static DecimalFormat df = new DecimalFormat("#.##");

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = Double.parseDouble(df.format(latitude));
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = Double.parseDouble(df.format(longitude));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

}
