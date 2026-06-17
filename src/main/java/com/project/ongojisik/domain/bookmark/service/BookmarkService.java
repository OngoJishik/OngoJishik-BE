package com.project.ongojisik.domain.bookmark.service;

import com.project.ongojisik.domain.analysis.dto.FoodSummaryResponse;
import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.domain.bookmark.dto.BookmarkedRecipeResponse;
import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import com.project.ongojisik.domain.bookmark.repository.BookmarkRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;

    @Transactional
    public FoodSummaryResponse addBookmark(Long userId, String foodId) {
        User user = findUser(userId);
        Food food = findFood(foodId);

        if (bookmarkRepository.existsByUserUserIdAndFoodFoodId(userId, foodId)) {
            throw new APIException(ErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        return FoodSummaryResponse.from(bookmarkRepository.save(Bookmark.create(user, food)).getFood());
    }

    @Transactional
    public void deleteBookmark(Long userId, String foodId) {
        Bookmark bookmark = bookmarkRepository.findByUserUserIdAndFoodFoodId(userId, foodId)
                .orElseThrow(() -> new APIException(ErrorCode.BOOKMARK_NOT_FOUND));
        bookmarkRepository.delete(bookmark);
    }

    @Transactional(readOnly = true)
    public List<FoodSummaryResponse> getBookmarks(Long userId) {
        findUser(userId);
        return bookmarkRepository.findByUserUserIdOrderByCreatedAtDesc(userId).stream()
                .map(bookmark -> FoodSummaryResponse.from(bookmark.getFood()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookmarkedRecipeResponse> getBookmarkedRecipes(Long userId) {
        findUser(userId);
        return bookmarkRepository.findBookmarkedFoodsWithRecipe(userId).stream()
                .map(bookmark -> BookmarkedRecipeResponse.from(bookmark.getFood()))
                .toList();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));
    }

    private Food findFood(String foodId) {
        return foodRepository.findById(foodId)
                .orElseThrow(() -> new APIException(ErrorCode.FOOD_NOT_FOUND));
    }
}
