package com.simsme.mycustomcalendar.calendar;

import static android.util.Log.e;

import android.app.AlertDialog;
import android.app.Application;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simsme.mycustomcalendar.db.Database;
import com.simsme.mycustomcalendar.ui.ItemTouchHelperListener;
import com.simsme.mycustomcalendar.MainActivity;
import com.simsme.mycustomcalendar.R;
import com.simsme.mycustomcalendar.ui.ScheduleDiffUtilCallback;
import com.simsme.mycustomcalendar.alarm.ActiveAlarms;
import com.simsme.mycustomcalendar.alarm.AlarmFunctions;
import com.simsme.mycustomcalendar.databinding.ModifyScheduleBinding;
import com.simsme.mycustomcalendar.databinding.ScheduleItemsBinding;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.Holder> implements ItemTouchHelperListener {

    private List<ScheduleItem> arrayList=new ArrayList<>();
    Context context;

    String sHour, sMinute, a, rqCode, sAlarm, y, m, d, from;
    int serial_num, hour, minute, n;
    int alarm_rqCode;
    ViewModel viewModel;
    ScheduleItemsBinding binding;
    Database db;

    public ScheduleAdapter(Context context){
        this.context=context;
        viewModel=new ViewModelProvider((ViewModelStoreOwner) context, new ViewModelFactory((Application) context.getApplicationContext())).get(ViewModel.class);
        db=Database.getInstance(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        binding= DataBindingUtil.inflate(inflater, R.layout.schedule_items,parent,false);
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

    public void addItem(int serial_num, String date, String title, String alarm, int alarm_rqCode, String from){
        arrayList.add(new ScheduleItem(title,alarm,date,alarm_rqCode));
        InsertSchedule(serial_num, date, title, alarm, alarm_rqCode,from);
        AlarmFunctions alarmFunctions =new AlarmFunctions(alarm_rqCode, title, context);
        alarmFunctions.callAlarm(from);
        db.alarmsDao().insert(new ActiveAlarms(serial_num,alarm_rqCode,from,title));
    }

    @Override
    public boolean onItemMove(int from_position, int to_position) {
        return false;
    }

    @Override
    public void onItemSwipe(int position) {

    }

    @Override
    public void onLeftClick(int position, RecyclerView.ViewHolder viewHolder) {
        String title,alarm,date;
        title=arrayList.get(position).getTitle();
        alarm=arrayList.get(position).getAlarm();
        date=arrayList.get(position).getDate();
        ModifyDialog(title, alarm, date, position);
    }

    @Override
    public void onRightClick(int position, RecyclerView.ViewHolder viewHolder) {
        deleteSchedule(position);
        if (arrayList.size()==1){
            deleteEvent(arrayList.get(position).getDate());
        }
        if (arrayList.get(position).getAlarm()!=null){
            int rqCode=arrayList.get(position).getRqCode();
            String title=arrayList.get(position).getTitle();
            AlarmFunctions functions=new AlarmFunctions(rqCode,title,context);
            functions.cancelAlarm();
        }
        arrayList.remove(position);
        Intent intent=new Intent(context, MainActivity.class);
        //????????? ?????? ???????????? ?????????
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        Toast.makeText(context, "??????", Toast.LENGTH_SHORT).show();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
        void onBind(ScheduleItem item){
            binding.setSchedule(item);
        }
    }

    public void updateList(List<ScheduleItem> s){
        /*old list, new list ??????*/
        final ScheduleDiffUtilCallback diffCallBack=new ScheduleDiffUtilCallback(this.arrayList, s);
        final DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff(diffCallBack);

        this.arrayList.clear();
        this.arrayList.addAll(s);
        diffResult.dispatchUpdatesTo(this);
    }

    public void ModifyDialog(String title, String alarm, String date, int position){
        AlertDialog dialog;
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ModifyScheduleBinding binding=ModifyScheduleBinding.inflate(inflater);
        View view=binding.getRoot();

        binding.titleInputEdit.setText(title);
        binding.timeShowText.setText(alarm);

        //?????? null????????? ??????
        if(!alarm.isEmpty()){
            createDateFormat(date, alarm); //from ??????
        }
        int iRqCode=arrayList.get(position).getRqCode();

        //????????? ????????? ?????? ??????????????? ???????????? ?????? ????????? rqCode??? ????????? ?????? ???????????????
        rqCode=String.valueOf(iRqCode);
        sAlarm=String.valueOf(arrayList.get(position).getAlarm());

        builder.setView(view);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        binding.timeSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alarm.isEmpty()){ //?????? null??? ??? ?????????????????? ??????
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        LocalTime now = LocalTime.now();
                        hour=now.getHour();
                        minute=now.getMinute();
                    }else{
                        Calendar c=Calendar.getInstance();
                        hour=c.get(Calendar.HOUR);
                        minute=c.get(Calendar.MINUTE);
                    }
                }else {
                    String[] sArray1=alarm.split("\\s"); //5:17, AM
                    String[] sArray2=sArray1[0].split(":"); //5, 17

                    if (sArray1[1].equals("PM")){
                        try {
                            int f_hour=Integer.parseInt(sArray2[0]);
                            hour=f_hour+12; //??????
                        }catch (NumberFormatException e){
                            e("time_selectBtn", "NumberFormatException");
                        }
                    }else{
                        try {
                            hour=Integer.parseInt(sArray2[0]);
                        }catch (NumberFormatException e){
                            e("time_selectBtn", "NumberFormatException");
                        }
                    }
                    minute=Integer.parseInt(sArray2[1]);
                }

                TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.themeOnverlay_timePicker, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        String mode;
                        n = i;
                        if (i<12) {
                            mode = "AM";
                        }else{
                            n = i - 12;
                            mode = "PM";
                        }
                        sHour = Integer.toString(i);
                        sMinute = Integer.toString(i1);
                        String m,d,h;
                        h=Integer.toString(i);
                        String[] eventDate=date.split("-");
                        m=eventDate[1];
                        d=eventDate[2];
                        sAlarm = String.valueOf(n) + ":" + sMinute + " " +mode;
                        rqCode=m+d+h+sMinute;
                        binding.timeShowText.setText(n+":"+i1+" "+mode);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
        binding.addOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //?????? ?????? ??????
                AlarmFunctions function=new AlarmFunctions(iRqCode,title,context);
                function.cancelAlarm();
                String newTitle = binding.titleInputEdit.getText().toString().trim();

                // ?????? ???????????? ?????? ?????? ??? ??? ??????(?????? x),?????? ?????? ??? ?????? ?????? ??????????????? ???????????? ???????????? ?????? ??????(??????????????????,??????????????? ??????x)
                if (newTitle.isEmpty()){
                    Toast.makeText(context,"????????? ??????????????????.",Toast.LENGTH_SHORT).show();
                }else{
                    if (!binding.cancelAlarmBtn.isChecked() && !binding.timeShowText.getText().toString().isEmpty()){ //?????? ???????????? ??????
                        try { //???????????? ?????? ??? ??????X??? ???????????? ?????? ?????? -> ???????????? ????????? ????????? ??????
                            alarm_rqCode=Integer.parseInt(rqCode);
                            deleteSchedule(position);
                            from=createDateFormat(date, sAlarm);
                            InsertSchedule(serial_num, date, newTitle, sAlarm, alarm_rqCode,from);
                            db.alarmsDao().insert(new ActiveAlarms(serial_num,alarm_rqCode,from,newTitle));
                            AlarmFunctions newFunction =new AlarmFunctions(alarm_rqCode, newTitle, context);
                            newFunction.callAlarm(from); //?????? ?????????
                            Toast.makeText(context,"??????", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }catch (NumberFormatException e){
                            e("NumberFormatException", "error");
                        }
                    }else if(binding.cancelAlarmBtn.isChecked()){
                        deleteSchedule(position); //?????? ???????????? ?????? ?????????, ??????X??? ????????? ?????? -> ???????????? ????????? ???????????? ?????? ??????
                        alarm_rqCode=0;
                        a="";
                        from="";
                        InsertSchedule(serial_num, date, newTitle, a, alarm_rqCode,from);
                        Toast.makeText(context,"??????", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else if(!binding.cancelAlarmBtn.isChecked() && binding.timeShowText.getText().toString().isEmpty()){ //???????????? ????????? ???????????? ?????????, ?????? ????????? ???????????? ?????? ??????
                        Toast.makeText(context,"?????? ????????? ??????????????????",Toast.LENGTH_SHORT).show();
                    }
                    else{ //??????????????? ????????? ????????? ??????????????? ????????? ??????X??? ????????? ??????
                        Toast.makeText(context,"?????? ????????? ??????????????????",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.addCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void InsertSchedule(int serial_num, String date, String title, String alarm, int alarm_rqCode, String from){
        viewModel.InsertSchedule(new Schedule(serial_num, date, title, alarm, alarm_rqCode));
        viewModel.InsertEvent(new Event(serial_num, date));
    }


    public void deleteSchedule(int i){
        viewModel.DeleteSchedule(arrayList.get(i).title,arrayList.get(i).getAlarm(),arrayList.get(i).getDate());
        db.alarmsDao().delete(arrayList.get(i).getRqCode());
    }

    public void deleteEvent(String d){
        viewModel.DeleteEvent(d);
    }


    private String createDateFormat(String date, String alarm){
        String[] date1=date.split("-"); // ???/???/???
        y=date1[0];
        m=date1[1];
        d=date1[2];
        String[] sArray1=alarm.split("\\s"); //5:17, AM
        String[] sArray2=sArray1[0].split(":"); //5, 17

        if (sArray1[1].equals("PM")){
            try {
                int f_hour=Integer.parseInt(sArray2[0]);
                hour=f_hour+12; //??????
                Log.e("hour", String.valueOf(hour));
            }catch (NumberFormatException e){
                Log.e("time_selectBtn", "NumberFormatException");
            }
        }else{
            try {
                hour=Integer.parseInt(sArray2[0]);
                Log.e("hour", String.valueOf(hour));
            }catch (NumberFormatException e){
                Log.e("time_selectBtn", "NumberFormatException");
            }
        }
        minute=Integer.parseInt(sArray2[1]);
        return y+"-"+m+"-"+d+" "+String.valueOf(hour)+":"+String.valueOf(minute)+":"+"00";
    }
}
