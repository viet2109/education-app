package com.studyapp.quizservice.enums;

import com.studyapp.quizservice.dto.response.CategoryDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public enum Category {
    MATHEMATICS("Mathematics", generateUrl("Mathematics")),
    LITERATURE("Literature", generateUrl("Literature")),
    NATURAL_SCIENCES("Natural Sciences", generateUrl("Natural Sciences")),
    SOCIAL_SCIENCES("Social Sciences", generateUrl("Social Sciences")),
    FOREIGN_LANGUAGES("Foreign Languages", generateUrl("Foreign Languages")),
    INFORMATION_TECHNOLOGY("Information Technology", generateUrl("Information Technology")),
    ART("Art", generateUrl("Art")),
    ECONOMICS("Economics", generateUrl("Economics")),
    HEALTH("Health", generateUrl("Health")),
    SPORTS("Sports", generateUrl("Sports")),
    OTHERS("Others", generateUrl("Subject"));

    String title;
    String imageUrl;

    public static List<CategoryDto> getAll() {
        return Arrays.stream(Category.values()).map(category -> CategoryDto.builder().title(category.getTitle()).imageUrl(category.getImageUrl()).build()).toList(); // Trả về tất cả các giá trị của enum
    }

    // Helper method to generate URL based on the title
    private static String generateUrl(String title) {
        return String.format("https://firebasestorage.googleapis.com/v0/b/education-app-25fc8.appspot.com/o/%s.avif?alt=media", title);
    }

}
