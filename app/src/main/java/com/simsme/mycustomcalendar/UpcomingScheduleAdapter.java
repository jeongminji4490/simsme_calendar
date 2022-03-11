package com.simsme.mycustomcalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UpcomingScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<UpcomingScheduleItem> list=new ArrayList<>();

    final int SCHEDULE=0;
    final int DATE=1;

    public UpcomingScheduleAdapter(Context context){
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //3
        switch (viewType){
            case SCHEDULE:
                LayoutInflater sInflater=LayoutInflater.from(context);
                View sView=sInflater.inflate(R.layout.upcoming_schedule_items, parent, false);
                return new scheduleViewHolder(sView);
            case DATE:
                LayoutInflater dInflater=LayoutInflater.from(context);
                View dView=dInflater.inflate(R.layout.upcoming_date_item, parent, false);
                return new dateViewHolder(dView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { //4
        if (getItemViewType(position)==SCHEDULE){
            scheduleViewHolder viewHolder=(scheduleViewHolder) holder;
            viewHolder.title.setText(list.get(position).getTitle());
            viewHolder.alarm.setText(list.get(position).getAlarm());
        }else{
            dateViewHolder viewHolder=(dateViewHolder) holder;
            viewHolder.date.setText(list.get(position).getDate());
        }
    }

    @Override
    public int getItemViewType(int position) { //2
        return (list.get(position).getViewType()==0) ? SCHEDULE : DATE;
    }

    @Override
    public int getItemCount() { //1
        return list.size();
    }

    public void addItem(UpcomingScheduleItem item){
        list.add(item);
    }

    public static class scheduleViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView alarm;
        public scheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title_inList);
            alarm=itemView.findViewById(R.id.alarm_inList);
        }
    }

    public static class dateViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        public dateViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.upcoming_date_text);
        }
    }
}
