package yzq.com.arfloater.server;

import yzq.com.arfloater.been.Floater;
import yzq.com.arfloater.been.FloaterLabel;

/**
 * Created by YZQ on 2018/1/15.
 */

public class FloaterServer {
    private FloaterServer(){}
    private static FloaterServer single = null;
    public static FloaterServer getInstance() {
        if (single == null) {
            single = new FloaterServer();
        }
        return single;
    }

    public Floater getFloater(FloaterLabel floaterLabel) {
        return null;
    }

    public boolean submit(Floater floater) {
        return true;
    }
}
