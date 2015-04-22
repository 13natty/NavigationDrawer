package com.nattysoft.navigationdrawer.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.nattysoft.navigationdrawer.listener.RequestResponseListener;
import com.nattysoft.navigationdrawer.net.ConnectionManager;

public class ConnectionManager {

	private static final String LOG_TAG = ConnectionManager.class.getSimpleName();
	
	private final int MAX_ATTEMPTS = 5;
	private final int BACKOFF_MILLI_SECONDS = 2000;
	
	private final Random random = new Random();

	private ProgressDialog progDailog;

	@SuppressWarnings("unchecked")
	public void post(Context context, RequestResponseListener listener, ProgressDialog dialog, Pair<String, JSONObject> pair) {
		Log.d(LOG_TAG, "pair.second "+pair.second);
		new DoPost(context, listener, dialog).execute(pair);
	}

	class DoPost extends AsyncTask<Pair<String, JSONObject>, String, String> {
		
		RequestResponseListener listener;

		public DoPost(Context context, RequestResponseListener listener, ProgressDialog progDialog) {
			
			this.listener = listener;
			progDailog = progDialog;
		}

		@Override
		protected String doInBackground(Pair<String, JSONObject>... vals) {
			try {
				JSONObject json = vals[0].second;
				String result = "";

				long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
				for (int i = 1; i <= MAX_ATTEMPTS; i++) {
					
					Log.d(LOG_TAG, "Attempt #" + i + " to server connection");
					try {
						//json.accumulate("ver", MainActivity.version);
						result = post(vals[0].first, json);
						Log.d(LOG_TAG, "RESULT : " + result);
						return result;
					} catch (IOException e) {

						Log.e(LOG_TAG, "ERROR", e);
						if (i == MAX_ATTEMPTS) {
							break;
						}
						try {
							Log.d(LOG_TAG, "Sleeping for " + backoff
									+ " ms before retry");
							Thread.sleep(backoff);
						} catch (InterruptedException e1) {
							Log.d(LOG_TAG,
									"Thread interrupted: abort remaining retries!");
							Thread.currentThread().interrupt();
							return "400";
						}
						// increase backoff exponentially
						backoff *= 2;
					}
				}

			} catch (Exception e) {
				Log.e(LOG_TAG, "ERROR", e);
			}
			return "400";
		}

		@Override
		protected void onPostExecute(String responseString) {

			if (progDailog != null) {
				progDailog.dismiss();
			}
			if (listener != null) {
				listener.hasResponse(responseString);
			}

		}
	}

	public void get(Context context, RequestResponseListener listener, ProgressDialog dialog, String url, String isLogin, String hasCert) {
		new DoGet(context, listener, dialog).execute(url, isLogin, hasCert);
	}

	class DoGet extends AsyncTask<String, String, String> {
		RequestResponseListener listener;

		public DoGet(Context context, RequestResponseListener listener,
				ProgressDialog progDialog) {
			this.listener = listener;

			progDailog = progDialog;
		}

		protected String doInBackground(String... vals) {
			
			String response = "";
			long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
			for (int i = 1; i <= MAX_ATTEMPTS; i++) {

				Log.d(LOG_TAG, "Attempt #" + i + " to ping");
				try {
					Log.d(LOG_TAG, "request : " + vals[0]);
					response = executeHttpGet(vals[0]);
					Log.d(LOG_TAG, "response : " + response);
					return response;
					
				} catch (IOException e) {
					
					Log.e(LOG_TAG, "ERROR", e);
					if (i == MAX_ATTEMPTS || e.getMessage().contains( "No authentication challenges found")) {
						break;
					}

					try {
						Log.d(LOG_TAG, "Sleeping for " + backoff + " ms before retry");
						Thread.sleep(backoff);
					} catch (InterruptedException e1) {
						Log.d(LOG_TAG, "Thread interrupted: abort remaining retries!");
						Thread.currentThread().interrupt();
						break;
					}
					
					// increase backoff exponentially
					backoff *= 2;
					
				} catch (URISyntaxException urise) {
					Log.e(LOG_TAG, "URI INCORRECTLY FROMATTED", urise);
					break;
				}
			}
			return "404";
		}

		protected void onPostExecute(String responseString) {

			if (listener != null) {
				listener.hasResponse(responseString);
			}
			if (progDailog != null) {
				progDailog.dismiss();
			}
		}
	}

	public String executeHttpGet(String url) throws IOException, URISyntaxException {
		HttpURLConnection conn = null;
		try {
			String line = "";
			conn = (HttpURLConnection) new URL(url).openConnection();

			InputStream in = conn.getInputStream();
			line = readStream(in, "utf-8");

			return line;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

	}

	/**
	 * Issue a POST request to the server.
	 * 
	 * @param endpoint
	 *            POST address.
	 * @param json
	 *            request parameters.
	 * @throws java.io.IOException
	 *             propagated from POST.
	 */
	public String post(String endpoint, JSONObject json) throws IOException {

		Log.d(LOG_TAG, "endpoint [" + endpoint + "]");
		Log.d(LOG_TAG, "json.toString() " + json.toString() + "");
		InputStream inputStream = null;
		String result = null;
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(endpoint);
	    httppost.setHeader("Content-type", "application/json");

	    try {	
	    	if(json != null)
	    	{
	    		httppost.setEntity(new StringEntity(json.toString()));
	    	}

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        inputStream = response.getEntity().getContent();
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	
	if(inputStream != null)
        result = convertInputStreamToString(inputStream);
    else
        result = "Did not work!";
	
	Log.d(LOG_TAG, "result [" + result + "]");
	
	return result;
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
		Log.d(LOG_TAG, "inputStream "+inputStream);
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
        {
        	Log.d(LOG_TAG, "line "+line);
            result += line;
        }
 
        inputStream.close();
        return result;
 
    }   

	private String readStream(InputStream inputStream, String encoding) throws IOException {
		return new String(readStream(inputStream), encoding);
	}

	private byte[] readStream(InputStream inputStream) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}
		return baos.toByteArray();
	}
}
