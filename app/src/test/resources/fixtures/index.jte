@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

<!doctype html>
<html lang="ru">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Page analyzer project.</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
          crossorigin="anonymous">

    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
            <div class="navbar navbar-expand-lg navbar-light bg-light">
                <ul class="navbar-nav">
                    <li class="nav-item active">
                        <a class="nav-link" href="/">Home</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${NamedRoutes.urlsPath()}">Added URLs</a>
                    </li>
                </ul>
            </div>
        </nav>

        @if(page != null && page.getFlash() != null)
            <div class="alert mb-3" role="alert">
                ${page.getFlash()}
            </div>
        <%--            <div class="alert alert-${page.getFlashType()}" role="alert">--%>
        <%--                ${page.getFlash()}--%>
        <%--            </div>--%>
        @endif
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
                             <tr>
                                 <td>
                                     3
                                 </td>
                                 <td>
                                     https://www.example.com
                                 </td>
                                 <td>

                                 </td>
                                 <td>
                                     200
                                 </td>
                             </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
`
)