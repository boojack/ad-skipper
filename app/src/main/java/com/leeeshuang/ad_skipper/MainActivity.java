package com.leeeshuang.ad_skipper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.leeeshuang.ad_skipper.service.DatabaseService;
import com.leeeshuang.ad_skipper.utils.ToastUtil;

public class MainActivity extends AppCompatActivity {
    public static final String blackPkgNameKey = "local_black_pkg_names";
    public static final String skipKeyWordKey = "skip_key_words";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    public void handleSaveBtnClick(View view) {
        EditText blackPkgNamesEt = findViewById(R.id.blackPkgNames);
        EditText skipKeyWordsEt = findViewById(R.id.skipKeyWords);
        System.out.println("blackPkgNamesEt " + blackPkgNamesEt);

        String blackPkgNames = blackPkgNamesEt.getText().toString();
        String skipKeyWords = skipKeyWordsEt.getText().toString();
        DatabaseService.blackPkgNames = blackPkgNames;
        DatabaseService.skipKeyWords = skipKeyWords;

        // 保存键值对
        @SuppressLint("CommitPrefEdits")
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putString(blackPkgNameKey, blackPkgNames);
        editor.putString(skipKeyWordKey, skipKeyWords);
        editor.apply();
        ToastUtil.showToast(this, "修改成功!");
    }

    private void initData() {
        this.preferences = getSharedPreferences("setting", Context.MODE_PRIVATE);
        String blackPkgNames = preferences.getString(blackPkgNameKey, getResources().getString(R.string.local_black_pkg_list));
        String skipKeyWords = preferences.getString(skipKeyWordKey, getResources().getString(R.string.skip_key_word));
        DatabaseService.blackPkgNames = blackPkgNames;
        DatabaseService.skipKeyWords = skipKeyWords;

        EditText blackPkgNamesEt = findViewById(R.id.blackPkgNames);
        EditText skipKeyWordsEt = findViewById(R.id.skipKeyWords);
        blackPkgNamesEt.setText(blackPkgNames);
        skipKeyWordsEt.setText(skipKeyWords);
    }
}
