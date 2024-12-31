package com.example.ticktick2.ui.habit;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitstatisticsday7Binding;
import com.example.ticktick2.dataobject.habit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitStatisticsDay7Fragment extends Fragment {

    FragmentHabitstatisticsday7Binding binding;

    private List<habit> habit7dayFilterList;


    private LocalDate m_weekDateStart;
    private LocalDate m_weekDateEnd;

    private Habit7dayAdapter habit7dayAdapter;


    private String makeWeekString()
    {
        String tmpstart = m_weekDateStart.getMonthValue()+"월 "+m_weekDateStart.getDayOfMonth()+"일 ~ ";
        String tmpend = m_weekDateEnd.getMonthValue()+"월 "+m_weekDateEnd.getDayOfMonth()+"일 ";
        return tmpstart+tmpend;
    }

    private LocalDate weekDateStart(LocalDate date)
    {
        int dayofweek = date.getDayOfWeek().getValue();
        return date.minusDays(dayofweek-1);
    }

    private LocalDate weekDateStartEnd(LocalDate date)
    {
        int dayofweek = date.getDayOfWeek().getValue();
        return date.plusDays(7-dayofweek);
    }

    private void GetHabit(LocalDate start,LocalDate End)
    {

        MainActivity activity = (MainActivity) getActivity();
        habit7dayFilterList.clear();

        synchronized (activity.synchronizedHabitList) {

            for(habit item:activity.synchronizedHabitList)
            {
                if(!item.StartDate.isAfter(End))
                {
                    if(item.EndDate!=null && start.isAfter(item.EndDate))
                    {
                        continue;
                    }
                    habit7dayFilterList.add(item);
                }
            }

        }


    }

    private HabitViewModel habitViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitstatisticsday7Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();
        {
            habit7dayFilterList = new ArrayList<>();

            m_weekDateStart = weekDateStart(LocalDate.now());
            m_weekDateEnd = weekDateStartEnd(LocalDate.now());




            GetHabit(m_weekDateStart,m_weekDateEnd);
        }

        BottomNavigationView navController = requireActivity().findViewById(R.id.nav_view);

        navController.setVisibility(View.GONE);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {


                MainActivity activity = (MainActivity) getActivity();
                NavController navController = activity.navController;

                BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);

                if (navView != null) {
                    navView.setVisibility(View.VISIBLE);
                }

                // 기본 뒤로 가기 동작 수행
                setEnabled(false); // 콜백 비활성화
                requireActivity().onBackPressed();

            }
        });

        {
            binding.daysedittext.setText(makeWeekString());
            habit7dayAdapter = new Habit7dayAdapter(getActivity());
            binding.listview7day.setAdapter(habit7dayAdapter);

        }

        {
            binding.button1.setOnClickListener(v->{

                m_weekDateStart =  m_weekDateStart.minusDays(7);
                m_weekDateEnd = m_weekDateEnd.minusDays(7);

                binding.daysedittext.setText(makeWeekString());
                GetHabit(m_weekDateStart,m_weekDateEnd);
                habit7dayAdapter.notifyDataSetChanged();

            });

            binding.button2.setOnClickListener(v->{

                m_weekDateStart =  m_weekDateStart.plusDays(7);
                m_weekDateEnd = m_weekDateEnd.plusDays(7);

                binding.daysedittext.setText(makeWeekString());
                GetHabit(m_weekDateStart,m_weekDateEnd);
                habit7dayAdapter.notifyDataSetChanged();

            });


        }





        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;

    }

    public class Habit7dayAdapter extends ArrayAdapter<habit> {
        private final Activity context;

        public Habit7dayAdapter(Activity context) {
            super(context, R.layout.sevendaystatistics, habit7dayFilterList);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.sevendaystatistics, null, true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageview);
            TextView title = (TextView) rowView.findViewById(R.id.textview);
            ImageView day1 = (ImageView) rowView.findViewById(R.id.w1day);
            ImageView day2 = (ImageView) rowView.findViewById(R.id.w2day);
            ImageView day3 = (ImageView) rowView.findViewById(R.id.w3day);
            ImageView day4 = (ImageView) rowView.findViewById(R.id.w4day);
            ImageView day5 = (ImageView) rowView.findViewById(R.id.w5day);
            ImageView day6 = (ImageView) rowView.findViewById(R.id.w6day);
            ImageView day7 = (ImageView) rowView.findViewById(R.id.w7day);

            habit de = habit7dayFilterList.get(pos);

            title.setText(de.Name);
            imageView.setImageResource(de.Icon);

            ImageView[] imageViews = {day1,day2,day3,day4,day5,day6,day7};

            LocalDate tmp = m_weekDateStart;

            for(int i=0;i<7;i++)
            {
                if(de.CheckedDate[tmp.getYear()-2024][tmp.getMonthValue()][tmp.getDayOfMonth()])
                {
                    imageViews[i].setBackgroundColor(getResources().getColor(R.color.blue_text));
                }

                else
                {
                    imageViews[i].setBackgroundColor(getResources().getColor(R.color.gray));
                }

                tmp = tmp.plusDays(1);
            }

            return rowView;
        }


    }




}


