package com.example.ticktick2;

import android.app.Activity;

import com.example.ticktick2.dataobject.diary;
import com.example.ticktick2.dataobject.habit;
import com.example.ticktick2.dataobject.schedule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FileIO_DiaryClass {


    private Activity context;

    private File internalstroageDir;

    public AtomicBoolean Diaryflag = new AtomicBoolean(false);

    private  String diarypath = "diaryv1.json";
    private Path path_diary;
    private String diarytmp = "diarytmpv1.json";
    private Path path_diarytmp;

    private String diarybackup = "diarybkv1.json";
    private Path path_diarybk;

    public List<diary> diaryList;

    public static AtomicInteger atomicInteger_diary = new AtomicInteger(0);

    public synchronized void Notify()
    {
        int val = atomicInteger_diary.incrementAndGet();

        if(val>1)
        {
            atomicInteger_diary.set(1);

        }

        notify();

    }

    public static void SaveThread(FileIO_DiaryClass fileIODiaryClass) {

        fileIODiaryClass.Save();
    }

    public synchronized void Save()
    {
        try {

            while (true) {

                wait();

                try {

                    File diaryFile = new File(diarypath);

                    ObjectMapper objectMapperDiary = new ObjectMapper();

                    objectMapperDiary.registerModule(new JavaTimeModule());

                    objectMapperDiary.writeValue(new File(diarytmp), diaryList);


                    ObjectMapper objectMapperSchedule = new ObjectMapper();


                    if (diaryFile.exists()) {
                        Files.copy(path_diary, path_diarybk, StandardCopyOption.REPLACE_EXISTING);
                    }

                    Files.move(path_diarytmp, path_diary, StandardCopyOption.REPLACE_EXISTING);

                    atomicInteger_diary.decrementAndGet();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public void SetDiaryList(List<diary> syncDiary)
    {
        this.diaryList = Collections.synchronizedList(syncDiary);
    }


    public FileIO_DiaryClass(Activity _context)
    {
        context = _context;
        internalstroageDir = context.getFilesDir();

        File file = new File(internalstroageDir,diarypath);
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
