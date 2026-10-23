package com.up366.toastservice;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * 共存无障碍服务——监听 TYPE_WINDOW_STATE_CHANGED（PopupWindow toast）。
 *
 * 输出双通道：
 * 1. Logcat（tag=TXW_TOAST）：零权限，adb logcat -s TXW_TOAST:I 读取
 * 2. App 私有目录文件：getExternalFilesDir()（无需权限）
 *
 * 走系统 AccessibilityService 通道（非 instrumentation），与 u2-server /
 * UiAutomation 互不冲突，可同时运行。
 */
public class ToastAccessibilityService extends AccessibilityService {

    private static final String TAG = "TXW_TOAST";
    private final StringBuilder sb = new StringBuilder(256);

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        String cls = String.valueOf(event.getClassName());
        if (cls.endsWith("Activity")) {
            return;
        }
        sb.setLength(0);
        if (event.getText() != null) {
            for (CharSequence cs : event.getText()) {
                String s = cs.toString().trim();
                if (!s.isEmpty()) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(s.replace("|", "/").replace("\n", " "));
                }
            }
        }
        if (sb.length() == 0) {
            return;
        }
        // 通道 1：logcat（主通道——零权限、最可靠）
        Log.i(TAG, cls.replace("|", "/") + "|" + sb);
    }

    @Override
    public void onInterrupt() {
    }
}
