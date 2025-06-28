package hexlet.code.controller;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.MalformedURLException;
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
        URI parsedUrl;
        try {
            parsedUrl = new URL(stringUrl).toURI();
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }
        String normalizedUrl = String
                .format(
                        "%s://%s%s",
                        parsedUrl.getScheme(),
                        parsedUrl.getHost(),
                        parsedUrl.getPort() == -1 ? "" : ":" + parsedUrl.getPort()
                )
                .toLowerCase();
        if (UrlRepository.findAnyMatch(normalizedUrl)) {
            ctx.sessionAttribute("flash", "Данный URL уже есть в базе данных");
            ctx.sessionAttribute("flash-type", "warning");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }
        Url url = new Url(normalizedUrl);
        UrlRepository.save(url);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect(NamedRoutes.urlsPath());

    }

    public static void index(Context ctx) throws SQLException {
        UrlsPage page = new UrlsPage();
        List<Url> urls = UrlRepository.getEntities();
        List<UrlCheck> urlChecks = UrlCheckRepository.getLatestChecks();
        Map<Long, UrlCheck> checkMap = new HashMap<>(Map.of());
        for (var check : urlChecks) {
            checkMap.put(check.getUrlId(), check);
        }
        page.setUrls(urls);
        page.setCheckMap(checkMap);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.findById(id)
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
        Url url = UrlRepository.findById(urlId)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));
        HttpResponse<String> stringHttpResponse = null;
        String body = "";
        int statusCode = 400;
        String title = "";
        String h1 = "";
        String description = "";
        try {
            stringHttpResponse = Unirest.get(url.getName()).asString();
            body = stringHttpResponse.getBody();
            statusCode = stringHttpResponse.getStatus();
            Document doc = Jsoup.parse(body);
            title = doc.select("title").text();
            h1 = doc.select("h1").text();
            description = doc.select("meta[name=description]").attr("content");
            UrlCheck urlCheck = new UrlCheck(statusCode, title, h1, description, urlId);
            UrlCheckRepository.save(urlCheck);
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Ошибка получения ответа от сайта");
            ctx.sessionAttribute("flash-type", "warning");
            ctx.redirect(NamedRoutes.urlPath(urlId));
            return;
        } catch (NullPointerException e) {
            ctx.sessionAttribute("flash", "Ошибка обработки ответа от сайта");
            ctx.sessionAttribute("flash-type", "warning");
            ctx.redirect(NamedRoutes.urlPath(urlId));
            return;
        }
        List<UrlCheck> urlChecks = UrlCheckRepository.find(urlId);
        UrlPage page = new UrlPage(url);
        page.setUrlChecks(urlChecks);
        ctx.render("urls/show.jte", model("page", page));
    }
}
