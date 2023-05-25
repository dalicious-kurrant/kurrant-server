package co.kurrant.app.admin_api.service;

import co.dalicious.client.core.dto.request.OffsetBasedPageRequest;
import co.dalicious.client.core.dto.response.ItemPageableResponseDto;
import co.dalicious.client.core.dto.response.ListItemResponseDto;
import co.dalicious.domain.food.dto.*;
import co.dalicious.domain.food.entity.FoodGroup;
import co.dalicious.domain.order.dto.OrderDto;
import co.dalicious.domain.recommend.dto.FoodRecommendDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface FoodService {
    ItemPageableResponseDto<FoodListDto> getAllFoodList(BigInteger makersIds, Integer limit, Integer page, OffsetBasedPageRequest pageable);
    MakersFoodDetailDto getFoodDetail(BigInteger foodId, BigInteger makersId);
    void updateFoodStatus(List<FoodStatusUpdateDto> foodStatusUpdateDto);
    void updateFoodMass(List<FoodListDto.FoodList> foodListDto);
    void updateFood(List<MultipartFile> files, MakersFoodDetailReqDto foodDetailDto) throws IOException;
    List<FoodListDto.FoodList> getAllFoodForExcel();
    List<FoodGroupDto.Response> getFoodGroups();
    void postFoodGroup(FoodGroupDto.Request request);
    void excelFoodGroups(List<FoodGroupDto.Request> requests);
    void deleteFoodGroups(OrderDto.IdList ids);
    List<FoodRecommendDto.Response> getRecommendsFoodGroup();
    void excelRecommendsFoodGroup(List<FoodRecommendDto.Response> foodRecommendDtos);
    void postRecommendsFoodGroup(FoodRecommendDto.Response foodRecommendDtos);
    void deleteFoodRecommends(OrderDto.IdList ids);
}
