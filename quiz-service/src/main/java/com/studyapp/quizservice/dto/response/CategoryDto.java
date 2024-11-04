package com.studyapp.quizservice.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class CategoryDto {
    String title;
    String imageUrl;
}
