<form action="${url.context}/search" method="get">
    <fieldset class="search-fieldset">
        <input type="hidden" value="${sectionId}" name="sectionId" />
        <input type="text" class="search-input" value="${phrase!'search'}" name="phrase" />
        <input type="submit" value="" class="input-arrow" />
    </fieldset>
</form>
