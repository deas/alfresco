// calculate if external auth is being used so we generate the appropriate download links
var conn = remote.connect("alfresco");
model.externalAuth = conn.getDescriptor().getExternalAuth();

// Repository Url
var repositoryUrl = null,
   repositoryConfig = config.scoped["DocumentLibrary"]["repository-url"];
if (repositoryConfig !== null)
{
   repositoryUrl = repositoryConfig.value;
}

model.repositoryUrl = repositoryUrl;