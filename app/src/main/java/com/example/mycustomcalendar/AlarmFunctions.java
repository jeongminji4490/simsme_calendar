package com.example.mycustomcalendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AlarmFunctions {
    int rqCode,serialNum;
    String title;
    Context context;
    Database db;

    public AlarmFunctions(int rqCode, String title, Context context){
        this.rqCode=rqCode;
        this.title=title;
        this.context=context;
        db=Database.getInstance(context);
    }

    @SuppressLint("MissingPermission")
    public void callAlarm(String from){
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent receiverIntent=new Intent(context, AlarmReceiver.class); //리시버로 전달될 인텐트 설정
        receiverIntent.putExtra("requestCode",rqCode); //요청 코드를 리시버에 전달
        receiverIntent.putExtra("alarmTitle",title); //수정_일정 제목을 리시버에 전달

        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,rqCode,receiverIntent,PendingIntent.FLAG_UPDATE_CURRENT); /*getBroadcast(fromContext,customRequestcode,toIntent,flag)*/
        Log.w("InCalendarPage_RC", String.valueOf(rqCode));

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        Log.e("dateFormat", String.valueOf(dateFormat));
        Date datetime=new Date();
        try {
            datetime=dateFormat.parse(from);
        }catch (ParseException e){
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(datetime);
        Log.e("datetime", String.valueOf(datetime));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }else { //API 23(android 6.0) 이상(해당 api 레벨부터 도즈모드 도입으로 setExact 사용 시 알람이 울리지 않음)
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }
    }

    public void cancelAlarm(){
        AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(context,rqCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        //요청코드 디비에서 삭제
        db.alarmsDao().delete(rqCode);
        Log.e("Alarm is canceled",String.valueOf(rqCode));
    }
}
