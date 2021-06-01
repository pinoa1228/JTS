package com.bae.dialogflowbot;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {
    }

    NotificationManager manager;
    NotificationCompat.Builder builder;
    Intent push;
    PendingIntent fullScreenPendingIntent;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";



    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        builder = null;
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        //알림창 클릭 시 activity 화면 부름
        Intent intent2 = new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,101,intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        //알림창 제목
        builder.setContentTitle("일주일이 지났어요! 상담을 다시 진행할 시간이에요 :)");
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        //알림창 터치시 자동 삭제
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        Notification notification = builder.build();
        manager.notify(1,notification);





//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context.getApplicationContext())
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setPriority(Notification.PRIORITY_DEFAULT)
//                .setCategory(Notification.CATEGORY_MESSAGE)
//                .setContentTitle("알람")
//                .setContentText("message")
//                .setWhen(0)
//                .setTicker(context.getString(R.string.app_name));
//
//        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        notificationBuilder.setContentText("message");
////        if(isHeaderNotification) {
////            notificationBuilder.setFullScreenIntent(fullScreenPendingIntent, false);
////        }
//
//        notificationBuilder.setContentIntent(fullScreenPendingIntent);
//        notificationBuilder.setAutoCancel(true);

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_launcher_background) //알람 아이콘
//                .setContentTitle("Title")  //알람 제목
//                .setContentText("Text") //알람 내용
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT); //알람 중요도
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(1, builder.build()); //알람 생성





//
//        Notification notification = notificationBuilder.build();
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(001, notification);

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent intent = new Intent(context);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.star_on));
//        builder.setSmallIcon(android.R.drawable.star_on);
//        builder.setTicker("알람 간단한 설명");
//        builder.setContentTitle("알람 제목");
//        builder.setContentText("알람 내용");
//        builder.setWhen(System.currentTimeMillis());
//        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
//        builder.setContentIntent(pendingIntent);
//        builder.setAutoCancel(true);
//        builder.setNumber(999);
//        builder.addAction(android.R.drawable.star_on, "반짝", pendingIntent);
//        builder.addAction(android.R.drawable.star_off, "번쩍", pendingIntent);
//        notificationManager.notify(0, builder.build());

    }
}