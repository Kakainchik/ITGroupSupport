package kz.itgroup.itgroupsupport;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

public class NotificationMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Messaging service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, TokenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            createNotificationChannel();

            String id = remoteMessage.getData().get("tokenID");
            if(id == null) {
                Log.e(TAG, "No id");
                return;
            }

            try {
                if(!IOFileHelper.contains(this, id)) {
                    Log.e(TAG, "No such file");
                } else {
                    TokenFile file = IOFileHelper.loadTokenFile(this, id);
                    file.getToken().setState(TokenState.HANDLED);
                    IOFileHelper.saveTokenFile(this, file.getToken());
                }

            } catch(IOException ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }

            NotificationCompat.Builder nBuilder =
                    new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.drawable.ic_sms_black_24dp)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, nBuilder.build());

            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_ID = getString(R.string.default_notification_channel_id);
            CharSequence name = "Common";
            String description = "Common notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
