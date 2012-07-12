// Widget instantiation metadata...
var usersConfig = config.scoped['Users']['users'],
    minPasswordLength = usersConfig.getChildValue('password-min-length');

model.widgets = [];
var changePassword = {};
changePassword.name = "Alfresco.ChangePassword";
changePassword.useMessages = true;
changePassword.useOptions = true;
changePassword.options = {};
changePassword.options.minPasswordLength = minPasswordLength;
model.widgets.push(changePassword);
