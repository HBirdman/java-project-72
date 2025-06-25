package hexlet.code.dto.urls;

import java.util.List;
import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UrlsPage extends BasePage {
    private List<Url> urls;
    private List<UrlCheck> urlChecks;
}
