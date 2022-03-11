package com.simsme.mycustomcalendar;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;

import androidx.annotation.Nullable;

public class RestartAlarmService extends IntentService {

    public RestartAlarmService() {
        super("RestartAlarmService");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
