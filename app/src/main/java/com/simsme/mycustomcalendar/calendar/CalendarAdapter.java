package com.simsme.mycustomcalendar.calendar;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.simsme.mycustomcalendar.db.Database;
import com.simsme.mycustomcalendar.ui.DayModel;
import com.simsme.mycustomcalendar.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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


        Date currentTime = Calendar.getInstance().getTime(); //????????????
        today = onlyDate.format(currentTime); //?????? ????????? yyyy-MM-dd ???????????? ??????
    }

    @Override
    public int getItemViewType(int position) {
        DayModel item = mCalendarList.get(position);
        if (item.getType() == 102031) {
            return EMPTY_TYPE; // ???????????? ?????? ??????
        } else {
            return DAY_TYPE; // ?????? ??????
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
        DayViewHolder holder = (DayViewHolder) viewHolder; //DayViewHolder??? ?????????

        if (viewType == EMPTY_TYPE) //???????????? ?????????
        {
            holder.timeText.setBackground(null);
            holder.timeText.setTextColor(Color.parseColor("#CCCCCC"));

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("d");
                //getTimeInMillis -> ?????? ????????? ???????????? ??????
                //???????????? ?????? ????????? ????????? ?????????
                Date d = new Date(mCalendarList.get(position).getCalendarModel().getTimeInMillis());
                holder.dayText.setText(formatter.format(d).toUpperCase()); //dayText??? ???????

                Calendar cal = Calendar.getInstance();
                cal.setTime(d); //?????? ??????

                int dayNum = cal.get(Calendar.DAY_OF_WEEK); //????????? ????????? ???????????? ?????? ??????

                if(holiday.contains(onlyDate.format(d)) || dayNum == 1) //???????????????
                {
                    holder.dayText.setTextColor(Color.RED);
                }
                else
                {
                    holder.timeText.setTextColor(Color.parseColor("F0FFF0")); //?????? ????????? ????????? ????????? ????????????
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
                .subscribe(eventList -> {
                    for (Event e : eventList){
                        if (e.date!=null && e.date.equals(mCalendarList.get(position).getDate())){
                            Log.e("calendaradapter","getAllEvents");
                            holder.mainCL.setBackgroundResource(R.color.mainColor);
                            break;
                        }
                    }
                });

    }

}
