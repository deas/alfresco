// Get the name of the user and place it in the model
model.firstname = person.properties.firstName;
model.lastname = person.properties.lastName;

// Get the visibility of the current user
model.visibility = kb.getUserVisibility(person.properties.userName);

