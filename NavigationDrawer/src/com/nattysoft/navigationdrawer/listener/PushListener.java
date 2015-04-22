package com.nattysoft.navigationdrawer.listener;

import android.content.Context;
import android.content.Intent;

public interface PushListener {
	public void pushReceived(Context context, Intent intent);
}
