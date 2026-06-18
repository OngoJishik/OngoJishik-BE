package com.project.ongojisik.domain.bookmark.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.ongojisik.domain.analysis.entity.Food;
import com.project.ongojisik.domain.analysis.dto.FoodSummaryResponse;
import com.project.ongojisik.domain.analysis.repository.FoodRepository;
import com.project.ongojisik.domain.bookmark.dto.BookmarkListResponse;
import com.project.ongojisik.domain.bookmark.entity.Bookmark;
import com.project.ongojisik.domain.bookmark.repository.BookmarkRepository;
import com.project.ongojisik.domain.user.entity.User;
import com.project.ongojisik.domain.user.repository.UserRepository;
import com.project.ongojisik.global.exception.APIException;
import com.project.ongojisik.global.exception.ErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    private static final Long USER_ID = 1L;
    private static final String FOOD_ID = "011763";

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private UserRepository userRepository;

    private BookmarkService bookmarkService;

    @BeforeEach
    void setUp() {
        bookmarkService = new BookmarkService(bookmarkRepository, foodRepository, userRepository);
    }

    @Test
    void addBookmarkCreatesBookmark() {
        User user = createUser();
        Food food = createFood();
        Bookmark bookmark = Bookmark.create(user, food);
        ReflectionTestUtils.setField(bookmark, "favoriteId", 100L);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(foodRepository.findById(FOOD_ID)).thenReturn(Optional.of(food));
        when(bookmarkRepository.existsByUserUserIdAndFoodFoodId(USER_ID, FOOD_ID)).thenReturn(false);
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);

        FoodSummaryResponse response = bookmarkService.addBookmark(USER_ID, FOOD_ID);

        assertThat(response.foodId()).isEqualTo(FOOD_ID);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void addBookmarkRejectsDuplicate() {
        User user = createUser();
        Food food = createFood();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(foodRepository.findById(FOOD_ID)).thenReturn(Optional.of(food));
        when(bookmarkRepository.existsByUserUserIdAndFoodFoodId(USER_ID, FOOD_ID)).thenReturn(true);

        assertThatThrownBy(() -> bookmarkService.addBookmark(USER_ID, FOOD_ID))
                .isInstanceOf(APIException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.BOOKMARK_ALREADY_EXISTS);
    }

    @Test
    void deleteBookmarkDeletesOwnedBookmark() {
        Bookmark bookmark = Bookmark.create(createUser(), createFood());
        when(bookmarkRepository.findByUserUserIdAndFoodFoodId(USER_ID, FOOD_ID))
                .thenReturn(Optional.of(bookmark));

        bookmarkService.deleteBookmark(USER_ID, FOOD_ID);

        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    void getBookmarksReturnsUserBookmarks() {
        Bookmark bookmark = Bookmark.create(createUser(), createFood());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(createUser()));
        when(bookmarkRepository.findByUserUserIdOrderByCreatedAtDesc(USER_ID))
                .thenReturn(List.of(bookmark));

        BookmarkListResponse response = bookmarkService.getBookmarks(USER_ID);

        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.bookmarks()).hasSize(1);
        assertThat(response.bookmarks().get(0).foodId()).isEqualTo(FOOD_ID);
        assertThat(response.bookmarks().get(0).features()).containsExactly("feature-a", "feature-b");
        assertThat(response.bookmarks().get(0).foodPicture()).isEqualTo("image-url");
    }

    private User createUser() {
        User user = User.create("google-123", "user@example.com", "tester");
        ReflectionTestUtils.setField(user, "userId", USER_ID);
        return user;
    }

    private Food createFood() {
        Food food = new Food();
        ReflectionTestUtils.setField(food, "foodId", FOOD_ID);
        ReflectionTestUtils.setField(food, "foodName", "food");
        ReflectionTestUtils.setField(food, "category", "category");
        ReflectionTestUtils.setField(food, "foodFeatures", "feature-a, feature-b");
        ReflectionTestUtils.setField(food, "foodPicture", "image-url");
        return food;
    }
}
