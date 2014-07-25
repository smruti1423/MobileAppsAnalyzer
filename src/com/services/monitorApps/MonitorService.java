 package com.services.monitorApps;

import java.util.List;

import android.R;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.TextView;
import android.widget.Toast;


public class MonitorService extends IntentService {
	
	public SharedPreferences apps,addedApps;
	public String pckgName,fgAppName,fgAppChange;
	public int timecount;
	public int currSysTime = (int) ((System.currentTimeMillis()/1000)%60);
	public boolean changed = false;
	
	
	public MonitorService() {
		super("MonitorService");
		// TODO Auto-generated constructor stub
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		apps = getSharedPreferences("appsMonitoredList", getApplicationContext().MODE_MULTI_PROCESS);
		addedApps = getSharedPreferences("applicationsAddedList",getApplicationContext().MODE_MULTI_PROCESS);
		SharedPreferences.Editor prefEdit = apps.edit();
		
		fgAppChange = intent.getExtras().getString("foreAppActive");
		timecount = intent.getExtras().getInt("timestamp");
		
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName compInfo = taskInfo.get(0).topActivity;
		
		pckgName = compInfo.getPackageName();
		
		PackageManager pm = this.getPackageManager();
		try {
			PackageInfo foreGroundAppInfo = pm.getPackageInfo(pckgName, 0);
			fgAppName = foreGroundAppInfo.applicationInfo.loadLabel(pm).toString();
			//onDestroy();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(addedApps.contains(fgAppName)){
			if(fgAppChange == fgAppName){
				timecount = currSysTime + timecount;
				prefEdit.putInt(fgAppChange, timecount);
			}else{
				fgAppChange = fgAppName;
				if(apps.contains(fgAppChange)){
					int elapsedtime = apps.getInt(fgAppChange, 0);
					timecount = currSysTime + elapsedtime;
					prefEdit.putInt(fgAppChange, timecount);
				}else{
					timecount = currSysTime + timecount;
					prefEdit.putInt(fgAppChange, timecount);
				}
				timecount = 0;
			}
			//Toast.makeText(this, fgAppChange,Toast.LENGTH_SHORT).show();
			//Toast.makeText(this,fgAppChange+","+ apps.getInt(fgAppChange, 0)+"",Toast.LENGTH_SHORT).show();
			prefEdit.apply();
			Intent inten = new Intent("com.project.mobileanalyzer");
		    inten.putExtra("foreAppActive", fgAppChange);
		    inten.putExtra("timestamp", timecount);
		    sendBroadcast(inten);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    
		return super.onStartCommand(intent,flags,startId);
	}
	
	@Override
	 public void onDestroy() {
        try {
            //mTimer.cancel();
            //timerTask.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
