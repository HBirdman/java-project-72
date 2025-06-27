package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AppTest {

    private static Javalin app;
    public static MockWebServer mockServer;

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath().normalize();
    }

    private static String readFixture(String fileName) throws Exception {
        var path = getFixturePath(fileName);
        return Files.readString(path).trim();
    }

    @BeforeAll
    public static void setUp() throws Exception {
        mockServer = new MockWebServer();
        MockResponse mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(readFixture("index.jte"));
        mockServer.enqueue(mockResponse);
        mockServer.start();
    }

    @BeforeEach
    public final void initialize() throws SQLException {
        app = App.getApp();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockServer.shutdown();
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
    public void testIndex() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://www.test.com";
            client.post("/urls", requestBody);
            assertThat(Objects.requireNonNull(client.get("/urls/1").body()).string()).contains("https://www.test.com");
        });
    }

    @Test
    public void testUrlCheck() throws SQLException {
        String mockUrl = mockServer.url("/").toString();
        Url url = new Url(mockUrl);
        UrlRepository.save(url);
        Long id = url.getId();

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(NamedRoutes.urlCheck(id));
            assertThat(response.code()).isEqualTo(200);

            List<UrlCheck> urlChecks = UrlCheckRepository.getEntities();
            assertThat(urlChecks).isNotEmpty();

            UrlCheck urlCheck = urlChecks.getFirst();
            assertThat(urlCheck.getStatusCode()).isEqualTo(200);
            assertThat(urlCheck.getTitle()).isEqualTo("Page analyzer project");
            assertThat(urlCheck.getH1()).isEqualTo("Список добавленных URL");
            assertThat(urlCheck.getDescription()).isEqualTo("Expected description");
            assertThat(urlCheck.getUrlId()).isEqualTo(id);
        });
    }
}
