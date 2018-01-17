package yzq.com.arfloater.been;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by YZQ on 2018/1/16.
 */

public class Floater {
    private String title;
    private double longitude, latitude;
    private String text;
    private ArrayList<String> leaveWords;
    private static DecimalFormat df = new DecimalFormat("#.##");

    public Floater(){
        leaveWords = new ArrayList<String>();
    }

    public Floater(FloaterLabel floaterLabel) {
        title = floaterLabel.getTitle();
        latitude = floaterLabel.getLatitude();
        longitude = floaterLabel.getLongitude();
        leaveWords = new ArrayList<String>();
    }

    public void setLongitude(double longitude) {
        this.longitude = Double.parseDouble(df.format(longitude));
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = Double.parseDouble(df.format(latitude));
    }

    public double getLatitude() {
        return latitude;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getLeaveWords() {
        return leaveWords;
    }

    public void addLeaveWord(String w) {
        leaveWords.add(w);
    }
}
