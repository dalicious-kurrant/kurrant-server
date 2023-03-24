package co.kurrant.app.makers_api.service.impl;

import co.dalicious.domain.file.dto.ImageWithEnumResponseDto;
import co.dalicious.domain.file.entity.embeddable.ImageWithEnum;
import co.dalicious.domain.file.entity.embeddable.enums.ImageType;
import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.food.dto.MakersInfoResponseDto;
import co.dalicious.domain.food.dto.OriginDto;
import co.dalicious.domain.food.entity.Makers;
import co.dalicious.domain.food.entity.MakersCapacity;
import co.dalicious.domain.food.entity.enums.Origin;
import co.dalicious.domain.food.repository.OriginRepository;
import co.dalicious.domain.food.repository.QMakersCapacityRepository;
import co.dalicious.domain.food.repository.QMakersRepository;
import co.dalicious.domain.order.dto.OrderDto;
import co.kurrant.app.makers_api.mapper.MakersMapper;
import co.kurrant.app.makers_api.model.SecurityUser;
import co.kurrant.app.makers_api.service.MakersInfoService;
import co.kurrant.app.makers_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MakersInfoServiceImpl implements MakersInfoService {

    private final UserUtil userUtil;
    private final MakersMapper makersMapper;
    private final ImageService imageService;
    private final QMakersCapacityRepository qMakersCapacityRepository;
    private final OriginRepository originRepository;

    @Override
    public Object getMakersInfo(SecurityUser securityUser) {

        Makers makers = userUtil.getMakers(securityUser);

        List<MakersInfoResponseDto> makersInfoResponseDtoList = new ArrayList<>();

        //dailyCapacity 구하기
        List<MakersCapacity> makersCapacity = qMakersCapacityRepository.findByMakersId(makers.getId());
        Integer dailyCapacity = 0;
        List<String> diningTypes = new ArrayList<>();
        for (MakersCapacity capacity : makersCapacity) {
            dailyCapacity += capacity.getCapacity();
            diningTypes.add(capacity.getDiningType().getDiningType());
        }

        MakersInfoResponseDto makersInfo = makersMapper.toDto(makers, dailyCapacity, diningTypes);

        makersInfoResponseDtoList.add(makersInfo);

        return makersInfoResponseDtoList;
    }

    @Override
    @Transactional
    public List<OriginDto.WithId> getMakersOrigins(SecurityUser securityUser) {
        Makers makers = userUtil.getMakers(securityUser);
        return makersMapper.originToDtos(makers.getOrigins());
    }

    @Override
    @Transactional
    public void postMakersOrigins(SecurityUser securityUser, List<OriginDto> originDtos) {
        Makers makers = userUtil.getMakers(securityUser);
        for (OriginDto originDto : originDtos) {
            originRepository.save(makersMapper.dtoToOrigin(originDto, makers));
        }
    }

    @Override
    @Transactional
    public void updateMakersOrigin(SecurityUser securityUser, OriginDto.WithId originDto) {
        Origin origin = originRepository.findById(originDto.getId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
        origin.updateOrigin(originDto);
    }

    @Override
    @Transactional
    public void deleteMakersOrigins(SecurityUser securityUser, OrderDto.IdList idList) {
        List<Origin> origins = originRepository.findAllById(idList.getIdList());
        originRepository.deleteAll(origins);
    }

    @Override
    @Transactional
    public List<ImageWithEnumResponseDto> getDocuments(SecurityUser securityUser) {
        Makers makers = userUtil.getMakers(securityUser);
        List<ImageWithEnum> images = makers.getImages();
        return makersMapper.imageWithEnumToDtos(images);
    }

    @Override
    @Transactional
    public void updateDocuments(SecurityUser securityUser, MultipartFile businessLicense, MultipartFile businessPermit, MultipartFile accountCopy, List<ImageWithEnumResponseDto> imageDtos) throws IOException {
        Makers makers = userUtil.getMakers(securityUser);
        List<ImageWithEnum> images = new ArrayList<>();

        for (ImageWithEnum image : makers.getImages()) {
            switch (image.getImageType()) {
                case BUSINESS_LICENSE -> {
                    ImageWithEnumResponseDto imageDto = imageDtos.stream().filter(v -> v.getImageType().equals(ImageType.BUSINESS_LICENSE.getCode())).findAny().orElse(null);
                    if (businessLicense != null) {
                        imageService.delete(image.getPrefix());
                    }
                    // 이미지가 같지 않다면 저장하지 않기.
                    else if (imageDto != null && image.isSameImage(imageDto)) {
                        images.add(image);
                    }
                }
                case BUSINESS_PERMIT -> {
                    ImageWithEnumResponseDto imageDto = imageDtos.stream().filter(v -> v.getImageType().equals(ImageType.BUSINESS_PERMIT.getCode())).findAny().orElse(null);
                    if (businessPermit != null) {
                        imageService.delete(image.getPrefix());
                    }
                    // 이미지가 같지 않다면 저장하지 않기.
                    else if (imageDto != null && image.isSameImage(imageDto)) {
                        images.add(image);
                    }
                }
                case ACCOUNT_COPY -> {
                    ImageWithEnumResponseDto imageDto = imageDtos.stream().filter(v -> v.getImageType().equals(ImageType.ACCOUNT_COPY.getCode())).findAny().orElse(null);;
                    if (accountCopy != null) {
                        imageService.delete(image.getPrefix());
                    }
                    // 이미지가 같지 않다면 저장하지 않기.
                    else if (imageDto != null && image.isSameImage(imageDto)) {
                        images.add(image);
                    }
                }
            }
        }

        if (businessLicense != null) {
            String dirName = "makers/" + makers.getId().toString() + "/businessLicense";
            images.add(new ImageWithEnum(imageService.upload(businessLicense, dirName), ImageType.BUSINESS_LICENSE));
        }
        if (businessPermit != null) {
            String dirName = "makers/" + makers.getId().toString() + "/businessPermit";
            images.add(new ImageWithEnum(imageService.upload(businessPermit, dirName), ImageType.BUSINESS_PERMIT));
        }
        if (accountCopy != null) {
            String dirName = "makers/" + makers.getId().toString() + "/accountCopy";
            images.add(new ImageWithEnum(imageService.upload(accountCopy, dirName), ImageType.ACCOUNT_COPY));
        }
        makers.updateImages(images);
    }

}
