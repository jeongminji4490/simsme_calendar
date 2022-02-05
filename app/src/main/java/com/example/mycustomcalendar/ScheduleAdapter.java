package com.example.mycustomcalendar;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycustomcalendar.databinding.ScheduleItemsBinding;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.Holder>{

    private List<ScheduleItem> arrayList=new ArrayList<>();
    Context context;

    String sHour, sMinute, a, rqCode, sAlarm, y, m, d;
    int serial_num, hour, minute, n;
    int alarm_rqCode;
    ViewModel viewModel;
    ScheduleItemsBinding binding;

    public ScheduleAdapter(Context context){
        this.context=context;
        viewModel=new ViewModelProvider((ViewModelStoreOwner) context, new ViewModelFactory((Application) context.getApplicationContext())).get(ViewModel.class);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        binding= DataBindingUtil.inflate(inflater,R.layout.schedule_items,parent,false);
        View view=binding.getRoot();
        return new ScheduleAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.onBind(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void addItem(ScheduleItem item){
        arrayList.add(item);
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
        void onBind(ScheduleItem item){
            binding.setSchedule(item);
        }
    }
}
