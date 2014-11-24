package wm.com.danteater.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import wm.com.danteater.app.MyApplication;

/**
 * Created by dhruvil on 18-11-2014.
 */
public class SessionReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {


     //   Toast.makeText(context, "Service running", Toast.LENGTH_SHORT).show();

        JSONObject params = new JSONObject();
        JSONObject request_params2 = new JSONObject();
        try {
            SharedPreferences preferences = context.getSharedPreferences("session_id", context.MODE_PRIVATE);
            String sessionId=preferences.getString("session_id","");
           // Log.e("==================================================", sessionId);
            params.put("session_id", sessionId);
            request_params2.put("methodname", "keepAlive");
            request_params2.put("type", "jsonwsp/request");
            request_params2.put("version", "1.0");
            request_params2.put("args", params);

        } catch (Exception e) {
            e.printStackTrace();
        }


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/UserService/jsonwsp", request_params2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jobj) {
                String res = jobj.toString();
             //   Log.e("????????????  response continue: ", res + "");
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        });
        MyApplication.getInstance().addToRequestQueue(req);
    }
}
