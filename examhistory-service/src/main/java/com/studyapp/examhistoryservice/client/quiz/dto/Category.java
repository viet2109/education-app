package com.studyapp.examhistoryservice.client.quiz.dto;

import lombok.RequiredArgsConstructor;

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

    private final String title;
    private final String imageUrl;


    // Helper method to generate URL based on the title
    private static String generateUrl(String title) {
        return String.format("https://firebasestorage.googleapis.com/v0/b/education-app-25fc8.appspot.com/o/%s.avif?alt=media", title);
    }

}
