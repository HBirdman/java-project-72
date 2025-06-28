package hexlet.code.dto.urls;
import hexlet.code.dto.BasePage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BuildUrlPage extends BasePage {
    private String url;
}
