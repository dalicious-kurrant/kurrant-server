package co.kurrant.app.admin_api.service.impl;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.domain.client.entity.DayAndTime;
import co.dalicious.domain.file.dto.ImageResponseDto;
import co.dalicious.domain.file.entity.embeddable.Image;
import co.dalicious.domain.file.service.ImageService;
import co.dalicious.domain.food.dto.*;
import co.dalicious.domain.food.entity.*;
import co.dalicious.domain.food.entity.enums.FoodStatus;
import co.dalicious.domain.food.mapper.CapacityMapper;
import co.dalicious.domain.food.mapper.FoodDiscountPolicyMapper;
import co.dalicious.domain.food.mapper.MakersFoodMapper;
import co.dalicious.domain.food.repository.*;
import co.dalicious.domain.order.mapper.FoodMapper;
import co.dalicious.system.enums.DiningType;
import co.dalicious.system.enums.DiscountType;
import co.dalicious.system.enums.FoodTag;
import co.kurrant.app.admin_api.service.FoodService;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {

    private final FoodRepository foodRepository;
    private final MakersFoodMapper makersFoodMapper;
    private final FoodMapper foodMapper;
    private final MakersRepository makersRepository;
    private final FoodDiscountPolicyMapper foodDiscountPolicyMapper;
    private final FoodDiscountPolicyRepository foodDiscountPolicyRepository;
    private final CapacityMapper capacityMapper;
    private final FoodCapacityRepository foodCapacityRepository;
    private final QFoodRepository qFoodRepository;
    private final ImageService imageService;

    @Override
    @Transactional
    public ItemPageableResponseDto<FoodListDto> getAllFoodList(BigInteger makersIds, Integer limit, Integer page, OffsetBasedPageRequest pageable) {
        BigInteger makersId = (makersIds == null) ? null : makersIds;

        // 모든 상품 불러오기
        Page<Food> allFoodPage = qFoodRepository.findAllPage(makersId, limit, page, pageable);

        // 상품 dto에 담기
        List<FoodListDto.FoodList> dtoList = new ArrayList<>();

        if(allFoodPage != null) {
            for(Food food : allFoodPage) {
                DiscountDto discountDto = DiscountDto.getDiscount(food);
                BigDecimal resultPrice = discountDto.getDiscountedPrice();
                FoodListDto.FoodList dto = makersFoodMapper.toAllFoodListDto(food, discountDto, resultPrice);
                dtoList.add(dto);
            }
        }
        List<Makers> makersList = makersRepository.findAll();
        FoodListDto responseList = FoodListDto.createFoodListDto(makersList, dtoList);

        return ItemPageableResponseDto.<FoodListDto>builder().items(responseList)
                .limit(pageable.getPageSize()).total(Objects.requireNonNull(allFoodPage).getTotalPages())
                .count(allFoodPage.getNumberOfElements()).build();
    }

    @Override
    @Transactional
    public MakersFoodDetailDto getFoodDetail(BigInteger foodId, BigInteger makersId) {
        // maker와 food를 찾고
        Makers makers = makersRepository.findById(makersId).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS)
        );
        Food food = qFoodRepository.findByIdAndMakers(foodId, makers);
        // 만약 food가 없으면 예외처리
        if(food == null) throw new ApiException(ExceptionEnum.NOT_FOUND_FOOD);

        DiscountDto discountDto = DiscountDto.getDiscount(food);

        return makersFoodMapper.toFoodManagingDto(food, discountDto);
    }

    @Override
    @Transactional
    public void updateFoodStatus(List<FoodStatusUpdateDto> foodStatusUpdateDto) {
        List<BigInteger> ids = new ArrayList<>();
        for (FoodStatusUpdateDto statusUpdateDto : foodStatusUpdateDto) {
            ids.add(statusUpdateDto.getFoodId());
        }
        List<Food> foods = foodRepository.findAllById(ids);
        for (Food food : foods) {
            FoodStatusUpdateDto selectedDto = foodStatusUpdateDto.stream()
                    .filter(v -> v.getFoodId().equals(food.getId()))
                    .findAny()
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND));
            food.updateFoodStatus(FoodStatus.ofCode(selectedDto.getFoodStatus()));
        }
    }

    //대량 수정
    @Override
    @Transactional
    public void updateFoodMass(List<FoodListDto.FoodList> foodListDtoList) {
        for(FoodListDto.FoodList foodListDto : foodListDtoList) {
            Food food = foodRepository.findById(foodListDto.getFoodId()).orElse(null);

            List<FoodTag> foodTags = new ArrayList<>();
            List<String> foodTagStrs = foodListDto.getFoodTags();
            if(foodTagStrs == null) foodTags = null;
            else { for (String tag : foodTagStrs) foodTags.add(FoodTag.ofString(tag)); }
            Makers makers = makersRepository.findById(foodListDto.getMakersId()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_MAKERS));

            // 기존 푸드가 없으면 생성
            if(food == null) {
                BigDecimal customPrice = BigDecimal.ZERO;

                // 푸드 생성
                Food newFood = foodMapper.toNewEntity(foodListDto, makers, customPrice, foodTags);
                foodRepository.save(newFood);

                // 푸드 할인 정책 생성
                FoodDiscountPolicy membershipDiscount = foodDiscountPolicyMapper.toEntity(DiscountType.MEMBERSHIP_DISCOUNT, foodListDto.getMembershipDiscount(), newFood);
                FoodDiscountPolicy makersDiscount = foodDiscountPolicyMapper.toEntity(DiscountType.MAKERS_DISCOUNT, foodListDto.getMakersDiscount(), newFood);
                FoodDiscountPolicy periodDiscount = foodDiscountPolicyMapper.toEntity(DiscountType.PERIOD_DISCOUNT, foodListDto.getEventDiscount(), newFood);

                foodDiscountPolicyRepository.save(membershipDiscount);
                foodDiscountPolicyRepository.save(makersDiscount);
                foodDiscountPolicyRepository.save(periodDiscount);

                // 푸드 capacity 생성
                List<MakersCapacity> makersCapacityList = makers.getMakersCapacities();
                if(makersCapacityList == null){ throw new ApiException(ExceptionEnum.NOT_FOUND_MAKERS_CAPACITY); }
                for (MakersCapacity makersCapacity : makersCapacityList) {
                    DiningType diningType = makersCapacity.getDiningType();
                    Integer capacity = makersCapacity.getCapacity();

                    FoodCapacity foodCapacity = capacityMapper.toEntity(diningType, capacity, newFood);
                    foodCapacityRepository.save(foodCapacity);
                }
            }

            // food가 있으면
            else {
                //food UPDATE
                food.updateFoodMass(foodListDto, foodTags, makers);
                foodRepository.save(food);

                //food discount policy UPDATE
                List<FoodDiscountPolicy> discountPolicyList = food.getFoodDiscountPolicyList();
                for(FoodDiscountPolicy discountPolicy : discountPolicyList) {
                    if(discountPolicy.getDiscountType().equals(DiscountType.MEMBERSHIP_DISCOUNT)) {
                        discountPolicy.updateFoodDiscountPolicy(foodListDto.getMembershipDiscount());
                        foodDiscountPolicyRepository.save(discountPolicy);
                    }
                    else if(discountPolicy.getDiscountType().equals(DiscountType.MAKERS_DISCOUNT)) {
                        discountPolicy.updateFoodDiscountPolicy(foodListDto.getMakersDiscount());
                        foodDiscountPolicyRepository.save(discountPolicy);
                    }
                    else if(discountPolicy.getDiscountType().equals(DiscountType.PERIOD_DISCOUNT)) {
                        discountPolicy.updateFoodDiscountPolicy(foodListDto.getEventDiscount());
                        foodDiscountPolicyRepository.save(discountPolicy);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void updateFood(List<MultipartFile> files, MakersFoodDetailReqDto foodDetailDto) throws IOException {
        Food food = foodRepository.findById(foodDetailDto.getFoodId()).orElseThrow(
                () -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD)
        );

        // 이미지가 삭제되었다면 S3에서도 삭제
        List<Image> images = new ArrayList<>();
        List<String> requestImage = foodDetailDto.getImages();
        if(requestImage != null && requestImage.size() != food.getImages().size()) {
            List<Image> deleteImages = food.getImages();
            List<Image> selectedImages = food.getImages().stream()
                    .filter(v -> requestImage.contains(v.getLocation()))
                    .toList();
            deleteImages.removeAll(selectedImages);
            if(!deleteImages.isEmpty()) {
                for (Image image : deleteImages) {
                    imageService.delete(image.getPrefix());
                }
            }
            images.addAll(selectedImages);
        } else {
            images.addAll(food.getImages());
        }

        if(files != null && !files.isEmpty()) {
            List<ImageResponseDto> imageResponseDtos = imageService.upload(files, "food");
            images.addAll(Image.toImages(imageResponseDtos));
        }

        //기존 설명을 수정하지 않으면
        if(foodDetailDto.getDescription() == null || foodDetailDto.getDescription().isEmpty() || foodDetailDto.getDescription().isBlank()) {
            foodDetailDto.setDescription(food.getDescription());
        }

        // 이미지 및 음식 업데이트
        food.updateImages(images);
        food.updateFood(foodDetailDto);

        if(food.updateFoodCapacity(DiningType.MORNING, foodDetailDto.getMorningCapacity(),  DayAndTime.stringToDayAndTime(foodDetailDto.getMorningLastOrderTime())) != null) {
            foodCapacityRepository.save(food.updateFoodCapacity(DiningType.MORNING, foodDetailDto.getMorningCapacity(), DayAndTime.stringToDayAndTime(foodDetailDto.getMorningLastOrderTime())));
        }
        if(food.updateFoodCapacity(DiningType.LUNCH, foodDetailDto.getLunchCapacity(),  DayAndTime.stringToDayAndTime(foodDetailDto.getLunchLastOrderTime())) != null) {
            foodCapacityRepository.save(food.updateFoodCapacity(DiningType.LUNCH, foodDetailDto.getLunchCapacity(), DayAndTime.stringToDayAndTime(foodDetailDto.getLunchLastOrderTime())));
        }
        if(food.updateFoodCapacity(DiningType.DINNER, foodDetailDto.getDinnerCapacity(),  DayAndTime.stringToDayAndTime(foodDetailDto.getDinnerLastOrderTime())) != null) {
            foodCapacityRepository.save(food.updateFoodCapacity(DiningType.DINNER, foodDetailDto.getDinnerCapacity(), DayAndTime.stringToDayAndTime(foodDetailDto.getDinnerLastOrderTime())));
        }

        //음식 할인 정책 저장
        if (food.getFoodDiscountPolicy(DiscountType.MEMBERSHIP_DISCOUNT) == null) {
            foodDiscountPolicyRepository.save(foodMapper.toFoodDiscountPolicy(food, DiscountType.MEMBERSHIP_DISCOUNT, foodDetailDto.getMembershipDiscountRate()));
        }
        else if (foodDetailDto.getMembershipDiscountRate() == 0) {
            foodDiscountPolicyRepository.delete(food.getFoodDiscountPolicy(DiscountType.MEMBERSHIP_DISCOUNT));
        }
        else {
            food.getFoodDiscountPolicy(DiscountType.MEMBERSHIP_DISCOUNT).updateFoodDiscountPolicy(foodDetailDto.getMembershipDiscountRate());
        }
        if (food.getFoodDiscountPolicy(DiscountType.MAKERS_DISCOUNT) == null) {
            foodDiscountPolicyRepository.save(foodMapper.toFoodDiscountPolicy(food, DiscountType.MAKERS_DISCOUNT, foodDetailDto.getMakersDiscountRate()));
        }
        else if (foodDetailDto.getMakersDiscountRate() == 0) {
            foodDiscountPolicyRepository.delete(food.getFoodDiscountPolicy(DiscountType.MAKERS_DISCOUNT));
        }
        else {
            food.getFoodDiscountPolicy(DiscountType.MAKERS_DISCOUNT).updateFoodDiscountPolicy(foodDetailDto.getMakersDiscountRate());
        }

        if (food.getFoodDiscountPolicy(DiscountType.PERIOD_DISCOUNT) == null) {
            foodDiscountPolicyRepository.save(foodMapper.toFoodDiscountPolicy(food, DiscountType.PERIOD_DISCOUNT, foodDetailDto.getMakersDiscountRate()));
        }
        else if (foodDetailDto.getPeriodDiscountRate() == 0) {
            foodDiscountPolicyRepository.delete(food.getFoodDiscountPolicy(DiscountType.PERIOD_DISCOUNT));
        }
        else {
            food.getFoodDiscountPolicy(DiscountType.PERIOD_DISCOUNT).updateFoodDiscountPolicy(foodDetailDto.getMakersDiscountRate());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodListDto.FoodList> getAllFoodForExcel() {
        List<Food> foodList = foodRepository.findAll();
        List<FoodListDto.FoodList> responseList = new ArrayList<>();

        if(foodList != null) {
            for(Food food : foodList) {
                DiscountDto discountDto = DiscountDto.getDiscount(food);
                BigDecimal resultPrice = discountDto.getDiscountedPrice();
                FoodListDto.FoodList dto = makersFoodMapper.toAllFoodListDto(food, discountDto, resultPrice);
                responseList.add(dto);
            }
        }

        return responseList;
    }
}
