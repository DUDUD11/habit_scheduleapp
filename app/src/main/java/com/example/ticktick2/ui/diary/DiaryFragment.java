package com.example.ticktick2.ui.diary;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticktick2.MainActivity;
import com.example.ticktick2.R;
import com.example.ticktick2.databinding.FragmentDiaryBinding;
import com.example.ticktick2.dataobject.diary;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class DiaryFragment extends Fragment {

    private boolean WriteDiaryToday=false;

    private FragmentDiaryBinding binding;
    private List<diary> DiaryList;


    private String EditTimeString(String _text)
    {
       return _text.substring(0,10)+"일 "+_text.substring(11,19);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDiaryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity activity = (MainActivity) getActivity();
        DiaryList = Collections.synchronizedList(activity.synchronizedDiaryList);


        {
            //test
/*
            for (int i = 1; i < 3; i++) {
                DiaryList.add(new diary(LocalDate.now().minusDays(i), LocalDateTime.now().minusDays(i), LocalDateTime.now().minusDays(i), (i + "번째 text")));

            }

            synchronized (DiaryList) {
                Collections.sort(DiaryList);
            }
*/
        }
        if(DiaryList!=null && !DiaryList.isEmpty())
        {
            if(DiaryList.get(0).date.isEqual(LocalDate.now()))
            {
                WriteDiaryToday=true;
            }
        }

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 어댑터 설정
        DiaryRecycleAdapter adapter = new DiaryRecycleAdapter();
        recyclerView.setAdapter(adapter);

        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

    public class DiaryRecycleAdapter extends RecyclerView.Adapter<DiaryRecycleAdapter.ViewHolder> {
        private List<diary> items;

        public DiaryRecycleAdapter() {

            items=new ArrayList<>();

            {
                MainActivity activity = (MainActivity) getActivity();

                synchronized (activity.synchronizedDiaryList) {
                    for (diary entry : activity.synchronizedDiaryList) {
                        items.add(new diary(entry.date,entry.time,entry.edittime,entry.text));
                    }
                }

                if(!WriteDiaryToday) items.add(new diary(LocalDate.now(), null,null,null));
            }

            Collections.sort(items);

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.diaryitem, parent, false);
            return new ViewHolder(view);
        }



        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



            diary holding_diary = items.get(position);



            holder.weekday.setText(holding_diary.date.getDayOfWeek().toString().substring(0,3));
            holder.day.setText(holding_diary.date.getDayOfMonth()+"");
            if(items.get(position).time!=null) {
                holder.Time.setText(EditTimeString(holding_diary.time.toString()));
            }

            if(holding_diary.edittime!=null)
            {
                holder.EditTime.setText(EditTimeString(holding_diary.edittime.toString()));
            }
            
            else {
                holder.EditTime.setText("");
                holder.EditTime.setHint("마지막으로 수정한 날짜");
            }

            if(holding_diary.text!=null)
            {
                holder.diarytext.setText(holding_diary.text);
            }
            
            else
            {
                holder.diarytext.setText("");
                holder.diarytext.setHint("오늘의 일기");
            }

            holder.weekday.setOnClickListener(new View.OnClickListener() {

                    private long selecteddate;
                    @Override
                    public void onClick(View v) {

                        CalendarView calendarView = new CalendarView(getActivity());

                        long millis = holding_diary.date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
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

                                    int real_pos = holder.getAdapterPosition(); // data바꾸기전에 찾아놓는다.
                                    int idx = DiaryList.indexOf(items.get(real_pos));


                                    LocalDate localDate = LocalDate.ofInstant(
                                            java.time.Instant.ofEpochMilli(selecteddate),
                                            ZoneId.of("Asia/Seoul") // 시스템의 기본 타임존

                                    );

                                    LocalDateTime now = LocalDateTime.now();
                                    LocalDate nowdate = LocalDate.now();

                                    if(localDate.isEqual(nowdate) || localDate.isAfter(nowdate))
                                    {

                                        Toast.makeText(getActivity().getBaseContext(), "미래로의 날짜 설정은 불가능합니다.",
                                                Toast.LENGTH_SHORT ).show();
                                        return;
                                    }

                                    if(holding_diary.time==null)
                                    {
                                        holding_diary.time=now;
                                        if(idx!=-1) {
                                            diary tmp = DiaryList.get(idx);
                                            tmp.time = now;
                                        }
                                        holder.Time.setText(EditTimeString(holding_diary.time.toString()));
                                    }

                                    else
                                    {
                                        holding_diary.edittime=now;
                                        if(idx!=-1) {
                                            diary tmp = DiaryList.get(idx);
                                            tmp.time = now;
                                        }
                                        holder.EditTime.setText(EditTimeString(holding_diary.edittime.toString()));
                                    }


                                    holding_diary.date = localDate;
                                    holder.weekday.setText(holding_diary.date.getDayOfWeek().toString().substring(0,3));
                                    holder.day.setText(holding_diary.date.getDayOfMonth()+"");

                                    if(WriteDiaryToday)
                                    {
                                        holder.diarytext.setText("");

                                        {
                                            diary tmp = DiaryList.get(idx);
                                            tmp.date = localDate;
                                            synchronized (DiaryList)
                                            {
                                                Collections.sort(DiaryList);
                                            }
                                        }

                                        Collections.sort(items);
                                        notifyDataSetChanged();

                                    }

                                    else
                                    {

                                        if(idx==-1)
                                        {

                                           DiaryList.add(new diary(holding_diary));

                                            synchronized (DiaryList)
                                            {
                                                Collections.sort(DiaryList);
                                            }

                                        }
                                        else {

                                            diary tmp = DiaryList.get(idx);
                                            tmp.date = localDate;
                                            synchronized (DiaryList)
                                            {
                                                Collections.sort(DiaryList);
                                            }

                                        }

                                        Collections.sort(items);
                                        notifyDataSetChanged();

                                    }

                                    MainActivity activity = (MainActivity) getActivity();
                                    activity.DiaryChangeNotify();

                                })
                                .setNegativeButton("취소", (dialog, which) -> {
                                    // 취소 버튼 클릭 시
                                    dialog.dismiss();
                                });

                        // 다이얼로그 표시
                        builder.create().show();
                    }
                });

            holder.editbutton.setOnClickListener(b -> {
                String _text = holder.diarytext.getText().toString();
                LocalDateTime now = LocalDateTime.now();
                LocalDate nowdate = LocalDate.now();

                int real_pos = holder.getAdapterPosition();
                items.get(real_pos).text = _text;

                if(!WriteDiaryToday)
                {
                    if(real_pos==0)
                    {
                        WriteDiaryToday = true;
                        DiaryList.add(new diary(nowdate, now, null,_text));
                        holder.Time.setText(EditTimeString(now.toString()));
                    }
                    else
                    {
                        diary tmp = DiaryList.get(real_pos-1);
                        tmp.edittime = now;
                        tmp.text = _text;
                        DiaryList.set(real_pos-1,tmp);
                        holder.EditTime.setText(EditTimeString(now.toString()));
                    }
                }

                else
                {
                    diary tmp = DiaryList.get(real_pos);
                    tmp.edittime = now;
                    tmp.text = _text;
                    DiaryList.set(real_pos,tmp);
                    holder.EditTime.setText(EditTimeString(now.toString()));
                }

                MainActivity activity = (MainActivity) getActivity();

                synchronized (DiaryList) {
                    Collections.sort(DiaryList);
                }
                activity.DiaryChangeNotify();
                //    notifyItemRangeInserted(0, newItems.size()); //  notifyItemRangeInserted(0, 1); 0부터 한개만갱신

            });

            holder.delbutton.setOnClickListener(b -> {

                int real_pos = holder.getAdapterPosition();

                Log.d("delete","pos = "+real_pos);
                if(real_pos==0)
                {
                    if(WriteDiaryToday)
                    {
                        DiaryList.remove(real_pos);
                    }

                    holder.diarytext.setText("");
                    holder.EditTime.setText("");
                    holder.Time.setText("");
                    holder.diarytext.setText("오늘의 일기");
                    holder.EditTime.setHint("마지막으로 수정한 날짜");
                    holder.Time.setHint("처음수정한 날짜");
                    WriteDiaryToday=false;
                }

                else
                {
                    items.remove(real_pos);

                    if(WriteDiaryToday)
                    {
                        DiaryList.remove(real_pos);
                    }
                    else
                    {
                        DiaryList.remove(real_pos-1);

                    }

                    notifyItemRemoved(real_pos);
                }

                MainActivity activity = (MainActivity) getActivity();
                activity.DiaryChangeNotify();


            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView weekday;
            TextView day;
            TextView Time;
            TextView EditTime;
            EditText diarytext;
            Button editbutton;
            Button delbutton;

            ViewHolder(View itemView) {
                super(itemView);
                weekday = itemView.findViewById(R.id.weekday);
                day = itemView.findViewById(R.id.day);
                Time = itemView.findViewById(R.id.FirstTime);
                EditTime = itemView.findViewById(R.id.SecondTime);
                diarytext = itemView.findViewById(R.id.diarytext);
                editbutton = itemView.findViewById(R.id.editbutton);
                delbutton = itemView.findViewById(R.id.delbutton);

            }
        }
    }


}
