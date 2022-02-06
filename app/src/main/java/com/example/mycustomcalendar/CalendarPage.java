package com.example.mycustomcalendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycustomcalendar.databinding.ActivityCalendarBinding;
import com.example.mycustomcalendar.databinding.AddScheduleBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CalendarPage extends Fragment {

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat defaultDateFormat =new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat todayDateFormat=new SimpleDateFormat("MM-dd");
    @SuppressLint("SimpleDateFormat")
    DateFormat defaultMonthFormat=new SimpleDateFormat("yyyy-MM"); //메인 타이틀에 들어갈 것??
    public String selectedDate = "";

    private Date startDate = new Date();
    private Date todayDate,defaultSelectedDate;
    private Date selectedDateTime = null;

    private CalendarAdapter calendarAdapter;
    private ScheduleAdapter scheduleAdapter;
    ItemTouchHelper itemTouchHelper;

    private Date nowDate = new Date();
    private Calendar nowCal = Calendar.getInstance();
    private int nowMonth = -1;
    Database db;
    List<Event> eventList=new ArrayList<>();
    List<ScheduleItem> item=new ArrayList<>();
    int serialNum;
    ActivityCalendarBinding binding;
    AddScheduleBinding addBinding;

    //dialog
    LinearLayoutManager layoutManager;
    private String sYear,sMonth,sDay,date,sHour,sMinute,alarm,rqCode,title,from;
    int alarm_rqCode,serial_num;
    ViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.activity_calendar,container,false);
        View view=binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarAdapter = new CalendarAdapter(getContext());
        binding.recyclerView.setAdapter(calendarAdapter);
        scheduleAdapter=new ScheduleAdapter(getContext());
        layoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        binding.schedulelistRecyclerView.setLayoutManager(layoutManager);

        binding.leftBtn.setOnClickListener(v -> {
            setDate(-1);
        });
        binding.rightBtn.setOnClickListener(v -> {
            setDate(1);
        });

        selectedDateTime = Calendar.getInstance().getTime();
        Log.e("onViewCreated",defaultDateFormat.format(selectedDateTime));
        date=String.valueOf(defaultDateFormat.format(selectedDateTime));
        binding.semiTitle.setText(todayDateFormat.format(selectedDateTime));

        setCalendarList(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), getContext());
        viewModel = new ViewModelProvider(this, new ViewModelFactory(getActivity().getApplication())).get(ViewModel.class);
        selectAll();
        itemTouchHelper=new ItemTouchHelper(new SwipeController(scheduleAdapter));
        itemTouchHelper.attachToRecyclerView(binding.schedulelistRecyclerView);

        binding.addScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog writeScheduleDialog;
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                LayoutInflater inflater=getLayoutInflater();
                addBinding=DataBindingUtil.inflate(inflater,R.layout.add_schedule,null,false);
                view=addBinding.getRoot();

                builder.setView(view);
                writeScheduleDialog = builder.create();
                writeScheduleDialog.setCancelable(false);
                writeScheduleDialog.show();

                addBinding.timeSelectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int hour, minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            LocalTime now = LocalTime.now();
                            hour=now.getHour();
                            minute=now.getMinute();
                        }else{
                            Calendar c=Calendar.getInstance();
                            hour=c.get(Calendar.HOUR);
                            minute=c.get(Calendar.MINUTE);
                        }
                        Log.e("hour",String.valueOf(hour));
                        Log.e("minute",String.valueOf(minute));

                        //PM 수정
                        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.themeOnverlay_timePicker, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                String mode;
                                int n = i;
                                if (i<12) {
                                    mode = "AM";
                                }else{
                                    n = i - 12;
                                    mode = "PM";
                                }
                                sHour = Integer.toString(i);
                                sMinute = Integer.toString(i1);
                                alarm = String.valueOf(n) + ":" + sMinute + " " +mode;;
                                String[] yArray=date.split("-"); //선택한 날짜로부터 년/월/일 구하기
                                sYear=yArray[0];
                                sMonth=yArray[1];
                                sDay=yArray[2];
                                rqCode = sMonth + sDay + sHour + sMinute;
                                Log.e("rqCode",rqCode);
                                addBinding.timeShowText.setText(n+":"+i1+" "+mode);
                            }
                        }, hour, minute, false);
                        timePickerDialog.show();
                    }
                });

                addBinding.addOkBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        title = addBinding.titleInputEdit.getText().toString().trim();
                        // 제목 내용중에 하나 입력 안 한 경우(저장 x),제목 내용 둘 중에 하나 입력했지만 알람시간 선택하지 않은 경우(알람요청코드,알람시간만 저장x)
                        if (title.isEmpty()){
                            Toast.makeText(getContext(),"제목을 입력해주세요.",Toast.LENGTH_SHORT).show();
                        }else{
                            Log.e("addOkBtn",date);
                            if (!addBinding.timeShowText.getText().toString().isEmpty()){ //알람 설정했을 경우
                                try {
                                    db=Database.getInstance(getContext());
                                    alarm_rqCode=Integer.parseInt(rqCode);
                                    Insert(new Schedule(serial_num, date, title, alarm, alarm_rqCode));
                                    ScheduleItem scheduleItem=new ScheduleItem(title,alarm,date,alarm_rqCode);
                                    String from=sYear+"-"+sMonth+"-"+sDay+" "+sHour+":"+sMinute+":"+"00"; /*알람으로 설정한 날짜*/
                                    AlarmFunctions alarmFunctions =new AlarmFunctions(alarm_rqCode, title, getContext());
                                    alarmFunctions.callAlarm(from);
                                    db.alarmsDao().insert(new ActiveAlarms(serialNum,alarm_rqCode,from,title));
                                    scheduleAdapter.addItem(scheduleItem);
                                    binding.schedulelistRecyclerView.setAdapter(scheduleAdapter);
                                }catch (NumberFormatException e){
                                    Log.e("NumberFormatException", "error");
                                }
                            }else{ //알람 설정 안했을 경우
                                alarm_rqCode=0;
                                alarm="";
                                Insert(new Schedule(serial_num, date, title, alarm, alarm_rqCode));
                                //binding.schedulelistRecyclerView.setLayoutManager(layoutManager);
                                ScheduleItem scheduleItem=new ScheduleItem(title,alarm,selectedDate,alarm_rqCode);
                                scheduleAdapter.addItem(scheduleItem);
                                binding.schedulelistRecyclerView.setAdapter(scheduleAdapter);
                            }
                            Toast.makeText(getContext(),"저장", Toast.LENGTH_SHORT).show();
                        }writeScheduleDialog.dismiss();
                    }
                });
                addBinding.addCancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        writeScheduleDialog.dismiss();
                    }
                });
            }
        });

    }

    private void setDate(int addAmount) {
        Calendar calendar = Calendar.getInstance();

        if (selectedDateTime != null) {
            calendar.setTime(selectedDateTime); //현재 시간으로
        }
        calendar.add(Calendar.MONTH, addAmount); //오늘 기준으로 한 달 더하기
        startDate =calendar.getTime(); //현재 시간

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1); //1일 설정

        int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); //특정 월의 마지막 날짜
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), max); //마지막 날짜 설정

        selectedDate = defaultDateFormat.format(nowDate); //yyyy-MM
        selectedDateTime = calendar.getTime();//strStartDate;

        int setMonth = calendar.get(Calendar.MONTH) + 1;
        if (setMonth == nowMonth) {
            selectedDate = defaultDateFormat.format(nowDate);
            selectedDateTime = calendar.getTime();//strStartDate;
        } else {
            Calendar dashboardDateCal = Calendar.getInstance();
            dashboardDateCal.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
            selectedDate = defaultDateFormat.format(dashboardDateCal.getTime());//strStartDate;
            selectedDateTime = calendar.getTime();//strStartDate;
        }

        if (selectedDateTime == null) {
            calendar.setTime(startDate);
            selectedDateTime = startDate;
        } else {
            calendar.setTime(selectedDateTime);
        }
        setCalendarList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), getContext());
    }

    public void setCalendarList(int year, int month, Context context) {

        try {
            binding.titleText.setText(defaultMonthFormat.format(selectedDateTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<DayModel> dayModelList = new ArrayList<>();

        try {
            GregorianCalendar calendar = new GregorianCalendar();
            GregorianCalendar preCalendar = new GregorianCalendar();

            if (year > 0 && month > -1) {
                calendar.set(GregorianCalendar.YEAR, year);
                calendar.set(GregorianCalendar.MONTH, month);
                calendar.set(GregorianCalendar.DATE, 1);

                preCalendar.set(GregorianCalendar.YEAR, year);
                preCalendar.set(GregorianCalendar.MONTH, month);
                preCalendar.set(GregorianCalendar.DATE, 1);

            } else {
                calendar.set(GregorianCalendar.YEAR, calendar.get(Calendar.YEAR));
                calendar.set(GregorianCalendar.MONTH, calendar.get(Calendar.MONTH));
                calendar.set(GregorianCalendar.DATE, 1);

                preCalendar.set(GregorianCalendar.YEAR, calendar.get(Calendar.YEAR));
                preCalendar.set(GregorianCalendar.MONTH, calendar.get(Calendar.MONTH));
                preCalendar.set(GregorianCalendar.DATE, 1);
            }

            preCalendar.add(Calendar.MONTH, -1);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
            int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 해당 월에 마지막 요일

            int preMonthMax = preCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            //앞의 EMPTY 생성
            for (int j = 0; j < dayOfWeek; j++) {
                DayModel dayModel = new DayModel();
                dayModel.setType(102031);
                dayModel.setCalendarModel(new GregorianCalendar(calendar.get(Calendar.YEAR), (calendar.get(Calendar.MONTH) - 1), preMonthMax - (dayOfWeek - 1 - j)));
                dayModelList.add(dayModel);
            }

            if (dayOfWeek > 0) {
                //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date d = new Date(dayModelList.get(0).getCalendarModel().getTimeInMillis());
            }

            for (int j = 1; j <= max; j++) {
                DayModel dayModel = new DayModel();
                dayModel.setType(1);
                dayModel.setCalendarModel(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j));
                dayModelList.add(dayModel);
            }

            //뒤의 Empty
            if (dayModelList.size() / 7 > 0) {
                int extra = dayModelList.size() % 7;
                if (extra > 0) {
                    extra = 7 - extra;
                }
                for (int k = 0; k < extra; k++) {
                    DayModel dayModel = new DayModel();
                    dayModel.setType(102031);
                    dayModel.setCalendarModel(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, k + 1));
                    dayModelList.add(dayModel);
                }

                if (extra > 0) {
                    Date d = new Date(dayModelList.get(dayModelList.size() - 1).getCalendarModel().getTimeInMillis());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        calendarAdapter.mCalendarList = dayModelList;
        calendarAdapter.setOnItemClickListener(new CalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) throws ParseException {
                String selectedDate=dayModelList.get(position).getDate();
                todayDate=new Date(defaultDateFormat.parse(selectedDate).getTime());
                date=String.valueOf(defaultDateFormat.format(todayDate));
                binding.semiTitle.setText(todayDateFormat.format(todayDate));
                selectAll();
            }
        });
        calendarAdapter.notifyDataSetChanged();
    }

    public void Insert(Schedule schedule){
        viewModel.InsertSchedule(new Schedule(serial_num, date, title, alarm, alarm_rqCode))
                .subscribe();
        viewModel.InsertEvent(new Event(serial_num, date))
                .subscribe();
    }

    @SuppressLint("CheckResult")
    public void selectAll(){
        viewModel.getAllSchedules()
                .subscribe(s -> {
                    item.clear();
                    int i=0;
                    for (Schedule schedule : s){
                        String sDate=s.get(i).getDate();
                        if (date.equals(sDate)){
                            String title=s.get(i).getTitle();
                            String alarm=s.get(i).getAlarm();
                            int rqCode=s.get(i).getAlarm_rqCode();
                            ScheduleItem scheduleItem=new ScheduleItem(title, alarm, sDate, rqCode);
                            item.add(scheduleItem);
                        }
                        i++;
                    }
                    scheduleAdapter.updateList(item);
                    binding.schedulelistRecyclerView.setAdapter(scheduleAdapter);
                });

    }

}
