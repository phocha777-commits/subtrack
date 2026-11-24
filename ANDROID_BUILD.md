# SUBTRACK 안드로이드 앱(APK) 만들기

## 방법 A. 내 PC 없이: GitHub에서 자동 빌드 (추천)
1. github.com 가입 → 오른쪽 위 `+` → **New repository** → 이름 `subtrack` → Create.
2. "uploading an existing file" 링크를 눌러 이 폴더 안의 내용을 **전부** 끌어다 놓고 Commit.
   - `.github` 폴더가 숨김이라 안 보이면, 탐색기에서 "숨김 항목 표시"를 켜고 같이 올리세요.
3. 위쪽 **Actions** 탭 → `Build Android APK`가 자동 실행(약 5~10분). 안 돌면 **Run workflow** 클릭.
4. 초록 체크가 뜨면 그 실행을 눌러 맨 아래 **Artifacts → subtrack-apk** 다운로드 → 압축 풀면 `app-debug.apk`.
5. 폰으로 APK를 옮겨 실행 → "출처를 알 수 없는 앱 설치 허용" → 설치.

## 방법 B. 내 PC에서 (Node.js LTS + Android Studio 설치 필요)
```
npm install
npm run android:add
npm run android:icons
npx cap sync android
npx cap open android
```
Android Studio에서 Build → Build APK(s). 결과: `android/app/build/outputs/apk/debug/app-debug.apk`

## 앱 수정 후
`www/index.html`만 고치면 됩니다. GitHub는 다시 올리면 자동 재빌드, PC는 `npx cap sync android` 후 다시 빌드.

## 처음 실행 후 확인할 것
1. **가성비** 탭 → "사용 정보 접근 허용하러 가기" → 목록에서 SUBTRACK 켜기 → 뒤로 가기.
2. 구독 이름이 넷플릭스·티빙·유튜브 등이면 앱이 자동으로 연결됩니다. 안 되면 항목을 눌러 직접 고르세요.

## 원스토어 출시 준비물 (다음 단계)
- 서명된 release APK: debug APK는 테스트용이라 스토어에 못 올립니다. 서명 키를 만들어 빌드에 연결하는 단계가 필요합니다.
- 개인정보처리방침 URL: `www/privacy.html`의 이메일 칸을 채운 뒤 인터넷에 올린 주소가 필요합니다(GitHub Pages로 무료 게시 가능).
- 앱 아이콘(512×512), 스크린샷, 앱 설명.

## 아이폰 / 설치 없이 쓰기
`www` 폴더를 웹에 올리고 폰에서 열어 "홈 화면에 추가"하면 앱처럼 쓸 수 있습니다(PWA).
