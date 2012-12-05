package nl.Justian.BatteryWidget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

public class Update extends Service
{
	int level = 0;
	
	RemoteViews updateViews = null;
	
	void onUpdate() {
	    RemoteViews updateViews = buildUpdate(this);
	    ComponentName thisWidget = new ComponentName(this, BatteryWidgetProvider.class);
	    AppWidgetManager manager = AppWidgetManager.getInstance(this);
	    manager.updateAppWidget(thisWidget, updateViews);
	    
	    //Notification icon
	    int icon = android.R.drawable.ic_lock_idle_low_battery;
	    
	    //Notifications
	    if(level == 100 && level != 0){
	    	batteryNotification(this, icon, "Batterij is volledig opgeladen", "Batterij: " + level + "%");
	    }
	    if(level == 10 && level != 0){
	    	batteryNotification(this, icon, "Batterij niveau laag", "Batterij: " + level + "%");
	    }
	    if(level == 5 && level != 0){
	    	batteryNotification(this, icon, "Sluit oplader aan!", "Batterij: " + level + "%");
	    	
	    	//Setting up an email to let someone know your battery is about to die there for u can't be reached.
	    	Intent intent = new Intent(Intent.ACTION_SENDTO);
	    	intent.setType("text/plain");
	    	intent.putExtra(Intent.EXTRA_SUBJECT, "Batterij telefoon bijna leeg!"); // email subject
	    	intent.putExtra(Intent.EXTRA_TEXT, "Hoi, even snel een mailtje. Mijn telefoon is bijna leeg dus ik ben onbereikbaar!"); // email body
	    	intent.setData(Uri.parse("mailto:JeVrouw@gmail.com")); // replace with desired email adress, mailto: to leave it empty
	    	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	startActivity(intent);
	    
	    }
	}

	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent batteryService) {
	        if (batteryService.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {

	            level = batteryService.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	            onUpdate();
	        }
	    }
	};

	@Override
	public void onStart(Intent intent, int startId) {
	    registerReceiver(mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    onUpdate();
	}

	public void onDestroy() {
	    unregisterReceiver(mBatInfoReceiver);
	}

	public RemoteViews buildUpdate(Context context) {
	    updateViews = null;
	    updateViews = new RemoteViews(context.getPackageName(), R.layout.main);
	        
	    	//displaying widget text
	    	updateViews.setTextViewText(R.id.battery_level, "" + level + "%");
			
	    	//updating widget image  
	        if(level <= 100 && level > 80){
	        	updateViews.setViewVisibility(R.id.bar20, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar40, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar60, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar80, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar100, View.VISIBLE);
	        }
	        if(level <= 80 && level > 60){
	        	updateViews.setViewVisibility(R.id.bar20, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar40, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar60, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar80, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar100, View.INVISIBLE);
	        }
	        if(level <= 60 && level > 40){
	        	updateViews.setViewVisibility(R.id.bar20, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar40, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar60, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar80, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar100, View.INVISIBLE);
	        }
	        if(level <= 40 && level > 20){
	        	updateViews.setViewVisibility(R.id.bar20, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar40, View.VISIBLE);
	    		updateViews.setViewVisibility(R.id.bar60, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar80, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar100, View.INVISIBLE);
	        }
	        if(level <= 20){
	        	updateViews.setViewVisibility(R.id.bar20, View.VISIBLE);
	        	updateViews.setViewVisibility(R.id.bar40, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar60, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar80, View.INVISIBLE);
	    		updateViews.setViewVisibility(R.id.bar100, View.INVISIBLE);
	        }
	    return updateViews;
	}
	
	
	public void batteryNotification(Context context, int icon, String text, String title){
	    String ns = context.NOTIFICATION_SERVICE;
	    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns); //I get the context, from onUpdate()
	    
	    CharSequence tickerText = "Batterij"; //this text appears in the notification bar with the icon, when the notification launches
	    long when = System.currentTimeMillis(); //Display the notification immediately
	    Notification notification = new Notification(icon, tickerText, when);

	    CharSequence contentTitle = title;
	    CharSequence contentText = text;
	   
	    Intent notificationIntent = new Intent();
	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
	    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	    // notification.defaults |= Notification.DEFAULT_VIBRATE;
	    
	    int ID = 1;
	    mNotificationManager.notify(ID, notification);	    
	}
	
	@Override
	public IBinder onBind(Intent intent) {
	    return null;
	}
}
