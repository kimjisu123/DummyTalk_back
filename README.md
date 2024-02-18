#  <img height="90" src="https://noticon-static.tammolo.com/dgggcrkxq/image/upload/v1673344675/noticon/ftoiwdw09co3cunifudf.gif"> 메신저 플랫폼 DUMMYTALK

<img src="https://github.com/haeunyy/readme_view/assets/120347036/3dd0776f-5c1d-4645-99ab-0db89ce20563">

## 1. 개발 기간 및 작업 관리

- 개발 기간 : 2023. 12. 01 ~ 2024. 01. 17

- 작업 관리  <a href="https://determined-shop-231.notion.site/Rainbow-503364cda4c44e7e8fbff27c4fa18519"><img src="https://img.shields.io/badge/Notion-ffffff?style=badge&logo=notion&logoColor=black"/></a>
    - 기획, 스프린트, 회의록, API 설계, WBS, ERD 등의 작업을 관리합니다.

<br>

## 2. 프로젝트 소개
- 웹에서 채팅을 통해 생성되는 다양한 멀티미디어 자료를 AI를 활용하여 효과적으로 관리할 수 있는 메신저 플랫폼입니다.
- 다국어 사용자들을 위한 실시간 번역 서비스를 제공합니다.
- 업로드 된 이미지와 텍스트 채팅을 인공지능 모델을 통하여 벡터화 해서 검색이 가능하도록 합니다.

<br>

## 3. 팀원 구성

<div align="center">

|                                                **최영욱**                                                 |                                                 **김희승**                                                 |                                                **권하은**                                                 | 
|:------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------:|
| ![image](https://github.com/haeunyy/readme_view/assets/120347036/63c59e3e-74ca-4f55-8af9-3460e6b09e1b) | ![image](https://github.com/haeunyy/readme_view/assets/120347036/7f7ff2ad-3ed5-4faf-80b6-d1485e328823)  | ![image](https://github.com/haeunyy/readme_view/assets/120347036/38d8369b-6267-4c5f-90cf-cc0b8a2caac9) |

|                                              **김예지**                                               |                                              **하승권**                                               |                                                       **김지수**                                                       | 
|:--------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------:| 
| ![i](https://github.com/haeunyy/readme_view/assets/120347036/9250078f-7cc1-406e-91cb-a7a8e96a221d) | ![d](https://github.com/haeunyy/readme_view/assets/120347036/1bda2ba2-e920-4487-a9ee-0a357872c86c) | <img src="https://github.com/haeunyy/readme_view/assets/120347036/44fcba74-6732-4d27-ac36-9eb9af453981" width=270 > | 


</div>

<br>



## 4. 개발 환경

| 분야       | 사용 기술                              |
|----------|------------------------------------|
| FrontEnd | React.js, Redux                    |
| BackEnd  | SpringBoot,JPA, FastAPI, SQLAlchemy |
| Database | Mysql, Milvus                      |
| DevOps   | AWS EC2, S3, AWS KMS, Docker       |
| TOOL     | VSCODE, InteliJ, Notion, Slack     |
| DESIGN   | Figma                              |




## 5. 채택한 개발 기술과 아키텍쳐

### React, AES-256, WebSocket, Live-kit

- React
    - 컴포넌트화를 통해 추후 유지보수와 재사용성을 고려했습니다.
    - 유저 배너, 상단과 하단 배너 등 중복되어 사용되는 부분이 많아 컴포넌트화를 통해 리소스 절약이 가능했습니다.
- AES-256
    - JWT 로그인 방식와 AES 대칭키 암호화를 결합하여 보안적으로 더욱 향상된 로그인 기능을 제공합니다.
    - 암호화를 통해 Secret Key를 DB에 저장을 할수 있게 되면서 보완성과 함께 Secret Key 관리에 대한 부담감 또한 줄어들었습니다.
    - AWS KMS 기능을 함께 사용하여 주기적인 대칭키 로테이션, 권한에 따른 키 관리로 인하여 대칭키를 수월하게 관리하였습니다.
- WebSocket
    - 실시간 채팅, 실시간 음성 채팅, 친구 기능과 같이 다양한 기능들을 실시간으로 제공을 하기 위해 채택하였습니다.
    - Http 요청의 단방향 통신과 달리 양방향 통신을 지원하기에 보다 낮은 지연 속도와 오버헤드 감소로 인한 <br>데이터 전송 비용을 줄일 수 있는 장점이 있습니다.
- Live-kit
    - 실시간 의사소통을 하기 위한 다양한 도구를 제공하기에 선택한 기술입니다.
    - 비슷한 기능인 WebRTC로 먼저 기능을 구현화 하였으나 Silero VAD Model와 함께 사용하는 과정에 문제가 생겨 <br> Live-kit으로 대체하게 되었습니다.
    - Live-kit과 음성을 감지하는 Silero VAD Model를 함께 사용하여 실시간 음성 채팅 기능을 구현하였습니다.

<br>

### 아키텍쳐

![아키텍쳐](https://github.com/haeunyy/readme_view/assets/120347036/9b36e578-0be7-4b5c-9c08-cb9540b59e4b)

## 6. 프로젝트 구조

###  Front
```
.
├── README.md
├── components.json
├── config-overrides.js
├── jsconfig.json
├── package-lock.json
├── package.json
├── package.json.bak
├── public
│   ├── index.html
├── src
│   ├── App.css
│   ├── App.js
│   ├── api
│   ├── components
│   │   ├── AudioRecorder
│   │   ├── channel
│   │   ├── chat
│   │   ├── hooks
│   │   ├── modals
│   │   ├── providers
│   │   └── ui
│   ├── hooks
│   ├── index.css
│   ├── index.js
│   ├── layouts
│   │   ├── IndexLayout
│   │   ├── LeftSide
│   │   ├── MainLayout
│   │   ├── RightSide
│   │   └── chat
│   ├── lib
│   │   ├── tokenUtils.js
│   │   └── utils.js
│   ├── modules
│   ├── page
│   └── store.js
└── tailwind.config.js
```

### Backend
```
└── DummyTalk_back
    ├── README.md
    ├── build.gradle
    ├── erd.mwb
    ├── gradle
    │   └── wrapper
    │       └── gradle-wrapper.properties
    ├── gradlew
    ├── gradlew.bat
    ├── http
    │   ├── Test.http
    │   └── chat.http
    ├── settings.gradle
    └── src
        ├── main
        │   ├── java
        │   │   └── com
        │   │       └── example
        │   │           └── DummyTalk
        │   │               ├── AES
        │   │               ├── Aws
        │   │               ├── Chat
        │   │               │   ├── Channel
        │   │               │   │   ├── Controller
        │   │               │   │   ├── Dto
        │   │               │   │   ├── Entity
        │   │               │   │   ├── Enum
        │   │               │   │   ├── Repository
        │   │               │   │   └── Service
        │   │               │   └── Server
        │   │               ├── Common
        │   │               │   ├── DTO
        │   │               │   ├── Entity
        │   │               │   └── WebSocket
        │   │               ├── Config
        │   │               │   ├── BeanConfiguration.java
        │   │               │   ├── JpaConfig.java
        │   │               │   ├── JwtSecurityConfig.java
        │   │               │   ├── SecurityConfig.java
        │   │               │   └── WebMvcConfig.java
        │   │               ├── DummyTalkApplication.java
        │   │               ├── Exception
        │   │               ├── Jwt
        │   │               ├── Mail
        │   │               │   ├── Controller
        │   │               │   └── Service
        │   │               ├── User
        │   │               │   ├── Controller
        │   │               │   ├── DTO
        │   │               │   ├── Entity
        │   │               │   ├── Repository
        │   │               │   └── Service
        │   │               └── Util
        │   └── resources
        │       ├── application-server.properties
        │       ├── application.properties
        │       └── static
        └── test
```

<br>

## 7. 기능 소개

<br>

| 주요 기능 시연 영상                                                                                                                                                |
|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <a href="https://www.youtube.com/watch?v=vbuXrG3HOkU"><img src="https://img.shields.io/badge/모델 시연 영상-ffffff?style=badge&logo=youtube&logoColor=red"/></a> |

<br>

| 필수기능                                                                                                   |
|--------------------------------------------------------------------------------------------------------|
| ![image](https://github.com/haeunyy/readme_view/assets/120347036/6ad41052-94e0-4ceb-9d22-07df1f8933f1) |

<br>

- 채팅 기능
    - 실시간 통신을 위한 채팅 방 생성과 관리, 1:N 텍스트 및 1:1 음성 채팅을 서버 채널을 통해 제공합니다.

- 실시간 번역
    - WebSocket을 이용하여 클라이언트와 서버 간 양방향 통신을 지원하며, 텍스트와 음성 간의 실시간 번역을 제공합니다.

- 데이터 처리
    - 멀티미디어 데이터 관리와 텍스트 기반 검색을 위해 이미지 임베딩을 지원하며, 추상적인 텍스트로 채팅 검색 시 연관성 있는 이미지 검색을 제공합니다.

- 계정 관리
    - 회원가입, 로그인, 프로필 관리를 포함한 계정 관리 기능을 제공하며, Spring Security와 AES를 활용하여 보안을 강화하고 SMTP를 통한 이메일 인증을 제공합니다.

- 친구 관리
    - 사용자가 플랫폼 내에서 친구를 추가하고 관리할 수 있습니다.

- 추가 기능
    - 일정 관리, 이벤트 기록, 화면 공유, ChatGPT를 활용한 검색, 이모티콘 지원, 메시지 좋아요 등 다양한 부가 기능을 제공합니다.

- 협업 도구
    - 협업 관련 컨텐츠 요약, 음성 요약, 시맨틱 검색, summary 모델을 사용한 채팅 요약 등 협업을 위한 다양한 기능을 제공합니다.

- 언어 최적화
    - 번역된 텍스트와 사용자의 채팅 기록을 비교하여 가장 적합한 언어로 출력됩니다.

- 향후 기능 개선 방향
    - 구독 및 결제 기능, 신고/차단 기능 등의 추가 기능이 계획되어 있습니다.

