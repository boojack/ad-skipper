package com.leeeshuang.ad_skipper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
            DatabaseService.blackPkgNames = DatabaseService.blackPkgNames + blackPkgNames;
            // 保存键值对
            @SuppressLint("CommitPrefEdits")
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(blackPkgNameKey, DatabaseService.blackPkgNames);
            editor.apply();
            ToastUtil.showToast(this, "新增成功!");

            TextView blackPkgNamesTv = findViewById(R.id.blackPkgNameList);
            blackPkgNamesTv.setText(String.join("\n", DatabaseService.blackPkgNames.split(" ")));
        }
    }

    private void initData() {
        preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String blackPkgNames = preferences.getString(blackPkgNameKey, getResources().getString(R.string.local_black_pkg_list));
        String skipKeyWords = preferences.getString(skipKeyWordKey, getResources().getString(R.string.skip_key_word));
        DatabaseService.blackPkgNames = blackPkgNames;
        DatabaseService.skipKeyWords = skipKeyWords;

        TextView blackPkgNamesTv = findViewById(R.id.blackPkgNameList);
        EditText skipKeyWordsEt = findViewById(R.id.skipKeyWordsInput);
        blackPkgNamesTv.setText(String.join("\n", blackPkgNames.split(" ")));
        skipKeyWordsEt.setText(skipKeyWords);
    }
}
