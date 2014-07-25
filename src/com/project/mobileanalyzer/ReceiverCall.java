package com.project.mobileanalyzer;

import java.util.List;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;

import com.services.monitorApps.*;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class ReceiverCall extends BroadcastReceiver {
	
	
	@Override
    public void onReceive(Context context, Intent intent) {
        
        String appPrev = intent.getExtras().getString("foreAppActive");
		int appPrevTimeStamp = intent.getExtras().getInt("timestamp");
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent inten = new Intent(context, MonitorService.class);
		inten.putExtra("foreAppActive",appPrev);
		inten.putExtra("timestamp", appPrevTimeStamp);
		context.startService(new Intent(inten));
        
	}
}
