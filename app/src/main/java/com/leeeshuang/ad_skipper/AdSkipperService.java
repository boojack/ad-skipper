package com.leeeshuang.ad_skipper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.leeeshuang.ad_skipper.utils.ToastUtil;

public class AdSkipperService extends AccessibilityService {
    private static final String CLS_BTN_NAME = "android.widget.Button";
    private static final String CLS_TEXT_NAME = "android.widget.TextView";
    private String blackPkgList = "";
    private String[] skipKeyWordList;
    private String lastLaunchedPkgName = "";

    @Override
    public void onCreate() {
        super.onCreate();

        // 数据存储相关
//        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.local_white_pkg_list), Context.MODE_PRIVATE);
//        String defaultWhitePkgList = getResources().getString(R.string.local_white_pkg_list_default_value);
//        String whitePkgList = sharedPref.getString("local_white_pkg_list", defaultWhitePkgList);
//
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("local_white_pkg_list", whitePkgList);
//        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 服务已连接
    @Override
    protected void onServiceConnected() {
        ToastUtil.showToast(this, "首屏广告跳过服务已开启！");

        this.blackPkgList = getResources().getString(R.string.local_black_pkg_list);
        String skipKeyWord = getResources().getString(R.string.skip_key_word);
        this.skipKeyWordList = skipKeyWord.split(" ");

        // 数据存储相关
        // SharedPreferences sharedPref = getSharedPreferences("local_white_pkg_list", Context.MODE_PRIVATE);
        // String defaultBlackPkgList = getResources().getString(R.string.local_black_pkg_list);

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.packageNames = null;
        info.feedbackType = AccessibilityServiceInfo.DEFAULT;
        info.notificationTimeout = 100;

        this.setServiceInfo(info);
    }

    // 接收事件
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();

        if (rootNode == null) {
            return;
        }

        String currentPkgName = String.valueOf(rootNode.getPackageName());

//        if (currentPkgName.equals(this.lastLaunchedPkgName)) {
//            return;
//        } else {
//            do nth
//        }

        if (!this.blackPkgList.contains(currentPkgName)) {
            return;
        }

//        System.out.println("currentPkgName: " + currentPkgName);
//        System.out.println("lastLaunchedPkgName: " + this.lastLaunchedPkgName);

        AccessibilityNodeInfo adNode = findDisgustingAdNode(rootNode);

        if (adNode != null) {
            this.lastLaunchedPkgName = currentPkgName;
            adNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

//        final int eventType = event.getEventType();
//
//        if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
//
//        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            System.out.println("TYPE_WINDOW_STATE_CHANGED " + currentPkgName + "   " + this.lastLaunchedPkgName);
//
//            this.lastLaunchedPkgName = currentPkgName;
//        }
    }

    // 递归查找包含跳过字样的Button/TextView
    private AccessibilityNodeInfo findDisgustingAdNode(AccessibilityNodeInfo node) {
        String text = String.valueOf(node.getText());

        if (!TextUtils.isEmpty(text) && (CLS_BTN_NAME.contentEquals(node.getClassName()) || CLS_TEXT_NAME.contentEquals(node.getClassName()))) {
            for (String s : this.skipKeyWordList) {
                if (text.contains(s)) {
                    if (node.isClickable()) {
                        return node;
                    } else {
                        AccessibilityNodeInfo parent = node.getParent();
                        if (parent.isClickable()) {
                            return parent;
                        }
                    }
                }
            }
        }

        final int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo subNode = node.getChild(i);

            if (subNode != null) {
                AccessibilityNodeInfo currentNode = findDisgustingAdNode(subNode);
                if (currentNode != null) {
                    return currentNode;
                }
            }
        }

        return null;
    }

    // 服务中断
    @Override
    public void onInterrupt() {
        ToastUtil.showToast(this, "首屏广告跳过服务已关闭！");
    }
}