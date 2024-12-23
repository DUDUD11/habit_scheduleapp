package com.example.ticktick2.dataobject;

import java.time.LocalDate;
import java.util.Objects;

public class schedule  implements Comparable<schedule>{

    public LocalDate due_date;
    public int importance;

    public String text;

    public boolean  done;

    public enum seriousness
    {
        important_busy,

        nimportant_busy,

        important_notbusy,
        nimportant_notbusy

    }

    @Override
    public int compareTo(schedule other) {
        // 1. importance 비교

        int dateCompare = this.due_date.compareTo(other.due_date);
        if (dateCompare != 0) {
            return dateCompare;
        }

        int importanceCompare = Integer.compare(this.importance, other.importance);
        if (importanceCompare != 0) {
            return importanceCompare;
        }

        // 3. text 비교
        return this.text.compareTo(other.text);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        schedule task = (schedule) o;
        return importance == task.importance &&
                (due_date == task.due_date) &&
                (text.equals(task.text));
    }

    @Override
    public int hashCode() {
        return Objects.hash(importance, due_date, text);
    }


    public schedule()
    {

    }

    public schedule(int _importance, LocalDate _duedate,String _text,boolean _done)
    {
        this.importance=_importance;
        this.due_date=_duedate;
        this.text=_text;
        this.done = _done;
    }

    public void clear()
    {
        this.importance=0;
        this.due_date=LocalDate.now();
        this.text=null;
        this.done = false;
    }

    public void deepCopySchedule(schedule original)
    {
        this.importance=original.importance;
        this.text=new String(original.text);
        this.done = original.done;
        this.due_date = original.due_date;
    }

}
