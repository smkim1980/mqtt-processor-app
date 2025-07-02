# Mosquitto MQTT 브로커 Windows 설치 및 설정 가이드

이 가이드는 Windows 환경에서 Mosquitto MQTT 브로커를 설치하고 설정하는 단계별 지침을 제공합니다. 인증 설정과 구독 예제도 포함되어 있습니다.

## 1. Mosquitto 설치 (Windows)

### 설치 단계
1. Mosquitto 설치 파일을 다운로드합니다:
   - 파일: `./file/mosquitto-2.0.21a-install-windows-x64.exe`
2. 설치 파일을 실행합니다.
3. 기본 설정으로 설치를 진행합니다. 일반적으로 `C:\Program Files\mosquitto` 경로에 설치됩니다.
4. 설치 중 'Service' 관련 옵션이 나타나면, 수동 실행 및 테스트를 위해 체크를 해제하는 것이 편리할 수 있습니다.
5. **선택 사항**: `mosquitto`, `mosquitto_pub`, `mosquitto_sub` 명령어를 어느 위치에서든 사용할 수 있도록 환경 변수에 Mosquitto 경로를 추가합니다.
   - **시스템 속성 > 고급 > 환경 변수**로 이동합니다.
   - `Path` 변수에 `C:\Program Files\mosquitto`를 추가합니다.

## 2. 인증 설정

MQTT 브로커에 아무나 접속하지 못하도록 사용자 이름과 비밀번호를 설정합니다.

### 2.1. 비밀번호 파일 생성
1. 명령 프롬프트(cmd) 또는 PowerShell을 엽니다.
2. Mosquitto 설치 폴더로 이동합니다:
   ```bash
   cd "C:\Program Files\mosquitto"
   ```
3. `mosquitto_passwd.exe` 유틸리티를 사용하여 비밀번호 파일을 생성합니다:
   - 사용자: `mqtt_user`
   - 비밀번호: `mqtt_password`
   - 다음 명령어를 실행하고 `mqtt_password`를 두 번 입력합니다:
     ```bash
     mosquitto_passwd.exe -c passwordfile.txt mqtt_user
     ```
   - `passwordfile.txt` 파일이 `C:\Program Files\mosquitto` 폴더에 생성됩니다.

### 2.2. 설정 파일 (`mosquitto.conf`) 작성
1. `C:\Program Files\mosquitto` 폴더에 `mosquitto.conf`라는 이름으로 새 텍스트 파일을 만듭니다.
2. 다음 내용을 `mosquitto.conf` 파일에 복사하여 붙여넣습니다:

   ```text
   # =================================================================
   # 일반 설정
   # =================================================================
   # 익명 접속을 허용하지 않음
   allow_anonymous false

   # 비밀번호 파일의 절대 경로 지정
   password_file "C:\Program Files\mosquitto\passwordfile.txt"

   # =================================================================
   # 리스너 설정
   # =================================================================
   # 기본 MQTT 포트 설정
   listener 1883
   protocol mqtt
   ```

   **참고**: `password_file` 경로는 `passwordfile.txt` 파일의 실제 위치와 정확히 일치해야 합니다.

## 3. Mosquitto 브로커 실행
1. 명령 프롬프트를 엽니다.
2. Mosquitto 설치 폴더로 이동합니다:
   ```bash
   cd "C:\Program Files\mosquitto"
   ```
3. 작성한 `mosquitto.conf` 설정 파일을 사용하여 브로커를 실행합니다. `-v` 옵션은 자세한 로그를 출력하여 연결 상태를 확인하는 데 유용합니다:
   ```bash
   mosquitto.exe -c mosquitto.conf -v
   ```
4. 브로커가 정상적으로 실행되면 명령 프롬프트 창에서 로그를 출력하며 활성 상태를 유지합니다.

## 4. MQTT 구독 예제
브로커가 실행 중인 상태에서 특정 토픽을 구독하여 테스트할 수 있습니다.

1. 브로커가 실행 중인 상태에서 새 명령 프롬프트 창을 엽니다.
2. 다음 명령어를 실행하여 토픽을 구독합니다:
   - 호스트: `localhost`
   - 포트: `1883`
   - 사용자 이름: `mqtt_user`
   - 비밀번호: `mqtt_password`
   - 클라이언트 ID: `bms-subscriber-12345` (고유한 ID 사용)
   - 토픽: `test/topic` (예시 토픽)

   ```bash
   mosquitto_sub -h localhost -p 1883 -u "mqtt_user" -P "mqtt_password" -i "bms-subscriber-12345" -t "test/topic" -d
   ```

   **명령어 옵션**:
   - `-h`: 호스트 주소
   - `-p`: 포트 번호
   - `-u`: 사용자 이름
   - `-P`: 비밀번호
   - `-i`: 클라이언트 ID
   - `-t`: 구독할 토픽
   - `-d`: 디버그 메시지 출력

3. 다른 클라이언트에서 `test/topic`으로 메시지를 발행하면, 이 구독 클라이언트 창에 해당 메시지가 표시됩니다.