package yzq.com.arfloater.been;

import java.util.ArrayList;

/**
 * Created by YZQ on 2018/1/16.
 */

public class Floater {
    private String title;
    // private Location location;
    private String text;
    private ArrayList<String> leaveWords;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addLeaveWord(String leaveWord) {
        if (leaveWords == null) {
            leaveWords = new ArrayList<String>();
        }
        leaveWords.add(leaveWord);
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
}
