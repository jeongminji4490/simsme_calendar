package com.example.mycustomcalendar;

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
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
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
    public boolean isTime = true;

    public List<String> holiday = new ArrayList<>();
    public List<DayModel> mCalendarList;

    private Context mContext;

    private NumberFormat numformat = NumberFormat.getIntegerInstance();
    String today;

    public CalendarAdapter(Context context){
        numformat.setMinimumIntegerDigits(2);
        mContext = context;

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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        DayViewHolder holder = (DayViewHolder) viewHolder; //DayViewHolder로 캐스팅

        if (viewType == EMPTY_TYPE) //비어있는 날짜면
        {
            if(position%7 == 0)
            {
                if(today.equals(onlyDate.format(mCalendarList.get(position).getCalendarModel().getTime())))
                {
                    //holder.mainLL.setBackgroundResource(R.drawable.background_border_top_blue);
                }
                else {

                    //holder.mainLL.setBackgroundResource(R.drawable.background_border_top);
                }
            }
            else
            {
                if(today.equals(onlyDate.format(mCalendarList.get(position).getCalendarModel().getTime())))
                {
                    //holder.mainLL.setBackgroundResource(R.drawable.background_border_left_top_blue);
                }
                else {

                    //holder.mainLL.setBackgroundResource(R.drawable.background_border_left_top);
                }
            }


            holder.timeText.setBackground(null);
            //holder.timeText.setTextColor(mContext.getResources().getColor(R.color.disable_color));
            //holder.timeText.setTextColor(mContext.getResources().getColor(Color.parseColor("#CCCCCC")));
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

            if(position%7 == 0)
            {

                if(today.equals(onlyDate.format(mCalendarList.get(position).getCalendarModel().getTime())))
                {
                    //holder.mainLL.setBackgroundResource(R.drawable.background_border_top_blue);
                }
                else {

                    //holder.mainLL.setBackgroundResource(R.drawable.background_border_top);
                }
            }
            else
            {
                if(today.equals(onlyDate.format(mCalendarList.get(position).getCalendarModel().getTime())))
                {
                    //holder.mainLL.setBackgroundResource(R.drawable.background_border_left_top_blue);
                }
                else {

                    //holder.mainLL.setBackgroundResource(R.drawable.background_border_left_top);
                }
            }

            holder.timeText.setBackground(null);
            holder.timeText.setTextColor(mContext.getResources().getColor(R.color.purple_200));

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

            dayText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    if (pos!=RecyclerView.NO_POSITION){
                        DayModel model=mCalendarList.get(pos);
                        Log.e("item", model.getDate());
                    }else{
                        Log.e("Adapter", "NO Position");
                    }
                }
            });
        }
    }
}
