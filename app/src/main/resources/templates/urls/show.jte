@import hexlet.code.dto.urls.UrlPage
@param UrlPage page

@template.layout.page(
    page = page,
    content = @`
        <div class="mb-3">
            <h1>Url:</h1>
            <h2>${page.getUrl().getName()}</h2>
        </div>

        <div class="mb-3">
            <h1>Добавлена:</h1>
            <h2>${String.valueOf(page.getUrl().getCreatedAt())}</h2>
        </div>
    `
)
