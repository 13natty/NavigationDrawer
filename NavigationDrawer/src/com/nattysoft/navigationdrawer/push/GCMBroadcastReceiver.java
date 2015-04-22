package com.nattysoft.navigationdrawer.push;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.analytics.n;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import com.nattysoft.navigationdrawer.DialogClass;
import com.nattysoft.navigationdrawer.FragmentOne;
import com.nattysoft.navigationdrawer.R;
import com.nattysoft.navigationdrawer.ZonesActivity;
//import com.nattySoft.mogalejobcard.listener.PushListener;
//import com.nattySoft.mogale.ChatActivity;
//import com.nattySoft.mogale.MainActivity;
//import com.nattySoft.mogale.listener.ChatResponceListener;
//import com.nattySoft.mogalejobcard.net.CommunicationHandler.Action;
import com.nattysoft.navigationdrawer.listener.PushListener;

public final class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

	private static final String LOG_TAG = GCMBroadcastReceiver.class.getSimpleName();
	private static final String REGISTRATION_SUCCESS = "0";
	private static final String NEW_INCIDENT = "1";
	private static final String INCIDENT_UPDATE = "2";
	private static final String CHAT_MESSAGE = "3";
	private static final String INCIDENT_ACCEPT = "4";
	public static PushListener pushListener = null;

	public static void setListener(PushListener pushListener) {
		GCMBroadcastReceiver.pushListener = pushListener;
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {

		Log.d(LOG_TAG, "onReceive(" + intent + ")");

		final Bundle extras = intent.getExtras();
		final GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
		final String messageType = googleCloudMessaging.getMessageType(intent);

		if (!extras.isEmpty()) {

			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				Log.d(LOG_TAG, "extras.size() " + extras.size());
				Log.d(LOG_TAG, "extras.toString() " + extras.toString());
				Log.d(LOG_TAG, "Message received: " + extras.getString("body"));

				if(pushListener == null)
				{
					pushListener = new ZonesActivity();
				}
				pushListener.pushReceived(context, intent);
						
			}
		}
	}

	

	private void startNotification(final Context context, final String tittle, final String message) {

		final Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		vibrator.vibrate(1000);

		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(com.nattysoft.navigationdrawer.R.drawable.ic_launcher).setContentTitle("tittle").setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message);

		notificationManager.notify(1337, mBuilder.build());
	}
}