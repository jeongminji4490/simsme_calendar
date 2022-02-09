package com.example.mycustomcalendar;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.mycustomcalendar.databinding.ActivityTimerBinding;

import java.util.Timer;
import java.util.TimerTask;

public class TimerPage extends Fragment {

    private ActivityTimerBinding binding;
    private int hour,minute,second;
    private String sHour,sMin,sSec;
    private CountDownTimer timer;
    private boolean booleanTimer=true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.activity_timer,container,false);
        View view=binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.startTimerBtn) {
                    String inputTime=binding.hourEdit.getText().toString()
                            +binding.minEdit.getText().toString()
                            +binding.secEdit.getText().toString();
                    CountTime(inputTime);
                }
                if (id == R.id.stopTimerBtn){
                    timer.cancel();
                    Toast.makeText(getContext(),"타이머 스톱",Toast.LENGTH_SHORT).show();
                }
                if (id==R.id.initTimerBtn){
                    timer.cancel();
//                    Intent intent=getActivity().getIntent();
//                    startActivity(intent);
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
                //노티피케이션
                //Toast.makeText(getContext(),"타이머 종료!",Toast.LENGTH_SHORT).show();
            }
        }.start();

    }

}
