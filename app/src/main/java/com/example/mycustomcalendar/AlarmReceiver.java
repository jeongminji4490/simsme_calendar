package com.example.mycustomcalendar;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver(){
    }
    NotificationManager manager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static final String CHANNEL_ID="channel";
    private static final String CHANNEL_NAME="Channel1";

    @SuppressLint("CheckResult")
    @Override
    public void onReceive(Context context, Intent intent) {

        Database db=Database.getInstance(context);
        builder=null;
        manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){ //Oreo 이상
            manager.createNotificationChannel( //NotificationChannel 인스턴스를 createNotificationChannel()에 전달하여 앱 알림 채널을 시스템에 등록
                    new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder=new NotificationCompat.Builder(context, CHANNEL_ID);

        }else {
            builder=new NotificationCompat.Builder(context);
        }

        Intent intent2=new Intent(context, AlarmService.class);
        int requestCode=intent.getExtras().getInt("requestCode");
        String title=intent.getExtras().getString("alarmTitle");

        Log.e("AlarmReceiver is Called", String.valueOf(requestCode));
        PendingIntent pendingIntent=PendingIntent.getActivity(context,requestCode,intent2,PendingIntent.FLAG_UPDATE_CURRENT); //Activity를 시작하는 인텐트 생성
        db.alarmsDao().delete(requestCode);

        builder.setContentTitle(title);
        builder.setAutoCancel(true); //알림창 터치시 자동 삭제
        builder.setSmallIcon(R.drawable.bell);
        builder.setContentIntent(pendingIntent);
        Notification notification=builder.build(); //Notification 객체 생성
        manager.notify(1,notification); //NotificationManager에게 알림 요청
    }

}
