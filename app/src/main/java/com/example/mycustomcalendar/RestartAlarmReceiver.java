package com.example.mycustomcalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RestartAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Database db=Database.getInstance(context);
            List<ActiveAlarms> alarmList=new ArrayList<>();
            alarmList=db.alarmsDao().findAll();
            for (int i=0;i<alarmList.size();i++){
                int rqCode=alarmList.get(i).getRqCode();
                String from=alarmList.get(i).getFrom();
                String title=alarmList.get(i).getTitle();
                Log.e("AlarmReceiver is Called", String.valueOf(rqCode)+","+from+","+title);
                AlarmFunctions rebootAlarms=new AlarmFunctions(rqCode,title,context);
                rebootAlarms.callAlarm(from);
            }
        }
    }
}
