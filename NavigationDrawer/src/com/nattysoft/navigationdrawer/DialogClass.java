package com.nattysoft.navigationdrawer;

import com.nattysoft.navigationdrawer.listener.RequestResponseListener;
import com.nattysoft.navigationdrawer.push.GCMBroadcastReceiver;
import com.nattysoft.navigationdrawer.util.Preferences;
import com.nattysoft.navigationdrawer.net.CommunicationHandler;
import com.nattysoft.navigationdrawer.net.CommunicationHandler.Action;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogClass extends Dialog implements android.view.View.OnClickListener {
	
	private static final String LOG_TAG = DialogClass.class.getSimpleName();
	public Activity c;
	public Dialog d;
	public Button yes, no, ok;
	public TextView acceptResults;
	public EditText reason;
	public String incidentID;
	public String incidentId_local = null; 
	public String message;
	public ZonesActivity main;
	
	private static final int ACT_DECLINE = 0;
	private static final int ACT_ACCEPT = 1;
	public int currentAct = ACT_DECLINE;

	public DialogClass(Activity activity, String incidentID) {
		//this constructor is confirm and reson for decline
		super(activity);
		currentAct = ACT_DECLINE;
		this.c = activity;
		this.incidentID = incidentID;
	}

	public DialogClass(ZonesActivity main, String incidentId_local, String incidentID, String message) {
		//this constructor is for accept incident success
		super(main);
		currentAct = ACT_ACCEPT;
		this.c = main;
		this.incidentID = incidentID;
		this.incidentId_local = incidentId_local;
		this.message = message;
		this.main = main;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "savedInstanceState "+savedInstanceState);
		Log.d(LOG_TAG, "currentAct "+currentAct);
		if(currentAct == ACT_DECLINE)
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.custom_dialog_decline);
			reason = (EditText) findViewById(R.id.reason);
			yes = (Button) findViewById(R.id.btn_yes);
			no = (Button) findViewById(R.id.btn_no);
			yes.setOnClickListener(this);
			no.setOnClickListener(this);
		}
		else
		{
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.custom_dialog_accept);
			acceptResults = (TextView) findViewById(R.id.accept_results);
			acceptResults.setText(message);
			ok = (Button) findViewById(R.id.btn_ok);			
			ok.setOnClickListener(this);
			
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_yes:
			 ZonesActivity.action = Action.DECLINE_INCIDENT;
			 CommunicationHandler.declineIncident(c,
			 (RequestResponseListener) c,
			 ProgressDialog.show(c, "Please wait",
			 "Declining Incidents..."),
			 Preferences.getPreference(c,
			 AppConstants.PreferenceKeys.KEY_EMPLOYEE_NUM), incidentID, reason.getText().toString());
			break;
		case R.id.btn_ok:
			dismiss();
			ZonesActivity.action = Action.GET_ALL_OPEN_INCIDENCES;
//			CommunicationHandler.getOpenIncidents(main, main, ProgressDialog.show(main, "Please wait", "Retrieving Open Incidents..."));
		case R.id.btn_no:
			dismiss();
			break;
		default:
			break;
		}
		dismiss();
	}

}
