package com.example.mycustomcalendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

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
    private ImageButton left_btn, right_btn, addScheduleBtn;
    private TextView titleText, semiTitle;

    public RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;

    private Date nowDate = new Date();//주에 오늘 날짜가 포함하는지 보기위해.
    private Calendar nowCal = Calendar.getInstance();
    private int nowMonth = -1;
    Database db;
    List<Event> eventList=new ArrayList<>();
    int serialNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarRecyclerView = findViewById(R.id.recyclerView);
        calendarAdapter = new CalendarAdapter(this);
        calendarRecyclerView.setAdapter(calendarAdapter);
        left_btn = findViewById(R.id.left_btn);
        right_btn = findViewById(R.id.right_btn);
        addScheduleBtn = findViewById(R.id.addSchedule);
        titleText = findViewById(R.id.titleText);
        semiTitle = findViewById(R.id.semiTitle);

        left_btn.setOnClickListener(v -> {
            setDate(-1);
        });
        right_btn.setOnClickListener(v -> {
            setDate(1);
        });

        selectedDateTime = Calendar.getInstance().getTime();
        semiTitle.setText(todayDateFormat.format(selectedDateTime));

        setCalendarList(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), MainActivity.this);
        db=Database.getInstance(MainActivity.this);

        addScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event e=new Event(serialNum,defaultDateFormat.format(todayDate));
                db.eventDao().insert(e);
                Toast.makeText(MainActivity.this,"저장",Toast.LENGTH_SHORT).show();
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
        setCalendarList(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), MainActivity.this);
    }

    public void setCalendarList(int year, int month, Context context) {

        try {
            titleText.setText(defaultMonthFormat.format(selectedDateTime));
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
                    //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
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
                //defaultSelectedDate=new Date(defaultDateFormat.parse(selectedDate).getTime());
                semiTitle.setText(todayDateFormat.format(todayDate));
                Toast.makeText(context,semiTitle.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });
        calendarAdapter.notifyDataSetChanged();
    }

}