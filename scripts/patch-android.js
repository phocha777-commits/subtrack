// `npx cap add android` 다음에 실행: 사용시간 플러그인을 안드로이드 프로젝트에 넣는다.
const fs = require('fs');
const path = require('path');
const root = path.join(__dirname, '..');
const javaDir = path.join(root, 'android/app/src/main/java/com/subtrack/mobile');
const manifestPath = path.join(root, 'android/app/src/main/AndroidManifest.xml');

fs.copyFileSync(path.join(root, 'native/UsageStatsPlugin.java'), path.join(javaDir, 'UsageStatsPlugin.java'));

fs.writeFileSync(path.join(javaDir, 'MainActivity.java'), `package com.subtrack.mobile;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(UsageStatsPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
`);

let m = fs.readFileSync(manifestPath, 'utf8');
if (!m.includes('PACKAGE_USAGE_STATS')) {
  m = m.replace('<manifest xmlns:android="http://schemas.android.com/apk/res/android"',
    '<manifest xmlns:android="http://schemas.android.com/apk/res/android"\n    xmlns:tools="http://schemas.android.com/tools"');
  m = m.replace('<uses-permission android:name="android.permission.INTERNET" />',
`<uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions" />

    <!-- 사용 기록이 있는 앱의 이름을 보여주기 위해 런처 앱만 조회 -->
    <queries>
        <intent>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent>
    </queries>`);
  fs.writeFileSync(manifestPath, m);
}
console.log('android patched: UsageStats plugin added');
