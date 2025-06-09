@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.urls.BuildUrlPage
@param BuildUrlPage page = null

@template.layout.page(
    page = page,
    content = @`
        @if(page != null && page.getErrors() != null)
            <div class="mb-3">
                <ul>
                    @for(var validator : page.getErrors().values())
                        @for(var error : validator)
                            <li>${error.getMessage()}</li>
                        @endfor
                    @endfor
                </ul>
            </div>
        @endif

        <div class="mx-auto p-4 py-md-5">
            <form action="${NamedRoutes.urlsPath()}" method="post">
                <div class="mb-3">
                    <label class="form-label">
                        Введите URL страницы
                        <input type="text" class="form-control" name="url" />
                    </label>
                </div>
            <input type="submit" class="btn btn-primary" value="Добавить в базу данных" />
            </form>
        </div>
    `
)