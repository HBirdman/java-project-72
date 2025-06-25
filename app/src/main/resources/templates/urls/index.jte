@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <div class="mx-auto p-4 py-md-5">
        <main>
            <section>
                <div class="container">
                    <h1>Список добавленных URL</h1>
                    <table class="table table-bordered table-hover mt-3">
                        <thead>
                            <td class="col-1">ID</td>
                            <td class="col-2">URL</td>
                            <td>Дата последней проверки</td>
                            <td>Код ответа</td>
                        </thead>
                        <tbody>
                            @for(int i = 0; i < page.getUrls().size(); i++)
                                    <tr>
                                        <td>
                                            ${page.getUrls().get(i).getId()}
                                        </td>
                                        <td>
                                            <a href="${NamedRoutes.urlPath(page.getUrls().get(i).getId())}">${page.getUrls().get(i).getName()}</a>
                                        </td>
                                        <td>
                                            ${String.valueOf(page.getUrlChecks().get(i).getCreatedAt())}
                                        </td>
                                        <td>
                                            ${page.getUrlChecks().get(i).getStatusCode()}
                                        </td>
                                    </tr>
                            @endfor
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
`
)