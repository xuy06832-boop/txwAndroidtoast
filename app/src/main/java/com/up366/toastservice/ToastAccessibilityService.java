package com.up366.toastservice;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 共存无障碍服务——监听 TYPE_WINDOW_STATE_CHANGED（PopupWindow toast），
 * 写入 /sdcard/toast_events.log（JSON Lines 格式，每行一条）。
 *
 * 走系统 AccessibilityService 通道（非 instrumentation），与 u2-server /
 * UiAutomation 互不冲突，可同时运行。
 */
public class ToastAccessibilityService extends AccessibilityService {

    private static final String LOG_PATH = "/sdcard/toast_events.log";
    private static final SimpleDateFormat TS =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
    private final StringBuilder sb = new StringBuilder(256);

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        String cls = String.valueOf(event.getClassName());
        if (cls.endsWith("Activity")) {
            return; // Activity 切换不是 toast
        }
        // 提取事件文本
        sb.setLength(0);
        if (event.getText() != null) {
            for (CharSequence cs : event.getText()) {
                String s = cs.toString().trim();
                if (!s.isEmpty()) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(s.replace("\"", "'").replace("\n", " "));
                }
            }
        }
        if (sb.length() == 0) {
            return; // 无文本
        }
        // JSON Lines 写入（append 模式）
        String line = String.format(Locale.US,
                "{\"ts\":\"%s\",\"cls\":\"%s\",\"texts\":\"%s\"}%n",
                TS.format(new Date()),
                cls.replace("\"", "'"),
                sb.toString());
        try {
            File f = new File(LOG_PATH);
            OutputStreamWriter w = new OutputStreamWriter(
                    new FileOutputStream(f, true), "UTF-8");
            w.write(line);
            w.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onInterrupt() {
    }
}
