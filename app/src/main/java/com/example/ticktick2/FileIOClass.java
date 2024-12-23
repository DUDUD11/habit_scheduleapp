package com.example.ticktick2;

import android.app.Activity;

import com.example.ticktick2.dataobject.diary;
import com.example.ticktick2.dataobject.habit;
import com.example.ticktick2.dataobject.schedule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FileIOClass {

    private Activity context;

    private File internalstroageDir;


    public AtomicBoolean flag = new AtomicBoolean(false);

    private  String habitpath = "habitv1.json";
    private Path path_habit;
    private String habittmp = "habittmpv1.json";
    private Path path_habittmp;

    private String habitbackup = "habitbkv1.json";
    private Path path_habitbk;
    private String schedulepath = "schedulev1.json";

    private Path path_schedule;
    private String scheduletmp = "scheduletmpv1.json";
    private Path path_scheduletmp;

    private String schedulebackup = "schedulebkv1.json";

    private Path path_schedulebk;

    private  String diarypath = "diaryv1.json";
    private Path path_diary;
    private String diarytmp = "diarytmpv1.json";
    private Path path_diarytmp;

    private String diarybackup = "diarybkv1.json";
    private Path path_diarybk;
    public List<habit> HabitList;
    public SortedSet<schedule> ScheduleSet;

    public List<diary> diaryList;

    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    public void Load()
    {

        try {

            File habitFile = new File(habitpath);
            File scheduleFile = new File(schedulepath);
            File diaryFile = new File(diarypath);

            if(habitFile.exists()) {
                ObjectMapper objectMapperHabit = new ObjectMapper();
                objectMapperHabit.registerModule(new JavaTimeModule());
                HabitList = objectMapperHabit.readValue(habitFile, objectMapperHabit.getTypeFactory().constructCollectionType(ArrayList.class, habit.class));

            }

            if(scheduleFile.exists()) {
                ObjectMapper objectMapperSchedule = new ObjectMapper();
                objectMapperSchedule.registerModule(new JavaTimeModule());
                ScheduleSet = objectMapperSchedule.readValue(scheduleFile, objectMapperSchedule.getTypeFactory().constructCollectionType(SortedSet.class, schedule.class));
            }

            if(diaryFile.exists()) {
                ObjectMapper objectMapperDiary = new ObjectMapper();
                objectMapperDiary.registerModule(new JavaTimeModule());
                diaryList = objectMapperDiary.readValue(diaryFile, objectMapperDiary.getTypeFactory().constructCollectionType(ArrayList.class, diary.class));
            }




        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public synchronized void Notify()
    {
        int val = atomicInteger.incrementAndGet();

        if(val>1)
        {
            atomicInteger.set(1);

        }

        notify();

    }

    public static void SaveThread(FileIOClass fileIOClass) {

        fileIOClass.Save();
    }

    public synchronized void Save()
    {
        try {

            while (true) {

                wait();

                try {

                    File habitFile = new File(habitpath);
                    File scheduleFile = new File(schedulepath);

                    ObjectMapper objectMapperHabit = new ObjectMapper();

                    objectMapperHabit.registerModule(new JavaTimeModule());

                    objectMapperHabit.writeValue(new File(habittmp), HabitList);


                    ObjectMapper objectMapperSchedule = new ObjectMapper();

                    objectMapperSchedule.registerModule(new JavaTimeModule());

                    objectMapperSchedule.writeValue(new File(scheduletmp), ScheduleSet);

                    if (habitFile.exists()) {
                        Files.copy(path_habit, path_habitbk, StandardCopyOption.REPLACE_EXISTING);
                    }

                    if (scheduleFile.exists()) {
                        Files.copy(path_schedule, path_schedulebk, StandardCopyOption.REPLACE_EXISTING);
                    }

                    Files.move(path_habittmp, path_habit, StandardCopyOption.REPLACE_EXISTING);
                    Files.move(path_scheduletmp, path_schedule, StandardCopyOption.REPLACE_EXISTING);

                    atomicInteger.decrementAndGet();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }



            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public static void LoadThread(FileIOClass fileIOClass)
    {
        fileIOClass.Load();
    }



    public FileIOClass(Activity _context)
    {
        context = _context;
        HabitList = Collections.synchronizedList(new ArrayList<>());
        ScheduleSet = Collections.synchronizedSortedSet(new TreeSet<>());
        diaryList = Collections.synchronizedList(new ArrayList<>());
        internalstroageDir = context.getFilesDir();

        File file = new File(internalstroageDir,habitpath);
        path_habit = file.toPath();
        habitpath = file.getAbsolutePath();

        file = new File(internalstroageDir,habittmp);
        path_habittmp = file.toPath();
        habittmp = file.getAbsolutePath();

        file = new File(internalstroageDir,habitbackup);
        path_habitbk = file.toPath();
        habitbackup = file.getAbsolutePath();

        file = new File(internalstroageDir,schedulepath);
        path_schedule = file.toPath();
        schedulepath = file.getAbsolutePath();

        file = new File(internalstroageDir,scheduletmp);
        path_scheduletmp = file.toPath();
        scheduletmp = file.getAbsolutePath();

        file = new File(internalstroageDir,schedulebackup);
        path_schedulebk = file.toPath();
        schedulebackup = file.getAbsolutePath();

        file = new File(internalstroageDir,diarypath);
        path_diary = file.toPath();
        diarypath = file.getAbsolutePath();

        file = new File(internalstroageDir,diarytmp);
        path_diarytmp = file.toPath();
        diarytmp = file.getAbsolutePath();

        file = new File(internalstroageDir,diarybackup);
        path_diarybk = file.toPath();
        diarybackup = file.getAbsolutePath();

    }
}
