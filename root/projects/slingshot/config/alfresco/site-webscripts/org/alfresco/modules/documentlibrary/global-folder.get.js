function main()
{
   var showRepositoryLink = false;
   if (config.scoped["RepositoryLibrary"] &&
       config.scoped["RepositoryLibrary"]["visible"])
   {
      showRepositoryLink = config.scoped["RepositoryLibrary"]["visible"].getValue();
   }
   model.showRepositoryLink = user.isAdmin || showRepositoryLink;
}

main();