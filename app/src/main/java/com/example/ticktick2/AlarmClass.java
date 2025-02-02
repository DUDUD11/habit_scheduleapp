package com.example.ticktick2;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Activity;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
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


    @Override
    public void onReceive(Context context, Intent intent) {



        
        showNotification(context, intent);

        setNextAlarm(context,intent);

    }

    public static long MakeAlarmLong(Context context, LocalTime time,int AlarmDate)
    {



        LocalDate date = LocalDate.now();

        // LocalDate와 LocalTime을 결합하여 LocalDateTime을 생성
        LocalDateTime dateTime = date.atTime(time);

        // AlarmDate만큼 날짜를 앞당기기 (예: 3일을 더 추가)
        LocalDateTime alarmDateTime = dateTime.plusDays(AlarmDate);

        Long x = alarmDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()-LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        x/=1000;

      //  Toast.makeText(context, x+"초" , Toast.LENGTH_SHORT).show();

        // LocalDateTime을 밀리초로 변환
        return alarmDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }



    public Intent MakeIntent(Context context, String alarmName, Long alarmTime, int drawId, int Frequency,int weekends)
    {


        Intent intent = new Intent(context, AlarmClass.class);

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

    public void cancelAlarm(Context context, Intent GetIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, GetIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 알람 취소
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent); // 알람 취소
        }

        else
        {
            Toast.makeText(context, "삭제할 알람이 없습니다. 업데이트 이후 첫수정 혹은 알람이 이미 울렸을때만 발생합니다." ,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void startAlarm(Context context, Intent getIntent)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Long alarmTime = getIntent.getLongExtra("alarmTime",-1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, getIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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

    public void setNextAlarm(Context context, Intent getIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmClass.class);

        String alarmName = getIntent.getStringExtra("alarmName");
        Long alarmTime = getIntent.getLongExtra("alarmTime",-1);
        int Frequency = getIntent.getIntExtra("Frequency",-1);
        int weekends = getIntent.getIntExtra("weekends",-1);
        int Icon = getIntent.getIntExtra("Icon",-1);



        if(alarmTime==-1)
        {
            Toast.makeText(context, "에러가 났으니 호출요망" ,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(Frequency!=-1)
        {
            alarmTime+= (long)(86400*1000*Frequency);
            intent.putExtra("Frequency",Frequency);
        }

        else
        {
            int today = LocalDate.now().getDayOfWeek().getValue()-1;
            boolean[] dates = new boolean[7];
            int tmp = weekends;
            int next=0;

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
            
            if(next==0)
            {
                Toast.makeText(context, "next날짜의 오류가 났네요" ,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            next = (next-today+7)%7;

            alarmTime+= (long)(86400*1000*next);
            intent.putExtra("weekends",weekends);
        }

        intent.putExtra("alarmTime",alarmTime);
        intent.putExtra("alarmName",alarmName);
        intent.putExtra("Icon",Icon);

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
    }
}
