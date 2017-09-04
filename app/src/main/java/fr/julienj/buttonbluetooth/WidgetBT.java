package fr.julienj.buttonbluetooth;

/**
 * Created by JulienJ on 02/09/2017.
 */


import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetBT extends AppWidgetProvider {

    private static final String ManagedSrvBT = "ManagedSrvBT";
    RemoteViews remoteViews;
    private static boolean isStartSrv=false;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                WidgetBT.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int i = 0; i < allWidgetIds.length; i++) {
            int widgetId = appWidgetIds[i];

            remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget);


            if(isStartSrv)
                remoteViews.setInt(R.id.btnWidget, "setBackgroundResource", R.drawable.power_on);
            else
                remoteViews.setInt(R.id.btnWidget, "setBackgroundResource", R.drawable.power_off);

            Intent intent = new Intent(context, WidgetBT.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setOnClickPendingIntent(R.id.btnWidget,
                    getPendingSelfIntent(context, ManagedSrvBT));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }


    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);//add this line

        if (ManagedSrvBT.equalsIgnoreCase(intent.getAction())){


            // Build the intent to call the service
            Intent intentSnd = new Intent(context.getApplicationContext(),
                    ButtonBluetoothService.class);

            String msgToDisplay="";

            isStartSrv=isMyServiceRunning(ButtonBluetoothService.class,context);

            // Update the widgets via the service
            if (isStartSrv==false) {
                context.startService(intentSnd);
                msgToDisplay="Activation du service Bouton ";
            }
            else {
                context.stopService(intentSnd);
                msgToDisplay="Désactivation du service Bouton ";
            }

            Toast.makeText(context, msgToDisplay, Toast.LENGTH_SHORT).show();

            isStartSrv=!(isStartSrv);

            AppWidgetManager myAWM = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, WidgetBT.class);
            onUpdate(context, myAWM, myAWM.getAppWidgetIds(cn));


        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass, Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}