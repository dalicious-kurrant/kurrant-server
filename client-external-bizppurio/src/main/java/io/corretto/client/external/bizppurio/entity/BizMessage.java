package io.corretto.client.external.bizppurio.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "BIZ_MSG")
public class BizMessage {
  @Column(name = "CMID", nullable = false, columnDefinition = "VARCHAR(32) COMMENT '데이터 ID'")
  private String cmid;

  @Column(name = "UMID", columnDefinition = "VARCHAR(32) COMMENT '데이터 ID'")
  private String umid;

  @Column(name = "MSG_TYPE", nullable = false,
      columnDefinition = "TINYINT UNSIGNED DEFAULT 0 COMMENT '데이터 ID'")
  private Integer type;

  @Column(name = "STATUS", nullable = false,
      columnDefinition = "TINYINT UNSIGNED DEFAULT 0 COMMENT '데이터 발송 상태 (대기 0/발송중 1/발송완료 2)'")
  private Integer status;

  @Column(name = "REQUEST_TIME", nullable = false,
      columnDefinition = "DATETIME DEFAULT NOW() COMMENT '데이터 등록 시간'")
  private Timestamp requestDateTime;

  @Column(name = "SEND_TIME", nullable = false,
      columnDefinition = "DATETIME DEFAULT NOW() COMMENT '발송 기준 시간'")
  private Timestamp sendDateTime;

  @Column(name = "REPORT_TIME", columnDefinition = "DATETIME COMMENT '단말기 수신 시간'")
  private Timestamp reportDateTime;

  @Column(name = "DEST_PHONE", nullable = false, columnDefinition = "VARCHAR(16) COMMENT '수신번호'")
  private String destinationPhoneNumber;

  @Column(name = "DEST_NAME", columnDefinition = "VARCHAR(32) COMMENT '수신자명'")
  private String destinationName;

  @Column(name = "SEND_PHONE", nullable = false, columnDefinition = "VARCHAR(16) COMMENT '발신자번호'")
  private String sourcePhoneNumber;

  @Column(name = "SEND_NAME", columnDefinition = "VARCHAR(32) COMMENT '발신자명'")
  private String sourceName;

  @Column(name = "SUBJECT", columnDefinition = "VARCHAR(64) COMMENT '제목'")
  private String title;

  @Column(name = "MSG_BODY", nullable = false, columnDefinition = "VARCHAR(2000) COMMENT '메시지 내용'")
  private String content;

  @Column(name = "NATION_CODE", nullable = false, columnDefinition = "VARCHAR(5) COMMENT '국가코드'")
  private String countryCode;

  @Column(name = "SENDER_KEY", nullable = false, columnDefinition = "VARCHAR(40) COMMENT '발신프로필 키'")
  private String senderKey;

  @Column(name = "TEMPLATE_CODE", nullable = false,
      columnDefinition = "VARCHAR(64) COMMENT '템플릿 코드'")
  private String templateCode;

  @Column(name = "RESPONSE_METHOD", columnDefinition = "VARCHAR(8) COMMENT '발송 방식 (PUSH)'")
  private String responseMethod;

  @Column(name = "TIMEOUT", columnDefinition = "VARCHAR(4) COMMENT '대체발송을 위한 타임아웃 시간설정'")
  private String timeout;

  @Column(name = "RE_TYPE", columnDefinition = "VARCHAR(3) COMMENT '대체발송 메시지 타입, 5.4 대체발송타입 참조'")
  private String alternativeMessageType;

  @Column(name = "RE_BODY", columnDefinition = "VARCHAR(2000) COMMENT '대체발송 메시지 내용'")
  private String alternativeMessageContent;

  @Column(name = "RE_PART",
      columnDefinition = "VARCHAR(1) COMMENT '대체발송 처리 주체 (C: CLIENT, S: SERVER)'")
  private String alternativeMessageProcessingTarget;

  @Column(name = "COVER_FLAG", columnDefinition = "TINYINT UNSIGNED DEFAULT 0 COMMENT '표지 발송 옵션'")
  private String sendCover;

  @Column(name = "SMS_FLAG",
      columnDefinition = "TINYINT UNSIGNED DEFAULT 0 COMMENT '실패 시 문자 전송 옵션'")
  private String sendSms;

  @Column(name = "REPLY_FLAG",
      columnDefinition = "TINYINT UNSIGNED DEFAULT 0 COMMENT '시나리오 답변기능 여부(Y:1, N:0)'")
  private String sendReply;

  @Column(name = "RETRY_CNT", columnDefinition = "TINYINT UNSIGNED COMMENT '재시도회수'")
  private String retryCount;

  @Column(name = "ATTACHED_FILE",
      columnDefinition = "VARCHAR(1000) COMMENT '첨부파일 전송 시 파일명 (여러개일 경우, | 문자로 구분)'")
  private String attachedFile;

  @Column(name = "VXML_FILE", columnDefinition = "VARCHAR(64) COMMENT '음성 시나리오 파일 이름'")
  private String voiceScenarioFileName;

  @Column(name = "CALL_STATUS", columnDefinition = "VARCHAR(4) COMMENT '발송결과 리포트'")
  private String callStatus;

  @Column(name = "USE_PAGE", columnDefinition = "INT(2) DEFAULT 0 COMMENT '발송 페이지 수'")
  private String pageCount;

  @Column(name = "USE_TIME", columnDefinition = "INT(4) DEFAULT 0 COMMENT '발송 소요 시간(단위: 초)'")
  private String sendDurationSeconds;

  @Column(name = "SN_RESULT", columnDefinition = "INT(1) DEFAULT 0 COMMENT '설문조사 응답 값(0~9)'")
  private String surveyResult;

  @Column(name = "TEL_INFO", columnDefinition = "VARCHAR(10) COMMENT '통신사 정보 (SKT/KTF/LGT/KKO)'")
  private String carrierCode;

  @Column(name = "CINFO", columnDefinition = "VARCHAR(32) COMMENT '특수기호 (\\/:*?\"<>|.) 사용 불가'")
  private String cliendIndexedInformation;

  @Column(name = "USER_KEY",
      columnDefinition = "VARCHAR(30) COMMENT '옐로아이디 봇을 이용해 받은 옐로아이디 사용자 식별키'")
  private String userKey;

  @Column(name = "AD_FLAG",
      columnDefinition = "VARCHAR(1) COMMENT '광고성 메시지 필수 표기 사항을 노출, 기본값 Y, Y/N'")
  private String notifyRequirementsForAds;

  @Column(name = "RCS_REFKEY", columnDefinition = "VARCHAR(32) COMMENT 'RCS 테이블 KEY'")
  private String rcsTableKey;
}
