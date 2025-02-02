package com.example.ticktick2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.ticktick2.dataobject.diary;
import com.example.ticktick2.dataobject.habit;

import com.example.ticktick2.dataobject.schedule;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ticktick2.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public NavController navController;
    public FileIOClass fileIOClass;
    public FileIO_DiaryClass fileIODiaryClass;

    public List<habit> synchronizedHabitList = Collections.synchronizedList(new ArrayList<>());
    public Set<schedule> syncTreeSet = Collections.synchronizedSortedSet(new TreeSet<>());

    public List<diary> synchronizedDiaryList = Collections.synchronizedList(new ArrayList<>());


/*
synchronizedList.add("Apple");
synchronizedList.remove("Banana");

synchronized (synchronizedList) {
    for (String item : synchronizedList) {
        System.out.println(item);
    }
}

 */

    public void DataChangeNotfiy()
    {
        fileIOClass.Notify();
    }

    public void DiaryChangeNotify()
    {
        fileIODiaryClass.Notify();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        {
            fileIOClass = new FileIOClass(this);
            fileIODiaryClass = new FileIO_DiaryClass(this);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 파일 IO 작업 또는 시간이 걸리는 작업
                    fileIOClass.LoadThread(fileIOClass);
                    synchronizedHabitList = Collections.synchronizedList(fileIOClass.HabitList);
                    syncTreeSet = Collections.synchronizedSortedSet(fileIOClass.ScheduleSet);
                    synchronizedDiaryList = Collections.synchronizedList(fileIOClass.diaryList);
                    fileIOClass.flag.set(true);
                    fileIODiaryClass.SetDiaryList(synchronizedDiaryList);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 파일 IO 작업 또는 시간이 걸리는 작업
                    fileIOClass.SaveThread(fileIOClass);

                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    fileIODiaryClass.SaveThread(fileIODiaryClass);
                }
            }).start();

        }

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_habits,R.id.navigation_diary)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        BottomNavigationView navHabitView = findViewById(R.id.nav_habitview);

        AppBarConfiguration appBarHabitConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_Habitadd, R.id.navigation_Habitadd2,  R.id.navigation_Habitadd3, R.id.navigation_Habitadd4, R.id.navigation_Habitadd5)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarHabitConfiguration);
        NavigationUI.setupWithNavController(binding.navHabitview, navController);




    }

}