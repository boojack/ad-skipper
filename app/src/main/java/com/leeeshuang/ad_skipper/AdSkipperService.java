package com.leeeshuang.ad_skipper;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.leeeshuang.ad_skipper.service.DatabaseService;
import com.leeeshuang.ad_skipper.utils.ToastUtil;

import java.util.Objects;

public class AdSkipperService extends AccessibilityService {
    private static final String CLS_BTN_NAME = "android.widget.Button";
    private static final String CLS_TEXT_NAME = "android.widget.TextView";

    private boolean isRunning = true;
    private String currentPackageName = "";
    private String currentActivityName = "";

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.showToast(this, "服务已创建！");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning = false;
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

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!this.isRunning) {
            return;
        }

        if (event.getPackageName() == null || event.getClassName() == null) {
            return;
        }

        final int eventType = event.getEventType();
        String pkgName = event.getPackageName().toString();
        String className = event.getClassName().toString();

        if (!DatabaseService.blackPkgNames.contains(pkgName)) {
            return;
        }

        if (!Objects.equals(DatabaseService.blackPkgNameMap.get(pkgName), "")) {
            if (!Objects.equals(DatabaseService.blackPkgNameMap.get(pkgName), className)) {
                return;
            }
        }

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            boolean isActivity = !className.startsWith("android.widget.") && !className.startsWith("android.view.");

            if (isActivity) {
                if (currentPackageName.equals(pkgName)) {
                    if (!currentActivityName.equals(className)) {
                        currentActivityName = className;
                    }
                } else {
                    currentPackageName = pkgName;
                    currentActivityName = className;

                    skipAd();
                }
            }
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (!pkgName.equals(currentPackageName)) {
                return;
            }

            skipAd();
        }
    }

    private void skipAd() {
        AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();

        if (rootNode == null) {
            return;
        }

        AccessibilityNodeInfo adNode = findDisgustingAdNode(rootNode);

        if (adNode != null) {
            adNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            ToastUtil.showToast(this, "已跳过广告!");
            DatabaseService.blackPkgNameMap.replace(currentPackageName, currentActivityName);
            DatabaseService.updateBlackPkgNames();
        }
    }

    private AccessibilityNodeInfo findDisgustingAdNode(AccessibilityNodeInfo node) {
        CharSequence rawText = node.getText();
        CharSequence rawDesc = node.getText();

        if ((!TextUtils.isEmpty(rawText) || !TextUtils.isEmpty(rawDesc)) && (CLS_BTN_NAME.contentEquals(node.getClassName()) || CLS_TEXT_NAME.contentEquals(node.getClassName()))) {
            String text = "";
            if (rawText != null) {
                text = rawText.toString();
            }

            String desc = "";
            if (rawDesc != null) {
                desc = rawDesc.toString();
            }

            for (String s : DatabaseService.skipKeyWords.split("/")) {
                if ((text.contains(s) && text.length() < 8) || (desc.contains(s) && desc.length() < 8)) {
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