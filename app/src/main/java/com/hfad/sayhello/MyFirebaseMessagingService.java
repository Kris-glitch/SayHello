package com.hfad.sayhello;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";
    private static final String NOTIFICATION_PERMISSION = "notification_permission";

    private static final String CHANNEL_NAME = "Test Notification";
    private static final String GROUP_NAME = "Test Group Notification";
    private static final String GROUP_ID = "test.notification";



    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.v(TAG, "From: " + message.getFrom());

        if (message.getData().size() > 0) {
            Log.v(TAG, "Data payload: " + message.getData());

            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }

        if (message.getNotification() != null) {
            Log.v(TAG, "Message Notification: " + message.getNotification().getBody());
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();
            String id = message.getData().get("link");
            Map<String, String> data = message.getData();

            sendNotification(title, body, data);

        }

    }
    @Override
    public void onNewToken(@NonNull String token) {
        Log.v(TAG, "Token : " + token);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean notificationPermission = preferences.getBoolean(NOTIFICATION_PERMISSION, false);
        String fcm_token = preferences.getString("fcm_token", "");

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("fcm_tokens");
        databaseReference.child(userID).setValue(token);

    }

    private void handleNow() {
    }

    private void scheduleJob() {
    }

    private void sendNotification(String title, String messageBody, Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            intent.putExtra(key, value);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channelId = getString(R.string.default_notification_channel_id);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notification = null;
        notification = setNotification(getApplicationContext(), channelId, title, messageBody, defaultSoundUri, GROUP_ID, pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel myChannel = new NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            long notificationId = System.currentTimeMillis();
            notificationManager.createNotificationChannel(myChannel);
            notificationManager.notify((int) notificationId, notification.build());
        }




    }

    public static NotificationCompat.Builder setNotification(
            Context context,
            String channelId,
            String title,
            String body,
            Uri soundUri,
            String groupId,
            PendingIntent pendingIntent
    ) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.baseline_circle_notifications_24)
                .setColor(ContextCompat.getColor(context.getApplicationContext(), R.color.notification))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setGroupSummary(false);

        if (groupId != null) {
            notification.setGroup(groupId);
        }

        notification.setContentIntent(pendingIntent);

        return notification;
    }

}
