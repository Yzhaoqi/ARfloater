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
import java.util.ArrayList;
import java.util.List;

import yzq.com.arfloater.been.Feature;

/**
 * Created by YZQ on 2018/3/27.
 */

public class OrienteeringServer {
    private static final String SERVER_HOST = "http://123.207.18.112:3000/orienteering";
    public static final String ERROR = "SERVER_ERROR";

    private List<Feature> mFeatures;

    private OrienteeringServer(){}
    private static OrienteeringServer single = null;
    public static OrienteeringServer getInstance() {
        if (single == null) {
            single = new OrienteeringServer();
        }
        return single;
    }

    public String getId() {
        try {
            return connectToURL(new URL(SERVER_HOST + "/getId"), "GET", "");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public boolean submitActivity(List<Feature> mFeatureArray, String id, String pwd) {
        String mes, result;
        JSONObject activity = new JSONObject(), data = new JSONObject();
        try {
            activity.put("activity_id", id);
            activity.put("password", pwd);
            for (int i = 0; i < mFeatureArray.size(); i++) {
                JSONObject in = new JSONObject();
                Feature f = mFeatureArray.get(i);
                in.put("title", f.getTitle());
                in.put("hint", f.getHint());
                in.put("has_question", f.getHas_question());
                if (f.getHas_question()) {
                    in.put("question", f.getQuestion());
                    in.put("answer", f.getAnswer());
                }
                in.put("latitude", f.getLatitude());
                in.put("longitude", f.getLongitude());
                data.put(String.valueOf(i), in);
            }
            activity.put("data", data);
            mes = activity.toString();
            result = connectToURL(new URL(SERVER_HOST + "/submitActivity"), "POST", mes);
            return result.equals("Success");
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateActivity(List<Feature> mFeatureArray, String id) {
        String mes, result;
        JSONObject activity = new JSONObject(), data = new JSONObject();
        try {
            activity.put("activity_id", id);
            for (int i = 0; i < mFeatureArray.size(); i++) {
                JSONObject in = new JSONObject();
                Feature f = mFeatureArray.get(i);
                in.put("title", f.getTitle());
                in.put("hint", f.getHint());
                in.put("has_question", f.getHas_question());
                if (f.getHas_question()) {
                    in.put("question", f.getQuestion());
                    in.put("answer", f.getAnswer());
                }
                in.put("latitude", f.getLatitude());
                in.put("longitude", f.getLongitude());
                data.put(String.valueOf(i), in);
            }
            activity.put("data", data);
            mes = activity.toString();
            result = connectToURL(new URL(SERVER_HOST + "/updateActivity"), "POST", mes);
            return result.equals("Success");
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkPassword(String id, String pwd) {
        JSONObject req = new JSONObject();
        String res;
        try {
            req.put("activity_id", id);
            req.put("password", pwd);
            res = connectToURL(new URL(SERVER_HOST+"/checkPassword"), "POST", req.toString());
            return res.equals("Success");
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getActivity(String id) {
        mFeatures = new ArrayList<Feature>();
        String res, mes;
        JSONObject aId = new JSONObject();
        try {
            aId.put("activity_id", id);
            mes = aId.toString();
            res = connectToURL(new URL(SERVER_HOST+"/getActivity"), "POST", mes);
            if (!res.equals(ERROR) && !res.equals("Fail")) {
                JSONObject obj = new JSONObject(res);
                int i = 0;
                while (obj.has(String.valueOf(i))) {
                    JSONObject data = (JSONObject) obj.get(String.valueOf(i));
                    Feature feature = new Feature();
                    feature.setHint(data.getString("hint"));
                    feature.setTitle(data.getString("title"));
                    feature.setHas_question(data.getBoolean("has_question"));
                    if (feature.getHas_question()) {
                        feature.setQuestion(data.getString("question"));
                        feature.setAnswer(data.getString("answer"));
                    }
                    feature.setLongitude(data.getDouble("longitude"));
                    feature.setLatitude(data.getDouble("latitude"));
                    mFeatures.add(feature);
                    i++;
                }
                return true;
            } else {
                return false;
            }
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String connectToURL(URL url, String method, String requestContent) {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod(method);
            if (method.equals("GET")) {
                conn.connect();
            } else if (method.equals("POST")) {
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                byte[] request = requestContent.getBytes("UTF-8");
                os.write(request, 0, request.length);
                os.flush();
                os.close();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return ERROR;
            } else {
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                }
                rd.close();
                return response.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public List<Feature> getFeatureList() {
        return mFeatures;
    }
}
