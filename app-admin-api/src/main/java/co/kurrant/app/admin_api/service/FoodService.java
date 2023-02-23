package co.kurrant.app.admin_api.service;

import co.dalicious.domain.food.dto.FoodStatusUpdateDto;
import co.dalicious.domain.food.dto.FoodListDto;
import co.dalicious.domain.food.dto.MakersFoodDetailDto;
import co.dalicious.domain.food.dto.MakersFoodDetailReqDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface FoodService {
    List<FoodListDto> getAllFoodList();
    List<FoodListDto> getAllFoodListByMakers(BigInteger makersId);
    MakersFoodDetailDto getFoodDetail(BigInteger foodId, BigInteger makersId);
    void updateFoodStatus(List<FoodStatusUpdateDto> foodStatusUpdateDto);
    void updateFoodMass(List<FoodListDto> foodListDto);
    void updateFood(List<MultipartFile> files, MakersFoodDetailReqDto foodDetailDto) throws IOException;
}
