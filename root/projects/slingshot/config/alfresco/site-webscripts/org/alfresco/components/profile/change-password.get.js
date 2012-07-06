// Widget instantiation metadata...
var usersConfig = config.scoped['Users']['users'],
    minPasswordLength = usersConfig.getChildValue('password-min-length');

model.webScriptWidgets = [];
var changePassword = {};
changePassword.name = "Alfresco.ChangePassword";
changePassword.provideMessages = true;
changePassword.provideOptions = true;
changePassword.options = {};
changePassword.options.minPasswordLength = minPasswordLength;
model.webScriptWidgets.push(changePassword);
