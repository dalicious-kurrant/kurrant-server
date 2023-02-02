package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.entity.*;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.repository.OrderItemRepository;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.order.repository.QOrderItemRepository;
import co.dalicious.domain.order.repository.QOrderRepository;
import co.dalicious.domain.payment.dto.PaymentCancelRequestDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.order.mapper.PaymentCancleHistoryMapper;
import co.dalicious.domain.order.repository.PaymentCancelHistoryRepository;
import co.dalicious.domain.payment.repository.QCreditCardInfoRepository;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.PaymentService;
import co.kurrant.app.public_api.service.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserUtil userUtil;
    private final TossUtil tossUtil;
    private final QOrderRepository qOrderRepository;
    private final QOrderItemRepository qOrderItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final QCreditCardInfoRepository qCreditCardInfoRepository;

    private final PaymentCancleHistoryMapper paymentCancleHistoryMapper;
    private final PaymentCancelHistoryRepository paymentCancelHistoryRepository;



    @Override
    @Transactional
    public void paymentCancelOne(SecurityUser securityUser, PaymentCancelRequestDto paymentCancelRequestDto) throws IOException, ParseException {

        User user = userUtil.getUser(securityUser);

        OrderItem orderItem = orderItemRepository.findById(paymentCancelRequestDto.getOrderItemId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.ORDER_ITEM_NOT_FOUND)
        );

        Order order = orderItem.getOrder();

        OrderItemDailyFoodGroup orderItemDailyFoodGroup = ((OrderItemDailyFood) orderItem).getOrderItemDailyFoodGroup();
        // 취소하려는 주문의 유저가 일치하는지 확인
        if (!order.getUser().equals(user)){
            throw new ApiException(ExceptionEnum.PAYMENT_CANCELLATION_FAILED);
        }
        //paymentKey 가져오기
        String paymentKey = order.getPaymentKey();

        //결제 정보가 없을 경우
        if (paymentKey == null || order.getCreditCardInfo() == null){
            if(order.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
                orderItem.updateOrderStatus(OrderStatus.CANCELED);
                return;
            }
            throw new ApiException(ExceptionEnum.NOT_FOUND);
        }

        //상태값이 이미 7L(취소)인지
        if (orderItem.getOrderStatus().equals(OrderStatus.CANCELED)){
            throw new ApiException(ExceptionEnum.DUPLICATE_CANCELLATION_REQUEST);
        }

        //결제 취소 요청
        JSONObject response = tossUtil.cardCancelOne(paymentKey, paymentCancelRequestDto.getCancelReason(), paymentCancelRequestDto.getCancelAmount());

        System.out.println(response);

        String orderCode = response.get("orderId").toString();
        System.out.println(orderCode + "orderCode");

        JSONObject checkout = (JSONObject) response.get("checkout");
        String checkOutUrl = checkout.get("url").toString();
        System.out.println(checkOutUrl + "checkOutUrl");
        JSONArray cancels = (JSONArray) response.get("cancels");
        Integer refundablePrice = null;

        if (cancels.size() != 0 && cancels.size() != 1){
            for (Object cancel : cancels){
                JSONObject cancel1 = (JSONObject) cancel;
                refundablePrice = Integer.valueOf(cancel1.get("refundableAmount").toString());
                System.out.println(refundablePrice + " = refundablePrice");
            }
        }
        JSONObject cancel = (JSONObject) cancels.get(0);
        refundablePrice = Integer.valueOf(cancel.get("refundableAmount").toString());

        JSONObject card = (JSONObject) response.get("card");
        String paymentCardNumber = card.get("number").toString();
        CreditCardInfo creditCardInfo = qCreditCardInfoRepository.findCardIdByCardNumber(paymentCardNumber, user.getId());

        //결제 취소 후 기록을 저장한다.
        System.out.println(orderItem.getId());
        PaymentCancelHistory paymentCancelHistory = paymentCancleHistoryMapper.toEntity(paymentCancelRequestDto.getCancelReason(), paymentCancelRequestDto.getCancelAmount(), orderItem, orderCode, checkOutUrl, refundablePrice, creditCardInfo);

        paymentCancelHistoryRepository.save(paymentCancelHistory);

        //orderItem의 Status를 변경한다.
        qOrderItemRepository.updateStatusToSeven(paymentCancelRequestDto.getOrderItemId());
    }
}
