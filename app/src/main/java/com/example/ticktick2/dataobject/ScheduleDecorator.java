package com.example.ticktick2.dataobject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

import androidx.core.content.ContextCompat;

import com.example.ticktick2.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class ScheduleDecorator implements DayViewDecorator {

    private final Drawable dateDrawable ; // 날짜별 색상 매핑
    private CalendarDay currentDay; // 데코레이터에서 현재 처리 중인 날짜

    public ScheduleDecorator(CalendarDay day, Context context) {

        this.currentDay = day;
        dateDrawable = ContextCompat.getDrawable(context, R.drawable.blue_squre);



    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // 현재 날짜를 저장하고, 매핑에 포함된 날짜인지 확인
        return currentDay.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {



        view.setBackgroundDrawable(dateDrawable);



    }


}

