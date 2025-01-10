package com.example.ticktick2.ui.notifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
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

import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentNotificationsBinding;
import com.example.ticktick2.dataobject.ScheduleViewModel;
import com.example.ticktick2.dataobject.habit;
import com.example.ticktick2.dataobject.habitstatistics;
import com.example.ticktick2.dataobject.schedule;
import com.example.ticktick2.ui.habit.EventDecorator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class NotificationsFragment extends Fragment {

    private ScheduletIAdapter scheduletIAdapter;
    private ScheduletIIAdapter scheduletIIAdapter;
    private ScheduletIIIAdapter scheduletIIIAdapter;
    private ScheduletIVAdapter scheduletIVAdapter;

    private Set<schedule> scheduleSet;

    private List<schedule> scheduleI;
    private List<schedule> scheduleII;
    private List<schedule> scheduleIII;
    private List<schedule> scheduleIV;


    private ScheduleViewModel scheduleViewModel = null;


    private FragmentNotificationsBinding binding;



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

        schedulelogtext.setText(item.text);
        datetitle.setText(temperalSchedule.due_date.getYear()+" 년"+(temperalSchedule.due_date.getMonthValue())+" 월"+temperalSchedule.due_date.getDayOfMonth()+" 일");

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

            int past_importance = item.importance;
            switch(past_importance)
            {
                case 1:
                    scheduleI.remove(item);
                    scheduletIAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    scheduleII.remove(item);
                    scheduletIIAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    scheduleIII.remove(item);
                    scheduletIIIAdapter.notifyDataSetChanged();
                    break;
                case 4:
                    scheduleIV.remove(item);
                    scheduletIVAdapter.notifyDataSetChanged();
                    break;
            }

            switch(temperalSchedule.importance) {
                case 1:
                    scheduleI.add(temperalSchedule);
                    scheduletIAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    scheduleII.add(temperalSchedule);
                    scheduletIIAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    scheduleIII.add(temperalSchedule);
                    scheduletIIIAdapter.notifyDataSetChanged();
                    break;
                case 4:
                    scheduleIV.add(temperalSchedule);
                    scheduletIVAdapter.notifyDataSetChanged();
                    break;

            }

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

        MainActivity activity = (MainActivity) getActivity();

        if(  activity.syncTreeSet.remove(item)) {

            activity.syncTreeSet.remove(item);
            activity.DataChangeNotfiy();
            switch (item.importance) {
                case 1:
                    scheduleI.remove(item);
                    scheduletIAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    scheduleII.remove(item);
                    scheduletIIAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    scheduleIII.remove(item);
                    scheduletIIIAdapter.notifyDataSetChanged();
                    break;
                case 4:
                    scheduleIV.remove(item);
                    scheduletIVAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        scheduleViewModel =
                new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();
        scheduleSet = activity.syncTreeSet;


        {
            scheduleI = new ArrayList<>();
            scheduleII = new ArrayList<>();
            scheduleIII = new ArrayList<>();
            scheduleIV = new ArrayList<>();
        }

        {

            scheduleSet.forEach(item -> {

                if (!item.done) {

                    switch (item.importance) {
                        case 1:
                            scheduleI.add(item);
                            break;
                        case 2:
                            scheduleII.add(item);
                            break;
                        case 3:
                            scheduleIII.add(item);
                            break;
                        case 4:
                            scheduleIV.add(item);
                            break;
                        default:
                            break;

                    }
                }

            });


        }

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



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
                      switch(temperalSchedule.importance)
                      {
                          case 1:
                              scheduleI.add(temperalSchedule);
                              scheduletIAdapter.notifyDataSetChanged();
                              break;
                          case 2:
                              scheduleII.add(temperalSchedule);
                              scheduletIIAdapter.notifyDataSetChanged();
                              break;
                          case 3:
                              scheduleIII.add(temperalSchedule);
                              scheduletIIIAdapter.notifyDataSetChanged();
                              break;
                          case 4:
                              scheduleIV.add(temperalSchedule);
                              scheduletIVAdapter.notifyDataSetChanged();
                              break;
                          default:
                              break;

                      }

                      activity.DataChangeNotfiy();


                    }

                    //temperalSchedule.clear();



                    dialog.dismiss(); // 다이얼로그 닫기
                });

                cancelbutton.setOnClickListener(b -> {
                    // 취소 버튼 클릭 시 동작
                    temperalSchedule.clear();

                    dialog.dismiss(); // 다이얼로그 닫기
                });

                dialog.show(); // 다이얼로그 표시
            });
        }

        {
            scheduletIAdapter = new ScheduletIAdapter(getActivity());

            binding.firsttext.setAdapter(scheduletIAdapter);

            binding.firsttext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                             scheduleViewModel.setSelectedSchedule(scheduleI.get(position));
                                                             MainActivity activity = (MainActivity) getActivity();
                                                             showOptionDialog(scheduleI.get(position));
                                                         }
                                                     }
            );
        }

        {
            scheduletIIAdapter = new ScheduletIIAdapter(getActivity());

            binding.secondtext.setAdapter(scheduletIIAdapter);

            binding.secondtext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                             scheduleViewModel.setSelectedSchedule(scheduleII.get(position));
                                                             MainActivity activity = (MainActivity) getActivity();
                                                             showOptionDialog(scheduleII.get(position));
                                                         }
                                                     }
            );
        }

        {
            scheduletIIIAdapter = new ScheduletIIIAdapter(getActivity());

            binding.thirdtext.setAdapter(scheduletIIIAdapter);

            binding.thirdtext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                          @Override
                                                          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                              scheduleViewModel.setSelectedSchedule(scheduleIII.get(position));
                                                              MainActivity activity = (MainActivity) getActivity();
                                                              showOptionDialog(scheduleIII.get(position));
                                                          }
                                                      }
            );
        }


        {
            scheduletIVAdapter = new ScheduletIVAdapter(getActivity());

            binding.forthtext.setAdapter(scheduletIVAdapter);

            binding.forthtext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                         @Override
                                                         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                             scheduleViewModel.setSelectedSchedule(scheduleIV.get(position));
                                                             MainActivity activity = (MainActivity) getActivity();
                                                             showOptionDialog(scheduleIV.get(position));
                                                         }
                                                     }
            );
        }

    }




    public class ScheduletIAdapter extends ArrayAdapter<schedule> {
        private final Activity context;

        public ScheduletIAdapter(Activity context) {
            super(context, R.layout.matrix, scheduleI);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.matrix, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView exday = (TextView) rowView.findViewById(R.id.exday);

            schedule de = scheduleI.get(pos);

            title.setText(de.text);

            exday.setText(de.due_date.toString());

            CheckBox button = (CheckBox)rowView.findViewById(R.id.button);
            button.setButtonTintList(ColorStateList.valueOf(Color.RED));
            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 경우 처리

                    scheduleI.remove(pos);

                    MainActivity activity = (MainActivity) getActivity();
                    activity.syncTreeSet.remove(de);
                    de.done=true;
                    activity.syncTreeSet.add(de);

                    activity.DataChangeNotfiy();


                    this.notifyDataSetChanged();

                } else {
                    // 체크 해제된 경우 처리

                }
            });





            return rowView;
        }


    }

    public class ScheduletIIAdapter extends ArrayAdapter<schedule> {
        private final Activity context;

        public ScheduletIIAdapter(Activity context) {
            super(context, R.layout.matrix, scheduleII);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.matrix, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView exday = (TextView) rowView.findViewById(R.id.exday);

            schedule de = scheduleII.get(pos);

            title.setText(de.text);

            exday.setText(de.due_date.toString());

            CheckBox button = (CheckBox)rowView.findViewById(R.id.button);
            button.setButtonTintList(ColorStateList.valueOf(Color.YELLOW));
            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 경우 처리

                    scheduleII.remove(pos);

                    MainActivity activity = (MainActivity) getActivity();
                    activity.syncTreeSet.remove(de);
                    de.done=true;
                    activity.syncTreeSet.add(de);

                    activity.DataChangeNotfiy();

                    this.notifyDataSetChanged();

                } else {
                    // 체크 해제된 경우 처리

                }
            });



            return rowView;
        }


    }

    public class ScheduletIIIAdapter extends ArrayAdapter<schedule> {
        private final Activity context;

        public ScheduletIIIAdapter(Activity context) {
            super(context, R.layout.matrix, scheduleIII);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.matrix, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView exday = (TextView) rowView.findViewById(R.id.exday);

            schedule de = scheduleIII.get(pos);

            title.setText(de.text);

            exday.setText(de.due_date.toString());

            CheckBox button = (CheckBox)rowView.findViewById(R.id.button);
            button.setButtonTintList(ColorStateList.valueOf(Color.BLUE));
            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 경우 처리

                    scheduleIII.remove(pos);

                    MainActivity activity = (MainActivity) getActivity();
                    activity.syncTreeSet.remove(de);
                    de.done=true;
                    activity.syncTreeSet.add(de);

                    activity.DataChangeNotfiy();


                    this.notifyDataSetChanged();

                } else {
                    // 체크 해제된 경우 처리

                }
            });



            return rowView;
        }


    }

    public class ScheduletIVAdapter extends ArrayAdapter<schedule> {
        private final Activity context;

        public ScheduletIVAdapter(Activity context) {
            super(context, R.layout.matrix, scheduleIV);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.matrix, null, true);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView exday = (TextView) rowView.findViewById(R.id.exday);

            schedule de = scheduleIV.get(pos);
            title.setText(de.text);

            exday.setText(de.due_date.toString());

            CheckBox button = (CheckBox)rowView.findViewById(R.id.button);
            button.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
            button.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 경우 처리

                    scheduleIV.remove(pos);

                    MainActivity activity = (MainActivity) getActivity();
                    activity.syncTreeSet.remove(de);
                    de.done=true;
                    activity.syncTreeSet.add(de);

                    activity.DataChangeNotfiy();


                    this.notifyDataSetChanged();

                } else {
                    // 체크 해제된 경우 처리

                }
            });



            return rowView;
        }


    }






    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}

