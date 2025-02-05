package com.example.ticktick2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {

    private Context context;
    private Activity activity;

    private List<Object> PermissionsList;

    private String[] permissions = {
            "android.permission.RECEIVE_BOOT_COMPLETED",
   "android.permission.WAKE_LOCK",
   "android.permission.USE_EXACT_ALARM",
    "com.android.alarm.permission.SET_ALARM",
    "android.permission.POST_NOTIFICATIONS"
    };

    public PermissionUtil(Activity _act,Context _con)
    {
        activity = _act;
        context = _con;
        PermissionsList = new ArrayList<>();

    }

    public boolean checkPermission()
    {
        int res;
        for(String pm : permissions)
        {
            res = ContextCompat.checkSelfPermission(context,pm);
            if(res!= PackageManager.PERMISSION_GRANTED)
            {
                PermissionsList.add(pm);
            }
        }
        return PermissionsList.isEmpty();
    }

    public void requestPermission()
    {
        ActivityCompat.requestPermissions(activity,PermissionsList.toArray(new String[PermissionsList.size()]),1023);
    }


}
