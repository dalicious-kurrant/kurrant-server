package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.client.entity.Corporation;
import co.dalicious.domain.client.repository.QCorporationRepository;
import co.dalicious.domain.user.entity.User;
import co.dalicious.domain.user.repository.UserRepository;
import co.kurrant.app.admin_api.dto.client.CorporationListDto;
import co.kurrant.app.admin_api.mapper.GroupMapper;
import co.kurrant.app.admin_api.service.GroupService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    public final QCorporationRepository qCorporationRepository;
    public final UserRepository userRepository;
    public final GroupMapper groupMapper;


    @Override
    @Transactional
    public ListItemResponseDto<CorporationListDto> getCorporationList(BigInteger groupId, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        Page<Corporation> corporationList = qCorporationRepository.findAll(groupId, limit, page, pageable);
        if(corporationList == null) throw new ApiException(ExceptionEnum.NOT_FOUND);

        // 기업 정보 dto 맵핑하기
        List<CorporationListDto> corporationListDtoList = new ArrayList<>();
        for(Corporation corporation : corporationList) {
            User managerUser = null;
            if(corporation.getManagerId() != null) { managerUser = userRepository.findById(corporation.getManagerId()).orElseThrow(() -> new ApiException(ExceptionEnum.USER_NOT_FOUND));}
            CorporationListDto corporationListDto = groupMapper.toCorporationListDto(corporation, managerUser);
            corporationListDtoList.add(corporationListDto);
        }

        return ListItemResponseDto.<CorporationListDto>builder().items(corporationListDtoList)
                .limit(pageable.getPageSize()).total((long) corporationList.getTotalPages())
                .offset(pageable.getOffset()).count(corporationList.getNumberOfElements()).build();
    }
}
