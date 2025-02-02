package com.example.ticktick2;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ticktick2.dataobject.Alarm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlarmBootClass {
    private static final String PREFS_NAME = "alarm_prefs";
    private static final String ALARM_LIST_KEY = "alarm_list";
    public static void saveAlarmList(Context context, List<Alarm> alarmList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(alarmList); // 리스트를 JSON으로 변환

        editor.putString(ALARM_LIST_KEY, json);  // JSON 문자열을 SharedPreferences에 저장
        editor.apply();
    }

    public static List<Alarm> loadAlarmList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(ALARM_LIST_KEY, "[]"); // 저장된 JSON 문자열을 가져옴

        Type listType = new TypeToken<ArrayList<Alarm>>() {}.getType();
        return gson.fromJson(json, listType); // JSON 문자열을 List<Alarm> 객체로 변환
    }

    public static void addAlarm(Context context, Alarm alarm) {
        List<Alarm> alarmList = loadAlarmList(context);
        alarmList.add(alarm);
        saveAlarmList(context, alarmList);
    }

    // 알람 삭제하기
    public static void removeAlarm(Context context, Alarm alarm) {
        List<Alarm> alarmList = loadAlarmList(context);
        alarmList.remove(alarm);
        saveAlarmList(context, alarmList);
    }




}
