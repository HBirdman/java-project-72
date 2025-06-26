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
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void add(Context ctx) throws Exception {
        String stringUrl = ctx.formParamAsClass("url", String.class).get();
        try {
            stringUrl = ctx.formParamAsClass("url", String.class)
                    .check(value -> value.startsWith("http") || value.startsWith("https"),
                            "Некорректный URL").get();
        } catch (ValidationException e) {
            BuildUrlPage page = new BuildUrlPage(stringUrl, e.getErrors());
            ctx.render("index.jte", model("page", page)).status(422);
            return;
        }
        URL url = new URI(stringUrl).toURL();
        String trimmedUrlAsString = url.getProtocol() + "://" + url.getHost()
                + (url.getPort() > 0 ? ":" + url.getPort() : "");
        if (UrlRepository.getEntities().stream()
                .anyMatch(u -> u.getName().equalsIgnoreCase(trimmedUrlAsString))) {
            BuildUrlPage page = new BuildUrlPage(stringUrl, null);
            page.setFlash("Данный URL уже есть в базе данных");
            page.setFlashType("warning");
            ctx.render("index.jte", model("page", page)).status(422);
            return;
        }
        Url trimmedUrl = new Url(trimmedUrlAsString);
        UrlRepository.save(trimmedUrl);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect(NamedRoutes.urlsPath());

    }

    public static void index(Context ctx) throws SQLException {
        UrlsPage page = new UrlsPage();
        List<Url> urls = UrlRepository.getEntities();
        List<UrlCheck> urlChecks = UrlCheckRepository.getEntities();
        Map<Long, UrlCheck> checkMap = new HashMap<>(Map.of());
        for (var url : urls) {
            if (urlChecks.stream().anyMatch(u -> u.getUrlId().equals(url.getId()))) {
                checkMap.put(url.getId(), urlChecks.stream()
                        .filter(u -> u.getUrlId().equals(url.getId()))
                        .reduce((a, b) -> b).get());
            }
        }
        page.setUrls(urls);
        page.setCheckMap(checkMap);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));
        var page = new UrlPage(url);
        List<UrlCheck> urlChecks = UrlCheckRepository.find(id);
        page.setUrlChecks(urlChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void check(Context ctx) throws SQLException {
        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));
        HttpResponse<String> stringHttpResponse = null;
        try {
            stringHttpResponse = Unirest.get(url.getName()).asString();
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Ошибка получения ответа от сайта");
            ctx.sessionAttribute("flash-type", "warning");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        }
        String body = "";
        int statusCode = 400;
        try {
            body = stringHttpResponse.getBody();
            statusCode = stringHttpResponse.getStatus();
        } catch (NullPointerException e) {
            ctx.sessionAttribute("flash", "Ошибка парсинга ответа");
            ctx.sessionAttribute("flash-type", "warning");
            ctx.redirect(NamedRoutes.urlPath(urlId));
        }
        Document doc = Jsoup.parse(body);
        String title = doc.title();
        String h1 = doc.select("h1").text();
        String description = doc.select("meta[name=description]").attr("content");
        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setStatusCode(statusCode);
        urlCheck.setTitle(title);
        urlCheck.setH1(h1);
        urlCheck.setDescription(description);
        urlCheck.setUrlId(urlId);
        UrlCheckRepository.save(urlCheck);
        List<UrlCheck> urlChecks = UrlCheckRepository.find(urlId);
        UrlPage page = new UrlPage(url);
        page.setUrlChecks(urlChecks);
        ctx.render("urls/show.jte", model("page", page));
    }
}
