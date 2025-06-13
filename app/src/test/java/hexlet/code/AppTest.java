package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.Objects;

public class AppTest {

    private Javalin app;

    @BeforeEach
    public final void setUp() throws SQLException {
        app = App.getApp();
    }

    @Test
    public void testAddWrongUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=htp://www.test.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(422);
            assert response.body() != null;
            assertThat(response.body().string()).doesNotContain("htp://www.test.com");
        });
    }

    @Test
    public void testAddCorrectUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://www.test.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("https://www.test.com");
        });
    }

    @Test
    public void testShow() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://www.test.com";
            client.post("/urls", requestBody);
            assertThat(Objects.requireNonNull(client.get("/urls/1").body()).string()).contains("https://www.test.com");
        });
    }
}
