package co.kurrant.app.public_api.service.impl;

import co.dalicious.domain.order.entity.OrderItem;
import co.dalicious.domain.order.repository.OrderItemRepository;
import co.dalicious.domain.order.repository.QOrderRepository;
import co.dalicious.domain.payment.dto.PaymentCancelRequestDto;
import co.dalicious.domain.payment.mapper.PaymentCancleHistoryMapper;
import co.dalicious.domain.payment.util.TossUtil;
import co.dalicious.domain.user.entity.User;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.PaymentService;
import co.kurrant.app.public_api.service.UserUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserUtil userUtil;
    private final TossUtil tossUtil;
    private final QOrderRepository qOrderRepository;
    private final OrderItemRepository orderItemRepository;

    private final PaymentCancleHistoryMapper paymentCancleHistoryMapper;



    @Override
    public void paymentCancelOne(SecurityUser securityUser, PaymentCancelRequestDto paymentCancelRequestDto) throws IOException, ParseException {

        User user = userUtil.getUser(securityUser);
        String paymentKey = qOrderRepository.getPaymentKey(paymentCancelRequestDto.getOrderItemId());
        System.out.println(paymentKey + "paymentKey를 잘 가져왔는지 확인");

        //결제 취소 요청
        JSONObject response = tossUtil.cardCancelOne(paymentKey, paymentCancelRequestDto.getCancelReason(), paymentCancelRequestDto.getCancelAmount());

        //결제 취소 후 기록을 저장한다.
        Optional<OrderItem> orderItem = orderItemRepository.findById(paymentCancelRequestDto.getOrderItemId());
//        paymentCancleHistoryMapper.toEntity()


//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.tosspayments.com/v1/payments/4mNQ3YdGf74_gHCGKKj1T/cancel"))
//                .header("Authorization", "Basic dGVzdF9za19ZWjFhT3dYN0s4bWdwYnEyUjRRVnlReHp2TlBHOg==")
//                .header("Content-Type", "application/json")
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"고객이 취소를 원함\"}"))
//                .build();
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response.body());



    }
}
