package com.example.ticktick2.dataobject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class diary implements Comparable<diary> {

    public LocalDate date;
    public LocalDateTime time;
    public LocalDateTime edittime;
    public String text;

    public diary(diary _diary)
    {
        this.date=_diary.date;
        this.time=_diary.time;
        this.edittime=_diary.edittime;
        if(_diary.text!=null)
        {
            this.text = new String(_diary.text);
        }
    }

    public diary()
    {

    }

    //text는 깊은복사입니다
    public diary(LocalDate _date, LocalDateTime _time, LocalDateTime _time2 ,String _text)
    {
        this.date=_date;
        this.time=_time;
        this.edittime = _time2;
        if(_text==null) this.text =null;
        else
        {
            this.text = new String(_text);
        }


    }

    public int compareTo(diary other)
    {
        return -this.date.compareTo(other.date);
    }


    public boolean nullcheck(Object a,Object b)
    {
        if(a==null && b!=null) return false;
        if(b==null && a!=null) return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        diary other = (diary) o;

        if(!nullcheck(other.date,this.date))
        {
            return false;
        }

        else
        {
            if(other.date !=null && this.date !=null && !other.date.equals(this.date))
                return false;
        }

        if(!nullcheck(other.time,this.time))
        {
            return false;
        }

        else
        {
            if(other.time !=null && this.time !=null && !other.time.equals(this.time))
                return false;
        }

        if(!nullcheck(other.edittime,this.edittime))
        {
            return false;
        }

        else
        {
            if(other.edittime !=null && this.edittime !=null && !other.edittime.equals(this.edittime))
                return false;
        }

        if(!nullcheck(other.text,this.text))
        {
            return false;
        }

        else
        {
            if(other.text !=null && this.text !=null && !other.text.equals(this.text))
                return false;
        }
       return true;
    }

    
}
