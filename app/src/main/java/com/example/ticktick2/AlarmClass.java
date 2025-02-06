package com.example.ticktick2;

import static android.app.PendingIntent.getActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.ticktick2.dataobject.Alarm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AlarmClass extends BroadcastReceiver {

    private static final String CHANNEL_ID = "example_channel";

    public static void showNotification(Context context, Intent intent) {

        String alarmName = intent.getStringExtra("alarmName");
        Long alarmTime = intent.getLongExtra("alarmTime",-1);
        int Icon = intent.getIntExtra("Icon",-1);

        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTimeInMillis(alarmTime);

        int hour = alarmCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = alarmCalendar.get(Calendar.MINUTE);

        // Android 8.0 이상에서는 NotificationChannel을 만들어야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            // 채널 설정
            channel.setDescription("This is an example notification channel.");

            // NotificationManager에 채널 등록
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent resultIntent = new Intent(context, MainActivity.class); // 앱의 메인 액티비티로 설정
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // 새로운 태스크로 실행

// PendingIntent로 앱 실행을 위한 Intent 설정
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // 알림 아이콘
                .setContentTitle(alarmName)  // 알림 제목
                .setContentText(hour+"시 "+minute+"분")  // 알림 내용
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // 우선순위
                .setAutoCancel(true)  // 알림을 클릭하면 자동으로 사라짐
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), Icon))
                .setContentIntent(resultPendingIntent)
                .build();

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), notification);  // 알림 ID가 1인 알림을 표시
    }

    private Alarm Intent_to_Alarm(Intent intent)
    {
        Long alarmTime = intent.getLongExtra("alarmTime",-1);
        Long lastalarm = intent.getLongExtra("lastalarm",-1);
        String alarmName = intent.getStringExtra("alarmName");
        int Frequency = intent.getIntExtra("Frequency",-1);
        int weekends = intent.getIntExtra("weekends",-1);
        int Icon = intent.getIntExtra("Icon",-1);
        return new Alarm(alarmName,alarmTime,Icon,Frequency,weekends,lastalarm);
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        long lastalarm =  intent.getLongExtra("lastalarm",-1);


        if(lastalarm != -1 && LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() > lastalarm)
        {
            return;
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            
            

            List<Alarm> alarmList = AlarmBootClass.loadAlarmList(context);
            for(Alarm a : alarmList)
            {
                Intent tmp_intent = MakeIntent(context,a.getAlarmName(),a.getAlarmTime(),a.getDrawId(),a.getFrequency(),a.getWeekends(),a.getLastalarm());

                if(alreadypass(a.getAlarmTime()))
                {
                    AlarmBootClass.removeAlarm(context,a);
                    Intent newIntent = setNextAlarm(context,tmp_intent);
                    Alarm newAlarm = Intent_to_Alarm(newIntent);
                    AlarmBootClass.addAlarm(context,newAlarm);
                }

                else
                {
                    setCurrentAlarm(context,tmp_intent);
                }

            }


        }

        else {


            
            showNotification(context, intent);
            setNextAlarm(context, intent);
        }
    }

    public static long MakeAlarmLong(Context context, LocalTime time,int AlarmDate)
    {
        LocalDate date = LocalDate.now();
        LocalDateTime dateTime = date.atTime(time);
        LocalDateTime alarmDateTime = dateTime.plusDays(AlarmDate);

       // Long x = alarmDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()-LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
       // x/=1000;


        // LocalDateTime을 밀리초로 변환
        return alarmDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static long MakeLastAlarm(Context context, LocalDate date)
    {
        LocalTime time = LocalTime.of(0,0);
        LocalDateTime dateTime = date.atTime(time);
        LocalDateTime alarmDateTime = dateTime.plusDays(1);

        // Long x = alarmDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()-LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        // x/=1000;


        // LocalDateTime을 밀리초로 변환
        return alarmDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }




    public Intent MakeIntent(Context context, String alarmName, Long alarmTime, int drawId, int Frequency,int weekends, long lastalarm)
    {


        Intent intent = new Intent(context, AlarmClass.class);
        intent.putExtra("lastalarm",lastalarm);
        intent.putExtra("alarmTime",alarmTime);
        intent.putExtra("alarmName",alarmName);
        intent.putExtra("Icon",drawId);

        if(Frequency==-1)
        {
            intent.putExtra("weekends",weekends);
        }
        else
        {
            intent.putExtra("Frequency",Frequency);
        }
        return intent;
    }

    public Intent MakeIntent(Context context, Alarm alarm)
    {

        if(alarm == null)
        {
            Toast.makeText(context, "makeintent에서 오류났으니 호출요망" ,
                    Toast.LENGTH_SHORT).show();
            return null;
            
        }
        

        Intent intent = new Intent(context, AlarmClass.class);

        intent.putExtra("alarmTime",alarm.getAlarmTime());
        intent.putExtra("alarmName",alarm.getAlarmName());
        intent.putExtra("Icon",alarm.getDrawId());
        intent.putExtra("lastalarm",alarm.getLastalarm());

        if(alarm.getFrequency()==-1)
        {
            intent.putExtra("weekends",alarm.getWeekends());
        }
        else
        {
            intent.putExtra("Frequency",alarm.getFrequency());
        }
        return intent;
    }


    public void cancelAlarmAndRemoveAlarm(Context context, Intent GetIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, GetIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Long alarmTime = GetIntent.getLongExtra("alarmTime",-1);
        Long lastalarm = GetIntent.getLongExtra("lastalarm",-1);
        String alarmName = GetIntent.getStringExtra("alarmName");
        int Frequency = GetIntent.getIntExtra("Frequency",-1);
        int weekends = GetIntent.getIntExtra("weekends",-1);
        int Icon = GetIntent.getIntExtra("Icon",-1);

        Alarm removeAlarm = new Alarm(alarmName,alarmTime,Icon,Frequency,weekends,lastalarm);
        AlarmBootClass.removeAlarm(context,removeAlarm);

        // 알람 취소
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent); // 알람 취소


        }

    }

    public void startAlarmAndAddAlarm(Context context, Intent getIntent)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Long alarmTime = getIntent.getLongExtra("alarmTime",-1);
        Long lastalarm = getIntent.getLongExtra("lastalarm",-1);
        String alarmName = getIntent.getStringExtra("alarmName");
        int Frequency = getIntent.getIntExtra("Frequency",-1);
        int weekends = getIntent.getIntExtra("weekends",-1);
        int Icon = getIntent.getIntExtra("Icon",-1);

        Alarm newalarm = new Alarm(alarmName,alarmTime,Icon,Frequency,weekends,lastalarm);
        AlarmBootClass.addAlarm(context,newalarm);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, getIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if(pendingIntent==null)
        {
            Toast.makeText(context, "생성이 잘안되네요" ,
                    Toast.LENGTH_SHORT).show();
            
        }
        
        if (alarmManager != null) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                );

            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                );
            }
        }
    }


    private boolean alreadypass (long alarmtime)
    {
        return System.currentTimeMillis()>alarmtime;
    }

    public void setCurrentAlarm(Context context, Intent getIntent)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, getIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Long alarmTime = getIntent.getLongExtra("alarmTime",-1);
        // 다음 알람 설정
        if (alarmManager != null) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                );

            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                );
            }
        }
    }

    public Intent setNextAlarm(Context context, Intent getIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmClass.class);

        String alarmName = getIntent.getStringExtra("alarmName");
        Long alarmTime = getIntent.getLongExtra("alarmTime",-1);
        Long lastalarm = getIntent.getLongExtra("lastalarm",-1);
        int Frequency = getIntent.getIntExtra("Frequency",-1);
        int weekends = getIntent.getIntExtra("weekends",-1);
        int Icon = getIntent.getIntExtra("Icon",-1);



        if(alarmTime==-1)
        {
            Toast.makeText(context, "에러가 났으니 호출요망" ,
                    Toast.LENGTH_SHORT).show();
            return null;
        }

        if(Frequency!=-1)
        {


            LocalDateTime now = LocalDateTime.now();

            while(now.toInstant(ZoneOffset.UTC).toEpochMilli()>alarmTime)
            {
                alarmTime+= (long)(86400*1000*Frequency);
                now.plusDays(Frequency);
            }

            intent.putExtra("Frequency",Frequency);
        }

        else
        {
            int today = LocalDate.now().getDayOfWeek().getValue()-1;
            LocalDateTime now = LocalDateTime.now();
            boolean[] dates = new boolean[7];
            int tmp = weekends;
            int next=-1;

            for(int i=0;i<7;i++)
            {


                if(tmp%10==0)
                {
                    dates[6-i]=false;
                }

                else
                {
                    dates[6-i]=true;
                }

                tmp/=10;
                if(tmp==0) break;
            }





            for(int i=today+1;i<today+8;i++ )
            {
                i = i%7;
                if(dates[i])
                {
                    next=i;
                    break;
                }
            }
            
            if(next==-1)
            {
                Toast.makeText(context, "next날짜의 오류가 났네요" ,
                        Toast.LENGTH_SHORT).show();
                return null;
            }

            next = (next-today+7)%7;

            alarmTime+= (long)(86400*1000*next);



            //while(now.toInstant(ZoneOffset.UTC).toEpochMilli()>=alarmTime+86400*1000*7)
            //{
            //    alarmTime+=alarmTime+86400*1000*7;
            //}


            intent.putExtra("weekends",weekends);
        }

        intent.putExtra("alarmTime",alarmTime);
        intent.putExtra("alarmName",alarmName);
        intent.putExtra("Icon",Icon);
        intent.putExtra("lastalarm",lastalarm);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 다음 알람 설정
        if (alarmManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                );

            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                );
            }
        }

        return intent;
    }
}
