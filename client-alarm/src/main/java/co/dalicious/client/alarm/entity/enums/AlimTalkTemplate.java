package co.dalicious.client.alarm.entity.enums;

import lombok.Getter;

import java.util.HashMap;

@Getter
public enum AlimTalkTemplate {

    /*메이커스*/
    ALL_MAKERS("(메이커스 전체 공지 알림)\n\n점주님 안녕하세요.\n\n전체 공지가 게시되었습니다.\n\n자세한 사항은 메이커스 페이지에서 확인부탁드립니다.", 1001),
    INDIVIDUAL_MAKERS("(메이커스 공지 알림)\n\n${makersName} 점주님 안녕하세요.\n\n${BoardType}(이)가 등록되었습니다.\n\n자세한 사항은 메이커스 공지 페이지에서 확인해주세요!", 1002),
    APPROVE_MAKERS("(메이커스 ${BoardType} 알림)\n\n${makersName} 점주님 안녕하세요.\n\n${BoardType}(이)가 완료 및 승인되었습니다.\n\n자세한 사항은 메이커스 공지 페이지에서 확인해주세요!", 1003),

    /*고객사*/
    ALL_CLIENT("(고객사 전체 공지 알림)\n\n담당자님 안녕하세요.\n\n전체 공지가 게시되었습니다.\n\n 자세한 사항은 고객사 페이지에서 확인부탁드립니다.", 2001),
    INDIVIDUAL_CLIENT("(고객사 공지 알림)\n\n${clientName} 담당자님 안녕하세요.\n\n${BoardType}(이)가 등록되었습니다.자세한 사항은 고객사 공지 페이지에서 확인해주세요!", 2002),
    APPROVE_CLIENT("(고객사 ${BoardType} 알림)\n\n${clientName} 담당자님 안녕하세요.\n\n${BoardType}(이)가 완료 및 승인되었습니다.\n\n자세한 사항은 고객사 공지 페이지에서 확인해주세요!", 2003),
    ;

    private String template;
    private Integer code;

    AlimTalkTemplate(String template, Integer code) {
        this.template = template;
        this.code = code;
    }
}
