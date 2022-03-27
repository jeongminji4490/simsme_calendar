package com.simsme.mycustomcalendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.simsme.mycustomcalendar.databinding.ActivityTimerBinding;

public class TimerPage extends Fragment {

    private ActivityTimerBinding binding;
    private int hour,minute,second;
    private String sHour,sMin,sSec;
    private CountDownTimer timer;
    private boolean booleanTimer=true;
    private static final String CHANNEL_ID="timer_channel";
    private static final String CHANNEL_NAME="t_channel";
    private static final int NOTIFICATION_ID=0;
    private NotificationManager notificationManager;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= ActivityTimerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext=getContext().getApplicationContext();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.startTimerBtn) {
                    try {
                        String inputTime=binding.hourEdit.getText().toString()
                                +binding.minEdit.getText().toString()
                                +binding.secEdit.getText().toString();
                        CountTime(inputTime);
                    }catch (StringIndexOutOfBoundsException e){
                        Toast.makeText(mContext,"시간은 두 자릿수로 입력해주세요 ex) 1분->01 ",Toast.LENGTH_SHORT).show();
                    }
                }
                if (id == R.id.stopTimerBtn){
                    timer.cancel();
                    Toast.makeText(getContext(),"타이머 스톱",Toast.LENGTH_SHORT).show();
                }
                if (id==R.id.initTimerBtn){
                    timer.cancel();
                    binding.hourEdit.setText("00");
                    binding.minEdit.setText("00");
                    binding.secEdit.setText("00");
                }
            }
        };

        binding.startTimerBtn.setOnClickListener(onClickListener);
        binding.stopTimerBtn.setOnClickListener(onClickListener);
        binding.initTimerBtn.setOnClickListener(onClickListener);
    }

    public void CountTime(String inputTime){
        sHour=inputTime.substring(0,2);
        sMin=inputTime.substring(2,4);
        sSec=inputTime.substring(4,6);

        long lInputTime=0;
        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간

        if (sHour.substring(0, 1) == "0") {
            sHour=sHour.substring(1,2);
        }
        if (sMin.substring(0, 1) == "0") {
            sMin=sMin.substring(1,2);
        }
        if (sSec.substring(0, 1) == "0") {
            sSec=sSec.substring(1,2);
        }
        // 변환시간
        lInputTime = Long.parseLong(sHour) * 1000 * 3600 + Long.parseLong(sMin) * 60 * 1000 + Long.parseLong(sSec) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        timer=new CountDownTimer(lInputTime, 1000){
            @Override
            public void onTick(long l) { //특정 시간마다 뷰 변경

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

                binding.hourEdit.setText(sHour);
                binding.minEdit.setText(sMin);
                binding.secEdit.setText(sSec);

            }

            @Override
            public void onFinish() {
                //화면 전환시 getContext를 사용하면 오류가 발생하기 때문에 애플리케이션 자체와 연동되는 getApplicationContext사용
                createNotification(mContext);
            }
        }.start();

    }

    public void createNotification(Context context){
        NotificationCompat.Builder builder;
        notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){ //Oreo 이상
            notificationManager.createNotificationChannel( //NotificationChannel 인스턴스를 createNotificationChannel()에 전달하여 앱 알림 채널을 시스템에 등록
                    new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder=new NotificationCompat.Builder(context, CHANNEL_ID);

        }else {
            builder=new NotificationCompat.Builder(context);
        }

        builder.setContentTitle("타이머 종료!");
        builder.setAutoCancel(true); //알림창 터치시 자동 삭제
        builder.setSmallIcon(R.drawable.bell);
        Notification notification=builder.build(); //Notification 객체 생성
        notificationManager.notify(NOTIFICATION_ID,notification); //NotificationManager에게 알림 요청
    }

}
