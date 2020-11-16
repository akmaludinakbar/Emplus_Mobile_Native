package bni.emplus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class msg extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getNotification()!=null){
        }

        if(remoteMessage.getData().containsKey("title") && remoteMessage.getData().containsKey("message")){

        }

        String title = remoteMessage.getData().get("title").toString();
        String message = remoteMessage.getData().get("message").toString();

        int icon = 0;
        /*if(title.contains("Disposisi")){
            icon = R.mipmap.ic_dis;
        }if(title.contains("Urgent")){
            icon = R.mipmap.ic_ur;
        }
        else if(title.contains("Approve")) {
            icon = R.mipmap.ic_app;
        }else{
            icon = R.mipmap.ic_newlogoeoffice;
        }*/
        icon = R.drawable.iconnotif;

        sendNotification(message,title,icon);
    }

    private void sendNotification(String messageBody,String title,int icon) {
        Intent intent = new Intent(this, ss.class);
        intent.putExtra("message",messageBody);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}