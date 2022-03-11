package com.simsme.mycustomcalendar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ScheduleDiffUtilCallback extends DiffUtil.Callback {

    private final List<ScheduleItem> oldList;
    private final List<ScheduleItem> newList;

    public ScheduleDiffUtilCallback(List<ScheduleItem> oldList, List<ScheduleItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getTitle()==newList.get(newItemPosition).getTitle();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final ScheduleItem oldSchedule=oldList.get(oldItemPosition);
        final ScheduleItem newSchedule=newList.get(newItemPosition);
        return oldSchedule.getAlarm().equals(newSchedule.getAlarm());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
