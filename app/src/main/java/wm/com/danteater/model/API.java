package wm.com.danteater.model;

import android.util.Log;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


public class API {

    //

    public static String link_retrievePlayContentsForPlayOrderId = "http://api.danteater.dk/api/playfull/";

    public static Reader callWebservicePost(String SERVER_URL,String jsonString) {

        Reader reader = null;
        InputStream is=null;

        try {

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SERVER_URL);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            post.setEntity(new StringEntity(jsonString));
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                reader = new InputStreamReader(is);
            } else {
                Log.e("Error", +statusLine.getStatusCode()+"");
            }
        }catch (JsonSyntaxException e) {
            Log.e("Error: ",e+"");
            e.printStackTrace();
        } catch (JsonIOException e) {
            Log.e("Error: ",e+"");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("Error: ",e+"");
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e("Error: ",e+"");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Error: ",e+"");
            e.printStackTrace();
        } catch (Exception e) {
            Log.e("Error: ",e+"");
            e.printStackTrace();
        }
        return reader;
    }
}
