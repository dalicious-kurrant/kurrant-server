package co.kurrant.app.public_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.board.entity.Notice;
import co.dalicious.domain.board.repository.QNoticeRepository;
import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.PaymentCancelHistory;
import co.dalicious.domain.order.repository.QOrderRepository;
import co.dalicious.domain.order.repository.QPaymentCancelHistoryRepository;
import co.dalicious.domain.review.entity.Reviews;
import co.dalicious.domain.review.repository.QReviewRepository;
import co.dalicious.domain.user.dto.PointResponseDto;
import co.dalicious.domain.user.entity.Founders;
import co.dalicious.domain.user.entity.PointHistory;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.QFoundersRepository;
import co.dalicious.domain.user.repository.QPointHistoryRepository;
import co.kurrant.app.public_api.mapper.PointHistoryMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.PointService;
import co.kurrant.app.public_api.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

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
    private final QFoundersRepository qFoundersRepository;

    @Override
    @Transactional(readOnly = true)
    public ItemPageableResponseDto<PointResponseDto> findAllPointLogs(SecurityUser securityUser, Integer condition, OffsetBasedPageRequest pageable) { //, Integer limit, Integer page
        User user = userUtil.getUser(securityUser);

        //포인트 히스토리를 찾는다. - 유저가 같고 포인트가 영이 아닌 것.
        Page<PointHistory> pointHistoryPage = qPointHistoryRepository.findAllPointHistoryByType(user, pageable, condition);
        List<PointResponseDto.PointHistoryDto> pointHistoryDtoList = new ArrayList<>();
        PointResponseDto pointResponseDto;
        if(pointHistoryPage == null || pointHistoryPage.isEmpty()) {
            pointResponseDto = PointResponseDto.create(user.getPoint(), pointHistoryDtoList);
            return ItemPageableResponseDto.<PointResponseDto>builder().items(pointResponseDto).count((pointHistoryPage == null) ? 0 : pointHistoryPage.getNumberOfElements())
                    .total((pointHistoryPage == null) ? 0 : pointHistoryPage.getTotalPages()).limit(pageable.getPageSize()).build();
        }

        // 히스토리 내역에서 각각 상세페이지로 갈 아이디를 찾는다.
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

        // 관련 컨텐츠를 찾는다.
        List<Reviews> reviewsList = reviewIds.isEmpty() ? null : qReviewRepository.findAllByIds(reviewIds);
        List<Order> orderList = orderIds.isEmpty() ? null : qOrderRepository.findAllByIds(orderIds);
        List<Notice> noticeList = boardIds.isEmpty() ? null : qNoticeRepository.findAllByIds(boardIds);
        List<PaymentCancelHistory> cancelList = cancelIds.isEmpty() ? null : qPaymentCancelHistoryRepository.findAllByIds(cancelIds);
        Founders founders = qFoundersRepository.findFoundersByUser(user);

        for(PointHistory pointHistory : pointHistoryPage) {
            PointResponseDto.PointHistoryDto pointRequestDto = pointMapper.toPointRequestDto(pointHistory, reviewsList, orderList, noticeList, cancelList, founders);

            pointHistoryDtoList.add(pointRequestDto);
        }

        pointResponseDto = PointResponseDto.create(user.getPoint(), pointHistoryDtoList);

        return ItemPageableResponseDto.<PointResponseDto>builder().items(pointResponseDto).count(pointHistoryPage.getNumberOfElements())
                .total(pointHistoryPage.getTotalPages()).limit(pageable.getPageSize()).isLast(pointHistoryPage.isLast()).build();
    }
}
