package com.hkubit.thespeakable;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by lenovo on 6/22/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String senderUID = remoteMessage.getData().get("sender");
        String click_action = remoteMessage.getNotification().getClickAction();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body);
        Log.i("BUILD","Build notification successful");
        Log.i("FBMS",senderUID);
        Log.i("FBMS",click_action);
        Intent profileIntent = new Intent(click_action);
        profileIntent.putExtra("id",senderUID);

        PendingIntent profilePendingIntent = PendingIntent.getActivity(this,0,profileIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(profilePendingIntent);

        int notificationId = (int)System.currentTimeMillis();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());
    }

}
