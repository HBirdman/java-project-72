package hexlet.code.controller;

import hexlet.code.dto.urls.BuildUrlPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.validation.ValidationException;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void add(Context ctx) throws Exception {
        String stringUrl = ctx.formParamAsClass("url", String.class).get();
        try {
            stringUrl = ctx.formParamAsClass("url", String.class)
                    .check(value -> value.startsWith("http") || value.startsWith("https"),
                            "Некорректный URL").get();
            URL url = new URI(stringUrl).toURL();
            String trimmedUrlAsString = url.getProtocol() + "://" + url.getHost()
                    + (url.getPort() > 0 ? ":" + url.getPort() : "");
            if (UrlRepository.getEntities().stream()
                    .anyMatch(site -> site.getName().equalsIgnoreCase(trimmedUrlAsString))) {
                throw new SQLException("Данный Url уже есть в базе");
            }
            Url trimmedUrl = new Url(trimmedUrlAsString);
            UrlRepository.save(trimmedUrl);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (ValidationException e) {
            BuildUrlPage page = new BuildUrlPage(stringUrl, e.getErrors());
            ctx.render("index.jte", model("page", page)).status(422);
        } catch (SQLException e) {
            BuildUrlPage page = new BuildUrlPage(stringUrl, null);
            page.setFlash(e.getMessage());
            ctx.render("index.jte", model("page", page)).status(422);
        }
    }

    public static void index(Context ctx) throws SQLException {
        UrlsPage page = new UrlsPage();
        List<Url> urls = UrlRepository.getEntities();
        List<UrlCheck> urlChecks = new ArrayList<>();
        for (var url : urls) {
            try {
                urlChecks.add(UrlCheckRepository.getEntities(url.getId()).getLast());
            } catch (NoSuchElementException e) {
                urlChecks.add(new UrlCheck());
            }
        }
        page.setUrls(urls);
        page.setUrlChecks(urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));
        var page = new UrlPage(url);
        List<UrlCheck> urlChecks = UrlCheckRepository.getEntities(id);
        page.setUrlChecks(urlChecks);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void check(Context ctx) throws SQLException {
        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));
        HttpResponse<String> stringHttpResponse = Unirest.get(url.getName()).asString();
        String body = stringHttpResponse.getBody();
        Document doc = Jsoup.parse(body);

        String title = doc.title();
        String h1 = doc.select("h1").text();
        String description = doc.select("meta[name=description]").attr("content");
        int statusCode = stringHttpResponse.getStatus();
        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setStatusCode(statusCode);
        urlCheck.setTitle(title);
        urlCheck.setH1(h1);
        urlCheck.setDescription(description);
        urlCheck.setUrlId(urlId);
        UrlCheckRepository.save(urlCheck);
        List<UrlCheck> urlChecks = UrlCheckRepository.getEntities(urlId);
        UrlPage page = new UrlPage(url);
        page.setUrlChecks(urlChecks);
        ctx.render("urls/show.jte", model("page", page));
    }
}
