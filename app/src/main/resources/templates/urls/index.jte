@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.model.Url
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
                            @for(Url url : page.getUrls())
                                <tr>
                                    <td>
                                        ${url.getId()}
                                    </td>
                                    <td>
                                        <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
                                    </td>
                                    @if(page.getCheckMap().containsKey(url.getId()))
                                            <td>
                                                ${String.valueOf(page.getCheckMap().get(url.getId()).getCreatedAt())}
                                            </td>
                                            <td>
                                                ${page.getCheckMap().get(url.getId()).getStatusCode()}
                                            </td>
                                    @else
                                        <td>-</td>
                                        <td>-</td>
                                    @endif
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