package co.kurrant.app.public_api.service.impl;

import co.dalicious.data.redis.entity.PushAlarmHash;
import co.dalicious.data.redis.repository.PushAlarmHashRepository;
import co.dalicious.domain.food.entity.DailyFood;
import co.dalicious.domain.food.entity.Food;
import co.dalicious.domain.food.repository.DailyFoodRepository;
import co.dalicious.domain.food.repository.FoodRepository;
import co.dalicious.domain.order.entity.OrderItemDailyFood;
import co.dalicious.domain.order.repository.QOrderDailyFoodRepository;
import co.dalicious.domain.user.dto.*;
import co.dalicious.domain.user.entity.*;
import co.dalicious.domain.user.entity.enums.Country;
import co.dalicious.domain.user.entity.enums.JobType;
import co.dalicious.domain.user.entity.enums.PushCondition;
import co.dalicious.domain.user.mapper.DailyReportMapper;
import co.dalicious.domain.user.mapper.UserPreferenceMapper;
import co.dalicious.domain.user.mapper.UserSelectTestDataMapper;
import co.dalicious.domain.user.repository.*;
import co.dalicious.system.enums.FoodTag;
import co.kurrant.app.public_api.dto.board.PushResponseDto;
import co.kurrant.app.public_api.dto.order.OrderItemDailyFoodToDailyReportDto;
import co.kurrant.app.public_api.mapper.DailyReport.OrderItemDailyFoodDailyReportMapper;
import co.kurrant.app.public_api.model.SecurityUser;
import co.kurrant.app.public_api.service.DailyReportService;
import co.kurrant.app.public_api.util.UserUtil;
import exception.ApiException;
import exception.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl implements DailyReportService {
    private final UserPreferenceMapper userPreferenceMapper;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserTasteTestDataRepository userTasteTestDataRepository;
    private final QUserPreferenceRepository qUserPreferenceRepository;
    private final FoodRepository foodRepository;
    private final UserSelectTestDataRepository userSelectTestDataRepository;
    private final UserSelectTestDataMapper userSelectTestDataMapper;
    private final PushAlarmHashRepository pushAlarmHashRepository;
    private final DailyReportMapper dailyReportMapper;
    private final DailyReportRepository dailyReportRepository;
    private final QDailyReportRepository qDailyReportRepository;
    private final OrderItemDailyFoodDailyReportMapper orderItemDailyFoodDailyReportMapper;
    private final UserUtil userUtil;
    private final DailyFoodRepository dailyFoodRepository;
    private final QOrderDailyFoodRepository qOrderDailyFoodRepository;
    @Override
    @Transactional
    public String userPreferenceSave(SecurityUser securityUser, UserPreferenceDto userPreferenceDto) {

        User user = userUtil.getUser(securityUser);

        List<UserPreference> preferenceList = userPreferenceRepository.findAllByUserId(user.getId());
        UserPreference userPreference = null;

        userPreference = userPreferenceMapper.toEntity(user, userPreferenceDto);
//            List<FoodTag> foodTags = userPreference.getFavoriteCountryFood();

//        foodTags = foodTags.stream()
//                .filter(v -> v.getCode().equals(1))
//                .toList();
//        userPreference.updateFavoriteCountryFood(foodTags);

        //기존에 있는 정보라면 수정
        if (!preferenceList.isEmpty()) {
            //삭제 후 저장
            qUserPreferenceRepository.deleteOthers(user.getId());
            userPreferenceRepository.save(userPreference);

            for (UserSelectTestDataDto selectData : userPreferenceDto.getUserSelectTestDataList()) {
                UserSelectTestData userSelectTestData = userSelectTestDataMapper.toEntity(selectData.getSelectedFoodId(), selectData.getUnselectedFoodId(), userPreference.getId(), userPreference.getUser());
                userSelectTestDataRepository.save(userSelectTestData);
            }

            return "기존 정보가 있어서 수정하였습니다.";
        }

        UserPreference saveResult = userPreferenceRepository.save(userPreference);
        if (saveResult.getId() == null) {
            return "유저 정보 저장에 실패했습니다.";
        }

        for (UserSelectTestDataDto selectData : userPreferenceDto.getUserSelectTestDataList()) {
            UserSelectTestData userSelectTestData = userSelectTestDataMapper.toEntity(selectData.getSelectedFoodId(), selectData.getUnselectedFoodId(), saveResult.getId(), saveResult.getUser());
            userSelectTestDataRepository.save(userSelectTestData);
        }


        return "유저 정보 저장에 성공했습니다.";

    }

    @Override
    public Object getCountry() {
        List<String> countryList = new ArrayList<>();
        for (int i = 1; i < Country.values().length + 1; i++) {
            countryList.add(Country.ofCodeByString(i));
        }
        return countryList;
    }

    @Override
    public Object getFavoriteCountryFoods(Integer code) {

        List<String> foodTagList = new ArrayList<>();
        //code가 1이면 알러지 정보 반환
        if (code == 1) {
            List<FoodTag> tagList = Arrays.stream(FoodTag.values())
                    .filter(v -> v.getCategory().equals("알레르기 체크"))
                    .toList();

            for (FoodTag tag : tagList) {
                foodTagList.add(tag.getTag());
            }

            return foodTagList;
        }

        //1이 아닐경우는 좋아하는 나라 음식 목록 반환
        List<FoodTag> countryList = Arrays.stream(FoodTag.values())
                .filter(v -> v.getCategory().equals("국가"))
                .toList();

        for (FoodTag countryTag : countryList) {
            foodTagList.add(countryTag.getTag());
        }

        return foodTagList;
    }

    @Override
    public Object getJobType(Integer category, String code) {
        //묶여있는 직종의 코드까지 같이 보내주기 위해 맵으로 된 목록 생성
        List<Map<String, String>> jobTypeResultList = new ArrayList<>();
        Map<String, String> jobTypeMap = new HashMap<>();

        if (category == 1) {
            //코드가 1이라면 상세 직종을 반환
            List<JobType> jobTypeList = Arrays.stream(JobType.values())
                    .filter(v -> v.getCategory().equals(code))
                    .toList();
            //상세 직종을 반환할 목록 생성
            List<String> jobTypeDetailList = new ArrayList<>();
            for (JobType jobType : jobTypeList) {
                jobTypeDetailList.add(jobType.getName());
            }

            return jobTypeDetailList;
        }

        List<JobType> jobTypeList = Arrays.stream(JobType.values())
                .filter(v -> v.getCategory().equals("묶음"))
                .toList();
        //이름과 코드를 같이 보내준다.
        for (JobType jobType : jobTypeList) {
            jobTypeMap.put(jobType.getCode().toString(), jobType.getName());
        }
        jobTypeResultList.add(jobTypeMap);

        return jobTypeResultList;
    }

    @Override
    public Object getFoodImage(List<BigInteger> foodIds) {
        //값을 저장해줄 LIST 생성
        List<UserPreferenceFoodImageResponseDto> resultList = new ArrayList<>();

        for (BigInteger foodId : foodIds) {

            UserPreferenceFoodImageResponseDto responseDto = new UserPreferenceFoodImageResponseDto();
            //유효한 FoodId 인지 검증
            Food food = foodRepository.findById(foodId)
                    .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));
            //이미지는 1번째 이미지로 일괄처리
            String imageLocation = food.getImages().get(0).getLocation();

            //DTO 설정 후 담아주기
            responseDto.setFoodId(food.getId());
            responseDto.setImageUrl(imageLocation);

            resultList.add(responseDto);

        }

        return resultList;
    }

    @Override
    @Transactional
    public Object getTestData() {

        List<UserTestDataDto> userTestDataList = new ArrayList<>();
        //테스트데이터 조회
        List<UserTasteTestData> userTasteTestDataList = userTasteTestDataRepository.findAll();
        for (UserTasteTestData testData : userTasteTestDataList) {
            UserTestDataDto userTestData = new UserTestDataDto();
            Map<BigInteger, String> foodImageMap = new HashMap<>();
            List<String> stringList = Arrays.stream(testData.getFoodIds().split(",")).toList();
            for (String id : stringList) {
                String foodId = id.replace(" ", "");
                //food 조회
                Food food = foodRepository.findById(BigInteger.valueOf(Long.parseLong(foodId)))
                        .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));
                //food의 imageUrl 가져오기
                if (food.getImages().size() != 0) {
                    String url = food.getImages().get(0).getLocation();
                    //id와 url을 같이 보내주기 위해 맵에 put
                    foodImageMap.put(food.getId(), url);
                } else {
                    System.out.println(food.getId() + "  : foodId");
                    throw new ApiException(ExceptionEnum.NOT_FOUND_FOOD_IMAGE);
                }
            }
            userTestData.setId(testData.getId());
            userTestData.setPage(testData.getPage());
            userTestData.setFoodIds(foodImageMap);
            userTestDataList.add(userTestData);
        }

        return userTestDataList;
    }

    @Override
    public Boolean userPreferenceCheck(SecurityUser securityUser) {
        User user = userUtil.getUser(securityUser);
        List<UserPreference> userPreferences = userPreferenceRepository.findAllByUserId(user.getId());
        return !userPreferences.isEmpty();
    }

    @Override
    public List<PushResponseDto> getAlarms(SecurityUser securityUser) {
        List<PushAlarmHash> pushAlarmHashes = pushAlarmHashRepository.findAllByUserIdOrderByCreatedDateTimeDesc(securityUser.getId());
        List<PushResponseDto> alarmResponseDtos = new ArrayList<>();
        for (PushAlarmHash pushAlarmHash : pushAlarmHashes) {
            alarmResponseDtos.add(new PushResponseDto(pushAlarmHash));
        }
        return alarmResponseDtos;
    }

    @Override
    @Transactional
    public void insertMyFood(SecurityUser securityUser, SaveDailyReportDto saveDailyReportDto) {
        User user = userUtil.getUser(securityUser);

        String type = "user";
        String title = user.getName() + "님의 식사";
        DailyReport dailyReport = dailyReportMapper.toEntity(user, saveDailyReportDto, type, title);

        DailyReport saved = dailyReportRepository.save(dailyReport);
        if (saved.getId() == null) {
            throw new ApiException(ExceptionEnum.SAVE_FAILED);
        }

    }

    @Override
    public Object getReport(SecurityUser securityUser, String date) {
        List<FindDailyReportResDto> resDtoArrayList = new ArrayList<>();
        DailyReportResDto result = new DailyReportResDto();

        User user = userUtil.getUser(securityUser);

        List<DailyReport> dailyReportList = qDailyReportRepository.findByUserIdAndDate(user.getId(), date);

        if (dailyReportList.isEmpty()) {
            result.setTotalProtein(0);
            result.setTotalFat(0);
            result.setTotalCarbohydrate(0);
            result.setTotalCalorie(0);
            result.setDailyReportResDtoList(resDtoArrayList);
            return result;
        }

        for (DailyReport dailyReport : dailyReportList) {
            FindDailyReportResDto findDailyReportDto = dailyReportMapper.toFindDailyReportDto(dailyReport);
            resDtoArrayList.add(findDailyReportDto);
        }
        result.setDailyReportResDtoList(resDtoArrayList);
        int n = 0;
        result.setTotalCalorie(resDtoArrayList.stream().map(v -> Math.addExact(n, v.getCalorie())).mapToInt(Integer::intValue).sum());
        result.setTotalCarbohydrate(resDtoArrayList.stream().map(v -> Math.addExact(n, v.getCarbohydrate())).mapToInt(Integer::intValue).sum());
        result.setTotalFat(resDtoArrayList.stream().map(v -> Math.addExact(n, v.getFat())).mapToInt(Integer::intValue).sum());
        result.setTotalProtein(resDtoArrayList.stream().map(v -> Math.addExact(n, v.getProtein())).mapToInt(Integer::intValue).sum());

        return result;
    }

    @Override
    @Transactional
    public void saveDailyReportFood(SecurityUser securityUser, SaveDailyReportFoodReqDto dto) {
        User user = userUtil.getUser(securityUser);

        //해당 날짜에 주문한 내역을 불러오기
        List<OrderItemDailyFood> orderItemDailyFoodList = qOrderDailyFoodRepository.findAllUserIdAndDate(user.getId(), LocalDate.parse(dto.getStartDate()), LocalDate.parse(dto.getEndDate()));

        List<DailyReport> dailyReports = qDailyReportRepository.findAllByUserId(user.getId());

        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
            //기존 등록된 dailyReport는 중복되지 않도록 처리
            if (dailyReports.stream().anyMatch(v -> v.getFoodName().equals(orderItemDailyFood.getDailyFood().getFood().getName())) &&
                    dailyReports.stream().anyMatch(v -> v.getEatDate().equals(orderItemDailyFood.getDailyFood().getServiceDate()))
            ){
                continue;
            }

            //매핑 후 저장
            String imageLocation = null;
            if (!orderItemDailyFood.getDailyFood().getFood().getImages().isEmpty()) {
                imageLocation = orderItemDailyFood.getDailyFood().getFood().getImages().get(0).getLocation();
            }
            OrderItemDailyFoodToDailyReportDto dailyReportDto = orderItemDailyFoodDailyReportMapper.toDailyReportDto(orderItemDailyFood, imageLocation);
            DailyReport dailyReport = dailyReportMapper.toEntityByOrderItemDailyFood(user, dailyReportDto, "order");
            dailyReportRepository.save(dailyReport);
        }
    }

    @Override
    @Transactional
    public String deleteReport(SecurityUser securityUser, BigInteger reportId) {
        User user = userUtil.getUser(securityUser);

        long deleteResult = qDailyReportRepository.deleteReport(user.getId(), reportId);
        if (deleteResult == 0) return "제거에 실패헸습니다.";

        return "제거에 성공했습니다.";
    }

    @Override
    @Transactional
    public Object getOrderByDateAndDiningType(SecurityUser securityUser, String date, Integer diningType) {
        List<OrderByDateAndDiningTypeResDto> resultList = new ArrayList<>();

        User user = userUtil.getUser(securityUser);

        List<OrderItemDailyFood> orderItemDailyFoodList = qOrderDailyFoodRepository.findAllByDateAndDiningType(user.getId(), date, diningType);

        if (orderItemDailyFoodList.isEmpty()) return resultList;

        List<String> dailyReportList = qDailyReportRepository.findByUserIdAndDateToString(user.getId(), date);


        for (OrderItemDailyFood orderItemDailyFood : orderItemDailyFoodList) {
            boolean isDuplicated = false;

            if (dailyReportList.contains(orderItemDailyFood.getDailyFood().getFood().getName()) &&
                orderItemDailyFood.getDailyFood().getServiceDate().toString().equals(date)) {
                isDuplicated = true;
            }

            String spotName = orderItemDailyFood.getDailyFood().getGroup().getName();
            String location = null;
            if (!orderItemDailyFood.getDailyFood().getFood().getImages().isEmpty())
                location = orderItemDailyFood.getDailyFood().getFood().getImages().get(0).getLocation();
            OrderByDateAndDiningTypeResDto orderByDateDto = orderItemDailyFoodDailyReportMapper.toOrderByDateDto(orderItemDailyFood, location, spotName, isDuplicated);
            resultList.add(orderByDateDto);
        }

        return resultList;
    }

    @Override
    public void allChangeAlarmSetting(SecurityUser securityUser, Boolean isActive) {
        User user = userUtil.getUser(securityUser);

        List<PushCondition> pushConditionList = List.of(PushCondition.class.getEnumConstants());

        user.updatePushCondition(pushConditionList);
    }

    @Override
    public MealHistoryResDto getMealHistory(SecurityUser securityUser, String startDate, String endDate) {

        MealHistoryResDto mealHistoryResDto = new MealHistoryResDto();
        User user = userUtil.getUser(securityUser);

        List<DailyReportByDate> dailyReportList = qDailyReportRepository.findByUserIdAndDateBetween(user.getId(), LocalDate.parse(startDate), LocalDate.parse(endDate));

        if (dailyReportList.isEmpty()) return mealHistoryResDto;

        mealHistoryResDto.setDailyReportList(dailyReportList.stream().filter(v -> !v.getCalorie().equals(0)).toList());

        return mealHistoryResDto;

    }

    @Override
    @Transactional
    public void saveDailyReport(SecurityUser securityUser, SaveDailyReportReqDto saveDailyReportDto) {

        User user = userUtil.getUser(securityUser);
        DailyFood dailyFood = dailyFoodRepository.findById(saveDailyReportDto.getDailyFoodId()).orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_FOOD));

        SaveDailyReportDto dailyReportDto = generatedSaveDailyReportDto(dailyFood);
        if (!dailyFood.getFood().getImages().isEmpty()) {
            dailyReportRepository.save(dailyReportMapper.toEntity(user, dailyReportDto, "add", dailyFood.getFood().getMakers().getName(), dailyFood.getFood().getImages().get(0).getLocation()));
        } else {
            dailyReportRepository.save(dailyReportMapper.toEntity(user, dailyReportDto, "add", dailyFood.getFood().getMakers().getName(), null));
        }

    }

    private SaveDailyReportDto generatedSaveDailyReportDto(DailyFood dailyFood) {
        return SaveDailyReportDto.builder()
                .fat(dailyFood.getFood().getFat() == null ? 0 : dailyFood.getFood().getFat())
                .carbohydrate(dailyFood.getFood().getCarbohydrate() == null ? 0 : dailyFood.getFood().getCarbohydrate())
                .protein(dailyFood.getFood().getProtein() == null ? 0 : dailyFood.getFood().getProtein())
                .calorie(dailyFood.getFood().getCalorie() == null ? 0 : dailyFood.getFood().getCalorie())
                .eatDate(dailyFood.getServiceDate().toString())
                .name(dailyFood.getFood().getName())
                .diningType(dailyFood.getDiningType().getCode())
                .build();
    }
}
