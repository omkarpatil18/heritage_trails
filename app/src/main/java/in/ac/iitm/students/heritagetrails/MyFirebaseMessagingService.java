package in.ac.iitm.students.heritagetrails;

/**
 * Created by sai_praneeth7777 on 25-Jan-17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by sai_praneeth7777 on 05-Jul-16.
 */
public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage message) {

        Intent i = new Intent(this,MapsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }

}

