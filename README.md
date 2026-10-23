# Toast Monitor Service

共存无障碍服务——监听 PopupWindow 弹窗事件（TYPE_WINDOW_STATE_CHANGED），
写入 `/sdcard/toast_events.log`（JSON Lines）。

与 u2-server / UiAutomation **互不冲突**（走系统 AccessibilityService
通道，非 instrumentation），可同时运行。

## 安装

1. 下载 APK 并安装：`adb install toast-service-debug.apk`
2. 启用无障碍服务：设置 → 更多设置 → 无障碍 → 已下载的应用 → Toast Monitor → 开启

## 使用

安装并启用后，每次 PopupWindow 弹窗自动追加一行 JSON：

```json
{"ts":"2026-10-23T14:30:00.123","cls":"com.up366.mobile.widget.BaseDialog","texts":"确定要退出登录吗？,取消,确定,close"}
```

自动化工具读取：

```bash
adb shell cat /sdcard/toast_events.log | tail -5
```

清空：

```bash
adb shell rm /sdcard/toast_events.log
```
