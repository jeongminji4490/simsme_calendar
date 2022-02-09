package com.example.mycustomcalendar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.OnErrorNotImplementedException;
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
    //List<Date> newDates;
    Date[] newDates;
    Observable<List<Schedule>> scheduleList;


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
        scheduleList=viewModel.getAllSchedules();

        recyclerView.setLayoutManager(manager);


        selectAll();
    }


    //예정된 일정 리스트업
    @SuppressLint("CheckResult")
    public void selectAll() {
        viewModel.getAllEvents()
                .subscribe(e->{
                    dates.clear();
                    for (Event event : e){
                        dates.add(event.getDate());
                    }
                    int i=0; //31일?
                    setDateArray(e.size());
                    Log.e("e size", String.valueOf(e.size()));
                    System.out.println(dates); //왜 date size가 많이 찍히지??
                        Collections.sort(dates);
                        Log.e("dates size", String.valueOf(dates.size()));
                        if (dates.size()!=0){
                            for (int n=0;n<dates.size();n++){
                                Log.e("n", String.valueOf(n));
                                newDates[n]=dateFormat.parse(dates.get(n));
                            }
                            Log.e("length", String.valueOf(newDates.length));
                            Log.e("tag", String.valueOf(today));
                            viewModel.getAllSchedules()
                                    .subscribe(s->{
                                        if (newDates.length!=0){
                                            for (int n=0;n< newDates.length;n++){
                                                if (newDates[n].equals(today) || newDates[n].after(today)){
                                                    UpcomingScheduleItem item=new UpcomingScheduleItem();
                                                    item.setDate(dates.get(n));
                                                    item.setViewType(1);
                                                    adapter.addItem(item);
                                                    recyclerView.setAdapter(adapter);
                                                    String o=dates.get(n);

                                                    for (Schedule schedule : s){
                                                        if (schedule.getDate().equals(o)){
                                                            String title=schedule.getTitle();
                                                            String alarm=schedule.getAlarm();
                                                            UpcomingScheduleItem scheduleItem=new UpcomingScheduleItem();
                                                            scheduleItem.setTitle(title);
                                                            scheduleItem.setAlarm(alarm);
                                                            scheduleItem.setViewType(0);
                                                            adapter.addItem(scheduleItem);
                                                            recyclerView.setAdapter(adapter);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    });
                        }
                });
    }

    void setDateArray(int size){
        newDates=new Date[size];
    }


}
