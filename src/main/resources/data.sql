INSERT INTO category (category_id, category_name, category_image)
VALUES
  (1, '문화', '/images/category/culture.png'),
  (2, '도서', '/images/category/book.png'),
  (3, '교육', '/images/category/study.png'),
  (4, '음악', '/images/category/music.png'),
  (5, '쇼핑', '/images/category/shopping.png'),
  (6, '배달', '/images/category/delivery.png'),
  (7, '기타', '/images/category/etc.png');

INSERT INTO platform (
  platform_id, platform_name, platform_image, platform_description,
  rating_avg, review_count, service_at, category_id
)
VALUES
  (1, '넷플릭스', '/images/platform/netflix.png', '다양한 영화와 드라마를 제공하는 OTT 서비스', 0, 0, '2016-01-07', 1),
  (2, '유튜브 프리미엄', '/images/platform/youtube.png', '광고 없는 영상 시청과 백그라운드 재생 지원', 0, 0, '2016-12-06', 1),
  (3, '디즈니+', '/images/platform/disney.png', '디즈니, 마블, 픽사 콘텐츠 제공', 0, 0, '2021-11-12', 1),
  (4, '웨이브', '/images/platform/wavve.png', '국내 방송 다시보기 및 영화 시청', 0, 0, '2019-09-18', 1),
  (5, '티빙', '/images/platform/tving.png', 'CJ 계열 콘텐츠 중심의 OTT 서비스', 0, 0, '2010-06-01', 1),
  (6, '쿠팡플레이', '/images/platform/coupangPlay.png', '쿠팡 회원 전용 OTT 서비스', 0, 0, '2020-12-24', 1),
  (7, '라프텔', '/images/platform/laftel.png', '애니메이션 전문 스트리밍 서비스', 0, 0, '2017-05-01', 1),
  (8, '왓챠', '/images/platform/watcha.png', '개인 맞춤 추천 기반 영화/드라마 OTT', 0, 0, '2016-01-31', 1),
  (28, 'Xbox 게임패스', '/images/platform/gamepass.png', 'Microsoft의 구독형 게임 제공 서비스', 0, 0, '2017-06-01', 1);


INSERT INTO platform (
  platform_id, platform_name, platform_image, platform_description,
  rating_avg, review_count, service_at, category_id
)
VALUES
  (9, '리디셀렉트', '/images/platform/ridiselect.png', '전자책 무제한 구독 서비스, 리디북스 제공', 0, 0, '2018-07-10', 2),
  (10, '밀리의 서재', '/images/platform/millie.png', '독서 습관을 위한 전자책/오디오북 구독 서비스', 0, 0, '2016-07-16', 2),
  (11, '교보문고 샘', '/images/platform/sam.png', '교보문고에서 제공하는 전자책 구독 플랫폼', 0, 0, '2013-02-22', 2),
  (12, '크레마클럽', '/images/platform/crema.png', 'YES24와 알라딘 연합 전자책 정액제 서비스', 0, 0, '2018-11-22', 2),
  (13, '노벨피아', '/images/platform/novelpia.png', '웹소설 중심의 콘텐츠 구독 플랫폼', 0, 0, '2021-01-07', 2);

INSERT INTO platform (
  platform_id, platform_name, platform_image, platform_description,
  rating_avg, review_count, service_at, category_id
)
VALUES
  (14, '클래스101', '/images/platform/class101.png', '취미부터 실무까지 다양한 온라인 클래스를 구독으로 제공하는 플랫폼', 0, 0, '2015-08-21', 3),
  (15, 'EBS Play', '/images/platform/ebsPlay.png', '다양한 연령층을 위한 온라인 교육 콘텐츠 제공 플랫폼', 0, 0, '2020-08-26', 3);

INSERT INTO platform (
  platform_id, platform_name, platform_image, platform_description,
  rating_avg, review_count, service_at, category_id
)
VALUES
  (16, '멜론', '/images/platform/melon.png', '국내 최대 음원 보유, 카카오 연동 가능한 대표 음악 스트리밍 서비스', 0, 0, '2004-11-06', 4),
  (17, '지니뮤직', '/images/platform/genie.png', 'KT 연계 혜택 제공, 국내 음악 중심의 고음질 스트리밍 서비스', 0, 0, '2012-04-01', 4),
  (18, '유튜브 뮤직', '/images/platform/youtubeMusic.png', '유튜브 콘텐츠 기반의 음악 스트리밍 서비스, 커버곡과 영상 음원 강점', 0, 0, '2015-11-12', 4),
  (19, '플로', '/images/platform/flo.png', 'SKT 연계 음악 플랫폼, 개인화 큐레이션 기능 강화', 0, 0, '2015-01-01', 4),
  (20, '스포티파이', '/images/platform/spotify.png', '글로벌 최대 음악 스트리밍 플랫폼, 다양한 해외 음원과 큐레이션 제공', 0, 0, '2021-02-02', 4);

INSERT INTO platform (
  platform_id, platform_name, platform_image, platform_description,
  rating_avg, review_count, service_at, category_id
)
VALUES
  (21, '쿠팡 와우', '/images/platform/coupangwow.png', '쿠팡의 프리미엄 멤버십, 무료배송·로켓프레시·OTT 포함 멤버십', 0, 0, '2018-10-11', 5),
  (22, '신세계 유니버스 클럽', '/images/platform/universeclub.png', '이마트, SSG닷컴, 스타벅스 혜택을 통합 제공하는 프리미엄 쇼핑 멤버십', 0, 0, '2023-06-08', 5),
  (23, '네이버플러스 멤버십', '/images/platform/naverplus.png', '네이버페이 적립, 쇼핑/콘텐츠/클라우드 등 다양한 통합 멤버십 서비스', 0, 0, '2020-06-01', 5),
  (24, '11번가 우주패스', '/images/platform/spacepass.png', '11번가 쇼핑 혜택과 넷플릭스, 디즈니+ 등의 구독을 결합한 통합 멤버십', 0, 0, '2021-08-31', 5),
  (25, '컬리 멤버스', '/images/platform/kurly.png', '샛별배송 전용 쇼핑몰 컬리의 프리미엄 회원제, 무료배송 및 할인 쿠폰 제공', 0, 0, '2023-08-01', 5);

INSERT INTO platform (
  platform_id, platform_name, platform_image, platform_description,
  rating_avg, review_count, service_at, category_id
)
VALUES
  (26, '배민클럽', '/images/platform/baemin.png', '배달의민족 배달팁 무료, 무제한 장보기 및 쇼핑 할인 혜택 포함 구독형 멤버십', 0, 0, '2024-05-28', 6),
  (27, '요기패스', '/images/platform/yogiyo.png', '요기요 배달비 무료, 마트·편의점 혜택 포함 구독형 멤버십', 0, 0, '2021-11-01', 6);


INSERT INTO platform (
  platform_id, platform_name, platform_image, platform_description,
  rating_avg, review_count, service_at, category_id
)
VALUES
  (29, 'ChatGPT', '/images/platform/chatgpt.png', 'OpenAI에서 개발한 GPT를 기반으로 하는 대화형 인공지능 서비스', 0, 0, '2023-02-01', 7);
