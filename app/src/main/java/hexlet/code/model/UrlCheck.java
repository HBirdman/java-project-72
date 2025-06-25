package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UrlCheck {
    private Long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private Long urlId;
    private LocalDateTime createdAt;
}
