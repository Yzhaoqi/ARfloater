package yzq.com.arfloater.server;

import android.util.Log;

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
    private static final String SERVER_HOST = "http://192.168.1.108:3000/floater";

    private FloaterServer(){}
    private static FloaterServer single = null;
    public static FloaterServer getInstance() {
        if (single == null) {
            single = new FloaterServer();
        }
        return single;
    }

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
        Floater floater = null;
        try {
            result = connectToURL(new URL(SERVER_HOST + "/getFloater"), mes);
            if ("".equals(result) || "Floater not Found".equals(result)) {
                Log.i("Floater", "not Found");
                return null;
            } else {
                Log.i("Floater", result);
                JSONObject obj = new JSONObject(result);
                Object lwobj = obj.get("leaveWord");

                JSONObject leaveWord = new JSONObject(lwobj.toString());
                floater = new Floater();
                floater.setLatitude(obj.getDouble("latitude"));
                floater.setLongitude(obj.getDouble("longitude"));
                floater.setTitle(obj.getString("title"));
                floater.setText(obj.getString("text"));
                int i = 0;
                while (leaveWord.has(String.valueOf(i))) {
                    floater.addLeaveWord(leaveWord.getString(String.valueOf(i)));
                    i++;
                }
                return floater;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean submit(Floater floater) {
        return submitFloater(floater, SERVER_HOST + "/submitFloater");
    }

    public boolean submitLeaveWords(Floater floater) {
        return submitFloater(floater, SERVER_HOST + "/submitLeaveWords");
    }

    private boolean submitFloater(Floater floater, String url) {
        String mes, result;
        JSONObject object = new JSONObject(), leaveWordsObj = new JSONObject();
        try {
            object.put("latitude", floater.getLatitude());
            object.put("longitude", floater.getLongitude());
            object.put("title", floater.getTitle());
            object.put("text", floater.getText());
            for (int i = 0; i < floater.getLeaveWords().size(); i++) {
                leaveWordsObj.put(String.valueOf(i), floater.getLeaveWords().get(i));
            }
            object.put("leaveWord", leaveWordsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mes = object.toString();
        try {
            result = connectToURL(new URL(url), mes);
            return "Success".equals(result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
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
