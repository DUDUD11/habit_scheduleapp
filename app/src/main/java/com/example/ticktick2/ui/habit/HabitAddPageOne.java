package com.example.ticktick2.ui.habit;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;


import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;

import com.example.ticktick2.databinding.FragmentHabitsaddpageoneBinding;
import com.example.ticktick2.dataobject.habit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HabitAddPageOne extends Fragment {

    private FragmentHabitsaddpageoneBinding binding;
    private HabitViewModel habitViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        habitViewModel =
                new ViewModelProvider(requireActivity()).get(HabitViewModel.class);

        binding = FragmentHabitsaddpageoneBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.editTextName.setText(habitViewModel.getNewHabit().Name);
        binding.editTextText.setText(habitViewModel.getNewHabit().Text);
        binding.IconImage.setImageResource(habitViewModel.getNewHabit().Icon);


        {
            // 여기들어왔으면 edithabit을 일단아닐것이라 생각한다.
            habitViewModel.editinghabit=false;
        }


        binding.editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 변경 전에 실행되는 로직 (필요 없으면 비워둡니다)
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                habit tmp_habit =habitViewModel.getNewHabit();
                tmp_habit.Name = s.toString();
                habitViewModel.setNewHabit(tmp_habit);
            }
        });


        binding.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 변경 전에 실행되는 로직 (필요 없으면 비워둡니다)
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                habit tmp_habit =habitViewModel.getNewHabit();
                tmp_habit.Text = s.toString();
                habitViewModel.setNewHabit(tmp_habit);
            }
        });



        BottomNavigationView HabitnavController = requireActivity().findViewById(R.id.nav_habitview);

        HabitnavController.setVisibility(View.GONE);

        GridView gridView = binding.habitgridicon;
        gridView.setAdapter(new HabitImageAdapter(getActivity()));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getBaseContext(), "" + position,
                        Toast.LENGTH_SHORT).show();

                habit tmp_habit =habitViewModel.getNewHabit();
                tmp_habit.pos= position;
                tmp_habit.Icon = habitViewModel.images[position];
                habitViewModel.setNewHabit(tmp_habit);
                binding.IconImage.setImageResource(tmp_habit.Icon);


            }
        });


        return root;

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        NavController navController = activity.navController;
        binding.newHabitCreatebutton.setOnClickListener(v -> {
            if(!Validate()) return;
            navController.navigate(R.id.action_habitaddPageone_to_habitaddPagetwo);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    public class HabitImageAdapter extends BaseAdapter
    {
        private Context mContext;
        public HabitImageAdapter(Context c){mContext=c;}
        public int getCount() {return habitViewModel.images.length;}
        public Object getItem(int pos){return null;}
        public long getItemId(int pos){return 0;}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if(convertView==null)
            {


                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(100,100));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(12,12,12,12);

            }
            else
            {
                    imageView = (ImageView) convertView;
            }
            imageView.setImageResource(habitViewModel.images[position]);
            return imageView;
        }

    }

    public boolean Validate()
    {
        if(habitViewModel.newHabit.getValue().Name==null )
        {

            Toast.makeText(getActivity().getBaseContext(), "이름을 설정해주세요",
                    Toast.LENGTH_SHORT ).show();
            return false;
        }

        if(habitViewModel.newHabit.getValue().Text==null )
        {

            Toast.makeText(getActivity().getBaseContext(), "인용문구를 설정해주세요",
                    Toast.LENGTH_SHORT ).show();
            return false;

        }
        

        return true;
    }


}