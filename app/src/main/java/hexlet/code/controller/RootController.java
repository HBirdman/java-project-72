package hexlet.code.controller;

import hexlet.code.dto.urls.BuildUrlPage;
import io.javalin.http.Context;
import static io.javalin.rendering.template.TemplateUtil.model;

public class RootController {
    public static void index(Context ctx) {
        String flash = ctx.consumeSessionAttribute("flash");
        BuildUrlPage page = new BuildUrlPage();
        page.setFlash(flash);
        ctx.render("index.jte", model("page", page));
    }
}
