package com.tiffin_dabba.admin.tiffindabba.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tiffin_dabba.admin.tiffindabba.DrawerActivity;
import com.tiffin_dabba.admin.tiffindabba.ProductListingActivity;
import com.tiffin_dabba.admin.tiffindabba.R;

/**
 * Created by NICE on 22-04-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        showNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("title"),remoteMessage.getData().get("catId"),remoteMessage.getData().get("prId"),remoteMessage.getData().get("catName"));
        Log.d("data", remoteMessage.getData().get("message") + remoteMessage.getData().get("title")+remoteMessage.getData().get("catId")+remoteMessage.getData().get("prId")+remoteMessage.getData().get("catName"));
    }

    private void showNotification(String message, String title,String catId,String prId,String catName) {
        Intent notificationIntent ;
//        Intent i = new Intent(this, DrawerActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent i = new Intent(this, DrawerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        Intent notificationIntent = new Intent(getApplicationContext(), DrawerActivity.class);
        // set intent so it does not start a new activity

        if(catId.equalsIgnoreCase(""))
        {
            notificationIntent = new Intent(getApplicationContext(), DrawerActivity.class);
        }
        else
        {
            notificationIntent=new Intent(getApplicationContext(), ProductListingActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("ProductType","FastFood");
            bundle.putString("CategoryId",catId);
            bundle.putString("CategoryName",catName);
            //bundle.putString("NotiProductId",prId);

            notificationIntent.putExtras(bundle);

        }
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent =
//                PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(uri)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());
    }


}

