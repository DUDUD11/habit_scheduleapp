package com.example.ticktick2.ui.habit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;


import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitsaddpagetwoBinding;
import com.example.ticktick2.dataobject.habit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HabitAddPageTwo extends Fragment {

    private long selectedDate;

    private long finishedDate;

    private habit AddHabit;

    private FragmentHabitsaddpagetwoBinding binding;
    private HabitViewModel habitViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitsaddpagetwoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        {
            binding.monbutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
            binding.tuebutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
            binding.wedbutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
            binding.thubutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
            binding.fributton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
            binding.satbutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
            binding.sunbutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));
            AddHabit = habitViewModel.newHabit.getValue();
            Arrays.fill(AddHabit.frequencyDay, true);
        }

        {
            binding.daybutton.setTextColor(getResources().getColor(R.color.blue_text));
            binding.frequencybutton.setTextColor(getResources().getColor(R.color.white));


            AddHabit.Frequency=0;
            Arrays.fill(AddHabit.frequencyDay,true);


            binding.frequencybuttonLayout.setVisibility(View.GONE);



            if(!habitViewModel.editinghabit) {
                binding.startdayTextview.setText(LocalDate.now().toString());
                AddHabit.StartDate = LocalDate.now();
            }

            else
            {
                binding.startdayTextview.setText(AddHabit.StartDate.toString());
            }


            AddHabit.group = habit.Group.etc;

            binding.radioetc.setChecked(true);

            LocalTime _time = LocalTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");


            String formattedTime = _time.format(formatter);

            AddHabit.Alarm_time = LocalTime.parse(formattedTime,formatter);

            binding.timeTextView.setText(formattedTime);



        }

        {
            binding.numberPicker.setMinValue(2);
            binding.numberPicker.setMaxValue(30);
            binding.numberPicker.setValue(2);

            binding.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    // 값이 변경될 때 실행할 코드

                    AddHabit.Frequency=newVal;

                }
            });


        }

        {
            binding.switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Switch가 켜졌을 때 실행되는 코드

                        AddHabit.HabitLog=true;

                    } else {
                        // Switch가 꺼졌을 때 실행되는 코드

                        AddHabit.HabitLog=false;

                    }
                }
            });

        }

        {
            binding.startdayFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CalendarView calendarView = new CalendarView(getActivity());
                    // CalendarView의 속성 설정

                    calendarView.setDate(System.currentTimeMillis(),false,true);

                    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                            // 선택된 날짜를 밀리초로 계산
                            selectedDate = new java.util.GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
                        }
                    });


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("날짜")
                            .setView(calendarView)  // 다이얼로그에 CalendarView 추가
                            .setPositiveButton("설정", (dialog, which) -> {
                                // 날짜가 선택되었을 때의 처리


                                LocalDate localDate = LocalDate.ofInstant(
                                        java.time.Instant.ofEpochMilli(selectedDate),
                                        ZoneId.of("Asia/Seoul") // 시스템의 기본 타임존


                                        // 참조 제거

                                );


                                Toast.makeText(getActivity().getBaseContext(), localDate.toString(),
                                        Toast.LENGTH_SHORT ).show();

                                binding.startdayTextview.setText(localDate.toString());

                                AddHabit.StartDate = localDate;


                            })
                            .setNegativeButton("취소", (dialog, which) -> {
                                // 취소 버튼 클릭 시
                                dialog.dismiss();
                            });

                    // 다이얼로그 표시
                    builder.create().show();

                }
            });





        }

        {
            binding.finishdayTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CalendarView calendarView = new CalendarView(getActivity());
                    // CalendarView의 속성 설정

                    calendarView.setDate(System.currentTimeMillis(),false,true);

                    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                            // 선택된 날짜를 밀리초로 계산
                            finishedDate = new java.util.GregorianCalendar(year, month, dayOfMonth).getTimeInMillis();
                        }
                    });


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("날짜")
                            .setView(calendarView)  // 다이얼로그에 CalendarView 추가
                            .setPositiveButton("설정", (dialog, which) -> {
                                // 날짜가 선택되었을 때의 처리


                                LocalDate localDate = LocalDate.ofInstant(
                                        java.time.Instant.ofEpochMilli(finishedDate),
                                        ZoneId.of("Asia/Seoul") // 시스템의 기본 타임존

                                );


                                if(localDate.isBefore(LocalDate.now()))
                                {
                                    Toast.makeText(getActivity().getBaseContext(), localDate.toString(),
                                            Toast.LENGTH_SHORT ).show();

                                    binding.finishdayTextview.setText("영원히");

                                    AddHabit.EndDate = null;

                                }

                                else {


                                    binding.finishdayTextview.setText(localDate.toString());

                                    AddHabit.EndDate = localDate;

                                }

                            })
                            .setNegativeButton("취소", (dialog, which) -> {
                                // 취소 버튼 클릭 시
                                dialog.dismiss();
                            });

                    // 다이얼로그 표시
                    builder.create().show();

                }
            });
        }

        binding.timeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton selectedRadioButton = root.findViewById(checkedId);




                if(selectedRadioButton.equals(binding.radiomorning))
                {
                    AddHabit.group= habit.Group.morning;
                }

                else if(selectedRadioButton.equals(binding.radioafternoon))
                {
                    AddHabit.group=habit.Group.afternoon;
                }


                else if(selectedRadioButton.equals(binding.radioNight))
                {
                    AddHabit.group= habit.Group.night;
                }

                else
                {
                    AddHabit.group=habit.Group.etc;
                }

            }
        });


        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;
        binding.daybutton.setOnClickListener(v -> {

            binding.daybutton.setTextColor(getResources().getColor(R.color.blue_text));
            binding.frequencybutton.setTextColor(getResources().getColor(R.color.white));


            AddHabit.Frequency=0;

            binding.frequencybuttonLayout.setVisibility(View.GONE);
            binding.daybuttonLayout.setVisibility(View.VISIBLE);

        });

        binding.frequencybutton.setOnClickListener(v -> {

            binding.frequencybutton.setTextColor(getResources().getColor(R.color.blue_text));
            binding.daybutton.setTextColor(getResources().getColor(R.color.white));


            AddHabit.Frequency=2;


            binding.frequencybuttonLayout.setVisibility(View.VISIBLE);
            binding.daybuttonLayout.setVisibility(View.GONE);


        });

        binding.monbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.monbutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));


                    AddHabit.frequencyDay[0]=true;


                } else {
                    binding.monbutton.setBackgroundColor(Color.argb(0, 255, 255, 255));

                    AddHabit.frequencyDay[0]=false;

                }
            }
        });

        binding.tuebutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tuebutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));


                    AddHabit.frequencyDay[1]=true;


                } else {
                    binding.tuebutton.setBackgroundColor(Color.argb(0, 255, 255, 255));

                    AddHabit.frequencyDay[1]=false;

                }
            }
        });

        binding.wedbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.wedbutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));


                    AddHabit.frequencyDay[2]=true;


                } else {
                    binding.wedbutton.setBackgroundColor(Color.argb(0, 255, 255, 255));

                    AddHabit.frequencyDay[2]=false;

                }
            }
        });

        binding.thubutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.thubutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));


                    AddHabit.frequencyDay[3]=true;


                } else {
                    binding.thubutton.setBackgroundColor(Color.argb(0, 255, 255, 255));

                    AddHabit.frequencyDay[3]=false;

                }
            }
        });

        binding.fributton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.fributton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));


                    AddHabit.frequencyDay[4]=true;


                } else {
                    binding.fributton.setBackgroundColor(Color.argb(0, 255, 255, 255));

                    AddHabit.frequencyDay[4]=false;

                }
            }
        });

        binding.satbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.satbutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));


                    AddHabit.frequencyDay[5]=true;


                } else {
                    binding.satbutton.setBackgroundColor(Color.argb(0, 255, 255, 255));

                    AddHabit.frequencyDay[5]=false;

                }
            }
        });

        binding.sunbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.sunbutton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_rounded));


                    AddHabit.frequencyDay[6]=true;


                } else {
                    binding.sunbutton.setBackgroundColor(Color.argb(0, 255, 255, 255));

                    AddHabit.frequencyDay[6]=false;

                }
            }
        });

        binding.timeTextView.setOnClickListener(v -> {
            // 현재 시간을 가져오기 위해 Calendar 사용
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY); // 현재 시간 (24시간 형식)
            int minute = calendar.get(Calendar.MINUTE); // 현재 분

            // TimePickerDialog 생성
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getActivity(),
                    (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
                        // 선택한 시간을 TextView에 표시
                        String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                        binding.timeTextView.setText(time);


                        AddHabit.Alarm_time= LocalTime.parse(time);


                    },
                    hour, minute, true
            );

            timePickerDialog.show();




        });

        binding.newHabitSavebutton.setOnClickListener(v -> {

            List<habit>  synchronizedHabitList = activity.synchronizedHabitList;

            if(!habitViewModel.editinghabit) {
                synchronizedHabitList.add(AddHabit);
            }
            else {
                int idx = synchronizedHabitList.indexOf(habitViewModel.getSelectedHabit());
                if(idx!=-1)
                {
                    synchronizedHabitList.set(idx,AddHabit);
                }

            }

            activity.DataChangeNotfiy();

            BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);

            navView.setVisibility(View.VISIBLE);

            habitViewModel.resetNewHabit();

            navController.navigate(R.id.navigation_habits, null,
                    new NavOptions.Builder()
                            .setPopUpTo(R.id.navigation_habits, true) // 스택에서 A를 남기고 모두 제거
                            .build());

        });

    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("delete", "habitaddpagetwo");
    }
}