package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UrlPage extends BasePage {
    private Url url;
    private List<UrlCheck> urlChecks;

    public UrlPage(Url url) {
        this.url = url;
    }
}
