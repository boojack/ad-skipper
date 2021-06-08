package com.leeeshuang.ad_skipper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.leeeshuang.ad_skipper.service.DatabaseService;
import com.leeeshuang.ad_skipper.utils.ToastUtil;

public class MainActivity extends AppCompatActivity {
    public static final String blackPkgNameKey = "local_black_pkg_names";
    public static final String skipKeyWordKey = "skip_key_words";
    public static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initListener();
    }

    public void handleSaveBtnClick(View view) {
        EditText blackPkgNamesEt = findViewById(R.id.blackPkgNames);
        EditText skipKeyWordsEt = findViewById(R.id.skipKeyWords);
        System.out.println("blackPkgNamesEt " + blackPkgNamesEt);

        String blackPkgNames = blackPkgNamesEt.getText().toString();
        String skipKeyWords = skipKeyWordsEt.getText().toString();
        DatabaseService.setBlackPkgNames(blackPkgNames);
        DatabaseService.skipKeyWords = skipKeyWords;

        // 保存键值对
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(blackPkgNameKey, blackPkgNames);
        editor.putString(skipKeyWordKey, skipKeyWords);
        editor.apply();
        ToastUtil.showToast(this, "修改成功!");
    }

    public void handleSyncUpdateBtnClick(View view) {
        DatabaseService.updateBlackPkgNames();
        EditText blackPkgNamesEt = findViewById(R.id.blackPkgNames);
        String blackPkgNames = DatabaseService.blackPkgNames;
        blackPkgNamesEt.setText(blackPkgNames);
    }

    private void initData() {
        preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String blackPkgNames = preferences.getString(blackPkgNameKey, getResources().getString(R.string.local_black_pkg_list));
        String skipKeyWords = preferences.getString(skipKeyWordKey, getResources().getString(R.string.skip_key_word));
        DatabaseService.setBlackPkgNames(blackPkgNames);
        DatabaseService.skipKeyWords = skipKeyWords;

        EditText blackPkgNamesEt = findViewById(R.id.blackPkgNames);
        EditText skipKeyWordsEt = findViewById(R.id.skipKeyWords);
        blackPkgNamesEt.setText(blackPkgNames);
        skipKeyWordsEt.setText(skipKeyWords);
    }

    private void initListener() {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch onOffSwitch = (Switch) findViewById(R.id.serviceStatus);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(MainActivity.this, AdSkipperService.class);

                if (isChecked) {
                    startService(intent);
                } else {
                    stopService(intent);
                }
            }
        });
    }
}
