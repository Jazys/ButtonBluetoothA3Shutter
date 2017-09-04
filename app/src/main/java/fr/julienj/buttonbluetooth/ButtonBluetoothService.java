package fr.julienj.buttonbluetooth;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.SmsManager;
import android.widget.RemoteViews;

import java.util.Random;

public class ButtonBluetoothService extends Service {
    private MediaSessionCompat mediaSession;

    private int oldDirection=-1;
    private int severalPush=-1;

    @Override
    public void onCreate() {
        super.onCreate();

        startRecognizeBT();
    }

    public void startRecognizeBT()
    {
        mediaSession = new MediaSessionCompat(this, "ButtonBluetoothService");

        //Pour recevoir les event sinon on ne passe pas "onAdjustVolume
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mediaSession.setCallback(new MediaSessionCompat.Callback()
            {

            });
        }

        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0) //you simulate a player which plays something.
                .build());


        VolumeProviderCompat myVolumeProvider =
                new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, /*max volume*/100, /*initial volume level*/50) {
                    @Override
                    public void onAdjustVolume(int direction) {

                        System.out.println("tesssssss "+ direction);

                        //sur un relaché
                        if(direction==0 && oldDirection==1)
                        {
                            //si un seul appui alors simple notif
                            if (severalPush==-1) {
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();
                                
                            }// si plusieurs appui
                            else
                            {
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();
                                severalPush=-1;
                            }
                        }
                        //plusieurs appui
                        else if(direction==1 && oldDirection==1)
                        {
                            //on alimente un compteur
                            severalPush=severalPush+direction;
                        }

                        oldDirection=direction;

                    }
                };

        mediaSession.setPlaybackToRemote(myVolumeProvider);
        mediaSession.setActive(true);

    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            System.out.println("tesssss79");
            SmsManager smsManager = SmsManager.getDefault();
            System.out.println("tesssss89");
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            System.out.println("tesssss99");
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaSession.release();
    }
}