package com.example.ticktick2.ui.habit;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitsBinding;
import com.example.ticktick2.dataobject.habit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HabitFragment extends Fragment {

    private HabitViewModel habitViewModel;
    private  List<habit> habitList;
    private List<habit> morningList=null;
    private List<habit> morningFilterList=null;
    private List<habit> afternoonList=null;
    private List<habit> afternoonFilterList=null;
    private List<habit> nightList =null;
    private List<habit> nightFilterList=null;
    private List<habit> etcList=null;
    private List<habit> etcFilterList=null;

    private LocalDate[] days;

    private  HabitGroupMorningListAdapter habitCustomMorningListAdapter;
    private  HabitGroupAfternoonListAdapter habitCustomAfternoonListAdapter;
    private  HabitGroupNightListAdapter habitCustomNightListAdapter;
    private  HabitGroupEtcListAdapter habitCustomEtcListAdapter;


    private FragmentHabitsBinding binding;

    private void ResetLists()
    {
        morningFilterList.clear();
        afternoonFilterList.clear();
        nightFilterList.clear();
        etcFilterList.clear();

    }

    public static void setListViewHeightBasedOnChildren(ListView listView, int listsize) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null ) return;

        if(listsize==0)
        {
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = 0;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return;
        }

        int totalHeight = 0;

        View listItem = listAdapter.getView(0, null, listView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight() * listsize;


        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listsize));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }


    private void FilterList(LocalDate targetday)
    {

        ResetLists();

        for(habit item:morningList)
        {
            long daysBetween  = ChronoUnit.DAYS.between(item.StartDate,targetday);

            if(item.EndDate!=null && ChronoUnit.DAYS.between(item.EndDate,targetday) > 0) continue;

            if(daysBetween<0) continue;


            if(item.Frequency==0)
            {
                int weekday = (targetday.getDayOfWeek().getValue()+6)%7;

                if(item.frequencyDay[weekday])
                {
                    morningFilterList.add(item);
                }

            }

            else
            {
            
                if(daysBetween%(item.Frequency)==0)
                {
                    morningFilterList.add(item);
                }

            }

        }

        for(habit item:afternoonList)
        {
            long daysBetween  = ChronoUnit.DAYS.between(item.StartDate,targetday);
            if(daysBetween<0) continue;
            if(item.EndDate!=null && ChronoUnit.DAYS.between(item.EndDate,targetday) > 0) continue;


            if(item.Frequency==0)
            {
                int weekday = (targetday.getDayOfWeek().getValue()+6)%7;

                if(item.frequencyDay[weekday])
                {
                    afternoonFilterList.add(item);
                }

            }

            else
            {
                if(daysBetween%(item.Frequency)==0)
                {
                    afternoonFilterList.add(item);
                }

            }

        }

        for(habit item:nightList)
        {
            if(item.EndDate!=null && ChronoUnit.DAYS.between(item.EndDate,targetday) > 0) continue;
            long daysBetween  = ChronoUnit.DAYS.between(item.StartDate,targetday);
            if(daysBetween<0) continue;


            if(item.Frequency==0)
            {
                int weekday = (targetday.getDayOfWeek().getValue()+6)%7;

                if(item.frequencyDay[weekday])
                {
                    nightFilterList.add(item);
                }

            }

            else
            {

                if(daysBetween%(item.Frequency)==0)
                {
                    nightFilterList.add(item);
                }

            }

        }

        for(habit item:etcList)
        {     if(item.EndDate!=null && ChronoUnit.DAYS.between(item.EndDate,targetday) > 0) continue;
            long daysBetween  = ChronoUnit.DAYS.between(item.StartDate,targetday);
            if(daysBetween<0) continue;


            if(item.Frequency==0)
            {
                int weekday = (targetday.getDayOfWeek().getValue()+6)%7;

                if(item.frequencyDay[weekday])
                {
                    etcFilterList.add(item);
                }

            }

            else
            {
                if(daysBetween%(item.Frequency)==0)
                {
                    etcFilterList.add(item);
                }

            }

        }















    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();

        habitList = activity.synchronizedHabitList;

        {
            BottomNavigationView controller = requireActivity().findViewById(R.id.nav_view);
            controller.setVisibility(View.VISIBLE);
        }



        {
            morningList = new ArrayList<>();
            morningFilterList = new ArrayList<>();
            afternoonList  = new ArrayList<>();
            afternoonFilterList = new ArrayList<>();
            nightList  = new ArrayList<>();
            nightFilterList = new ArrayList<>();
            etcList  = new ArrayList<>();
            etcFilterList  = new ArrayList<>();
            days=new LocalDate[7];

            LocalDate currentDate = LocalDate.now();

            for(int i=6;i>=0;i--)
            {
                days[i]=currentDate.minusDays(6-i);
            }

            String[] daystr = {"월","화","수","목","금","토","일"};

            int todayweekday = LocalDate.now().getDayOfWeek().getValue();

            todayweekday = (todayweekday+6)%7;

            binding.day7textview.setText(daystr[todayweekday--]);
            binding.day6textview.setText(daystr[(todayweekday--+7)%7]);
            binding.day5textview.setText(daystr[(todayweekday--+7)%7]);
            binding.day4textview.setText(daystr[(todayweekday--+7)%7]);
            binding.day3textview.setText(daystr[(todayweekday--+7)%7]);
            binding.day2textview.setText(daystr[(todayweekday--+7)%7]);
            binding.day1textview.setText(daystr[(todayweekday+7)%7]);

            binding.firstday.setText(days[0].getDayOfMonth()+"");
            binding.secondday.setText(days[1].getDayOfMonth()+"");
            binding.thirdday.setText(days[2].getDayOfMonth()+"");
            binding.forthday.setText(days[3].getDayOfMonth()+"");
            binding.fifthday.setText(days[4].getDayOfMonth()+"");
            binding.sixthday.setText(days[5].getDayOfMonth()+"");
            binding.sevenday.setText(days[6].getDayOfMonth()+"");




                for (habit item : habitList) {

                    if(item.group==habit.Group.morning)
                    {
                        morningList.add(item);
                    }

                    else if(item.group==habit.Group.afternoon)
                    {
                        afternoonList.add(item);
                    }

                    else if(item.group==habit.Group.night)
                    {
                        nightList.add(item);
                    }
                    else
                    {
                        etcList.add(item);
                    }
                }
        }

        {
            if(habitViewModel.MorningCheckbox)
            {
                binding.Morninglist.setVisibility(View.GONE);
                binding.MorningCheckBox.setChecked(true);
            }

            if(habitViewModel.AfternoonCheckbox)
            {
                binding.Afternoonlist.setVisibility(View.GONE);
                binding.AfternoonCheckBox.setChecked(true);
            }

            if(habitViewModel.NightCheckbox)
            {
                binding.Nightlist.setVisibility(View.GONE);
                binding.NightCheckBox.setChecked(true);
            }

            if(habitViewModel.etcCheckbox)
            {
                binding.Etclist.setVisibility(View.GONE);
                binding.EtcCheckBox.setChecked(true);
            }


        }


        //habitViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        Log.d("create", "habit");
        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;


        binding.fab.setOnClickListener(v -> {
            navController.navigate(R.id.HabitFragment_to_HabitAddFragment);

        });


        {

            habitCustomMorningListAdapter = new HabitGroupMorningListAdapter(getActivity());


            binding.Morninglist.setAdapter(habitCustomMorningListAdapter);







            binding.Morninglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                           @Override
                                                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                               habitViewModel.setSelectedHabit(morningFilterList.get(position));

                                                               MainActivity activity = (MainActivity) getActivity();
                                                               NavController navController = activity.navController;

                                                               navController.navigate(R.id.HabitFragment_to_HabitDetail);


                                                           }


                                                       }


            );


            binding.MorningCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // 체크박스가 선택되었을 때
                        binding.Morninglist.setVisibility(View.GONE);

                        habitViewModel.MorningCheckbox=true;

                    } else {
                        // 체크박스가 선택 해제되었을 때

                        binding.Morninglist.setVisibility(View.VISIBLE);
                        habitViewModel.MorningCheckbox=false;

                    }
                }
            });

        }

        {

            habitCustomAfternoonListAdapter = new HabitGroupAfternoonListAdapter(getActivity());


            binding.Afternoonlist.setAdapter(habitCustomAfternoonListAdapter);


            binding.Afternoonlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                           @Override
                                                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                               habitViewModel.setSelectedHabit(afternoonFilterList.get(position));

                                                               MainActivity activity = (MainActivity) getActivity();
                                                               NavController navController = activity.navController;

                                                               navController.navigate(R.id.HabitFragment_to_HabitDetail);



                                                           }


                                                       }


            );


            binding.AfternoonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // 체크박스가 선택되었을 때
                        binding.Afternoonlist.setVisibility(View.GONE);

                        habitViewModel.AfternoonCheckbox=true;

                    } else {
                        // 체크박스가 선택 해제되었을 때

                        binding.Afternoonlist.setVisibility(View.VISIBLE);
                        habitViewModel.AfternoonCheckbox=false;

                    }
                }
            });

        }


        {

            habitCustomNightListAdapter = new HabitGroupNightListAdapter(getActivity());


            binding.Nightlist.setAdapter(habitCustomNightListAdapter);


            binding.Nightlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                           @Override
                                                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                               habitViewModel.setSelectedHabit(nightFilterList.get(position));

                                                               MainActivity activity = (MainActivity) getActivity();
                                                               NavController navController = activity.navController;

                                                               navController.navigate(R.id.HabitFragment_to_HabitDetail);

                                                           }


                                                       }


            );


            binding.NightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // 체크박스가 선택되었을 때
                        binding.Nightlist.setVisibility(View.GONE);

                        habitViewModel.NightCheckbox=true;

                    } else {
                        // 체크박스가 선택 해제되었을 때

                        binding.Nightlist.setVisibility(View.VISIBLE);
                        habitViewModel.NightCheckbox=false;

                    }
                }
            });

        }

        {

            habitCustomEtcListAdapter = new HabitGroupEtcListAdapter(getActivity());


            binding.Etclist.setAdapter(habitCustomEtcListAdapter);


            binding.Etclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                             @Override
                                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                                 habitViewModel.setSelectedHabit(etcFilterList.get(position));

                                                                 MainActivity activity = (MainActivity) getActivity();
                                                                 NavController navController = activity.navController;

                                                                 navController.navigate(R.id.HabitFragment_to_HabitDetail);


                                                             }


                                                         }


            );


            binding.EtcCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // 체크박스가 선택되었을 때
                        binding.Etclist.setVisibility(View.GONE);

                        habitViewModel.etcCheckbox=true;

                    } else {
                        // 체크박스가 선택 해제되었을 때

                        binding.Etclist.setVisibility(View.VISIBLE);
                        habitViewModel.etcCheckbox=false;

                    }
                }
            });

        }


        binding.dayRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                binding.firstday.setBackgroundColor(Color.argb(0, 255, 255, 255));
                binding.secondday.setBackgroundColor(Color.argb(0, 255, 255, 255));
                binding.thirdday.setBackgroundColor(Color.argb(0, 255, 255, 255));
                binding.forthday.setBackgroundColor(Color.argb(0, 255, 255, 255));
                binding.fifthday.setBackgroundColor(Color.argb(0, 255, 255, 255));
                binding.sixthday.setBackgroundColor(Color.argb(0, 255, 255, 255));
                binding.sevenday.setBackgroundColor(Color.argb(0, 255, 255, 255));

                RadioButton selectedRadioButton = getActivity().findViewById(checkedId);

                if(selectedRadioButton.equals(binding.firstday))
                {
                    FilterList(days[0]);


                    binding.firstday.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));

                }

                else if(selectedRadioButton.equals(binding.secondday))
                {
                    FilterList(days[1]);
                    binding.secondday.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
                }


                else if(selectedRadioButton.equals(binding.thirdday))
                {
                    FilterList(days[2]);
                    binding.thirdday.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
                }

                else if(selectedRadioButton.equals(binding.forthday))
                {
                    FilterList(days[3]);
                    binding.forthday.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
                }

                else if(selectedRadioButton.equals(binding.fifthday))
                {
                    FilterList(days[4]);
                    binding.fifthday.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
                }

                else if(selectedRadioButton.equals(binding.sixthday))
                {
                    FilterList(days[5]);
                    binding.sixthday.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
                }

                else
                {
                    FilterList(days[6]);
                    binding.sevenday.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
                }

                setListViewHeightBasedOnChildren(binding.Morninglist,morningFilterList.size());
                setListViewHeightBasedOnChildren(binding.Afternoonlist,afternoonFilterList.size());
                setListViewHeightBasedOnChildren(binding.Nightlist,nightFilterList.size());
                setListViewHeightBasedOnChildren(binding.Etclist,etcFilterList.size());

                habitCustomMorningListAdapter.notifyDataSetChanged();
                habitCustomAfternoonListAdapter.notifyDataSetChanged();
                habitCustomNightListAdapter.notifyDataSetChanged();
                habitCustomEtcListAdapter.notifyDataSetChanged();

            }
        });






        {


            FilterList(days[6]);

            setListViewHeightBasedOnChildren(binding.Morninglist,morningFilterList.size());
            setListViewHeightBasedOnChildren(binding.Afternoonlist,afternoonFilterList.size());
            setListViewHeightBasedOnChildren(binding.Nightlist,nightFilterList.size());
            setListViewHeightBasedOnChildren(binding.Etclist,etcFilterList.size());

            habitCustomMorningListAdapter.notifyDataSetChanged();
            habitCustomAfternoonListAdapter.notifyDataSetChanged();
            habitCustomNightListAdapter.notifyDataSetChanged();
            habitCustomEtcListAdapter.notifyDataSetChanged();



            binding.sevenday.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));





        }



    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // 메뉴 XML을 인플레이트

        inflater.inflate(R.menu.habitstatic, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 메뉴 항목을 사용하려면 이 호출이 필요합니다.
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.static_menu) {

            MainActivity activity = (MainActivity) getActivity();
            NavController navController = activity.navController;
            navController.navigate(R.id.HabitFragment_to_navigation_habitstatistics);
            return true;
        }

        else if(item.getItemId()==R.id.static_day7menu)
        {
            MainActivity activity = (MainActivity) getActivity();
            NavController navController = activity.navController;
            navController.navigate(R.id.HabitFragment_to_navigation_habitstatisticsday7);
            return true;

        }
        
        super.onOptionsItemSelected(item);
        return true;


    }




    @Override
    public void onDestroyView() {


        super.onDestroyView();
        binding = null;

        Log.d("delete", "habit");
    }

    public class HabitGroupMorningListAdapter extends ArrayAdapter<habit> {
        private final Activity context;


        public HabitGroupMorningListAdapter(Activity context) {
            super(context, R.layout.habitlistitemgroup, morningFilterList);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.habitlistitemgroup, null, true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView days = (TextView) rowView.findViewById(R.id.days);

            habit de = morningFilterList.get(pos);

            title.setText(de.Name);

            if(de.CheckedDate[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()])
            {
                imageView.setImageResource(habitViewModel.doneimage[de.pos]);
            }

            else
            {
                imageView.setImageResource(de.Icon);

                if(de.CheckedFeeling[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()]!=0)
                {
                    imageView.setImageAlpha(128);
                }
            }

            days.setText(de.AchieveDay+" 일");

            return rowView;
        }


    }

    public class HabitGroupAfternoonListAdapter extends ArrayAdapter<habit> {
        private final Activity context;

        public HabitGroupAfternoonListAdapter(Activity context) {
            super(context, R.layout.habitlistitemgroup, afternoonFilterList);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.habitlistitemgroup, null, true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView days = (TextView) rowView.findViewById(R.id.days);

            habit de = afternoonFilterList.get(pos);

            title.setText(de.Name);
            if(de.CheckedDate[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()])
            {
                imageView.setImageResource(habitViewModel.doneimage[de.pos]);
            }

            else
            {
                imageView.setImageResource(de.Icon);
                if(de.CheckedFeeling[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()]!=0)
                {
                    imageView.setImageAlpha(128);
                }
            }
            days.setText(de.AchieveDay+" 일");
            return rowView;
        }


    }

    public class HabitGroupNightListAdapter extends ArrayAdapter<habit> {
        private final Activity context;

        public HabitGroupNightListAdapter(Activity context) {
            super(context, R.layout.habitlistitemgroup, nightFilterList);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.habitlistitemgroup, null, true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView days = (TextView) rowView.findViewById(R.id.days);

            habit de = nightFilterList.get(pos);

            title.setText(de.Name);
            if(de.CheckedDate[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()])
            {
                imageView.setImageResource(habitViewModel.doneimage[de.pos]);
            }

            else
            {
                imageView.setImageResource(de.Icon);

                if(de.CheckedFeeling[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()]!=0)
                {
                    imageView.setImageAlpha(128);
                }
            }
            days.setText(de.AchieveDay+" 일");
            return rowView;
        }


    }

    public class HabitGroupEtcListAdapter extends ArrayAdapter<habit> {
        private final Activity context;

        public HabitGroupEtcListAdapter(Activity context) {
            super(context, R.layout.habitlistitemgroup, etcFilterList);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.habitlistitemgroup, null, true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView days = (TextView) rowView.findViewById(R.id.days);

            habit de = etcFilterList.get(pos);

            title.setText(de.Name);
            if(de.CheckedDate[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()])
            {
                imageView.setImageResource(habitViewModel.doneimage[de.pos]);
            }

            else
            {
                imageView.setImageResource(de.Icon);

                if(de.CheckedFeeling[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()]!=0)
                {
                    imageView.setImageAlpha(128);
                }
            }
            days.setText(de.AchieveDay+" 일");
            return rowView;
        }


    }

}



