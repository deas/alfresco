// Repository Url
var repositoryUrl = null,
   repositoryConfig = config.scoped["DocumentLibrary"]["repository-url"];
if (repositoryConfig !== null)
{
   repositoryUrl = repositoryConfig.value;
}

model.repositoryUrl = repositoryUrl;