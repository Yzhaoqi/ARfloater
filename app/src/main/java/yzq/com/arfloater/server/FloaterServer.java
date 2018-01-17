package yzq.com.arfloater.server;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    // TODO add Server
    public Floater getFloater(FloaterLabel floaterLabel) {
        String mes, result;
        JSONObject object = new JSONObject();
        try {
            object.put("latitude", floaterLabel.getLatitude());
            object.put("longitude", floaterLabel.getLongitude());
            object.put("title", floaterLabel.getTitle());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mes = object.toString();
        try {
            result = connectToURL(new URL("http://172.18.68.174:3000/data/getFloater"), mes);
            if (result == null || result == "Response Error") {
                return null;
            } else {
                Floater floater = new Floater();
                return floater;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean submit(Floater floater) {
        return true;
    }

    private String connectToURL(URL url, String requestContent) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            byte[] request = requestContent.getBytes("UTF-8");
            os.write(request, 0, request.length);
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return "Response Error";
            } else {
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                    }
                rd.close();
                return response.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
