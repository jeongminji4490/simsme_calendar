package com.example.mycustomcalendar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpcomingPage extends Fragment {

    //또다른 프래그먼트에 가려질 때를 고려하여 작성할 것
    ViewModel viewModel;
    UpcomingScheduleAdapter adapter;
    LinearLayoutManager manager;
    RecyclerView recyclerView;
    int y= CalendarDay.today().getYear();
    int m= CalendarDay.today().getMonth()+1;
    int d=CalendarDay.today().getDay();
    String sToday=String.valueOf(y)+"-"+String.valueOf(m)+"-"+String.valueOf(d);
    Date today;
    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    List<String> dates=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.activity_upcoming, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter=new UpcomingScheduleAdapter(getContext());
        manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView=(RecyclerView)view.findViewById(R.id.upScheduleRecyclerView);

        LayoutInflater inflater=LayoutInflater.from(getContext());

        try {
            today=dateFormat.parse(sToday);
            Log.e("today", String.valueOf(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication())).get(ViewModel.class);

        recyclerView.setLayoutManager(manager);
        selectAll();
    }

    @SuppressLint("CheckResult")
    public void selectAll(){
        dates.clear();
        viewModel.getAllEvents()
                .subscribe(e->{
                    int i=0; //31일?
                    for (Event event : e){
                        if (i==0){
                            for(int n=0;n<e.size();n++){
                                dates.add(e.get(n).getDate());
                            }
                            Collections.sort(dates);
                        }
                        String originDate=dates.get(i);
                        System.out.println("date"+dates);
                        Date stringToDate= dateFormat.parse(originDate);
                        //이벤트의 날짜가 오늘 이후인지 확인(날짜 비교)
                        if (stringToDate.equals(today) || stringToDate.after(today)){
                            viewModel.getAllSchedules()
                                    .subscribe(s -> {
                                        int j=0;
                                        UpcomingScheduleItem item=new UpcomingScheduleItem();
                                        item.setDate(originDate);
                                        item.setViewType(1);
                                        adapter.addItem(item);
                                        recyclerView.setAdapter(adapter);
                                        Log.e("originDate",originDate);
                                        for (Schedule schedule : s){
                                            //동일 날짜가 여러개라??흠
                                            if(s.get(j).getDate().equals(originDate)){
                                                //Log.e("비교", String.valueOf(date1.after(today)));
                                                String title=s.get(j).getTitle();
                                                String alarm=s.get(j).getAlarm();
                                                Log.e("title" , title);

                                                UpcomingScheduleItem scheduleItem=new UpcomingScheduleItem();
                                                scheduleItem.setTitle(title);
                                                scheduleItem.setAlarm(alarm);
                                                scheduleItem.setViewType(0);
                                                adapter.addItem(scheduleItem);
                                                recyclerView.setAdapter(adapter);
                                            }
                                            j++;
                                        }
                                    });
                        }
                        i++;
                    }
                });
    }


}
