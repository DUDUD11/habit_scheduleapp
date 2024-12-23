package com.example.ticktick2.ui.habit;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import java.util.Map;


public class EventDecorator implements DayViewDecorator {
    private final Drawable dateColor; // 날짜별 색상 매핑
    private CalendarDay currentDay; // 데코레이터에서 현재 처리 중인 날짜



    public EventDecorator(CalendarDay day,Drawable dateColorMap) {

        this.currentDay = day;
        this.dateColor = dateColorMap;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // 현재 날짜를 저장하고, 매핑에 포함된 날짜인지 확인
        return currentDay.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {

       // Drawable drawable = dateColorMap.get(currentDay);

            view.setBackgroundDrawable(dateColor);
    }
}
