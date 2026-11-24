package com.subtrack.mobile;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.Map;

/** 기기 안에서만 앱별 사용 시간을 읽어 웹 화면에 넘겨주는 플러그인 (외부 전송 없음) */
@CapacitorPlugin(name = "UsageStats")
public class UsageStatsPlugin extends Plugin {

    private boolean isGranted() {
        Context ctx = getContext();
        AppOpsManager ops = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
        if (ops == null) return false;
        int mode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mode = ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.getPackageName());
        } else {
            mode = ops.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @PluginMethod
    public void checkPermission(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("granted", isGranted());
        call.resolve(ret);
    }

    @PluginMethod
    public void openSettings(PluginCall call) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
        call.resolve();
    }

    @PluginMethod
    public void getUsage(PluginCall call) {
        if (!isGranted()) {
            call.reject("NO_PERMISSION");
            return;
        }
        Integer daysArg = call.getInt("days", 30);
        int days = daysArg == null ? 30 : daysArg;
        long end = System.currentTimeMillis();
        long start = end - days * 86400000L;

        UsageStatsManager usm = (UsageStatsManager) getContext().getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager pm = getContext().getPackageManager();
        JSArray apps = new JSArray();
        if (usm != null) {
            Map<String, UsageStats> stats = usm.queryAndAggregateUsageStats(start, end);
            for (UsageStats s : stats.values()) {
                long ms = s.getTotalTimeInForeground();
                if (ms < 60000L) continue;
                String pkg = s.getPackageName();
                if (pkg.equals(getContext().getPackageName())) continue;
                String label = pkg;
                try {
                    ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                    label = pm.getApplicationLabel(ai).toString();
                } catch (Exception ignored) { }
                JSObject o = new JSObject();
                o.put("pkg", pkg);
                o.put("label", label);
                o.put("minutes", ms / 60000L);
                o.put("lastUsed", s.getLastTimeUsed());
                apps.put(o);
            }
        }
        JSObject ret = new JSObject();
        ret.put("apps", apps);
        ret.put("days", days);
        call.resolve(ret);
    }
}
