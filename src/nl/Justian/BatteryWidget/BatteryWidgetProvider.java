package nl.Justian.BatteryWidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;


public class BatteryWidgetProvider extends AppWidgetProvider 
{
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	    super.onUpdate(context, appWidgetManager, appWidgetIds);
	    context.startService(new Intent(context, Update.class));
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
	    context.stopService(new Intent(context, Update.class));
	    super.onDeleted(context, appWidgetIds);
	}
}