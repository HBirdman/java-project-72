@import hexlet.code.dto.urls.UrlPage
@import hexlet.code.util.NamedRoutes
@param UrlPage page

@template.layout.page(
    page = page,
    content = @`
        <section>
            <div class="container mt-5">
                <h1>Сайт: ${page.getUrl().getName()}</h1>
                    <table class="table table-bordered">
                        <tbody>
                            <tr>
                                <td>ID</td>
                                <td>${page.getUrl().getId()}</td>
                            </tr>
                            <tr>
                                <td>Имя</td>
                                <td>${page.getUrl().getName()}</td>
                            </tr>
                            <tr>
                                <td>Добавлен:</td>
                                <td>${String.valueOf(page.getUrl().getCreatedAt())}</td>
                            </tr>
                        </tbody>
                    </table>
                <form method="post" action="${NamedRoutes.urlCheck(page.getUrl().getId())}">
                    <button type="submit" class="btn btn-primary">Проверить доступность сайта</button>
                </form>
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Код ответа</th>
                            <th>title</th>
                            <th>h1</th>
                            <th>description</th>
                            <th>Дата проверки</th>
                        </tr>
                    </thead>
                    <tbody>
                        @if(page.getUrlChecks() != null)
                            @for(var urlCheck : page.getUrlChecks())
                                <tr>
                                    <td>${urlCheck.getId()}</td>
                                    <td>${urlCheck.getStatusCode()}</td>
                                    <td>Пусто</td>
                                    <td>Пусто</td>
                                    <td>Пусто</td>
                                    <td>${String.valueOf(urlCheck.getCreatedAt())}</td>
                                </tr>
                            @endfor
                        @endif
                    </tbody>
                </table>
            </div>
        </section>
    `
)
