package com.project.ongojisik.domain.home.service;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.domain.home.dto.HomeFoodResponse;
import com.project.ongojisik.domain.home.dto.HomeResponse;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeService {

    private static final int RECOMMENDATION_COUNT = 3;
    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private final FoodRepository foodRepository;

    @Transactional(readOnly = true)
    public HomeResponse getTodayFoods() {
        List<Food> foods = foodRepository.findByFoodPictureIsNotNullAndFoodPictureNot("").stream()
                .sorted(Comparator.comparing(Food::getFoodId))
                .toList();
        if (foods.isEmpty()) {
            throw new APIException(ErrorCode.FOOD_NOT_FOUND);
        }

        int startIndex = calculateTodayIndex(foods.size());
        int recommendationCount = Math.min(RECOMMENDATION_COUNT, foods.size());
        List<HomeFoodResponse> todayFoods = java.util.stream.IntStream.range(0, recommendationCount)
                .mapToObj(index -> HomeFoodResponse.from(
                        index + 1,
                        foods.get((startIndex + index) % foods.size())
                ))
                .toList();

        return HomeResponse.from(todayFoods);
    }

    private int calculateTodayIndex(int foodCount) {
        return Math.floorMod(LocalDate.now(SERVICE_ZONE).toEpochDay(), foodCount);
    }
}
