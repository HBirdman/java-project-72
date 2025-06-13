package hexlet.code.controller;

import hexlet.code.dto.urls.BuildUrlPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.validation.ValidationException;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

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
        UrlsPage page = new UrlsPage(UrlRepository.getEntities());
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));
        var page = new UrlPage(url);
        ctx.render("urls/show.jte", model("page", page));
    }
}
