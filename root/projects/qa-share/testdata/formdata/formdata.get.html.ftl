<html>
    <head>
        <title>Formdata test</title>
    </head>
    <body>
        <form action="${url.service}" method="post" enctype="multipart/form-data">
            <input type="file" name="file" />
            <input type="submit" value="Upload" />
        </form>
    </body>
</html>