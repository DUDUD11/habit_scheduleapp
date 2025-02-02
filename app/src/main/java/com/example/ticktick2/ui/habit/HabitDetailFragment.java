package com.example.ticktick2.ui.habit;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import com.example.ticktick2.AlarmBootClass;
import com.example.ticktick2.AlarmClass;
import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitsdetailBinding;
import com.example.ticktick2.dataobject.Alarm;
import com.example.ticktick2.dataobject.habit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;

public class HabitDetailFragment extends Fragment {

    private boolean checked;
    private float startY = 0f;

    private LocalDate LD_now;

    private habit temperallogsave;

    private FragmentHabitsdetailBinding binding;
    private HabitViewModel habitViewModel=null;

    private   FragmentStateAdapter fragmentStateAdapter=null;

    private String date_string;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // 메뉴 XML을 인플레이트

        inflater.inflate(R.menu.delete_option, menu);



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

        if (item.getItemId() == R.id.delete_menu) {


            MainActivity activity = (MainActivity) getActivity();

            habit tmp = habitViewModel.getSelectedHabit();
            if(tmp.alarm)
            {
                AlarmClass alarmClass = new AlarmClass();
                List<Alarm> alarmList = AlarmBootClass.loadAlarmList(getContext());
                Alarm save = null;

                for(Alarm a : alarmList)
                {
                    if(a.getAlarmName().equals(tmp.Name))
                    {
                        Long time = a.getAlarmTime();
                        LocalTime localTime = Instant.ofEpochMilli(time)
                                .atZone(ZoneOffset.UTC)  // UTC 시간대에 맞게 변환
                                .toLocalDateTime().toLocalTime();

                        if(!localTime.isAfter(tmp.Alarm_time) && !localTime.isBefore(tmp.Alarm_time))
                        {
                            save = a;
                            break;

                        }

                    }

                }

                if(save ==null)
                {

                    Toast.makeText(getContext(), "알람 업데이트 이전생성했다면 나타납니다" ,
                            Toast.LENGTH_SHORT).show();
                }
                
                else {

                    Intent intent = alarmClass.MakeIntent(getContext(), save);
                    alarmClass.cancelAlarmAndRemoveAlarm(getContext(), intent);
                }
            }


            activity.synchronizedHabitList.remove(habitViewModel.getSelectedHabit());
            activity.DataChangeNotfiy();
            habitViewModel.setSelectedHabit(null);

            requireActivity().onBackPressed();


            return true;
        }

        else if(item.getItemId() == R.id.edit_menu)
        {

            habitViewModel.editinghabit=true;
            habitViewModel.setNewHabit(habitViewModel.getSelectedHabit());

            MainActivity activity = (MainActivity) getActivity();
            NavController navController = activity.navController;

            navController.navigate(R.id.action_HabitDetail_to_HabitAddtwo);

        }

        super.onOptionsItemSelected(item);
        return true;


    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitsdetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final float originalX = root.getX();
        final float originalY = root.getY();


        checked=false;


        LD_now = LocalDate.now();


        date_string = LD_now.getMonthValue()+" 월"+LD_now.getDayOfMonth()+" 일";
        temperallogsave = habitViewModel.getSelectedHabit();


        DayOfWeek dayOfWeek = LD_now.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY:
                date_string += "월요일";
                break;
            case TUESDAY:
                date_string += "화요일";
                break;
            case WEDNESDAY:
                date_string += "수요일";
                break;
            case THURSDAY:
                date_string += "목요일";
                break;
            case FRIDAY:
                date_string += "금요일";
                break;
            case SATURDAY:
                date_string += "토요일";
                break;
            case SUNDAY:
                date_string += "일요일";
                break;
        }

        binding.backgroundImage.setImageResource(habitViewModel.getSelectedHabit().Icon);
        binding.backgroundImage.setAlpha(0.5f);
        binding.detailtitle.setText(habitViewModel.getSelectedHabit().Name);

        binding.detatiltext.setText(habitViewModel.getSelectedHabit().Text);

        BottomNavigationView navController = requireActivity().findViewById(R.id.nav_view);

        navController.setVisibility(View.GONE);




        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                {
                    BottomNavigationView controller = requireActivity().findViewById(R.id.nav_view);
                    controller.setVisibility(View.VISIBLE);
                }

                MainActivity activity = (MainActivity) getActivity();
                NavController navController = activity.navController;

                BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
                navView.setVisibility(View.VISIBLE);


                // 기본 뒤로 가기 동작 수행
                setEnabled(false); // 콜백 비활성화

                requireActivity().onBackPressed();
            }
        });



        {
            LocalDate today = LocalDate.now();

            int q=today.getYear();
            int p=today.getMonthValue();
            int t=today.getDayOfMonth();


            if (habitViewModel.getSelectedHabit().CheckedDate[q-2024][today.getMonthValue()][today.getDayOfMonth()])
            {
                binding.myseek.setVisibility(View.GONE);

            }
        }

        {
            root.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {


                        case MotionEvent.ACTION_MOVE:
                            // 손가락이 움직일 때마다 뷰를 부드럽게 따라가도록 애니메이션
                            float deltaX = event.getRawX() - v.getWidth() / 2;
                            float deltaY = event.getRawY() - v.getHeight() / 2;

                            // ObjectAnimator를 사용해 부드럽게 이동
                            ObjectAnimator animatorX = ObjectAnimator.ofFloat(v, "x", deltaX);
                            ObjectAnimator animatorY = ObjectAnimator.ofFloat(v, "y", deltaY);

                            // 애니메이션 실행
                            animatorX.start();
                            animatorY.start();
                            break;

                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float endY = event.getY();
                            float changedY = startY - endY;
                            // 아래로 스와이프
                            if (changedY > 100) {
                                showNextFragment();
                            }
/*
                            else if (changedY < -100) {
                               // showPreviousFragment();
                            }
*/
                            else
                            {
                                ObjectAnimator animatorX_ = ObjectAnimator.ofFloat(v, "x", v.getX(), originalX);
                                ObjectAnimator animatorY_ = ObjectAnimator.ofFloat(v, "y", v.getY(), originalY);
                                animatorX_.setDuration(300); // 300ms 동안 애니메이션
                                animatorY_.setDuration(300);
                                animatorX_.start();
                                animatorY_.start();
                            }

                            break;
                    }
                    return true;
                }
            });
        }


        return root;
    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.myseek.post(() -> {
            // post()는 UI 스레드에서 실행되므로, 진행 값을 강제로 30으로 설정
            if ( binding.myseek.getProgress() < 25) {
                binding.myseek.setProgress(25); // 30 이하로 내려가지 않도록 설정
            }
        });

        binding.myseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if (checked) return;

                if (progress < 25) {
                    binding.myseek.setProgress(25);

                }

                if (progress > 70) {
                    checked = true;

                    binding.myseek.setProgress(70);

                    EmitterConfig emitterConfig = new Emitter(2, TimeUnit.SECONDS).perSecond(60);
                    binding.konfettiView.start(
                            // 왼쪽 방향
                            new PartyFactory(emitterConfig)

                                    .angle(Angle.RIGHT - 45)

                                    .spread(Spread.WIDE)

                                    .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))

                                    .setSpeedBetween(10f, 60f)

                                    .position(new Position.Relative(0.0, 0.2))
                                    .build(),
                            // 오른쪽 방향
                            new PartyFactory(emitterConfig)
                                    .angle(Angle.LEFT + 45)
                                    .spread(Spread.WIDE)

                                    .colors(Arrays.asList(0x76E12F, 0x3B7017, 0x0b1604, 0x5eb425))
                                    .setSpeedBetween(10f, 60f)
                                    .position(new Position.Relative(1.0, 0.2))
                                    .build()
                    );

                    LocalDate today = LocalDate.now();

                    habit tmp = habitViewModel.getSelectedHabit();

                    MainActivity activity = (MainActivity) getActivity();

                    int idx = activity.synchronizedHabitList.indexOf(tmp);

                    if (idx != -1) {

                        tmp.AchieveDay++;
                        tmp.CheckedDate[today.getYear() - 2024][today.getMonthValue()][today.getDayOfMonth()] = true;

                        habitViewModel.setSelectedHabit(tmp);
                        activity.synchronizedHabitList.set(idx, tmp);


                        activity.DataChangeNotfiy();

                    }
                    binding.myseek.setVisibility(view.GONE);


                    if (tmp.HabitLog) {

                        // 1초 뒤에 실행
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {

                                if (!isAdded()) {
                                    return;
                                }

                                requireActivity().runOnUiThread(() -> {
                                    // Dialog 생성 및 설정


                                    Dialog dialog = new Dialog(requireActivity());
                                    dialog.setContentView(R.layout.habitdialog);

                                    // 다이얼로그 내부 요소 참조
                                    ImageView iconimage = dialog.findViewById(R.id.iconimage);
                                    TextView dialogtitle = dialog.findViewById(R.id.dialog_title);
                                    TextView datetitle = dialog.findViewById(R.id.date_title);
                                    ImageView firstfeel = dialog.findViewById(R.id.firstfeel);
                                    ImageView secondfeel = dialog.findViewById(R.id.secondfeel);
                                    ImageView thirdfeel = dialog.findViewById(R.id.thirdfeel);
                                    ImageView forthfeel = dialog.findViewById(R.id.forthfeel);
                                    ImageView fifthfeel = dialog.findViewById(R.id.fifthfeel);
                                    EditText habitlogtext = dialog.findViewById(R.id.habitlogtext);
                                    Button Okbutton = dialog.findViewById(R.id.ok_button);
                                    Button cancelbutton = dialog.findViewById(R.id.cancel_button);


                                    // 데이터 설정
                                    iconimage.setImageResource(habitViewModel.getSelectedHabit().Icon);
                                    dialogtitle.setText(habitViewModel.getSelectedHabit().Name);
                                    datetitle.setText(date_string);
                                    firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);
                                    secondfeel.setImageResource(R.drawable.feeling4_happy1);
                                    thirdfeel.setImageResource(R.drawable.feeling3_natural1);
                                    forthfeel.setImageResource(R.drawable.feeling2_sad1);
                                    fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);

                                    firstfeel.setOnClickListener(v -> {

                                        temperallogsave.CheckedFeeling[LD_now.getYear()-2024][LD_now.getMonthValue()][LD_now.getDayOfMonth()]=R.drawable.feeling5_verryhappy1;

                                        firstfeel.setImageResource(R.drawable.feeling5_verryhappy2);
                                        secondfeel.setImageResource(R.drawable.feeling4_happy1);
                                        thirdfeel.setImageResource(R.drawable.feeling3_natural1);
                                        forthfeel.setImageResource(R.drawable.feeling2_sad1);
                                        fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);


                                    });
                                    secondfeel.setOnClickListener(v -> {
                                        temperallogsave.CheckedFeeling[LD_now.getYear()-2024][LD_now.getMonthValue()][LD_now.getDayOfMonth()]=R.drawable.feeling4_happy1;

                                        secondfeel.setImageResource(R.drawable.feeling4_happy2);
                                        firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);
                                        thirdfeel.setImageResource(R.drawable.feeling3_natural1);
                                        forthfeel.setImageResource(R.drawable.feeling2_sad1);
                                        fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);


                                    });

                                    thirdfeel.setOnClickListener(v -> {
                                        temperallogsave.CheckedFeeling[LD_now.getYear()-2024][LD_now.getMonthValue()][LD_now.getDayOfMonth()]=R.drawable.feeling3_natural1;

                                        thirdfeel.setImageResource(R.drawable.feeling3_natural2);
                                        firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);
                                        secondfeel.setImageResource(R.drawable.feeling4_happy1);
                                        forthfeel.setImageResource(R.drawable.feeling2_sad1);
                                        fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);



                                    });

                                    forthfeel.setOnClickListener(v -> {
                                        temperallogsave.CheckedFeeling[LD_now.getYear()-2024][LD_now.getMonthValue()][LD_now.getDayOfMonth()]=R.drawable.feeling2_sad1;
                                        forthfeel.setImageResource(R.drawable.feeling2_sad2);
                                        firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);
                                        secondfeel.setImageResource(R.drawable.feeling4_happy1);
                                        thirdfeel.setImageResource(R.drawable.feeling3_natural1);

                                        fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);


                                    });

                                    fifthfeel.setOnClickListener(v -> {
                                        temperallogsave.CheckedFeeling[LD_now.getYear()-2024][LD_now.getMonthValue()][LD_now.getDayOfMonth()]=R.drawable.feeling1_verrysad1;
                                        fifthfeel.setImageResource(R.drawable.feeling1_verrysad2);
                                        secondfeel.setImageResource(R.drawable.feeling4_happy1);
                                        thirdfeel.setImageResource(R.drawable.feeling3_natural1);
                                        forthfeel.setImageResource(R.drawable.feeling2_sad1);
                                        firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);
                                    });

                                    // 버튼 클릭 리스너 설정
                                    Okbutton.setOnClickListener(v -> {
                                        // 확인 버튼 클릭 시 동작

                                        if(temperallogsave.CheckedFeeling[LD_now.getYear()-2024][LD_now.getMonthValue()][LD_now.getDayOfMonth()]==0)
                                        {
                                            Toast.makeText(getActivity().getBaseContext(), "기분을 설정해주세요",
                                                    Toast.LENGTH_SHORT ).show();
                                            return;
                                        }

                                        temperallogsave.CheckedString[LD_now.getYear()-2024][LD_now.getMonthValue()][LD_now.getDayOfMonth()]=habitlogtext.getText().toString();

                                        int idx = activity.synchronizedHabitList.indexOf(habitViewModel.Selected);

                                        if (idx != -1) {
                                            activity.synchronizedHabitList.set(idx, temperallogsave);
                                            activity.DataChangeNotfiy();

                                        }


                                        dialog.dismiss(); // 다이얼로그 닫기
                                    });

                                    cancelbutton.setOnClickListener(v -> {
                                        // 취소 버튼 클릭 시 동작
                                        dialog.dismiss(); // 다이얼로그 닫기
                                    });

                                    dialog.show(); // 다이얼로그 표시
                                });
                            }
                        }, 1000); // 1초 후 실행

                    }
                }
            }

        });
    }


    private void showNextFragment() {

        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;

        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.navigation_habitdetail, true) // 현재 Fragment를 제거
                .build();



        navController.navigate(R.id.action_HabitDetail_to_HabitLog,null,navOptions);


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;

    }


}
