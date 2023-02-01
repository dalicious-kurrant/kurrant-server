package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.entity.Order;
import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.entity.enums.OrderStatus;
import co.dalicious.domain.order.repository.OrderItemRepository;
import co.dalicious.domain.order.repository.OrderRepository;
import co.dalicious.domain.order.repository.QOrderItemRepository;
import co.dalicious.domain.order.repository.QOrderRepository;
import co.dalicious.domain.payment.dto.PaymentCancelRequestDto;
import co.dalicious.domain.payment.entity.CreditCardInfo;
import co.dalicious.domain.payment.entity.PaymentCancelHistory;
import co.dalicious.domain.payment.mapper.PaymentCancleHistoryMapper;
import co.dalicious.domain.payment.repository.PaymentCancelHistoryRepository;
import co.dalicious.domain.payment.repository.QCreditCardInfoRepository;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.PaymentService;
import co.kurrant.app.public_api.service.UserUtil;
import com.sun.xml.bind.v2.TODO;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
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

        Optional<OrderItem> orderItem = orderItemRepository.findById(paymentCancelRequestDto.getOrderItemId());

        Optional<Order> order = orderRepository.findById(orderItem.get().getOrder().getId());
        //userId 검증
        if (!order.get().getUser().getId().equals(user.getId())){
            throw new ApiException(ExceptionEnum.PAYMENT_CANCELLATION_FAILED);
        }
        //paymentKey 가져오기
        String paymentKey = qOrderRepository.getPaymentKey(paymentCancelRequestDto.getOrderItemId());

        //취소할 주문이 없다면
        if (paymentKey == null){
            throw new ApiException(ExceptionEnum.NOT_FOUND);
        }
        System.out.println(paymentKey + " paymentKey 확인");

        //상태값이 이미 7L(취소)인지
        if (orderItem.get().getOrderStatus().equals(OrderStatus.CANCELED)){
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
        System.out.println(orderItem.get().getId());
        PaymentCancelHistory paymentCancelHistory = paymentCancleHistoryMapper.toEntity(paymentCancelRequestDto.getCancelReason(), paymentCancelRequestDto.getCancelAmount(), orderItem.get(), orderCode, checkOutUrl, refundablePrice, creditCardInfo);

        paymentCancelHistoryRepository.save(paymentCancelHistory);

        //orderItem의 Status를 변경한다.
        qOrderItemRepository.updateStatusToSeven(paymentCancelRequestDto.getOrderItemId());


    }
}
