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

    public boolean alarm;

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
        this.alarm=false;

        this.Name = _name;
        frequencyDay = new boolean[7];
        Arrays.fill(frequencyDay, true);
        AchieveDay=0;
        Frequency=0;
        pos=0;
        Icon=0;
    }

    public habit deepcopy()
    {
        habit tmp = new habit();
        tmp.pos = this.pos;

        tmp.Name = this.Name;
        tmp.Icon = this.Icon;
        tmp.Text=this.Text;
        tmp.Frequency= this.Frequency;
        for(int i=0;i<7;i++)
        {
            tmp.frequencyDay[i]=this.frequencyDay[i];

        }


        tmp.group = this.group;
        tmp.StartDate = this.StartDate;
        tmp.EndDate = this.EndDate;
        tmp.Alarm_time =this.Alarm_time;
        tmp.HabitLog = this.HabitLog;
        tmp.AchieveDay = this.AchieveDay;
        tmp.alarm = this.alarm;

        for(int i=0;i<10;i++)
        {
            for(int j=0;j<13;j++)
            {
                for(int h=0;h<32;h++)
                {
                    tmp.CheckedDate[i][j][h]=this.CheckedDate[i][j][h];
                    tmp.CheckedString[i][j][h]=this.CheckedString[i][j][h];
                    tmp.CheckedFeeling[i][j][h]=this.CheckedFeeling[i][j][h];

                }

            }



        }


        return tmp;
    }


}
