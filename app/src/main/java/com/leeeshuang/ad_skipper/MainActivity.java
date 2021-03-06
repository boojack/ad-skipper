package com.leeeshuang.ad_skipper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.leeeshuang.ad_skipper.service.DatabaseService;
import com.leeeshuang.ad_skipper.utils.ToastUtil;

public class MainActivity extends AppCompatActivity {
    public static final String blackPkgNameKey = "local_black_pkg_names";
    public static final String skipKeyWordKey = "skip_key_words";
    public static final String showSkipTipKey = "show_skip_tip";
    public static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        initData();
        initListeners();
        DatabaseService.create(this);
    }


    public void handleSkipKeyWordSaveBtnClick(View view) {
        EditText skipKeyWordsEt = findViewById(R.id.skipKeyWordsInput);
        String skipKeyWords = skipKeyWordsEt.getText().toString();

        DatabaseService.skipKeyWords = skipKeyWords;

        // 保存键值对
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(skipKeyWordKey, skipKeyWords);
        editor.apply();
        ToastUtil.showToast(this, "修改成功!");
    }

    public void handleInsertBlackPkgBtnClick(View view) {
        EditText blackPkgNamesEt = findViewById(R.id.blackPkgNameInput);
        String blackPkgNames = blackPkgNamesEt.getText().toString();

        if (!TextUtils.isEmpty(blackPkgNames) && !DatabaseService.blackPkgNames.contains(blackPkgNames)) {
            DatabaseService.blackPkgNames = DatabaseService.blackPkgNames + " " + blackPkgNames.trim();
            // 保存键值对
            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(blackPkgNameKey, DatabaseService.blackPkgNames);
            editor.apply();
            ToastUtil.showToast(this, "新增成功!");

            TextView blackPkgNamesTv = findViewById(R.id.blackPkgNameList);
            blackPkgNamesTv.setText(String.join("\n", DatabaseService.blackPkgNames.split(" ")));
        }
        blackPkgNamesEt.setText("");
    }

    public void handleResetBtnClick(View view) {
        String blackPkgNames = getResources().getString(R.string.local_black_pkg_list);
        String skipKeyWords = getResources().getString(R.string.skip_key_word);
        DatabaseService.blackPkgNames = blackPkgNames;
        DatabaseService.skipKeyWords = skipKeyWords;
        DatabaseService.showSkipTip = true;

        // 保存键值对
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(blackPkgNameKey, DatabaseService.blackPkgNames);
        editor.putString(skipKeyWordKey, DatabaseService.skipKeyWords);
        editor.apply();
        ToastUtil.showToast(this, "重置成功!");

        TextView blackPkgNamesTv = findViewById(R.id.blackPkgNameList);
        EditText skipKeyWordsEt = findViewById(R.id.skipKeyWordsInput);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch showSkipTipSwitch = findViewById(R.id.showSkipTipSwitch);
        blackPkgNamesTv.setText(String.join("\n", blackPkgNames.split(" ")));
        skipKeyWordsEt.setText(skipKeyWords);
        showSkipTipSwitch.setChecked(true);
    }

    private void initData() {
        String blackPkgNames = preferences.getString(blackPkgNameKey, getResources().getString(R.string.local_black_pkg_list));
        String skipKeyWords = preferences.getString(skipKeyWordKey, getResources().getString(R.string.skip_key_word));
        boolean showSkipTip = preferences.getBoolean(showSkipTipKey, true);
        DatabaseService.blackPkgNames = blackPkgNames;
        DatabaseService.skipKeyWords = skipKeyWords;

        TextView blackPkgNamesTv = findViewById(R.id.blackPkgNameList);
        EditText skipKeyWordsEt = findViewById(R.id.skipKeyWordsInput);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch showSkipTipSwitch = findViewById(R.id.showSkipTipSwitch);
        blackPkgNamesTv.setText(String.join("\n", blackPkgNames.split(" ")));
        skipKeyWordsEt.setText(skipKeyWords);
        showSkipTipSwitch.setChecked(showSkipTip);
    }

    private void initListeners() {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch showSkipTipSwitch = findViewById(R.id.showSkipTipSwitch);

        showSkipTipSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DatabaseService.showSkipTip = isChecked;
            // 保存键值对
            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(showSkipTipKey, DatabaseService.showSkipTip);
            editor.apply();
        });
    }
}
