package com.example.mycustomcalendar;

import android.os.CountDownTimer;

public class Timer extends CountDownTimer {
    String inputTime;

    public Timer(long millisInFuture, long countDownInterval, String inputTime) {
        super(millisInFuture, countDownInterval);
        this.inputTime=inputTime;
    }

    @Override
    public void onTick(long l) {
        String sHour=String.valueOf(l/(60*60*1000));
        long lMin=l-(l/(60*60*1000));
        String sMin=String.valueOf(lMin/(60*1000));
        String sSec=String.valueOf(lMin%(60*1000)/1000);
        String sMilliSec=String.valueOf((lMin%(60*1000))%1000);

        if (sHour.length()==1){ //시간이 한자리면 0을 붙임
            sHour="0"+sHour;
        }
        if (sMin.length()==1){
            sMin="0"+sMin;
        }
        if (sSec.length()==1){
            sSec="0"+sSec;
        }

    }

    @Override
    public void onFinish() {

    }
}
