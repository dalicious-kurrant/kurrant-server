package co.dalicious.client.alarm.entity.enums;

import lombok.Getter;

@Getter
public enum AlimTalkTemplate {

    /*메이커스*/
    NOTICE_MAKERS("(메이커스 ${BoardType} 알림)\n\n${makersName} 점주님 안녕하세요.\n\n${BoardType}(이)가 등록되었습니다.\n\n자세한 사항은 메이커스 공지 페이지에서 확인해주세요!\n\n▶ https://makers.dalicious.co/", "50091","https://makers.dalicious.co/"),
    PAYCHECK_MAKERS("(메이커스 정산 완료 알림)\n\n${makersName} 점주님 안녕하세요.\n\n정산이 완료 되었습니다.\n\n자세한 사항은 메이커스 정산 페이지에서 확인해주세요!\n\n▶ https://makers.dalicious.co/calculate", "50093","https://makers.dalicious.co/calculate"),
    APPROVE_MAKERS("(메이커스 ${BoardType} 알림)\n\n${makersName} 점주님 안녕하세요.\n\n${BoardType}(이)가 완료 및 승인되었습니다.\n\n자세한 사항은 메이커스 공지 페이지에서 확인해주세요!\n\n▶ https://makers.dalicious.co/", "50087","https://makers.dalicious.co/"),

    /*고객사*/
    NOTICE_CLIENT("(고객사 ${BoardType} 알림)\n\n${clientName} 담당자님 안녕하세요.\n\n${BoardType}(이)가 등록되었습니다.\n\n자세한 사항은 고객사 공지 페이지에서 확인해주세요!\n\n▶ https://group.dalicious.co/notice", "50092","https://group.dalicious.co/notice"),
    INDIVIDUAL_CLIENT("(고객사 정산 완료 알림)\n\n${clientName} 담당자님 안녕하세요.\n\n정산이 완료 되었습니다.\n\n자세한 사항은 고객사 정산 페이지에서 확인해주세요!\n\n▶ https://group.dalicious.co/calc", "50094", "https://group.dalicious.co/calc"),
    APPROVE_CLIENT("(고객사 ${BoardType} 알림) \n\n${clientName} 담당자님 안녕하세요. \n\n${BoardType}(이)가 완료 및 승인되었습니다.\n\n자세한 사항은 고객사 공지 페이지에서 확인해주세요!\n\n▶ https://group.dalicious.co/notice", "50090","https://group.dalicious.co/notice"),
    ;

    private String template;
    private String templateId;
    private String redirectUrl;

    AlimTalkTemplate(String template, String templateId, String redirectUrl) {
        this.template = template;
        this.templateId = templateId;
        this.redirectUrl = redirectUrl;
    }
}