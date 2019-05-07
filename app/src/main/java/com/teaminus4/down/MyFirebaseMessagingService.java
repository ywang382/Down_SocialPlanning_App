package com.teaminus4.down;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static android.support.constraint.Constraints.TAG;

// this class creates a service that waits for a message from Firebase cloud
// and will use the message to create a notification on the device

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // when you receive a message from the firebase
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification().getBody() != null) {
            //Log.e("FIREBASE", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage);
        }
    }

    @Override
    public void onNewToken(String token) {
        //Log.d(TAG, "Refreshed token: " + token);
        super.onNewToken(token);
        // need to sent the token (device identifier) to the
        // server
        //sendRegistrationToFirebase(token);
        // NOTE: Implementation of token storage takes place in
        // MyFeedActivity which is post-auth
        // However, it often runs more times than necessary
    }

    // creates a notification and displays on the device
    private void sendNotification(RemoteMessage remoteMessage) {
        // getting shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // then you use

        // do not display notifications if they have the button off
        //Log.d(TAG, "Preference Status: " + prefs.getBoolean("notif_on_off", true));
        if (!prefs.getBoolean("notif_on_off", true)) {
            //Log.d(TAG, "Notification not displayed");
            return;
        }

        String type = remoteMessage.getData().get("type");
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        // intent to travel from notification to the main feed
        Intent intent;
        PendingIntent pendingIntent;
        //Log.d(TAG, "Type of notification received: " + type);

        // depending on the type of intent
        if (type.equals("down")){
            if (prefs.getBoolean("notif_down", true)) {
                intent = new Intent(this, MyFeedActivity.class);
            } else {
                //Log.d(TAG, "Down Notifs Shut Off");
                return;
            }
        } else if (type.equals("request")){
            if (prefs.getBoolean("notif_request", true)) {
                intent = new Intent(this, FriendsFragment.class);
            } else {
                //Log.d(TAG, "Request Nofits Shut Off");
                return;
            }
        } else if (type.equals("status")){
            if (prefs.getBoolean("notif_status", true)) {
                intent = new Intent(this, MyFeedActivity.class);
            } else {
                //Log.d(TAG, "Status Notifs Shut Off");
                return;
            }
        } else if (type.equals("down_deleted")){
            if (prefs.getBoolean("notif_downdelete", true)) {
                intent = new Intent(this, MyFeedActivity.class);
            } else {
                //Log.d(TAG, "Down Deleted Notifs Shut Off");
                return;
            }
        } else {
            intent = new Intent(this, MyFeedActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        // set ringtone for notification
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                // setting icons
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logocircle))
                .setSmallIcon(R.drawable.logocircle)
                // setting values based off message from firebase
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    // take the token that was generated and store with the user
    // alongside the userID
    // ISSUE: cannot access user when signing in because pre-auth
    // yet this method is called for the token is generated upon opening the app
    public void sendRegistrationToFirebase(String token) {
        // initializing database
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        // getting the user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Log.d(TAG, "Setting token in firebase");
        db.child(user.getUid()).child("token").setValue(token);
    }


}
