package com.example.ticktick2.ui.habit;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;


import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentHabitsaddBinding;
import com.example.ticktick2.dataobject.habit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HabitAddFragment extends Fragment{

    private FragmentHabitsaddBinding binding;
    private HabitViewModel habitViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitsaddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();

        Log.d("create", "habitadd");

        BottomNavigationView navController = requireActivity().findViewById(R.id.nav_view);

        navController.setVisibility(View.GONE);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {


                MainActivity activity = (MainActivity) getActivity();
                NavController navController = activity.navController;

                BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
                BottomNavigationView navHabitView = requireActivity().findViewById(R.id.nav_habitview);


                if (navView != null && navHabitView != null) {
                    navView.setVisibility(View.VISIBLE);
                    navHabitView.setVisibility((View.GONE));
                }

                // 기본 뒤로 가기 동작 수행
                setEnabled(false); // 콜백 비활성화


                navController.navigate(R.id.navigation_habits, null,
                        new NavOptions.Builder()
                                .setPopUpTo(R.id.navigation_home, false).build());

                //navController.navigate(R.id.action_HabitAddFragment_to_HabitFragment);

            }
        });

        BottomNavigationView navHabitController = requireActivity().findViewById(R.id.nav_habitview);


        navHabitController.setVisibility(View.VISIBLE);


        HabitCustomListAdapter habitCustomListAdapter = new HabitCustomListAdapter(getActivity());

        ListView list;


        list = binding.list;

        list.setAdapter(habitCustomListAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                {
                                    MainActivity activity = (MainActivity) getActivity();
                                    NavController navController = activity.navController;

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        habit newhabit = new habit(habitViewModel.titles[position]);
                                        newhabit.pos = position;
                                        newhabit.Text = habitViewModel.texts[position];
                                        newhabit.Icon = habitViewModel.images[position];

                                        habitViewModel.setNewHabit(newhabit);
                                        navController.navigate(R.id.action_HabitAddFragment_to_HabitAddPageOneFragment);



                                    }


                                }


        );




        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;

        binding = null;
        Log.d("delete", "habitadd");
    }



    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;


        binding.button.setOnClickListener(v -> {

            habit newhabit = new habit();
            newhabit.Icon = habitViewModel.images[newhabit.Icon];

            habitViewModel.setNewHabit(newhabit);

            navController.navigate(R.id.action_HabitAddFragment_to_HabitAddPageOneFragment);
        });

    }


    public class HabitCustomListAdapter extends ArrayAdapter<String> {
        private final Activity context;
        public HabitCustomListAdapter(Activity context)
        {
            super(context, R.layout.habitlistitem,habitViewModel.titles);
            this.context = context;
        }

        @Override
        public View getView(int pos, View view, ViewGroup parent)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.habitlistitem,null,true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            TextView title = (TextView) rowView.findViewById(R.id.title);
            TextView text = (TextView) rowView.findViewById(R.id.text);

            title.setText(habitViewModel.titles[pos]);
            imageView.setImageResource(habitViewModel.images[pos]);
            text.setText(habitViewModel.texts[pos]);
            return rowView;
        }


    }

}
