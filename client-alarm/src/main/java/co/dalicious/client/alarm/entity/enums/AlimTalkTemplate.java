package co.dalicious.client.alarm.entity.enums;

import lombok.Getter;

@Getter
public enum AlimTalkTemplate {

    /*메이커스*/
    NOTICE_MAKERS("(메이커스 ${BoardType} 알림)\n\n${makersName} 점주님 안녕하세요.\n\n${BoardType}(이)가 등록되었습니다.\n\n자세한 사항은 메이커스 공지 페이지에서 확인해주세요!", ""),
    PAYCHECK_MAKERS("(메이커스 정산 완료 알림)\n\n${makersName} 점주님 안녕하세요.\n\n정산이 완료 되었습니다.\n\n자세한 사항은 메이커스 정산 페이지에서 확인해주세요!", ""),
    APPROVE_MAKERS("(메이커스 ${BoardType} 알림)\n\n${makersName} 점주님 안녕하세요.\n\n${BoardType}(이)가 완료 및 승인되었습니다.\n\n자세한 사항은 메이커스 공지 페이지에서 확인해주세요!", "50087"),

    /*고객사*/
    NOTICE_CLIENT("(고객사 ${BoardType} 알림)\n\n${clientName} 담당자님 안녕하세요.\n\n${BoardType}(이)가 등록되었습니다.\n\n자세한 사항은 고객사 공지 페이지에서 확인해주세요!", ""),
    INDIVIDUAL_CLIENT("(고객사 정산 완료 알림)\n\n${clientName} 담당자님 안녕하세요.\n\n정산이 완료 되었습니다.\n\n자세한 사항은 고객사 정산 페이지에서 확인해주세요!", ""),
    APPROVE_CLIENT("(고객사 ${BoardType} 알림)\n\n${clientName} 담당자님 안녕하세요.\n\n${BoardType}(이)가 완료 및 승인되었습니다.\n\n자세한 사항은 고객사 공지 페이지에서 확인해주세요!", "50090"),
    ;

    private String template;
    private String templateId;

    AlimTalkTemplate(String template, String templateId) {
        this.template = template;
        this.templateId = templateId;
    }

}
