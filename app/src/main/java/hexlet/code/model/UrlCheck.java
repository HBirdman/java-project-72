package hexlet.code.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
public class UrlCheck {
    private Long id;
    private final int statusCode;
    private final String title;
    private final String h1;
    private final String description;
    private final Long urlId;
    private LocalDateTime createdAt;
}
