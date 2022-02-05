package com.example.mycustomcalendar;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SimpleDateFormat onlyDate=new SimpleDateFormat("yyyy-MM-dd");

    private final int HEADER_TYPE = 0;
    private final int EMPTY_TYPE = 1;
    private final int DAY_TYPE = 2;
    //public boolean isTime = true;

    public List<String> holiday = new ArrayList<>();
    public List<DayModel> mCalendarList;
    List<Event> eventList=new ArrayList<>();

    private Context mContext;
    private OnItemClickListener mListener=null;

    private NumberFormat numformat = NumberFormat.getIntegerInstance();
    String today;
    Database db;
    ViewModel viewModel;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) throws ParseException;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }

    public CalendarAdapter(Context context){
        numformat.setMinimumIntegerDigits(2);
        mContext = context;
        db=Database.getInstance(context);
        viewModel=new ViewModelProvider((ViewModelStoreOwner) context, new ViewModelFactory((Application) context.getApplicationContext())).get(ViewModel.class);
        //eventList=db.eventDao().findAll();


        Date currentTime = Calendar.getInstance().getTime(); //현재시간
        today = onlyDate.format(currentTime); //오늘 날짜를 yyyy-MM-dd 형식으로 변환
    }

    @Override
    public int getItemViewType(int position) {
        DayModel item = mCalendarList.get(position);
        if (item.getType() == 102031) {
            return EMPTY_TYPE; // 비어있는 일자 타입
        } else {
            return DAY_TYPE; // 일자 타입
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new DayViewHolder(inflater.inflate(R.layout.viewholder_day, parent, false));
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        DayViewHolder holder = (DayViewHolder) viewHolder; //DayViewHolder로 캐스팅

        if (viewType == EMPTY_TYPE) //비어있는 날짜면
        {
            holder.timeText.setBackground(null);
            holder.timeText.setTextColor(Color.parseColor("#CCCCCC"));

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("d");
                //getTimeInMillis -> 현재 시간을 밀리초로 변환
                //리스트의 현재 위치의 날짜를 가져옴
                Date d = new Date(mCalendarList.get(position).getCalendarModel().getTimeInMillis());
                holder.dayText.setText(formatter.format(d).toUpperCase()); //dayText는 날짜?

                Calendar cal = Calendar.getInstance();
                cal.setTime(d); //날짜 설정

                int dayNum = cal.get(Calendar.DAY_OF_WEEK); //설정한 날짜에 해당하는 요일 번호

                if(holiday.contains(onlyDate.format(d)) || dayNum == 1) //일요일이면
                {
                    holder.dayText.setTextColor(Color.RED);
                }
                else
                {
                    holder.timeText.setTextColor(Color.parseColor("F0FFF0")); //해당 날짜가 아니면 텍스트 안보이게
                }
            } catch (Exception e) {
                holder.dayText.setText("");
            }
        }
        else if (viewType == DAY_TYPE) {
            holder.timeText.setBackground(null);
            holder.timeText.setTextColor(mContext.getResources().getColor(R.color.purple_200));

            if (getItemCount()!=0){
                DotAll(position,holder);
            }

            //에러
//            viewModel.getAllEvents()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(eventList -> {
//                        for(Event e : eventList){
//                            if (getItemCount()==0 && e.date.equals(mCalendarList.get(position).getDate())){
//                                Log.e("calendaradapter","getAllEvents");
//                                holder.mainCL.setBackgroundResource(R.color.mainColor);
//                                break;
//                            }
//                        }
//                    });

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("d");
                Date d = new Date(mCalendarList.get(position).getCalendarModel().getTimeInMillis());
                holder.dayText.setText(formatter.format(d).toUpperCase());

                Calendar cal = Calendar.getInstance();
                cal.setTime(d);

                int dayNum = cal.get(Calendar.DAY_OF_WEEK);

                if(holiday.contains(onlyDate.format(d)) || dayNum == 1)
                {
                    holder.dayText.setTextColor(Color.RED);
                }
                else
                {
                    holder.dayText.setTextColor(mContext.getResources().getColor(R.color.black));
                }
            } catch (Exception e) {
                holder.dayText.setText("");
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mCalendarList != null) {
            return mCalendarList.size();
        }
        return 0;
    }

    public class DayViewHolder extends RecyclerView.ViewHolder{
        public TextView dayText;
        public TextView timeText;
        public ConstraintLayout mainCL;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayText);
            timeText = itemView.findViewById(R.id.timeText);
            mainCL=itemView.findViewById(R.id.mainCL);

            timeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION){
                        if (mListener!=null){
                            try {
                                mListener.onItemClick(view, pos);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        Log.e("DayViewHolder", "NO Position");
                    }
                }
            });
        }
    }

    @SuppressLint("CheckResult")
    public void DotAll(int position, DayViewHolder holder){
        viewModel.getAllEvents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventList -> {
                    for(Event e : eventList){
                        if (e.date!=null && e.date.equals(mCalendarList.get(position).getDate())){
                            Log.e("calendaradapter","getAllEvents");
                            holder.mainCL.setBackgroundResource(R.color.mainColor);
                            break;
                        }
                    }
                });
    }

}
