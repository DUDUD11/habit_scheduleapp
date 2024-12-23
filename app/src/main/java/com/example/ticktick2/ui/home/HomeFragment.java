package com.example.ticktick2.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHomeBinding;
import com.example.ticktick2.dataobject.ScheduleViewModel;
import com.example.ticktick2.dataobject.habit;
import com.example.ticktick2.dataobject.schedule;
import com.example.ticktick2.ui.habit.HabitFragment;
import com.example.ticktick2.ui.habit.HabitViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private HabitGroupListAdapter habitCustomListAdapter;
    private ScheduleDelayGroupListAdapter scheduleDelayGroupListAdapter;
    private ScheduleTodayGroupListAdapter scheduleTodayGroupListAdapter;

    private boolean Createatonce;
    private FragmentHomeBinding binding;
    private List<habit> habitList;
    private Set<schedule> scheduleSet;

    private HabitViewModel habitViewModel = null;
    private ScheduleViewModel scheduleViewModel = null;

    private List<habit> habitToday;
    private List<schedule> scheduleDelay;
    private List<schedule> scheduleToday;


    private void showOptionDialog(schedule item,boolean today) {
        new AlertDialog.Builder(getActivity())
                .setTitle("옵션 선택")
                .setMessage("변경 또는 삭제를 선택하세요.")

                // 변경 버튼
                .setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 변경 버튼을 클릭했을 때의 동작
                        performChangeAction(item,today);
                    }
                })

                // 삭제 버튼
                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 삭제 버튼을 클릭했을 때의 동작
                        performDeleteAction(item,today);
                    }
                })

                // 취소 버튼
                .setNeutralButton("취소", null)

                // 다이얼로그 표시
                .show();
    }

    private void performChangeAction(schedule item,boolean today) {
        // 변경 동작 처리

        schedule temperalSchedule = new schedule();
        temperalSchedule.deepCopySchedule(item);

        Dialog dialog = new Dialog(requireActivity());

        dialog.setContentView(R.layout.scheduledialog);

        TextView datetitle = dialog.findViewById(R.id.date_title);
        ImageButton calenderbutton = dialog.findViewById(R.id.CalenderButton);

        ImageView first= dialog.findViewById(R.id.Im_Ur);
        ImageView second = dialog.findViewById(R.id.nIm_Ur);
        ImageView third = dialog.findViewById(R.id.Im_nUr);
        ImageView forth = dialog.findViewById(R.id.nIm_nUr);
        ImageView fifth = dialog.findViewById(R.id.Etc);
        EditText schedulelogtext = dialog.findViewById(R.id.scheduletext);
        Button Okbutton = dialog.findViewById(R.id.okschedulebutton);
        Button cancelbutton = dialog.findViewById(R.id.cancelschedulebutton);

        schedulelogtext.setText(item.text);
        datetitle.setText(temperalSchedule.due_date.getYear()+"년 "+(temperalSchedule.due_date.getMonthValue())+"월 "+temperalSchedule.due_date.getDayOfMonth()+"일 ");

        first.setOnClickListener(b -> {
            temperalSchedule.importance=1;
        });
        second.setOnClickListener(b -> {
            temperalSchedule.importance=2;
        });

        third.setOnClickListener(b -> {
            temperalSchedule.importance=3;
        });

        forth.setOnClickListener(b -> {
            temperalSchedule.importance=4;
        });

        fifth.setOnClickListener(b -> {
            temperalSchedule.importance=5;
        });

        calenderbutton.setOnClickListener(new View.OnClickListener() {

            private long selecteddate;

            @Override
            public void onClick(View v) {

                CalendarView calendarView = new CalendarView(getActivity());

                long millis = temperalSchedule.due_date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                calendarView.setDate(millis,false,true);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                        // 선택된 날짜를 밀리초로 계산
                        selecteddate = new java.util.GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("날짜")
                        .setView(calendarView)  // 다이얼로그에 CalendarView 추가
                        .setPositiveButton("설정", (dialog, which) -> {
                            // 날짜가 선택되었을 때의 처리

                            LocalDate localDate = LocalDate.ofInstant(
                                    java.time.Instant.ofEpochMilli(selecteddate),
                                    ZoneId.of("Asia/Seoul") // 시스템의 기본 타임존

                            );

                            temperalSchedule.due_date=localDate;
                            datetitle.setText(temperalSchedule.due_date.toString());
                        })
                        .setNegativeButton("취소", (dialog, which) -> {
                            // 취소 버튼 클릭 시
                            dialog.dismiss();
                        });

                // 다이얼로그 표시
                builder.create().show();
            }
        });

        // 버튼 클릭 리스너 설정
        Okbutton.setOnClickListener(b -> {
            // 확인 버튼 클릭 시 동작

            temperalSchedule.text=schedulelogtext.getText().toString();

            if( temperalSchedule.importance==0)
            {
                Toast.makeText(getActivity().getBaseContext(), "중요도를 설정해주세요",
                        Toast.LENGTH_SHORT ).show();
                return;
            }

            MainActivity activity = (MainActivity) getActivity();

            activity.syncTreeSet.remove(item);
            activity.syncTreeSet.add(temperalSchedule);

            if(today)
            {
                scheduleToday.remove(item);

                setListViewHeightBasedOnChildren(binding.todaylist,scheduleToday.size());
                scheduleTodayGroupListAdapter.notifyDataSetChanged();
            }
            else
            {
                scheduleDelay.remove(item);
                setListViewHeightBasedOnChildren(binding.Delayedlist,scheduleDelay.size());
                scheduleDelayGroupListAdapter.notifyDataSetChanged();

            }

            if(temperalSchedule.due_date.isEqual(LocalDate.now()))
            {
                scheduleToday.add(temperalSchedule);
                setListViewHeightBasedOnChildren(binding.todaylist,scheduleToday.size());
                scheduleTodayGroupListAdapter.notifyDataSetChanged();
            }

            else if(temperalSchedule.due_date.isBefore(LocalDate.now()))
            {
                scheduleDelay.add(temperalSchedule);
                setListViewHeightBasedOnChildren(binding.Delayedlist,scheduleDelay.size());
                scheduleDelayGroupListAdapter.notifyDataSetChanged();
            }
            //temperalSchedule.clear();

            activity.DataChangeNotfiy();
            dialog.dismiss(); // 다이얼로그 닫기
        });

        cancelbutton.setOnClickListener(b -> {
            // 취소 버튼 클릭 시 동작
            //temperalSchedule.clear();

            dialog.dismiss(); // 다이얼로그 닫기
        });

        dialog.show(); // 다이얼로그 표시
    }

    private void performDeleteAction(schedule item, boolean today) {


        if(scheduleSet.remove(item))
        {
            if(today)
            {
                scheduleToday.remove(item);
                setListViewHeightBasedOnChildren(binding.todaylist,scheduleToday.size());
                scheduleTodayGroupListAdapter.notifyDataSetChanged();

            }

            else
            {
                scheduleDelay.remove(item);
                setListViewHeightBasedOnChildren(binding.Delayedlist,scheduleDelay.size());
                scheduleDelayGroupListAdapter.notifyDataSetChanged();
            }

            MainActivity activity = (MainActivity) getActivity();
            activity.DataChangeNotfiy();

        }

    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        scheduleViewModel =
                new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        habitToday = new ArrayList<>();

        scheduleDelay = new ArrayList<>();
        scheduleToday = new ArrayList<>();

        Createatonce = false;

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();

        {
            MainActivity activity = (MainActivity) getActivity();

            while(!activity.fileIOClass.flag.get())
            {

            }

            habitList = activity.synchronizedHabitList;
            scheduleSet = activity.syncTreeSet;


            if(!Createatonce)
            {
                    Createatonce=true;
                for(int i=0;i<habitList.size();i++)
                {
                    if(habitList.get(i).StartDate.isAfter(LocalDate.now())) continue;
                    if(habitList.get(i).EndDate != null && habitList.get(i).EndDate.isBefore(LocalDate.now())) continue;


                    if(habitList.get(i).Frequency==0)
                    {
                        int today = (LocalDate.now().getDayOfWeek().getValue()+6)%7;

                        if(habitList.get(i).frequencyDay[today])
                        {
                            habitToday.add(habitList.get(i));
                        }

                    }
                    else
                    {
                        long daysBetween = ChronoUnit.DAYS.between(habitList.get(i).StartDate, LocalDate.now());
                        if(daysBetween<0) continue;

                        if(daysBetween%habitList.get(i).Frequency==0)
                        {
                            habitToday.add(habitList.get(i));
                        }



                    }
                }

                scheduleSet.forEach(task -> {

                    if(!task.done) {
                        if (task.due_date.isBefore(LocalDate.now())) {
                            scheduleDelay.add(task);
                        } else if (task.due_date.isEqual(LocalDate.now())) {
                            scheduleToday.add(task);
                        } else return;
                    }
                });
            }

            {


                setListViewHeightBasedOnChildren(binding.habitlist,habitToday.size());
                setListViewHeightBasedOnChildren(binding.Delayedlist,scheduleDelay.size());
                setListViewHeightBasedOnChildren(binding.todaylist,scheduleToday.size());

                habitCustomListAdapter.notifyDataSetChanged();
                scheduleDelayGroupListAdapter.notifyDataSetChanged();
                scheduleTodayGroupListAdapter.notifyDataSetChanged();


            }





        }
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            habitCustomListAdapter = new HabitGroupListAdapter(getActivity());

            binding.habitlist.setAdapter(habitCustomListAdapter);

            binding.habitlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                             habitViewModel.setSelectedHabit(habitToday.get(position));
                                                             MainActivity activity = (MainActivity) getActivity();
                                                             NavController navController = activity.navController;
                                                             navController.navigate(R.id.HomeFragment_to_HabitDetail);
                                                         }
                                                     }

            );
        }

        {
            scheduleDelayGroupListAdapter = new ScheduleDelayGroupListAdapter(getActivity());

            binding.Delayedlist.setAdapter(scheduleDelayGroupListAdapter);

            binding.Delayedlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                             scheduleViewModel.setSelectedSchedule(scheduleDelay.get(position));
                                                             MainActivity activity = (MainActivity) getActivity();

                                                             showOptionDialog(scheduleDelay.get(position),false);


                                                         }
                                                     }

            );
        }

        {
            scheduleTodayGroupListAdapter = new ScheduleTodayGroupListAdapter(getActivity());

            binding.todaylist.setAdapter(scheduleTodayGroupListAdapter);

            binding.todaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                             scheduleViewModel.setSelectedSchedule(scheduleToday.get(position));
                                                             MainActivity activity = (MainActivity) getActivity();

                                                             showOptionDialog(scheduleToday.get(position),true);

                                                         }
                                                     }

            );
        }


        {
            binding.fab.setOnClickListener(v -> {

                schedule temperalSchedule = new schedule();
                temperalSchedule.clear();

                Dialog dialog = new Dialog(requireActivity());

                dialog.setContentView(R.layout.scheduledialog);

                TextView datetitle = dialog.findViewById(R.id.date_title);
                ImageButton calenderbutton = dialog.findViewById(R.id.CalenderButton);

                ImageView first= dialog.findViewById(R.id.Im_Ur);
                ImageView second = dialog.findViewById(R.id.nIm_Ur);
                ImageView third = dialog.findViewById(R.id.Im_nUr);
                ImageView forth = dialog.findViewById(R.id.nIm_nUr);
                ImageView fifth = dialog.findViewById(R.id.Etc);
                EditText schedulelogtext = dialog.findViewById(R.id.scheduletext);
                Button Okbutton = dialog.findViewById(R.id.okschedulebutton);
                Button cancelbutton = dialog.findViewById(R.id.cancelschedulebutton);

                datetitle.setText(LocalDate.now().getYear()+"년 "+(LocalDate.now().getMonth().getValue())+"월 "+LocalDate.now().getDayOfMonth()+"일 ");


                first.setOnClickListener(b -> {
                    temperalSchedule.importance=1;
                });
                second.setOnClickListener(b -> {
                    temperalSchedule.importance=2;
                });

                third.setOnClickListener(b -> {
                    temperalSchedule.importance=3;
                });

                forth.setOnClickListener(b -> {
                    temperalSchedule.importance=4;
                });

                fifth.setOnClickListener(b -> {
                    temperalSchedule.importance=5;
                });

                calenderbutton.setOnClickListener(new View.OnClickListener() {

                    private long selecteddate;

                    @Override
                    public void onClick(View v) {

                        CalendarView calendarView = new CalendarView(getActivity());
                        calendarView.setDate(System.currentTimeMillis(),false,true);
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                                // 선택된 날짜를 밀리초로 계산
                                selecteddate = new java.util.GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
                            }
                        });

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("날짜")
                                .setView(calendarView)  // 다이얼로그에 CalendarView 추가
                                .setPositiveButton("설정", (dialog, which) -> {
                                    // 날짜가 선택되었을 때의 처리




                                    LocalDate localDate = LocalDate.ofInstant(
                                            java.time.Instant.ofEpochMilli(selecteddate),
                                            ZoneId.of("Asia/Seoul") // 시스템의 기본 타임존

                                    );

                                    temperalSchedule.due_date=localDate;
                                    datetitle.setText(temperalSchedule.due_date.toString());
                                })
                                .setNegativeButton("취소", (dialog, which) -> {
                                    // 취소 버튼 클릭 시
                                    dialog.dismiss();
                                });

                        // 다이얼로그 표시
                        builder.create().show();
                    }
                });

                // 버튼 클릭 리스너 설정
                Okbutton.setOnClickListener(b -> {
                    // 확인 버튼 클릭 시 동작

                    temperalSchedule.done=false;
                    temperalSchedule.text=schedulelogtext.getText().toString();

                    if( temperalSchedule.importance==0)
                    {
                        Toast.makeText(getActivity().getBaseContext(), "중요도를 설정해주세요",
                                Toast.LENGTH_SHORT ).show();
                        return;
                    }


                    MainActivity activity = (MainActivity) getActivity();
                    if(activity.syncTreeSet.add(temperalSchedule))
                    {
                        if(temperalSchedule.due_date.isEqual(LocalDate.now()))
                        {
                            scheduleToday.add(temperalSchedule);
                            setListViewHeightBasedOnChildren(binding.todaylist,scheduleToday.size());
                            scheduleTodayGroupListAdapter.notifyDataSetChanged();

                        }

                        else if(temperalSchedule.due_date.isBefore(LocalDate.now()))
                        {
                            scheduleDelay.add(temperalSchedule);
                            setListViewHeightBasedOnChildren(binding.Delayedlist,scheduleDelay.size());
                            scheduleDelayGroupListAdapter.notifyDataSetChanged();
                        }


                    }

                    //temperalSchedule.clear();

                    activity.DataChangeNotfiy();

                    dialog.dismiss(); // 다이얼로그 닫기
                });

                cancelbutton.setOnClickListener(b -> {
                    // 취소 버튼 클릭 시 동작
                    //temperalSchedule.clear();

                    dialog.dismiss(); // 다이얼로그 닫기
                });

                dialog.show(); // 다이얼로그 표시
            });


        }




    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        binding = null;
    }


    public class HabitGroupListAdapter extends ArrayAdapter<habit> {
        private final Activity context;


        public HabitGroupListAdapter(Activity context) {
            super(context, R.layout.habitlistitemgroup, habitToday);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.habitlistitemgroup, null, true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView days = (TextView) rowView.findViewById(R.id.days);

            habit de = habitToday.get(pos);

            title.setText(de.Name);
            if(de.CheckedDate[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()])
            {
                imageView.setImageResource(habitViewModel.doneimage[de.pos]);
            }

            else
            {
                imageView.setImageResource(de.Icon);
            }

            days.setText("오늘");

            return rowView;
        }


    }
    public class ScheduleTodayGroupListAdapter extends ArrayAdapter<schedule> {
        private final Activity context;


        public ScheduleTodayGroupListAdapter(Activity context) {
            super(context, R.layout.matrix, scheduleToday);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.matrix, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView exday = (TextView) rowView.findViewById(R.id.exday);

            schedule de = scheduleToday.get(pos);

            title.setText(de.text);
            exday.setText("오늘");

            CheckBox button = (CheckBox)rowView.findViewById(R.id.button);








            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 경우 처리

                    scheduleToday.remove(pos);

                    MainActivity activity = (MainActivity) getActivity();
                    activity.syncTreeSet.remove(de);
                    de.done=true;
                    activity.syncTreeSet.add(de);

                    this.notifyDataSetChanged();

                    setListViewHeightBasedOnChildren(binding.todaylist,scheduleToday.size());
                    activity.DataChangeNotfiy();


                } else {
                    // 체크 해제된 경우 처리

                }
            });


            return rowView;
        }


    }
    public class ScheduleDelayGroupListAdapter extends ArrayAdapter<schedule> {
        private final Activity context;

        public ScheduleDelayGroupListAdapter(Activity context) {
            super(context, R.layout.matrix, scheduleDelay);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.matrix, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView days = (TextView) rowView.findViewById(R.id.exday);

            schedule de = scheduleDelay.get(pos);

            title.setText(de.text);
            days.setText(de.due_date.toString());

            CheckBox button = (CheckBox)rowView.findViewById(R.id.button);

            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 경우 처리

                    scheduleDelay.remove(pos);

                    MainActivity activity = (MainActivity) getActivity();
                    activity.syncTreeSet.remove(de);
                    de.done=true;
                    activity.syncTreeSet.add(de);

                    this.notifyDataSetChanged();

                    setListViewHeightBasedOnChildren(binding.Delayedlist,scheduleDelay.size());

                    activity.DataChangeNotfiy();


                } else {
                    // 체크 해제된 경우 처리

                }
            });



            return rowView;
        }


    }


    public void setListViewHeightBasedOnChildren(ListView listView, int listsize) {
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



}