package com.example.ticktick2.ui.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentDashboardBinding;
import com.example.ticktick2.dataobject.ScheduleDecorator;
import com.example.ticktick2.dataobject.ScheduleViewModel;
import com.example.ticktick2.dataobject.habit;
import com.example.ticktick2.dataobject.habitstatistics;
import com.example.ticktick2.dataobject.schedule;

import com.example.ticktick2.ui.habit.HabitViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private HabitTodayGroupListAdapter habitCustomListAdapter;
    private ScheduleGroupListAdapter scheduleTodayGroupListAdapter;
    private ScheduleTodayFinishGroupListAdapter scheduleTodayDoneGroupListAdapter;

    private List<habit> habitList = null;
    private Set<schedule> scheduleSet;

    private HabitViewModel habitViewModel = null;
    private ScheduleViewModel scheduleViewModel = null;

    private List<habit> habitToday;

    private List<schedule> scheduleMonth;

    private List<schedule> scheduleTodayDone;
    private List<schedule> scheduleToday;

    private Set<CalendarDay> dayScheduleSet;

    private CalendarDay SelectedDay=CalendarDay.today();

    private final CalendarDay today = CalendarDay.today();
    private final LocalDate today_local = LocalDate.now();

    private void showOptionDialog(schedule item) {
        new AlertDialog.Builder(getActivity())
                .setTitle("옵션 선택")
                .setMessage("변경 또는 삭제를 선택하세요.")

                // 변경 버튼
                .setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 변경 버튼을 클릭했을 때의 동작
                        performChangeAction(item);
                    }
                })

                // 삭제 버튼
                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 삭제 버튼을 클릭했을 때의 동작
                        performDeleteAction(item);
                    }
                })

                // 취소 버튼
                .setNeutralButton("취소", null)

                // 다이얼로그 표시
                .show();
    }

    private void performChangeAction(schedule item) {
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

        datetitle.setText(temperalSchedule.due_date.getYear()+"년 "+(temperalSchedule.due_date.getMonthValue())+"월 "+temperalSchedule.due_date.getDayOfMonth()+"일 ");
        schedulelogtext.setText(item.text);

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
            activity.DataChangeNotfiy();

            GetMonthlySchedule(item.due_date.getYear(),item.due_date.getMonthValue());
            GetScheduleday(item.due_date);
            scheduleTodayGroupListAdapter.notifyDataSetChanged();
            scheduleTodayDoneGroupListAdapter.notifyDataSetChanged();

            setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
            setListViewHeightBasedOnChildren(binding.finishlist,scheduleTodayDone.size());
            binding.calendarView.removeDecorators();
            SetDayColor(item.due_date.getYear(),item.due_date.getMonthValue());
            dayScheduleSet.forEach((key) ->
            {
                ScheduleDecorator decorator = new ScheduleDecorator(key,getActivity());
                binding.calendarView.addDecorators(decorator);

            });

            dialog.dismiss(); // 다이얼로그 닫기
        });

        cancelbutton.setOnClickListener(b -> {
            // 취소 버튼 클릭 시 동작
            //temperalSchedule.clear();

            dialog.dismiss(); // 다이얼로그 닫기
        });

        dialog.show(); // 다이얼로그 표시
    }

    private void performDeleteAction(schedule item) {


        if(scheduleSet.remove(item))
        {
            MainActivity activity = (MainActivity) getActivity();

            activity.syncTreeSet.remove(item);
            activity.DataChangeNotfiy();

            GetMonthlySchedule(item.due_date.getYear(),item.due_date.getMonthValue());
            GetScheduleday(item.due_date);
            scheduleTodayGroupListAdapter.notifyDataSetChanged();
            scheduleTodayDoneGroupListAdapter.notifyDataSetChanged();

            setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
            setListViewHeightBasedOnChildren(binding.finishlist,scheduleTodayDone.size());
            binding.calendarView.removeDecorators();
            SetDayColor(item.due_date.getYear(),item.due_date.getMonthValue());
            dayScheduleSet.forEach((key) ->
            {
                ScheduleDecorator decorator = new ScheduleDecorator(key,getActivity());
                binding.calendarView.addDecorators(decorator);

            });
        }
    }


    private void SetDayColor(int year, int month)
    {

        dayScheduleSet.clear();
        for(schedule item : scheduleMonth)
        {
            dayScheduleSet.add(CalendarDay.from(year,month-1,item.due_date.getDayOfMonth()));
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



    private void GetMonthlySchedule(int year,int month)
    {
        scheduleMonth.clear();

        LocalDate monthstart = LocalDate.of(year,month,1).minusDays(1);
        LocalDate monthend = LocalDate.of(year,month,1).plusMonths(1);

        scheduleSet.forEach(task -> {

            if (task.due_date.isAfter(monthstart) && task.due_date.isBefore(monthend))
            {
                scheduleMonth.add(task);
            }

            else if(task.due_date.isAfter(monthend))
            {
                //하루더계산하긴 할거임
                return;
            }
        });

    }

    private void GetScheduleday(LocalDate date)
    {
        scheduleToday.clear();
        scheduleTodayDone.clear();

        scheduleMonth.forEach(task -> {

            if (task.due_date.isEqual(date))
            {

                if(task.done)
                {
                    scheduleTodayDone.add(task);
                }
                else
                {
                    scheduleToday.add(task);
                }
            }

            else if(task.due_date.isAfter(date))
            {
                return;
            }
        });
    }


    private void GetHabitToday(LocalDate date)
    {
        habitToday.clear();

        for(int i=0;i<habitList.size();i++)
        {
            if(habitList.get(i).StartDate.isAfter(date)) continue;

            if(habitList.get(i).EndDate != null && habitList.get(i).EndDate.isBefore(date)) continue;


            if(habitList.get(i).Frequency==0)
            {
                int today = (date.getDayOfWeek().getValue()+6)%7;

                if(habitList.get(i).frequencyDay[today])
                {
                    habitToday.add(habitList.get(i));
                }
            }
            else
            {
                long daysBetween = ChronoUnit.DAYS.between(habitList.get(i).StartDate, date);
                if(daysBetween<0) continue;

                if(daysBetween%habitList.get(i).Frequency==0)
                {
                    habitToday.add(habitList.get(i));
                }
            }
        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);
        scheduleViewModel =
                new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();

        habitList = activity.synchronizedHabitList;
        scheduleSet = activity.syncTreeSet;

        habitToday = new ArrayList<>();

        scheduleMonth = new ArrayList<>();
        scheduleTodayDone = new ArrayList<>();
        scheduleToday = new ArrayList<>();

        {

            GetMonthlySchedule(LocalDate.now().getYear(),LocalDate.now().getMonthValue());
            GetScheduleday(LocalDate.now());
            GetHabitToday(LocalDate.now());
            binding.calendarView.setSelectedDate(CalendarDay.today());
        }

        {
            binding.scheduledateview.setText("일정        "+LocalDate.now().getMonthValue()+"월 "+LocalDate.now().getDayOfMonth()+"일");
            dayScheduleSet = new HashSet<>();
            SetDayColor(LocalDate.now().getYear(),LocalDate.now().getMonthValue());
            dayScheduleSet.forEach((key) ->
            {
                ScheduleDecorator decorator = new ScheduleDecorator(key,getActivity());
                binding.calendarView.addDecorators(decorator);

            });





        }


        return root;
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            habitCustomListAdapter = new HabitTodayGroupListAdapter(getActivity());

            binding.habitlist.setAdapter(habitCustomListAdapter);

            binding.habitlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                             habitViewModel.setSelectedHabit(habitToday.get(position));
                                                             MainActivity activity = (MainActivity) getActivity();
                                                             NavController navController = activity.navController;
                                                             navController.navigate(R.id.dashboardFragment_to_HabitDetail);
                                                         }
                                                     }
            );
        }

        {

            scheduleTodayGroupListAdapter = new ScheduleGroupListAdapter(getActivity());

            binding.schedulelist.setAdapter(scheduleTodayGroupListAdapter);

            binding.schedulelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                           @Override
                                                           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                               scheduleViewModel.setSelectedSchedule(scheduleToday.get(position));
                                                               MainActivity activity = (MainActivity) getActivity();
                                                               showOptionDialog(scheduleToday.get(position));
                                                           }
                                                       }

            );
        }

        {
            scheduleTodayDoneGroupListAdapter = new ScheduleTodayFinishGroupListAdapter(getActivity());

            binding.finishlist.setAdapter(scheduleTodayDoneGroupListAdapter);

            binding.finishlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                             scheduleViewModel.setSelectedSchedule(scheduleTodayDone.get(position));
                                                             MainActivity activity = (MainActivity) getActivity();
                                                             showOptionDialog(scheduleTodayDone.get(position));
                                                         }
                                                     }
            );
        }


        {


            binding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                    if (selected) {

                        SelectedDay = date;

                        LocalDate localDate = LocalDate.of(date.getYear(),date.getMonth()+1,date.getDay());
                        GetMonthlySchedule(date.getYear(),date.getMonth()+1);
                        GetScheduleday(localDate);
                        GetHabitToday(localDate);

                        scheduleTodayGroupListAdapter.notifyDataSetChanged();
                        scheduleTodayDoneGroupListAdapter.notifyDataSetChanged();
                        habitCustomListAdapter.notifyDataSetChanged();

                        setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
                        setListViewHeightBasedOnChildren(binding.finishlist,scheduleTodayDone.size());
                        setListViewHeightBasedOnChildren(binding.habitlist,habitToday.size());

                        binding.scheduledateview.setText("일정        "+localDate.getMonthValue()+"월 "+localDate.getDayOfMonth()+"일");

                        SetDayColor(date.getYear(),date.getMonth()+1);
                        dayScheduleSet.forEach((key) ->
                        {
                            ScheduleDecorator decorator = new ScheduleDecorator(key,getActivity());
                            binding.calendarView.addDecorators(decorator);

                        });


                    }

                }

            });

        }

        {

            binding.calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
                @Override
                public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                    SelectedDay = date;



                    if(date.getMonth()!=LocalDate.now().getMonthValue()-1) {
                        binding.calendarView.setSelectedDate(date);

                        LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth() + 1, date.getDay());
                        GetMonthlySchedule(date.getYear(), date.getMonth() + 1);
                        GetScheduleday(localDate);
                        GetHabitToday(localDate);

                        scheduleTodayGroupListAdapter.notifyDataSetChanged();
                        scheduleTodayDoneGroupListAdapter.notifyDataSetChanged();
                        habitCustomListAdapter.notifyDataSetChanged();

                        setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
                        setListViewHeightBasedOnChildren(binding.finishlist,scheduleTodayDone.size());
                        setListViewHeightBasedOnChildren(binding.habitlist,habitToday.size());
                        binding.scheduledateview.setText("일정        "+localDate.getMonthValue()+"월 "+localDate.getDayOfMonth()+"일");
                        SetDayColor(date.getYear(),date.getMonth()+1);
                        dayScheduleSet.forEach((key) ->
                        {
                            ScheduleDecorator decorator = new ScheduleDecorator(key,getActivity());
                            binding.calendarView.addDecorators(decorator);

                        });
                    }

                    else
                    {
                        SelectedDay = CalendarDay.today();

                        binding.calendarView.setSelectedDate(SelectedDay);

                        GetMonthlySchedule(SelectedDay.getYear(), SelectedDay.getMonth() + 1);
                        GetScheduleday(LocalDate.now());
                        GetHabitToday(LocalDate.now());

                        scheduleTodayGroupListAdapter.notifyDataSetChanged();
                        scheduleTodayDoneGroupListAdapter.notifyDataSetChanged();
                        habitCustomListAdapter.notifyDataSetChanged();

                        setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
                        setListViewHeightBasedOnChildren(binding.finishlist,scheduleTodayDone.size());
                        setListViewHeightBasedOnChildren(binding.habitlist,habitToday.size());

                        binding.scheduledateview.setText("일정        "+LocalDate.now().getMonthValue()+"월 "+LocalDate.now().getDayOfMonth()+"일");
                        SetDayColor(date.getYear(),date.getMonth()+1);
                        dayScheduleSet.forEach((key) ->
                        {
                            ScheduleDecorator decorator = new ScheduleDecorator(key,getActivity());
                            binding.calendarView.addDecorators(decorator);

                        });

                    }



                  //  setListViewHeightBasedOnChildren(binding.loglist,temperalHabitLog.size());

                   /*
                    dayColorMap.forEach((key, value) ->
                    {
                        EventDecorator decorator = new EventDecorator(key,value);
                        binding.calendarView.addDecorators(decorator);

                    });
                    */

                }
            });
        }


        {
            binding.fab.setOnClickListener(v -> {

                schedule temperalSchedule = new schedule();
                temperalSchedule.clear();
                temperalSchedule.due_date=LocalDate.of(SelectedDay.getYear(),SelectedDay.getMonth()+1,SelectedDay.getDay());

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

                if(SelectedDay==null) {
                    datetitle.setText(LocalDate.now().getYear() + "년 " + (LocalDate.now().getMonth().getValue()) + "월 " + LocalDate.now().getDayOfMonth() + "일 ");
                }

                else
                {
                    datetitle.setText(SelectedDay.getYear() + "년 " + (SelectedDay.getMonth()+1) + "월 " + SelectedDay.getDay() + "일 ");
                }

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

                        Calendar tmp = Calendar.getInstance();
                        tmp.set(SelectedDay.getYear(),SelectedDay.getMonth(),SelectedDay.getDay(),12,0,0);
                        tmp.set(Calendar.MILLISECOND,0);
                        calendarView.setDate(tmp.getTimeInMillis(),false,true);
                        selecteddate = tmp.getTimeInMillis();

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

                        if(SelectedDay.getYear()==temperalSchedule.due_date.getYear() && SelectedDay.getMonth()==temperalSchedule.due_date.getMonthValue()-1)
                        {
                            GetMonthlySchedule(temperalSchedule.due_date.getYear(),temperalSchedule.due_date.getMonthValue());
                            SetDayColor(temperalSchedule.due_date.getYear(),temperalSchedule.due_date.getMonthValue());
                            dayScheduleSet.forEach((key) ->
                            {
                                ScheduleDecorator decorator = new ScheduleDecorator(key,getActivity());
                                binding.calendarView.addDecorators(decorator);

                            });

                            if(SelectedDay.getDay() == temperalSchedule.due_date.getDayOfMonth())
                            {
                                GetScheduleday(temperalSchedule.due_date);

                                scheduleTodayGroupListAdapter.notifyDataSetChanged();
                                setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
                            }

                        }

                        activity.DataChangeNotfiy();

                    }

                    //temperalSchedule.clear();



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



        {

            setListViewHeightBasedOnChildren(binding.habitlist,habitToday.size());
            setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
            setListViewHeightBasedOnChildren(binding.finishlist,scheduleTodayDone.size());

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    public class HabitTodayGroupListAdapter extends ArrayAdapter<habit> {
        private final Activity context;


        public HabitTodayGroupListAdapter(Activity context) {
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

                if(de.CheckedFeeling[LocalDate.now().getYear()-2024][LocalDate.now().getMonthValue()][LocalDate.now().getDayOfMonth()]!=0)
                {
                    imageView.setImageAlpha(80);
                }
            }

            days.setText("오늘");

            return rowView;
        }


    }
    public class ScheduleGroupListAdapter extends ArrayAdapter<schedule> {
        private final Activity context;


        public ScheduleGroupListAdapter(Activity context) {
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

            LocalDate tmp_selectday =  LocalDate.of(SelectedDay.getYear(),SelectedDay.getMonth()+1,SelectedDay.getDay());

            long daysBetween = ChronoUnit.DAYS.between(today_local, tmp_selectday);
            daysBetween = Math.abs(daysBetween);

            if(SelectedDay.isInRange(today,today)) {
                exday.setText("오늘");
            }

            else
            {
               if(SelectedDay.isAfter(today))
               {
                   exday.setText(daysBetween+"일 후");
               }

               else
               {
                   exday.setText(daysBetween+"일 전");
               }

            }
            CheckBox button = (CheckBox)rowView.findViewById(R.id.button);

            switch(de.importance)
            {
                case 1:
                    button.setButtonTintList(ColorStateList.valueOf(Color.RED));
                    break;
                case 2:
                    button.setButtonTintList(ColorStateList.valueOf(Color.YELLOW));
                    break;
                case 3:
                    button.setButtonTintList(ColorStateList.valueOf(Color.BLUE));
                    break;
                case 4:
                    button.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
                    break;

            }

            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 경우 처리

                    scheduleToday.remove(pos);
                    MainActivity activity = (MainActivity) getActivity();
                    activity.syncTreeSet.remove(de);
                    de.done=true;
                    activity.syncTreeSet.add(de);
                    scheduleTodayDone.add(de);

                    this.notifyDataSetChanged();
                    scheduleTodayDoneGroupListAdapter.notifyDataSetChanged();


                    setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
                    setListViewHeightBasedOnChildren(binding.finishlist,scheduleTodayDone.size());

                    activity.DataChangeNotfiy();



                } else {
                    // 체크 해제된 경우 처리

                }
            });




            return rowView;
        }


    }
    public class ScheduleTodayFinishGroupListAdapter extends ArrayAdapter<schedule> {
        private final Activity context;

        public ScheduleTodayFinishGroupListAdapter(Activity context) {
            super(context, R.layout.matrix, scheduleTodayDone);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.matrix, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView days = (TextView) rowView.findViewById(R.id.exday);

            schedule de = scheduleTodayDone.get(pos);

            title.setText(de.text);
            days.setText("오늘");

            CheckBox button = (CheckBox)rowView.findViewById(R.id.button);
            button.setChecked(true);

            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 경우 처리


                } else {

                    scheduleTodayDone.remove(pos);

                    MainActivity activity = (MainActivity) getActivity();
                    activity.syncTreeSet.remove(de);
                    de.done=false;
                    activity.syncTreeSet.add(de);
                    scheduleToday.add(de);

                    this.notifyDataSetChanged();

                    scheduleTodayGroupListAdapter.notifyDataSetChanged();


                    setListViewHeightBasedOnChildren(binding.schedulelist,scheduleToday.size());
                    setListViewHeightBasedOnChildren(binding.finishlist,scheduleTodayDone.size());

                    activity.DataChangeNotfiy();


                }
            });




            return rowView;
        }


    }

}




/*



   SetDayColor(LocalDate.now().getYear(),LocalDate.now().getMonthValue());
            dayColorMap.forEach((key, value) ->
            {
                EventDecorator decorator = new EventDecorator(key,value);
                binding.calendarView.addDecorators(decorator);

            });

 */