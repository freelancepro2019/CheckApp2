package com.check.apps.checkapp.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.check.apps.checkapp.R;
import com.check.apps.checkapp.models.UserModel;
import com.check.apps.checkapp.preferences.Preferences;
import com.check.apps.checkapp.tags.Tags;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseNotifications extends FirebaseMessagingService {
    private Preferences preferences = Preferences.newInstance();
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> map = remoteMessage.getData();
        for (String key: map.keySet())
        {
            Log.e("key",key+"value"+map.get(key));
        }
        if (getSession().equals(Tags.session_login))
        {
            if (map.get("id").equals(getUser().getId()))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    initNewVersionNotification(map);
                } else {
                    initOldVersionNotification(map);
                }
            }
        }

    }

    private void initOldVersionNotification(Map<String,String> map) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(map.get("title"));
        builder.setContentText(map.get("content"));


        if (manager!=null)
        {
            manager.notify("check_tag", 1585, builder.build());

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initNewVersionNotification(Map<String,String> map) {
        String channel_id = "check_channel_id";
        CharSequence channel_name = "check_channel_name";
        int Importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(channel_id, channel_name, Importance);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id);
        builder.setChannelId(channel_id);

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(map.get("title"));
        builder.setContentText(map.get("content"));

        if (manager!=null)
        {
            manager.createNotificationChannel(channel);
            manager.notify("check_tag", 1585, builder.build());
        }


    }



    private String getSession()
    {
        return preferences.getSession(this);
    }

    private UserModel getUser()
    {
        return preferences.getUserData(this);
    }

}
