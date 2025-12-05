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

#### 박수현
* 내용

#### 박세혁
 1. 카카오 로그인 요청 시 API 응답 값을 프론트엔드로 보내지 못하는 문제
문제점 : 카카오 로그인 요청 시 redirect_uri를 백엔드 주소로 정하였더니
API로 요청받은 토큰값과 사용자 로그인 정보를 프론트엔드에 응답을 ResponseEntity의 body로 전달하지 못하고 프론트엔드는 화면을 띄우지 못함.

해결 시도 1 : HttpServletResponse의 sendRedirect 메소드를 호출하여 프론트엔드에 화면을 띄웠음. 그 후 응답받은 토큰 정보와 사용자 정보를 보내주려 했으나
쿼리스트링으로 보내는 방법밖에 떠오르지 않음. 그러나 사용자 정보를 쿼리스트링으로 보내면 사용자 정보가 URL에 모두 노출되는 문제가 생겨 그렇게 하지 않았음.

해결 방법 : redirect_uri를 프론트엔드로 바꿔 프론트엔드에서 받은 코드를 다시 axios요청을 통해 백엔드로 보내는 방식을 채택함.
프론트엔드에서는 axios요청 성공 시 마다 다른 페이지로 이동함.axios 요청을 보내기 때문에 ResponseEntity의 body로 안전하게 사용자 정보를 전달할 수 있음.

#### 백준걸
* 내용

#### 최준영
* 내용

#### 현금자
* 내용

> ## 배운점

#### 박수현
* 내용

#### 박세혁
* Git 협업 방식 및 흐름
* MVC 패턴 활용에 대한 이해
* 소스코드 책임 분리
* 스프링 부트 프로젝트 세팅 및 생성 
* 스프링 시큐리티및 JWT 토큰 방식 로그인에 대한 이해
* 소셜 로그인 API 활용

#### 백준걸
* 내용

#### 최준영
* 내용

#### 현금자
* 내용

> ## 개선 사항

#### 박수현
* 내용

#### 박세혁
* 일반 로그인 과 소셜 로그인 구조 분리
* JWT 토큰 방식 로그인 구조 리팩토링링 

#### 백준걸
* 내용

#### 최준영
* 내용

#### 현금자
* 내용
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
