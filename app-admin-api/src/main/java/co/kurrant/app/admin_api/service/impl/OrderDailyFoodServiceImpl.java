package co.kurrant.app.admin_api.service.impl;

import co.dalicious.domain.client.entity.Group;
import co.dalicious.domain.client.repository.ApartmentRepository;
import co.dalicious.domain.client.repository.CorporationRepository;
import co.dalicious.domain.client.repository.GroupRepository;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.entity.UserGroup;
import co.dalicious.domain.user.entity.enums.ClientStatus;
import co.dalicious.domain.user.entity.enums.ClientType;
import co.dalicious.domain.user.repository.UserGroupRepository;
import co.dalicious.system.util.DateUtils;
import co.dalicious.system.util.StringUtils;
import co.kurrant.app.admin_api.dto.GroupDto;
import co.kurrant.app.admin_api.dto.OrderDto;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.mapper.OrderMapper;
import co.kurrant.app.admin_api.service.OrderDailyFoodService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderDailyFoodServiceImpl implements OrderDailyFoodService {
    private final GroupRepository groupRepository;
    private final ApartmentRepository apartmentRepository;
    private final CorporationRepository corporationRepository;
    private final GroupMapper groupMapper;
    private final OrderMapper orderMapper;
    private final UserGroupRepository userGroupRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    @Override
    @Transactional
    public List<OrderDto.OrderItemDailyFoodList> retrieveOrder(Map<String, Object> parameters) {
        LocalDate startDate = parameters.containsKey("startDate") ? DateUtils.stringToDate((String) parameters.get("startDate")) : null;
        LocalDate endDate = parameters.containsKey("endDate") ? DateUtils.stringToDate((String) parameters.get("endDate")) : null;
        BigInteger groupId = parameters.containsKey("group") ? BigInteger.valueOf(Integer.parseInt((String) parameters.get("group"))) : null;
        List<BigInteger> spotIds = parameters.containsKey("spots") ? StringUtils.parseBigIntegerList((String) parameters.get("spots")) : null;
        Integer diningTypeCode = (Integer) parameters.getOrDefault("diningType", null);
        BigInteger userId = parameters.containsKey("userId") ? BigInteger.valueOf(Integer.parseInt((String) parameters.get("group"))) : null;

        if(groupId == null) throw new ApiException(ExceptionEnum.NOT_FOUND);

        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)
        );

        List<OrderItemDailyFood> orderItemDailyFoods = qOrderDailyFoodRepository.findAllByGroupFilter(startDate, endDate, group, spotIds, diningTypeCode, userId);

        return orderMapper.ToDtoByGroup(orderItemDailyFoods);
    }

    @Override
    public void getOrderDetail(BigInteger orderItemDailyFoodId) {

    }

    @Override
    public List<GroupDto.Group> getGroup(Integer clientType) {
        List<? extends Group> groups = new ArrayList<>();
        if(clientType == null) {
            groups = groupRepository.findAll();
        } else if (ClientType.ofCode(clientType) == ClientType.APARTMENT) {
            groups = apartmentRepository.findAll();
        } else if (ClientType.ofCode(clientType) == ClientType.CORPORATION) {
            groups = corporationRepository.findAll();
        }

        return groupMapper.groupsToDtos(groups);
    }

    @Override
    @Transactional
    public GroupDto getGroupInfo(BigInteger groupId) {
        List<User> users = new ArrayList<>();
        Group group = groupRepository.findById(groupId).orElseThrow(
                () -> new ApiException(ExceptionEnum.GROUP_NOT_FOUND)
        );
        List<UserGroup> userGroups = userGroupRepository.findAllByGroupAndClientStatus(group, ClientStatus.BELONG);
        for (UserGroup userGroup : userGroups) {
            users.add(userGroup.getUser());
        }
        return groupMapper.groupToGroupDto(group, users);
    }
}
