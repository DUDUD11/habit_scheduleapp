package com.example.ticktick2.ui.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitstatisticsBinding;
import com.example.ticktick2.dataobject.habit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class HabitStatisticsFragment extends Fragment {

    FragmentHabitstatisticsBinding binding;

    private int dpToPx(int dp) {
        return Math.round(getResources().getDisplayMetrics().density * dp);
    }


    private LocalDate MonthStart(LocalDate date)
    {
        return LocalDate.of(date.getYear(),date.getMonthValue(),1);
    }

    private LocalDate MonthEnd(LocalDate date)
    {
        LocalDate NextMonthStart = LocalDate.of(date.getYear(),date.getMonthValue(),1).plusMonths(1);
        return NextMonthStart.minusDays(1);
    }

    private void UpdateGridLayout()
    {
        GridLayout gridLayout = binding.gridlayout;
        binding.daysedittext.setText(m_MonthStart.getYear()+"년 "+m_MonthStart.getMonthValue()+" 월");
        gridLayout.removeAllViews();

        // 동적으로 커스텀 레이아웃 추가
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        for (habit item:habitFilterList) {
            // 커스텀 레이아웃 inflate
            View customView = layoutInflater.inflate(R.layout.monthlystatistics,gridLayout, false);
            ImageView icon = customView.findViewById(R.id.image);
            icon.setImageResource(item.Icon);
            TextView textView = customView.findViewById(R.id.titletext);
            textView.setText(item.Name);
            GridLayout gridLayout1 = customView.findViewById(R.id.monthlygridlayout);

            long daysBetween = ChronoUnit.DAYS.between(m_MonthStart,m_MOnthEnd)+1;

            for(int i=1;i<=daysBetween;i++) {

                // 새 TextView 추가
                EditText newEditText = new EditText(getContext());

                newEditText.setText(i+"");
                newEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                if(item.CheckedDate[m_MonthStart.getYear()-2024][m_MonthStart.getMonthValue()][i]) {
                    newEditText.setBackgroundColor(getResources().getColor(R.color.blue_text));
                }

                else
                {
                    newEditText.setBackgroundColor(getResources().getColor(R.color.white));
                }

                newEditText.setTextSize(16);


                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0; // 너비를 0dp로 설정
                params.height = dpToPx(40); // 높이를 30dp로 설정
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // layout_columnWeight="1" 동작
                newEditText.setLayoutParams(params);

                gridLayout1.addView(newEditText);
            }

            gridLayout.addView(customView);
        }



    }


    private void UpdateHabitList()
    {
        MainActivity activity = (MainActivity) getActivity();
        habitFilterList.clear();

        synchronized (activity.synchronizedHabitList) {

            for(habit item:activity.synchronizedHabitList)
            {
                if(!item.StartDate.isAfter(m_MOnthEnd))
                {
                    if(item.EndDate!=null && m_MonthStart.isAfter(item.EndDate))
                    {
                        continue;
                    }
                    habitFilterList.add(item);
                }
            }

        }

    }

    private List<habit> habitFilterList;

    private LocalDate m_MonthStart;
    private LocalDate m_MOnthEnd;

    private HabitViewModel habitViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitstatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();
        {
            habitFilterList = new ArrayList<>();

            m_MonthStart = MonthStart(LocalDate.now());
            m_MOnthEnd = MonthEnd(LocalDate.now());

            UpdateHabitList();
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
            UpdateGridLayout();

            binding.button1.setOnClickListener(v -> {
                m_MonthStart = m_MonthStart.minusMonths(1);
                m_MOnthEnd = m_MOnthEnd.plusDays(1).minusMonths(1).minusDays(1);

                UpdateHabitList();
                UpdateGridLayout();
            });

            binding.button2.setOnClickListener(v -> {
                m_MonthStart = m_MonthStart.plusMonths(1);
                m_MOnthEnd = m_MOnthEnd.plusDays(1).plusMonths(1).minusDays(1);

                UpdateHabitList();
                UpdateGridLayout();
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

}
