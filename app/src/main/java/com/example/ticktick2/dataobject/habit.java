package com.example.ticktick2.dataobject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;




public class habit {



    public int pos;

    public String Name;
    public Integer Icon;
    public String Text;
    public int Frequency;

    public boolean[] frequencyDay;

    public Group group;
    public LocalDate StartDate;
    public LocalDate EndDate;
    public LocalTime Alarm_time;
    public boolean HabitLog;
    public int AchieveDay;
    public boolean CheckedDate[][][];
    public String CheckedString[][][];

    public int CheckedFeeling[][][];

    public enum Frequency_day
    {
        Mon,
        Tue,
        Wed,
        Thu,
        Fri,
        Sat,
        Sun

    };


    public enum Group
    {
        morning,
        afternoon,
        night,
        etc
    };

    public enum Check_in
    {
        non,
        not,
        ok
    }

    public habit()
    {
        CheckedDate = new boolean[10][13][32];
        CheckedString = new String[10][13][32];
        CheckedFeeling = new int[10][13][32];
        frequencyDay = new boolean[7];
        Arrays.fill(frequencyDay, true);

        Frequency=0;
        pos=0;
        Icon=0;
        AchieveDay=0;
    }
    public habit(String _name)
    {
        CheckedDate = new boolean[10][13][32];
        CheckedString = new String[10][13][32];
        CheckedFeeling = new int[10][13][32];


        this.Name = _name;
        frequencyDay = new boolean[7];
        Arrays.fill(frequencyDay, true);
        AchieveDay=0;
        Frequency=0;
        pos=0;
        Icon=0;
    }


}
