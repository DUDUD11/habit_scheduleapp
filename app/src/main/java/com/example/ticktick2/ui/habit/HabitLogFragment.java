package com.example.ticktick2.ui.habit;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitlogBinding;

import com.example.ticktick2.dataobject.habit;
import com.example.ticktick2.dataobject.habitstatistics;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HabitLogFragment extends Fragment {


    private Map<CalendarDay, Drawable> dayColorMap;

    private FragmentHabitlogBinding binding;
    private HabitViewModel habitViewModel;

    private  HabitLogListAdapter habitLogListAdapter=null;

    private habit temperalHabit;
    private ArrayList<LocalDate> temperalHabitLog;

    private int scrollY = 0;

    private float startY = 0f;
    private ListView listView;

    private int idx=-1;


    public habitstatistics validatestatistics(int year, int month)
    {

        habitstatistics tmp = new habitstatistics();

        tmp.wholecheckin=temperalHabit.AchieveDay;

        int consecutive=0;
        int monthcheckinday=0;

        if(temperalHabit.Frequency==0)
        {

            for(LocalDate indexdate = LocalDate.now();indexdate.isAfter(temperalHabit.StartDate) || indexdate.isEqual(temperalHabit.StartDate);indexdate = indexdate.minusDays(1))
            {

                int dayofweek = (indexdate.getDayOfWeek().getValue()+6)%7;

                if(temperalHabit.CheckedDate[indexdate.getYear()-2024][indexdate.getMonthValue()][indexdate.getDayOfMonth()])
                {
                    consecutive++;
                }

                else if(!temperalHabit.frequencyDay[dayofweek])
                {
                    continue;
                }
                else break;
            }
            tmp.consecutive=consecutive;
        }

        else
        {
            for(LocalDate indexdate = LocalDate.now();indexdate.isAfter(temperalHabit.StartDate) || indexdate.isEqual(temperalHabit.StartDate);indexdate = indexdate.minusDays(1))
            {
                long daysBetween = ChronoUnit.DAYS.between(indexdate, temperalHabit.StartDate);

                if(temperalHabit.CheckedDate[indexdate.getYear()-2024][indexdate.getMonthValue()][indexdate.getDayOfMonth()])
                {
                    consecutive++;
                }

                else if(daysBetween%temperalHabit.Frequency!=0)
                {
                    continue;
                }

                else break;
            }
            tmp.consecutive=consecutive;
        }


        LocalDate lastday = LocalDate.of(year,month,1).plusMonths(1).minusDays(1);
        LocalDate start = LocalDate.of(year,month,1);

        if(temperalHabit.StartDate.isAfter(lastday))
        {
            return tmp;
        }

        for(LocalDate it=start;!it.isEqual(lastday);it=it.plusDays(1)) {
            if (temperalHabit.CheckedDate[it.getYear() - 2024][it.getMonthValue()][it.getDayOfMonth()])
            {
                monthcheckinday++;
            }
        }

        tmp.monthlycheckin=monthcheckinday;

        float quotient = 100*monthcheckinday;
        float remainder = 0;


        lastday.plusDays(1);


        if(temperalHabit.Frequency==0) {

            for (LocalDate indexdate = start; indexdate.isBefore(lastday) ; indexdate = indexdate.plusDays(1)) {


                if (indexdate.isBefore(temperalHabit.StartDate)) {
                    continue;
                }

                int dayofweek = (indexdate.getDayOfWeek().getValue() + 6) % 7;

                if (temperalHabit.frequencyDay[dayofweek]) {
                    remainder++;
                }
            }
        }


        else
        {
            for (LocalDate indexdate = start; indexdate.isBefore(lastday); indexdate = indexdate.plusDays(1)) {


                long daysBetween = ChronoUnit.DAYS.between(indexdate, temperalHabit.StartDate);

                if (indexdate.isBefore(temperalHabit.StartDate)) {
                    continue;
                }
                if (daysBetween%temperalHabit.Frequency==0) {
                    remainder++;
                }
            }
        }

        tmp.monthlyratio=Math.round(quotient/remainder);


        return tmp;

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


        for (int i = 0; i < listsize; i++) {
            View listItem = listAdapter.getView(i, null, listView);

            // MeasureSpec을 사용하여 더 정확하게 측정
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            totalHeight += listItem.getMeasuredHeight();
        }


        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listsize));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    private void SetDayColor(int year, int month)
    {
//EventDecorator decorator = new EventDecorator(dateColorMap);
//calendarView.addDecorator(decorator);

        LocalDate start = LocalDate.of(year,month,1);
        LocalDate lastday = LocalDate.of(year,month,1).plusMonths(1).minusDays(1);
        if(start.isBefore(temperalHabit.StartDate)) {
            start = temperalHabit.StartDate;
        }

        dayColorMap.clear();

        if(temperalHabit.Frequency==0) {
            for (LocalDate day = start; !day.isAfter(lastday); day = day.plusDays(1)) {

                if(day.isAfter(LocalDate.now())) break;

                int dayofweek = (day.getDayOfWeek().getValue()+6)%7;

                if (temperalHabit.CheckedDate[year - 2024][month][day.getDayOfMonth()]) {
                    dayColorMap.put(CalendarDay.from(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth()), ContextCompat.getDrawable(getContext(), R.drawable.blue_squre));

                }

                else if(temperalHabit.frequencyDay[dayofweek]) {

                    if(temperalHabit.CheckedFeeling[year-2024][month][day.getDayOfMonth()]==0)
                    {
                        dayColorMap.put(CalendarDay.from(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth()), ContextCompat.getDrawable(getContext(), R.drawable.light_gray_square));
                    }

                    else
                    {
                        dayColorMap.put(CalendarDay.from(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth()), ContextCompat.getDrawable(getContext(),R.drawable.light_blue_square));
                    }
                }

            }
        }

        else
        {
            for (LocalDate day = start; !day.isAfter(lastday); day = day.plusDays(1)) {

                if(day.isAfter(LocalDate.now())) break;

                long daysBetween = ChronoUnit.DAYS.between(day, temperalHabit.StartDate);

                if (temperalHabit.CheckedDate[year - 2024][month][day.getDayOfMonth()]) {
                    dayColorMap.put(CalendarDay.from(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth()), ContextCompat.getDrawable(getContext(),R.drawable.blue_squre));

                }

                else if(daysBetween % temperalHabit.Frequency == 0) {

                    if(temperalHabit.CheckedFeeling[year-2024][month][day.getDayOfMonth()]==0)
                    {
                        dayColorMap.put(CalendarDay.from(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth()), ContextCompat.getDrawable(getContext(),R.drawable.light_gray_square));
                    }

                    else
                    {
                        dayColorMap.put(CalendarDay.from(day.getYear(), day.getMonthValue() - 1, day.getDayOfMonth()), ContextCompat.getDrawable(getContext(),R.drawable.light_blue_square));
                    }
                }

            }


        }
    }


    private void SetTempralHabitLog(int year,int month)
    {

        temperalHabitLog.clear();

        for(int i=1;i<32;i++)
        {
            if(habitViewModel.getSelectedHabit().CheckedString[year-2024][month][i] == null || habitViewModel.getSelectedHabit().CheckedString[year-2024][month][i].isEmpty() )
            {
                continue;
            }
            LocalDate a = str_to_localdate((year)+"-"+month+"-"+i);
            temperalHabitLog.add(a);
        }


    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;

        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitlogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final float originalX = root.getX();
        final float originalY = root.getY();

        {
            binding.monthtextview.setText(LocalDate.now().getMonth().getValue()+"월 습관 로그");

        }
        

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                {
                    BottomNavigationView controller = requireActivity().findViewById(R.id.nav_view);
                    controller.setVisibility(View.VISIBLE);
                }

                // 기본 뒤로 가기 동작 수행
                setEnabled(false); // 콜백 비활성화

                requireActivity().onBackPressed();
            }
        });
        {
            temperalHabit = habitViewModel.getSelectedHabit();
            idx = activity.synchronizedHabitList.indexOf(temperalHabit);
            temperalHabitLog = new ArrayList<>();
            habitLogListAdapter = new HabitLogListAdapter(getActivity());
            dayColorMap = new HashMap<>();

            listView = binding.loglist;
            listView.setAdapter(habitLogListAdapter);
            SetTempralHabitLog(LocalDate.now().getYear(),LocalDate.now().getMonthValue());

            listView.post(new Runnable() {
                @Override
                public void run() {
                    setListViewHeightBasedOnChildren(binding.loglist,temperalHabitLog.size());
                }
            });

            habitstatistics habitstatistics = validatestatistics(LocalDate.now().getYear(),LocalDate.now().getMonthValue());

            binding.consecutivetext.setText(habitstatistics.consecutive+"");
            binding.monthlypercentagetext.setText(String.format("%.0f", habitstatistics.monthlyratio)+"%");
            binding.monthlytext.setText(habitstatistics.monthlycheckin+"");
            binding.wholechecktext.setText(habitstatistics.wholecheckin+"");
            SetDayColor(LocalDate.now().getYear(),LocalDate.now().getMonthValue());
            dayColorMap.forEach((key, value) ->
            {
                EventDecorator decorator = new EventDecorator(key,value);
                binding.calendarView.addDecorators(decorator);

            });
        }

        {
            binding.moreseetextview.setOnClickListener(
                    v->{
                        navController.navigate(R.id.action_Habitlog_to_habitstatisticsOne);
                    }
            );

        }



        {
            binding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                    if (selected) {

                        if(date.isAfter(CalendarDay.today()))
                        {
                            return;
                        }
                        Dialog dialog = new Dialog(requireActivity());
                        dialog.setContentView(R.layout.habitdialog2);
                        TextView datetitle = dialog.findViewById(R.id.date_title);
                        ImageView firstfeel = dialog.findViewById(R.id.firstfeel);
                        ImageView secondfeel = dialog.findViewById(R.id.secondfeel);
                        ImageView thirdfeel = dialog.findViewById(R.id.thirdfeel);
                        ImageView forthfeel = dialog.findViewById(R.id.forthfeel);
                        ImageView fifthfeel = dialog.findViewById(R.id.fifthfeel);
                        RadioGroup radioGroup = dialog.findViewById(R.id.radiogroup);
                        RadioButton sucRadioButton = dialog.findViewById(R.id.radioButton);
                        RadioButton failRadioButton = dialog.findViewById(R.id.radioButton2);

                        EditText habitlogtext = dialog.findViewById(R.id.habitlogtext);
                        Button Okbutton = dialog.findViewById(R.id.ok_button);
                        Button cancelbutton = dialog.findViewById(R.id.cancel_button);

                        datetitle.setText(date.getYear()+"년 "+(date.getMonth()+1)+"월 "+date.getDay()+"일 ");






                        firstfeel.setOnClickListener(v -> {

                            firstfeel.setImageResource(R.drawable.feeling5_verryhappy2);

                            temperalHabit.CheckedFeeling[date.getYear()-2024][date.getMonth()+1][date.getDay()]=R.drawable.feeling5_verryhappy1;

                            secondfeel.setImageResource(R.drawable.feeling4_happy1);
                            thirdfeel.setImageResource(R.drawable.feeling3_natural1);
                            forthfeel.setImageResource(R.drawable.feeling2_sad1);
                            fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);

                        });
                        secondfeel.setOnClickListener(v -> {
                            secondfeel.setImageResource(R.drawable.feeling4_happy2);
                            temperalHabit.CheckedFeeling[date.getYear()-2024][date.getMonth()+1][date.getDay()]=R.drawable.feeling4_happy1;

                            firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);

                            thirdfeel.setImageResource(R.drawable.feeling3_natural1);
                            forthfeel.setImageResource(R.drawable.feeling2_sad1);
                            fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);


                        });

                        thirdfeel.setOnClickListener(v -> {
                            thirdfeel.setImageResource(R.drawable.feeling3_natural2);
                            temperalHabit.CheckedFeeling[date.getYear()-2024][date.getMonth()+1][date.getDay()]=R.drawable.feeling3_natural1;

                            firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);
                            secondfeel.setImageResource(R.drawable.feeling4_happy1);

                            forthfeel.setImageResource(R.drawable.feeling2_sad1);
                            fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);

                        });

                        forthfeel.setOnClickListener(v -> {
                            forthfeel.setImageResource(R.drawable.feeling2_sad2);
                            temperalHabit.CheckedFeeling[date.getYear()-2024][date.getMonth()+1][date.getDay()]=R.drawable.feeling2_sad1;

                            firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);
                            secondfeel.setImageResource(R.drawable.feeling4_happy1);
                            thirdfeel.setImageResource(R.drawable.feeling3_natural1);

                            fifthfeel.setImageResource(R.drawable.feeling1_verrysad1);

                        });

                        fifthfeel.setOnClickListener(v -> {
                            fifthfeel.setImageResource(R.drawable.feeling1_verrysad2);
                            temperalHabit.CheckedFeeling[date.getYear()-2024][date.getMonth()+1][date.getDay()]=R.drawable.feeling1_verrysad1;


                            secondfeel.setImageResource(R.drawable.feeling4_happy1);
                            thirdfeel.setImageResource(R.drawable.feeling3_natural1);
                            forthfeel.setImageResource(R.drawable.feeling2_sad1);
                            firstfeel.setImageResource(R.drawable.feeling5_verryhappy1);


                        });


                        // 버튼 클릭 리스너 설정
                        Okbutton.setOnClickListener(v -> {
                            // 확인 버튼 클릭 시 동작

                            if(temperalHabit.CheckedFeeling[date.getYear()-2024][date.getMonth()+1][date.getDay()]==0)
                            {
                                Toast.makeText(getActivity().getBaseContext(), "기분을 설정해주세요",
                                        Toast.LENGTH_SHORT ).show();
                                return;
                            }

                            temperalHabit.CheckedString[date.getYear()-2024][date.getMonth()+1][date.getDay()]=habitlogtext.getText().toString();


                            activity.synchronizedHabitList.set(idx, temperalHabit);
                            activity.DataChangeNotfiy();

                            SetTempralHabitLog(date.getYear(),date.getMonth()+1);

                            setListViewHeightBasedOnChildren(binding.loglist,temperalHabitLog.size());



                            habitstatistics habitstatistics = validatestatistics(LocalDate.now().getYear(),LocalDate.now().getMonthValue());

                            binding.consecutivetext.setText(habitstatistics.consecutive+"");
                            binding.monthlypercentagetext.setText(String.format("%.0f", habitstatistics.monthlyratio)+"%");
                            binding.monthlytext.setText(habitstatistics.monthlycheckin+"");
                            binding.wholechecktext.setText(habitstatistics.wholecheckin+"");


                            SetDayColor(LocalDate.now().getYear(),LocalDate.now().getMonthValue());
                            dayColorMap.forEach((key, value) ->
                            {
                                EventDecorator decorator = new EventDecorator(key,value);
                                binding.calendarView.addDecorators(decorator);

                            });


                            habitLogListAdapter.notifyDataSetChanged();


                            dialog.dismiss(); // 다이얼로그 닫기
                        });

                        cancelbutton.setOnClickListener(v -> {
                            // 취소 버튼 클릭 시 동작
                            temperalHabit = habitViewModel.getSelectedHabit();

                            dialog.dismiss(); // 다이얼로그 닫기
                        });

                        dialog.show(); // 다이얼로그 표시


                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {

                                RadioButton selectedRadioButton = group.findViewById(checkedId);

                                if(selectedRadioButton.equals(sucRadioButton))
                                {

                                    if(!temperalHabit.CheckedDate[date.getYear()-2024][date.getMonth()+1][date.getDay()])
                                    {
                                        temperalHabit.AchieveDay++;
                                    }

                                    temperalHabit.CheckedDate[date.getYear()-2024][date.getMonth()+1][date.getDay()]=true;
                                }

                                else if(selectedRadioButton.equals(failRadioButton))
                                {

                                    if(temperalHabit.CheckedDate[date.getYear()-2024][date.getMonth()+1][date.getDay()])
                                    {
                                        temperalHabit.AchieveDay--;
                                    }

                                    temperalHabit.CheckedDate[date.getYear()-2024][date.getMonth()+1][date.getDay()]=false;
                                }

                                else
                                {
                                    Log.e("habitlogfragment radiobutton error","no button equal");
                                }

                            }
                        });
                    }


                }

            });

        }

        {
            binding.calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
                @Override
                public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                    SetTempralHabitLog(date.getYear(), date.getMonth() + 1);
                    setListViewHeightBasedOnChildren(binding.loglist,temperalHabitLog.size());

                    SetDayColor(date.getYear(), date.getMonth() + 1);
                    dayColorMap.forEach((key, value) ->
                    {
                        EventDecorator decorator = new EventDecorator(key,value);
                        binding.calendarView.addDecorators(decorator);

                    });

                    habitstatistics habitstatistics = validatestatistics(LocalDate.now().getYear(),LocalDate.now().getMonthValue());
                    binding.consecutivetext.setText(habitstatistics.consecutive+"");
                    binding.monthlypercentagetext.setText(String.format("%.0f", habitstatistics.monthlyratio)+"%");
                    binding.monthlytext.setText(habitstatistics.monthlycheckin+"");
                    binding.wholechecktext.setText(habitstatistics.wholecheckin+"");
                    habitLogListAdapter.notifyDataSetChanged();

                    binding.monthtextview.setText((date.getMonth()+1)+"월 습관 로그");
                }
            });

        }

/*
        {


            root.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    Log.d("drag","abc");

                    switch (event.getAction()) {

                        case DragEvent.ACTION_DRAG_STARTED:
                            // 드래그 시작 시점의 y 좌표 저장
                            startY = v.getY();
                            scrollY = binding.scrollView.getScrollY();

                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            float endY = event.getY();
                            float changedY = startY - endY;
                            Log.d("drag",changedY+"");
                            if (changedY < -100 && scrollY == 0) {
                                showPreviousFragment();
                            }
                            break;
                    }
                    return true;
                }
            });
        }
*/


        return root;
    }

    private void showPreviousFragment() {

        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;

        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.navigation_habitlog, true) // 현재 Fragment를 제거
                .build();

        navController.navigate(R.id.action_HabitLog_to_HabitDetail,null,navOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class HabitLogListAdapter extends ArrayAdapter<LocalDate> {
        private final Activity context;

        public HabitLogListAdapter(Activity context) {
            super(context, R.layout.haibtlogitem,temperalHabitLog);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.haibtlogitem, null, true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            ImageView imagemark = (ImageView) rowView.findViewById(R.id.imagemark);
            TextView text = (TextView) rowView.findViewById(R.id.text);

            LocalDate date = temperalHabitLog.get(pos);

            title.setText(date.getMonthValue()+" 월"+date.getDayOfMonth()+" 일 "+getKoreanDayName(date.toString()));




            if(!temperalHabit.CheckedDate[date.getYear()-2024][date.getMonthValue()][date.getDayOfMonth()])
            {
                imageView.setImageResource(R.drawable.round_cancel_24);

            }
            else
            {
                imageView.setImageResource(R.drawable.ic_check);
            }

            imagemark.setImageResource(temperalHabit.CheckedFeeling[date.getYear()-2024][date.getMonthValue()][date.getDayOfMonth()]);



            text.setText(temperalHabit.CheckedString[date.getYear()-2024][date.getMonthValue()][date.getDayOfMonth()]);

            return rowView;
        }


    }

    public LocalDate str_to_localdate(String str)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate localDate = LocalDate.parse(str, formatter);

        return localDate;

    }

    public String getKoreanDayName(String stringdate)
    {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate localDate = str_to_localdate(stringdate);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        switch (dayOfWeek) {
            case MONDAY: return "월요일";
            case TUESDAY: return "화요일";
            case WEDNESDAY: return "수요일";
            case THURSDAY: return "목요일";
            case FRIDAY: return "금요일";
            case SATURDAY: return "토요일";
            case SUNDAY: return "일요일";
            default: return "";
        }

    }

}