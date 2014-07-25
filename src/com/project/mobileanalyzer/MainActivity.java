package com.project.mobileanalyzer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

import com.services.monitorApps.MonitorService;

import android.R.bool;
import android.R.string;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends ListActivity {
	
	//private ArrayAdapter<string> adapter;
	
	private static final int option1 = Menu.FIRST;
	ArrayAdapter<ResolveInfo> homeListAdapter;
	public static ArrayList<ResolveInfo> homeAppsList;
	ArrayList<ResolveInfo> tempAppsList;
	
	protected SharedPreferences apps;
	public static SharedPreferences appsMonitored;
	PackageManager pm; 
	private ListView lv;
	private TextView analysisMsg,welcomeMsg,timeText,timeFormat;
	private int appscount = 0, totalTime = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
/*		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		ComponentName compInfo = taskInfo.get(0).topActivity;
		String pckgName = compInfo.getPackageName().toString();
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage(pckgName);
		builder.setPositiveButton("Ok", null);
		AlertDialog error1log = builder.create();
		error1log.show();
*/		
		getListView().setOnItemLongClickListener(onLongClick);
		 
		TextView callsTxt = (TextView) findViewById(R.id.TextView04);
		TextView MsgsTxt = (TextView) findViewById(R.id.TextView02);
		TextView GamesTxt = (TextView) findViewById(R.id.TextView01);
		TextView OthersTxt = (TextView) findViewById(R.id.TextView03);
		
		callsTxt.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent callActivity = new Intent(getApplicationContext(),call_activity.class);
		        startActivity(callActivity);
		    }
		});
		
		MsgsTxt.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent callActivity = new Intent(getApplicationContext(),text_activity.class);
		        startActivity(callActivity);
		    }
		});
		
		GamesTxt.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent callActivity = new Intent(getApplicationContext(),games_activity.class);
		        startActivity(callActivity);
		    }
		});
		
		OthersTxt.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	Intent callActivity = new Intent(getApplicationContext(),others_activity.class);
		        startActivity(callActivity);
		    }
		});
		
		
	}
	
	@Override
	public void onResume(){
	    super.onResume();
	    populateList();   
	}
	
	@Override
	protected void onRestart() {
	    super.onRestart();  // Always call the superclass method first
	    
		tempAppsList = new ArrayList<ResolveInfo>();
		homeAppsList = new ArrayList<ResolveInfo>();
		homeListAdapter = new ArrayAdapter<ResolveInfo>(this, R.layout.home_list_item, tempAppsList);
		getListView().setAdapter(homeListAdapter);
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		// put your code here...
	    
	}
	
	@Override
	
	public boolean onCreateOptionsMenu(Menu menu){
		
		super.onCreateOptionsMenu(menu);
		menu.add(menu.NONE, option1, menu.NONE, R.string.menu_option1);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		
		switch(item.getItemId()){
			case option1:
				Intent trackApps = new Intent(getApplicationContext(),track_apps_list.class);
				startActivity(trackApps);
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	//On item long click listener to untrack the applications
OnItemLongClickListener onLongClick = new OnItemLongClickListener(){

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		
		TextView tx = (TextView) view.findViewById(R.id.listTextView);
		final String tagValue = tx.getText().toString();
		
		final SharedPreferences.Editor editor = apps.edit();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		
		builder.setTitle(R.string.Untrack_Title);
		builder.setMessage( getString(R.string.Untrack_Text,tagValue));
		
		builder.setNegativeButton("YES",new DialogInterface.OnClickListener() 
        {
            // called when the "Cancel" Button is clicked
             public void onClick(DialogInterface dialog, int id) 
             {
            	editor.remove(tagValue);
            	editor.apply();
            	populateList();
        		//recreate();
             }
          } 
       );
		builder.setPositiveButton("NO",new DialogInterface.OnClickListener() 
        {
            // called when the "Cancel" Button is clicked
             public void onClick(DialogInterface dialog, int id) 
             {
            	dialog.cancel();
             }
          } 
       );
		AlertDialog dialog = builder.create();
		dialog.show();
		Log.i("item",tagValue);
		return false;
	}
	
};
	//populating the updated tracked list
	protected void populateList(){

		tempAppsList = new ArrayList<ResolveInfo>();
		homeAppsList = new ArrayList<ResolveInfo>();
		homeListAdapter = new ArrayAdapter<ResolveInfo>(this, R.layout.home_list_item, tempAppsList);
		appscount = 0;
		totalTime = 0;
		//pm = null;
		
		apps = getSharedPreferences(getString(R.string.APP_LIST), MODE_PRIVATE);
		appsMonitored = getSharedPreferences(getString(R.string.APP_MONITOR), getApplicationContext().MODE_MULTI_PROCESS);
		pm = getPackageManager();
		
		Intent populateList = new Intent(Intent.ACTION_MAIN,null);
		populateList.addCategory(Intent.CATEGORY_LAUNCHER);
		
		analysisMsg = (TextView)findViewById(R.id.txtAnalysisMsg);
		welcomeMsg = (TextView)findViewById(R.id.welcomeText);
		timeText = (TextView)findViewById(R.id.txtTime);
		timeFormat = (TextView)findViewById(R.id.txtTimeFormat);
		
		homeAppsList = (ArrayList<ResolveInfo>) pm.queryIntentActivities(populateList, PackageManager.PERMISSION_GRANTED); 
		SharedPreferences.Editor preferencesEditor = apps.edit();
		
			for(ResolveInfo rInfo : homeAppsList)
	        {
	        	String app_name = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
	        	if(apps.contains(app_name)){
	        		tempAppsList.add(homeAppsList.get(appscount));
	        		if(appsMonitored.contains(app_name)){
		        		totalTime = totalTime + appsMonitored.getInt(app_name,0);
		        	}
	        	}else{
	        	}
	        	appscount++;
	        }
			
			if((tempAppsList.size()) > 0 && tempAppsList != null){
				homeListAdapter =  new ArrayAdapter<ResolveInfo>(this, R.layout.home_list_item, tempAppsList)
	                {
						@Override
	                    public View getView(final int position, View convertView, ViewGroup parent)
	                    {
	                    	//convertView = null;
	                    	if (convertView == null){
	                            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);
	                    	}   
	                    	
	                    	if(position < tempAppsList.size()){
		                    	final String text = tempAppsList.get(position).activityInfo.applicationInfo.loadLabel(pm).toString();
		                        
		                    	if(apps.contains(text)){
		                        		String timeTraq = apps.getString(text,"");
			                        	((TextView)convertView.findViewById(R.id.listTextView)).setText(text);
			                        	
			                        	if(appsMonitored.contains(text)){
			                        		
			                        		int  spentTime = appsMonitored.getInt(text, 0);
			                        		//totalTime = totalTime + spentTime;
			                        		TimeZone tz = TimeZone.getTimeZone("UTC");
			                        	    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
			                        	    df.setTimeZone(tz);
			                        	    String time = df.format(new Date(spentTime*1000)).toString();
			                        	    String[] formats = time.split(":");
			                        	    int hrs = Integer.parseInt(formats[0]);
			                        	    int mins = Integer.parseInt(formats[1]);
			                        	    int secs = Integer.parseInt(formats[2]);
			                        	    String actualTime = "";
			                        	    if(hrs != 0){
			                        	    	actualTime = hrs + " Hrs, ";
			                        	    }
			                        	    if(mins != 0){
			                        	    	actualTime = actualTime +mins +" Mins, ";
			                        	    }
			                        	    if(secs != 0){
			                        	    	actualTime = actualTime +secs +" Secs";
			                        	    }
			                        	    ((TextView)convertView.findViewById(R.id.listTxtTime)).setText(actualTime);
			                        	}else{
			                        		((TextView)convertView.findViewById(R.id.listTxtTime)).setText(timeTraq);
			                        	}
			                        	final Drawable drawable = tempAppsList.get(position).activityInfo.applicationInfo.loadIcon(pm);
			                    		((ImageView)convertView.findViewById(R.id.image)).setImageDrawable(drawable);
		                        	}
	                    	}
	                    
	                		return convertView;
	                    }
	                };
	            setListAdapter(homeListAdapter);
	            homeListAdapter.notifyDataSetChanged();
	            getListView().setVisibility(View.VISIBLE);
	            
	            TimeZone tz = TimeZone.getTimeZone("UTC");
        	    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        	    df.setTimeZone(tz);
        	    String time = df.format(new Date(totalTime*1000)).toString();
        	    
        	    String[] formats = time.split(":");
        	    int hrs = Integer.parseInt(formats[0]);
        	    int mins = Integer.parseInt(formats[1]);
        	    int secs = Integer.parseInt(formats[2]);
        	    String actualTime = "";
        	    if(hrs != 0){
        	    	actualTime = hrs + " Hrs, ";
        	    }
        	    if(mins != 0){
        	    	actualTime = actualTime +mins +" Mins, ";
        	    }
        	    if(secs != 0){
        	    	actualTime = actualTime +secs +" Secs";
        	    }
        	    
        	    timeText.setVisibility(View.VISIBLE);
	            timeText.setText(actualTime);
	            //timeFormat.setVisibility(View.VISIBLE);
	            analysisMsg.setText(getString(R.string.analysisMessage)); 
		}else{
			getListView().setVisibility(View.INVISIBLE);
			analysisMsg.setText(getString(R.string.emptyList));
			welcomeMsg.setText(getString(R.string.welcomeMessageAlt));
			timeText.setVisibility(View.GONE);
            timeFormat.setVisibility(View.GONE); 
		}
        

	}
}