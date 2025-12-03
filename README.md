> # 프로젝트명 : Carbonlease ( 탄소 절약 인증 플랫폼 )

### 한 줄 소개
* 탄소 절약 활동을 기록·공유하고, 캠페인 및 공지사항을 통해 친환경 활동을 장려하는 웹 서비스입니다.

> ## 메인 화면

<img width="1216" height="771" alt="메인1" src="https://github.com/user-attachments/assets/6fa7f362-c375-49de-8b9f-dd542da0bd24" />

> ## 프로젝트 소개

&nbsp; 이 프로젝트는 Spring Boot 4 + MyBatis + Oracle + JWT 인증 구조의 백엔드 서버로,<br />
다음과 같은 기능을 제공합니다.
* CRUD 기반의 게시판 서버
* 인증/인가(JWT 기반 Login / Token Refresh)
* 캠페인 및 공지사항 관리
* 실시간 대기오염 공공데이터 API 연동
* 파일 업로드 · 이미지 썸네일 처리
* 관리자 페이지 전용 API
* RESTful API 구조 준수

> ## 개발 기간

* 2025.11.10 ~ 2025.12.10 (약 4주)
* 개발 인원 총 5명

> ## 주요 기능

* ### 회원 관리 ###
  * 회원가입 / 로그인 (JWT 발급)
  * AccessToken & RefreshToken 구조
  * 사용자 권한(ROLE_USER / ROLE_ADMIN)
  * 프로필 정보 수정 및 탈퇴
  * 카카오 주소 검색 API 연동

* ### 일반 게시판 ###
  * CRUD
  * 페이징
  * 조회수 카운트
  * 댓글 CRUD

* ### 인증 게시판 ###
  * 게시글 CRUD
  * 이미지 파일 업로드 (Multipart + UUID 저장)
  * 썸네일 이미지 자동 지정
  * 지도 좌표(lat/lng) 저장
  * 조회수 증가
  * 좋아요 토글 로직
  * 댓글 CRUD
  * 페이징 처리

* ### 공지사항 게시판 ###
  * 관리자 전용 등록/수정/삭제
  * 목록 조회 및 페이징
  * 공지 상세 보기
  * 일정 캘린더 API 연동
 
*  ### 캠페인 ###
   * 캠페인 생성 / 수정 / 삭제
   * 캠페인 상태 (진행 / 종료) 표시
   * 좋아요 기능

* ### 실시간 대기오염 정보 제공 ###
  * 공공데이터 API 연동
  * 시군구/시도별 미세먼지 정보 조회
  * 캐싱 처리로 요청 최소화
  * Rate-limit 방어 로직 포함
 
* ### 관리자 페이지 ###
  * 게시글 숨김 / 복구 / 삭제
  * 사용자 상태 관리
  * 공지사항 및 캠페인 등록 및 수정
  * 모든 게시판 모니터링 API 제공
 
 
> ## 기술 스택
### Backend
* Java 21
* Spring Boot 3.5.x
* Spring Security + JWT
* MyBatis
* Oracle Database
* Lombok

### Infra
* Maven build

> ## 프로젝트 구조

```
src
└── main
    ├── java
    │   └── com.kh
    │       ├── activity
    │       ├── admin
    │       │   ├── activity
    │       │   ├── campaign
    │       │   ├── member
    │       │   └── notice
    │       ├── auth
    │       ├── board
    │       ├── campaign
    │       ├── common
    │       │   ├── configuration
    │       │   ├── exception
    │       │   └── util
    │       ├── member
    │       ├── notice
    │       ├── openapi
    │       │   ├── common
    │       │   │   ├── client
    │       │   │   └── config
    │       │   ├── main
    │       │   └── sidebar
    │       └── token
    │
    ├── resources
    │   ├── mapper
    │   └── application.yml
    │
└── uploads
    ├── activity
    ├── campaign
    └── notice
```


> ## 주요 트러블 슈팅

* 작성 해야됨 개인별로

> ## 배운점

* 작성 해야댐

> ## 개선 사항

* 개인별로 작성! 

> ## 팀원 정보


| 이름 | 담당 | GitHub |
|------|------------------------|------------------------------|
| 박수현 (팀장) | 메인페이지 & 캠페인 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/Elinasu001) &nbsp; https://github.com/Elinasu001 |
| 백준걸 | 인증게시판 & 사이드바 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/ajungeul93-rgb) &nbsp; https://github.com/ajungeul93-rgb|
| 최준영 | 공지사항 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/cjysy0104) &nbsp;https://github.com/cjysy0104|
| 현금자 | 일반게시판 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/yoonja486) &nbsp;https://github.com/yoonja486|
| 박세혁 | 회원관리 & 로그인 | [![GitHub](https://img.shields.io/badge/GitHub-000?logo=github&logoColor=white)](https://github.com/Rostreaca) &nbsp;https://github.com/Rostreaca|

> ## 문의
* Email : (누군가의 이메일)
* GitHub Issues 활용
