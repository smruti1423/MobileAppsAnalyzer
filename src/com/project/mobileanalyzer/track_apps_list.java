package com.project.mobileanalyzer;

import java.util.ArrayList;

import com.services.monitorApps.MonitorService;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class track_apps_list extends ListActivity{
	
	private static final String TAG = "hello error checking";
	private Button btnSelectApps;
	private Button btnUnSelectApps;
	private Button btnAddApp;
	private ListView lv;
	private boolean checkAll;
	public static int appsCount = 0;
	private static final String APP_LIST = "applicationList";
	protected SharedPreferences apps,addedApps; // Applciation List
	 ArrayAdapter<ResolveInfo> adapter;
	 ArrayList<ResolveInfo> appslist;
	 
	 private String timestamp = "0 Secs";
	 PackageManager pm;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.track_apps_list);
		
		btnSelectApps = (Button)findViewById(R.id.btnSelectAll);
		btnUnSelectApps = (Button)findViewById(R.id.btnUnSelectAll);
		btnAddApp = (Button)findViewById(R.id.btnAdd);
		
		apps = getSharedPreferences(APP_LIST, MODE_PRIVATE);
		addedApps = getSharedPreferences(getString(R.string.APP_LIST),MODE_MULTI_PROCESS);
		
		pm = getPackageManager();
		
		Intent populateList = new Intent(Intent.ACTION_MAIN,null);
		populateList.addCategory(Intent.CATEGORY_LAUNCHER);
		
		
		appslist = (ArrayList<ResolveInfo>) pm.queryIntentActivities(populateList, PackageManager.PERMISSION_GRANTED); 
		SharedPreferences.Editor preferencesEditor = apps.edit();
		for(ResolveInfo rInfo : appslist)
        {
        	String app_name = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
        	String checkValue = "false";
        	Log.i(TAG, ": Installed Applications " + checkValue);
        	preferencesEditor.putString(app_name, checkValue);
        }
        preferencesEditor.apply();
        
		
		adapter =  new ArrayAdapter<ResolveInfo>(this, R.layout.list_item, appslist)
                {
					@Override
                    public View getView(final int position, View convertView, ViewGroup parent)
                    {
                    	//convertView = null;
                    	if (convertView == null){
                            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                    	}   
                    	final String text = appslist.get(position).activityInfo.applicationInfo.loadLabel(pm).toString();
                        ((TextView)convertView.findViewById(R.id.listTextView)).setText(text);
                        
                		final Drawable drawable = appslist.get(position).activityInfo.applicationInfo.loadIcon(pm);
                		((ImageView)convertView.findViewById(R.id.image)).setImageDrawable(drawable);
                        
                        String state = apps.getString(text,"");
                        
                        if(state.equals("false")){
                        	((CheckBox)convertView.findViewById(R.id.chckItem)).setChecked(false);
                        }else{
                        	((CheckBox)convertView.findViewById(R.id.chckItem)).setChecked(true);
                        }
                    
                		return convertView;
                    }
                };
            setListAdapter(adapter);
           
            getListView().setOnItemClickListener(new OnItemClickListener() {
            	 public void onItemClick(AdapterView<?> parent, View view,int position, long id){
	            		TextView tx = (TextView) view.findViewById(R.id.listTextView);
	          			String text = tx.getText().toString();
	          			String state = apps.getString(text, "");
	          			Log.i("onClick",text);
	          			SharedPreferences.Editor preferencesEditor = apps.edit();
	          			SharedPreferences.Editor addPreferencesEditor = addedApps.edit();
	          			
	          			if(state.equals("false"))
	          			{
	          				
	          				preferencesEditor.putString(text,"true");
	          				((CheckBox)view.findViewById(R.id.chckItem)).setChecked(true);
	          				addPreferencesEditor.putString(text, timestamp);
	          				
	          			}
	          			else
	          			{
	          				preferencesEditor.putString(text, "false");
	          				((CheckBox)view.findViewById(R.id.chckItem)).setChecked(false);
	          				addPreferencesEditor.remove(text);
	          			}
	          			preferencesEditor.apply();
	          			addPreferencesEditor.apply();
            	 }
			});
            
            
            
            // Event handler for onclick of select all button
            btnSelectApps.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO select all the checkboxes
					updateCheckBoxes(true);
				}
			});
            
            //Event handler for onclick of add button
            btnUnSelectApps.setOnClickListener(new View.OnClickListener() {
				
				 
				public void onClick(View v) {
					// TODO Unselect all the checkboxes
					updateCheckBoxes(false);
				}
			});
            
            //Event handler for onclick of cancel button
            btnAddApp.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(track_apps_list.this);
					for(ResolveInfo rInfo:appslist){
						String appKey = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
						String state = apps.getString(appKey, "");
						if(state.equalsIgnoreCase("true")){
							appsCount++;
							
						}
					}
					
					
					if(appsCount == 0){
						//AlertDialog.Builder builder = new AlertDialog.Builder(track_apps_list.this);
						builder.setMessage(R.string.AppsNegValidation);
						builder.setPositiveButton("Ok", null);
						AlertDialog error1log = builder.create();
						error1log.show();
						appsCount = 0;								
					}else if(appsCount == 1){
						Toast.makeText(getApplicationContext(), R.string.AppAddMsg, Toast.LENGTH_SHORT).show();
						appsCount = 0;
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			            startActivity(intent);
					}else{
						Toast.makeText(getApplicationContext(), R.string.AppsAddMsg, Toast.LENGTH_SHORT).show();
						appsCount = 0;
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			            startActivity(intent);
					}
					Intent inten = new Intent(getApplicationContext(),MonitorService.class);
					inten.putExtra("foreAppActive", "");
					inten.putExtra("timestamp", 0);
					startService(inten);
				}
				
			});
            
            
		}
		
		//updates the checkboxes to select/unselect all in the list view
		protected void updateCheckBoxes(boolean check){
	    	SharedPreferences.Editor preferencesEditor = apps.edit();
	    	SharedPreferences.Editor addPreferencesEditor = addedApps.edit();
  			
			for(ResolveInfo rInfo : appslist)
	        {
	        	String app_name = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
	        	Log.i(TAG, ": Installed Applications " + app_name);
	        	preferencesEditor.putString(app_name, check+"");
	        	addPreferencesEditor.putString(app_name, timestamp);
	        }
	        preferencesEditor.apply();
	        addPreferencesEditor.apply();
	        adapter.notifyDataSetChanged();
	    }
}
