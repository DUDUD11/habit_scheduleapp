package com.example.ticktick2.ui.habit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ticktick2.MainActivity;

import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitsaddBinding;
import com.example.ticktick2.databinding.FragmentHabitstatisticsoneBinding;
import com.example.ticktick2.dataobject.habit;

import java.time.DateTimeException;
import java.time.LocalDate;

public class HabitStatisticsOneFragment extends Fragment {
    private int dpToPx(int dp) {
        return Math.round(getResources().getDisplayMetrics().density * dp);
    }

    private HabitViewModel habitViewModel;

    private habit SelectedHabit;
    private int m_Year;

    FragmentHabitstatisticsoneBinding binding;

    private void UpdateGridLayout(int year)
    {




        binding.dayseditonetext.setText(year+"년");
        GridLayout gridLayout = binding.gridlayout;

        gridLayout.removeAllViews();

        // 동적으로 커스텀 레이아웃 추가
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        {
            // 커스텀 레이아웃 inflate


            for(int i=1;i<=12;i++)
            {
                View customView = layoutInflater.inflate(R.layout.monthlyonestatistics,gridLayout, false);
                //ImageView icon = (R.id.imagestaticone);

                GridLayout gridLayout1 = customView.findViewById(R.id.monthlyonegridlayout);
                TextView textView1 = customView.findViewById(R.id.monthtextviewone);



                for(int j=1;j<=31;j++)
                {
                    try {
                        LocalDate.of(year,i,j);

                    } catch (DateTimeException e) {
                       continue;
                    }

                    EditText newEditText = new EditText(getContext());
                    newEditText.setText(j+"");
                    newEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    if(SelectedHabit.CheckedDate[year-2024][i][j])
                    {
                        newEditText.setBackgroundColor(getResources().getColor(R.color.blue_text));
                    }

                    else
                    {
                        newEditText.setBackgroundColor(getResources().getColor(R.color.white));
                    }

                    newEditText.setTextSize(16);
                    textView1.setText(i+"월");
                    textView1.setTextSize(30);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 30; // 너비를 0dp로 설정
                params.height = dpToPx(40); // 높이를 30dp로 설정
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // layout_columnWeight="1" 동작
                    params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
                newEditText.setLayoutParams(params);



                    gridLayout1.addView(newEditText);
                }

                gridLayout.addView(customView);

            }


        }

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitstatisticsoneBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();
        {
            SelectedHabit = habitViewModel.getSelectedHabit();

            m_Year = LocalDate.now().getYear();
            
            binding.imagestaticone.setImageResource(SelectedHabit.Icon);
            binding.titletextstaticone.setText(SelectedHabit.Name);
            //binding.dayseditonetext.setText(m_Year+"년");
            UpdateGridLayout(m_Year);
        }

        {

            binding.buttonone1.setOnClickListener(v -> {

                if(m_Year<=2024)
                {
                    Toast.makeText(getActivity().getBaseContext(), "2024년부터 구현되었습니다",
                            Toast.LENGTH_SHORT ).show();
                    return;
                }

                UpdateGridLayout(--m_Year);
                UpdateGridLayout(m_Year);
            });

            binding.buttonone2.setOnClickListener(v -> {

                if(m_Year>=2033)
                {
                    Toast.makeText(getActivity().getBaseContext(), "2033년까지 구현되었습니다",
                            Toast.LENGTH_SHORT ).show();
                    return;
                }

                UpdateGridLayout(++m_Year);
                UpdateGridLayout(m_Year);
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

    }



}



