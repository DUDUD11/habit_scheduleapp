package com.example.ticktick2.ui.habit;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitsadd5Binding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HabitAddFragment5 extends  Fragment{

    private FragmentHabitsadd5Binding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HabitViewModel habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitsadd5Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textView;
      //  habitViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        Log.d("create", "habitadd5");
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // BottomNavigationView 다시 활성화
                BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
                BottomNavigationView navHabitView = requireActivity().findViewById(R.id.nav_habitview);

                MainActivity activity = (MainActivity) getActivity();
                NavController navController = activity.navController;

                if (navView != null && navHabitView != null) {
                    navView.setVisibility(View.VISIBLE);
                    navHabitView.setVisibility((View.GONE));
                }

                // 기본 뒤로 가기 동작 수행
                setEnabled(false); // 콜백 비활성화
                if(!navController.popBackStack()) {
                    requireActivity().onBackPressed();
                }
                else
                {
                    //navController.navigate(R.id.action_HabitAddFragment2_to_HabitFragment);
                    navController.navigateUp();
                }


            }
        });




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        Log.d("delete", "habitadd5");
    }
}
