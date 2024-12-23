package com.example.ticktick2.dataobject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScheduleViewModel extends ViewModel {

    public MutableLiveData<schedule> newSchedule;
    public MutableLiveData<schedule> Selected;

    public ScheduleViewModel()
    {
        newSchedule = new MutableLiveData<>();
        Selected = new MutableLiveData<>();

    }

    public void setNewSchedule(schedule newSchedule) {
        this.newSchedule.setValue(newSchedule);
    }

    public schedule getNewSchedule()
    {
        return this.newSchedule.getValue();
    }

    public void setSelectedSchedule(schedule SelectedSchedule) {
        this.Selected.setValue(SelectedSchedule);
    }

    public schedule getSelectedSchedule()
    {
        return this.Selected.getValue();
    }




}

