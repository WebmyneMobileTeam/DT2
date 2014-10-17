package wm.com.danteater.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class APIDelete {
	private static Reader reader=null;
	public static Reader postData(String SERVER_URL) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpDelete delete = new HttpDelete(SERVER_URL);
			HttpResponse response = httpClient.execute(delete);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				reader = new InputStreamReader(content);
			} else {
//				Log.e("error:", "Server responded with status code: "+ statusLine.getStatusCode());

			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return reader;
	}
}
