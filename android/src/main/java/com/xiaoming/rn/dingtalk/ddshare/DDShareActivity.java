package com.xiaoming.rn.dingtalk.ddshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.xiaoming.rn.dingtalk.RNDingTalkModule;

public class DDShareActivity extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("xm.dingtalk" ,"onCreate==========>");
        try {
            RNDingTalkModule.handleIntent(getIntent());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("xm.dingtalk" , "e===========>"+e.toString());
        }
        finish();
    }

}
