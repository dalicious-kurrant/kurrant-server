package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.repository.QCustomerBoardRepository;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.PaymentCancelHistory;
import co.dalicious.domain.order.entity.QPaymentCancelHistory;
import co.dalicious.domain.order.repository.QOrderRepository;
import co.dalicious.domain.order.repository.QPaymentCancelHistoryRepository;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.user.dto.PointRequestDto;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.enums.PointStatus;
import co.dalicious.domain.user.mapper.PointMapper;
import co.dalicious.domain.user.repository.QPointHistoryRepository;
import co.kurrant.app.public_api.mapper.PointHistoryMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.PointService;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ansi.AnsiColors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final UserUtil userUtil;
    private final QPointHistoryRepository qPointHistoryRepository;
    private final QReviewRepository qReviewRepository;
    private final QOrderRepository qOrderRepository;
    private final QNoticeRepository qNoticeRepository;
    private final QPaymentCancelHistoryRepository qPaymentCancelHistoryRepository;
    private final PointHistoryMapper pointMapper;
    @Override
    @Transactional(readOnly = true)
    public List<PointRequestDto> findAllPointLogs(SecurityUser securityUser, Integer condition, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        User user = userUtil.getUser(securityUser);

        Page<PointHistory> pointHistoryPage = qPointHistoryRepository.findAllPointHistory(user, limit, page, pageable);
        List<PointRequestDto> pointRequestDtoList = new ArrayList<>();
        if(pointHistoryPage.isEmpty()) return pointRequestDtoList;

        Set<BigInteger> reviewIds = new HashSet<>();
        Set<BigInteger> orderIds = new HashSet<>();
        Set<BigInteger> boardIds = new HashSet<>();
        Set<BigInteger> cancelIds = new HashSet<>();

        for(PointHistory pointHistory : pointHistoryPage) {
            if(pointHistory.getReviewId() != null) reviewIds.add(pointHistory.getReviewId());
            else if(pointHistory.getOrderId() != null) orderIds.add(pointHistory.getOrderId());
            else if(pointHistory.getBoardId() != null) boardIds.add(pointHistory.getBoardId());
            else if(pointHistory.getPaymentCancelHistoryId() != null) cancelIds.add(pointHistory.getPaymentCancelHistoryId());
        }

        List<Reviews> reviewsList = reviewIds.isEmpty() ? null : qReviewRepository.findAllByIds(reviewIds);
        List<Order> orderList = orderIds.isEmpty() ? null : qOrderRepository.findAllByIds(orderIds);
        List<Notice> noticeList = boardIds.isEmpty() ? null : qNoticeRepository.findAllByIds(boardIds);
        List<PaymentCancelHistory> cancelList = cancelIds.isEmpty() ? null : qPaymentCancelHistoryRepository.findAllByIds(cancelIds);

        BigDecimal leftPoint = user.getPoint();

        for(PointHistory pointHistory : pointHistoryPage) {
            BigDecimal historyPoint = pointHistory.getPoint();
            PointRequestDto pointRequestDto = pointMapper.toPointRequestDto(pointHistory, reviewsList, orderList, noticeList, cancelList, leftPoint);
            if(pointHistory.getPointStatus().equals(PointStatus.REVIEW_REWARD) && pointHistory.getPointStatus().equals(PointStatus.EVENT_REWARD) && pointHistory.getPointStatus().equals(PointStatus.CANCEL)) {
                leftPoint = leftPoint.subtract(historyPoint);
            }
            else if(pointHistory.getPointStatus().equals(PointStatus.USED)) {
                leftPoint = leftPoint.add(historyPoint);
            }

            pointRequestDtoList.add(pointRequestDto);
        }

        return pointRequestDtoList;
    }
}
