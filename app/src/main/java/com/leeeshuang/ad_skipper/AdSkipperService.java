package com.leeeshuang.ad_skipper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.leeeshuang.ad_skipper.service.DatabaseService;
import com.leeeshuang.ad_skipper.utils.ToastUtil;

public class AdSkipperService extends AccessibilityService {
    private static final String CLS_BTN_NAME = "android.widget.Button";
    private static final String CLS_TEXT_NAME = "android.widget.TextView";

    // 屏蔽间隔 5s
    private static final int DURATION = 5000;
    private long lastSkipAt = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.showToast(this, "服务已创建！");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToastUtil.showToast(this, "服务已摧毁！");
    }

    // 服务已连接
    @Override
    protected void onServiceConnected() {
        ToastUtil.showToast(this, "首屏广告跳过服务已开启！");

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

        if (!DatabaseService.blackPkgNames.contains(currentPkgName)) {
            return;
        }

        if (System.currentTimeMillis() < this.lastSkipAt + DURATION) {
            return;
        }

//        System.out.println("currentPkgName: " + currentPkgName);
//        System.out.println("lastLaunchedPkgName: " + this.lastLaunchedPkgName);

        AccessibilityNodeInfo adNode = findDisgustingAdNode(rootNode);

        if (adNode != null) {
            adNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            this.lastSkipAt = System.currentTimeMillis();
            ToastUtil.showToast(this, "已跳过广告!");
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

    private AccessibilityNodeInfo findDisgustingAdNode(AccessibilityNodeInfo node) {
        String text = String.valueOf(node.getText());

        if (!TextUtils.isEmpty(text) && (CLS_BTN_NAME.contentEquals(node.getClassName()) || CLS_TEXT_NAME.contentEquals(node.getClassName()))) {
            for (String s : DatabaseService.skipKeyWords.split("/")) {
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

    @Override
    public void onInterrupt() {
        ToastUtil.showToast(this, "首屏广告跳过服务已中断！");
    }
}