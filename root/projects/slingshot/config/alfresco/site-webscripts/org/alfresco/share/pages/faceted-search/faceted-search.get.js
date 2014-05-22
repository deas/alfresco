<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">

// Get Search sorting configuration from share-config
var sortConfig = config.scoped["Search"]["sorting"];

// Get the initial header services and widgets...
var services = getHeaderServices(),
    widgets = getHeaderModel(msg.get("faceted-search.page.title"));

// Scope the model IDs
var rootWidgetId = "FCTSRCH_";

// TODO: Currently commented out until we roll-out faceted search configuration...
// Insert a configuration page link if the user has the appropriate permissions...
// if (_processedUserData.groups["GROUP_ALFRESCO_ADMINISTRATORS"] == true ||
//     _processedUserData.groups["GROUP_SEARCH_ADMINISTRATORS"] == true ||
//     _processedUserData.isNetworkAdmin == true)
// {
   var titleMenu = widgetUtils.findObject(widgets, "id", "HEADER_TITLE_MENU");
   var searchConfigMenuItem = {
      id: "FCTSRCH_CONFIG_PAGE_LINK",
      name: "alfresco/menus/AlfMenuBarItem",
      config: {
         label: "",
         title: msg.get("faceted-search.config.link"),
         iconAltText: msg.get("faceted-search.config.link"),
         iconClass: "alf-configure-icon",
         targetUrl: "dp/ws/faceted-search-config",
         renderFilter: [
            {
               target: "groupMemberships",
               property: "GROUP_ALFRESCO_ADMINISTRATORS",
               values: [true]
            }
         ]
      }
   };
   titleMenu.config.widgets.push(searchConfigMenuItem);
// }

// Accessibility menu
var accessMenu = {
   id: "FCTSRCH_ACCESSIBILITY_MENU",
   name: "alfresco/accessibility/AccessibilityMenu",
   config: {
      titleMsg: msg.get("faceted-search.access-key.title"),
      menu: [
         {url: "#" + "FCTSRCH_SEARCH_FORM", key: "f", msg: msg.get("faceted-search.access-key.search-form")},
         {url: "#" + "FCTSRCH_SEARCH_RESULTS_LIST", key: "r", msg: msg.get("faceted-search.access-key.search-results-list")},
         {url: "#" + "FCTSRCH_FACET_MENU", key: "q", msg: msg.get("faceted-search.access-key.facet-menu")},
         {url: "#" + "FCTSRCH_SORT_MENU", key: "m", msg: msg.get("faceted-search.access-key.sort-menu")}
      ]
   }
};

// Compose the search form model
var searchForm = {
   id: "FCTSRCH_SEARCH_FORM",
   name: "alfresco/forms/SingleTextFieldForm",
   config: {
      useHash: true,
      okButtonLabel: msg.get("faceted-search.search-form.ok-button-label"),
      okButtonPublishTopic : "ALF_SET_SEARCH_TERM",
      okButtonPublishGlobal: true,
      okButtonIconClass: "alf-white-search-icon",
      okButtonClass: "call-to-action",
      textFieldName: "searchTerm",
      textBoxIconClass: "alf-search-icon",
      textBoxCssClasses: "long"
   }
};

// TODO: The following code describes two different visibilityConfig behaviours. Initially they were bundled together 
// but it was found that this did not work as each rule fires independently. One rule would apply a condition and then
// the other would overrule it. A workaround was found within this example, but a more solid solution might be to
// create a multiple topic listener service that would gather payloads from configured topic publishes, concatenate
// them and then re-publish the compound payload on a new topic.

// Compose the zero results configuration
var hideOnZeroResultsConfig = {
   initialValue: false,
   rules: [
      {
         topic: "ALF_SEARCH_RESULTS_COUNT",
         attribute: "count",
         isNot: [0]
      }
   ]
};

//Compose the not sortable configuration
var hideOnNotSortableConfig = {
   initialValue: true,
   rules: [
      {
         topic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
         attribute: "sortable",
         is: [true]
      }
   ]
};

// Compose the facet menu column
var sideBarMenu = {
   id: "FCTSRCH_FACET_MENU",
   name: "alfresco/layout/LeftAndRight",
   config: {
      visibilityConfig: hideOnZeroResultsConfig,
      widgets: [
         {
            name: "alfresco/html/Label",
            align: "left",
            config: {
               label: msg.get("faceted-search.facet-menu.instruction")
            }
         }
      ]
   }
};

// Compose the individual facets
var facets = [
   {
      id: "FCTSRCH_FACET_FORMATS",
      name: "alfresco/search/FacetFilters",
      config: {
         label: msg.get("faceted-search.facet-menu.facet.formats"),
         facetQName: "{http://www.alfresco.org/model/content/1.0}content.mimetype",
         sortBy: "DESCENDING",
         maxFilters: 6,
         useHash: true
      }
   },
   {
      id: "FCTSRCH_FACET_CREATOR",
      name: "alfresco/search/FacetFilters",
      config: {
         label: msg.get("faceted-search.facet-menu.facet.creator"),
         facetQName: "{http://www.alfresco.org/model/content/1.0}creator.__.u",
         sortBy: "ALPHABETICALLY",
         maxFilters: 6,
         useHash: true
      }
   },
   {
      id: "FCTSRCH_FACET_MODIFIER",
      name: "alfresco/search/FacetFilters",
      config: {
         label: msg.get("faceted-search.facet-menu.facet.modifier"),
         facetQName: "{http://www.alfresco.org/model/content/1.0}modifier.__.u",
         sortBy: "ALPHABETICALLY",
         maxFilters: 6,
         useHash: true
      }
   },
   {
      id: "FCTSRCH_FACET_CREATED",
      name: "alfresco/search/FacetFilters",
      config: {
         label: msg.get("faceted-search.facet-menu.facet.created"),
         facetQName: "{http://www.alfresco.org/model/content/1.0}created",
         blockIncludeFacetRequest: true,
         sortBy: "INDEX",
         maxFilters: 6,
         useHash: true
      }
   },
   {
      id: "FCTSRCH_FACET_MODIFIED",
      name: "alfresco/search/FacetFilters",
      config: {
         label: msg.get("faceted-search.facet-menu.facet.modified"),
         facetQName: "{http://www.alfresco.org/model/content/1.0}modified",
         blockIncludeFacetRequest: true,
         sortBy: "INDEX",
         maxFilters: 6,
         useHash: true
      }
   },
   {
      id: "FCTSRCH_FACET_DESCRIPTION",
      name: "alfresco/search/FacetFilters",
      config: {
         label: msg.get("faceted-search.facet-menu.facet.description"),
         facetQName: "{http://www.alfresco.org/model/content/1.0}description.__",
         sortBy: "DESCENDING",
         hitThreshold: 1,
         minFilterValueLength: 5,
         maxFilters: 6,
         useHash: true
      }
   },
   {
      id: "FCTSRCH_FACET_SIZE",
      name: "alfresco/search/FacetFilters",
      config: {
         label: msg.get("faceted-search.facet-menu.facet.size"),
         facetQName: "{http://www.alfresco.org/model/content/1.0}content.size",
         blockIncludeFacetRequest: true,
         sortBy: "INDEX",
         maxFilters: 6,
         useHash: true
      }
   }
];

// Function to compose the sort fields from share-config
function getSortFieldsFromConfig()
{
   // Get sort fields element from the configuration
   var configSortFields = sortConfig.getChildren();

   // Initialise the sort fields array
   var sortFields = new Array(configSortFields.length);

   // Iterate over configuration sort fields
   for(var i=0; i < configSortFields.size(); i+=1)
   {
      // Extract sort properties from configuration
      var configSortField = configSortFields.get(i),
          label = String(configSortField.attributes["labelId"]),
          sortable = String(configSortField.attributes["isSortable"]) == "true" ? true : false,
          valueTokens = String(configSortField.value).split("|"),
          value = valueTokens[0],
          direction = "ascending",
          checked = (i==0 ? true : false);

      // The value may contain 2 pieces of data - the optional 2nd is for sort direction
      if(valueTokens instanceof Array && valueTokens.length > 1 && valueTokens[1] === "false")
      {
         direction = "descending";
      }

      // Create a new sort widget
      var labelMsg = msg.get(label);
      var sort = {
         name: "alfresco/menus/AlfCheckableMenuItem",
         config: {
            label: labelMsg,
            title: msg.get("faceted-search.sort-by.title", [labelMsg]),
            value: value,
            group: "DOCUMENT_LIBRARY_SORT_FIELD",
            publishTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
            checked: checked,
            publishPayload: {
               label: msg.get(label),
               direction: direction,
               sortable: sortable
            }
         }
      };

      // Add to the sortFields array
      sortFields[i] = sort;
   }

   return sortFields;
}

// Compose the sort menu
var sortMenu = {
   id: "FCTSRCH_SORT_MENU",
   name: "alfresco/menus/AlfMenuBarSelect",
   config: {
      title: msg.get("faceted-search.sort-field.title"),
      visibilityConfig: hideOnZeroResultsConfig,
      selectionTopic: "ALF_DOCLIST_SORT_FIELD_SELECTION",
      widgets: [
         {
            id: "DOCLIB_SORT_FIELD_SELECT_GROUP",
            name: "alfresco/menus/AlfMenuGroup",
            config: {
               widgets: getSortFieldsFromConfig()
            }
         }
      ]
   }
};

// Compose result menu bar
var searchResultsMenuBar = {
   id: "FCTSRCH_RESULTS_MENU_BAR",
   name: "alfresco/layout/LeftAndRight",
   config: {
      widgets: [
         {
            name: "alfresco/html/Label",
            align: "left",
            config: {
               label: msg.get("faceted-search.results-menu.no"),
               additionalCssClasses: "bold",
               subscriptionTopic: "ALF_SEARCH_RESULTS_COUNT"
            }
         },
         {
            name: "alfresco/html/Label",
            align: "left",
            config: {
               label: msg.get("faceted-search.results-menu.results-found")
            }
         },
         {
            name: "alfresco/menus/AlfMenuBar",
            align: "right",
            config: {
               visibilityConfig: hideOnZeroResultsConfig,
               widgets: [
                  {
                     id: "FCTSRCH_SORT_ORDER_TOGGLE",
                     name: "alfresco/menus/AlfMenuBarToggle",
                     config: {
                        visibilityConfig: hideOnNotSortableConfig,
                        checked: true,
                        onConfig: {
                           title: msg.get("faceted-search.sort-order-desc.title"),
                           iconClass: "alf-sort-ascending-icon",
                           iconAltText: msg.get("faceted-search.sorted-as-asc.title"),
                           publishTopic: "ALF_DOCLIST_SORT",
                           publishPayload: {
                              direction: "ascending"
                           }
                        },
                        offConfig: {
                           title: msg.get("faceted-search.sort-order-asc.title"),
                           iconClass: "alf-sort-descending-icon",
                           iconAltText: msg.get("faceted-search.sorted-as-desc.title"),
                           publishTopic: "ALF_DOCLIST_SORT",
                           publishPayload: {
                              direction: "descending"
                           }
                        }
                     }
                  },
                  sortMenu
               ]
            }
         }
      ]
   }
};

// Build the searchDocLib model
var searchDocLib = {
   id: "FCTSRCH_SEARCH_RESULTS_LIST",
   name: "alfresco/documentlibrary/AlfSearchList",
   config: {
      waitForPageWidgets: true,
      useHash: true,
      hashVarsForUpdate: [
         "searchTerm",
         "facetFilters",
         "sortField",
         "sortAscending",
         "allSites",
         "repo",
         "searchScope"
      ],
      selectedScope: "REPO",
      useInfiniteScroll: true,
      siteId: "$$site$$", // Get the current site context from the URL template (if set)
      rootNode: null,
      repo: true,
      widgets: [
//         {
//            id: "FCTSRCH_SEARCH_ADVICE_LANDING",
//            name: "alfresco/documentlibrary/views/AlfSearchListView",
//            config: {
//               searchAdviceTitle: "faceted-search.landing.title",
//               searchAdvice: [
//                  "faceted-search.landing.suggestion1",
//                  "faceted-search.landing.suggestion2",
//                  "faceted-search.landing.suggestion3"
//               ]
//            }
//         },
         {
            id: "FCTSRCH_SEARCH_ADVICE_NO_RESULTS",
            name: "alfresco/documentlibrary/views/AlfSearchListView",
            config: {
               searchAdviceTitle: "faceted-search.advice.title",
               searchAdvice: [
                  "faceted-search.advice.suggestion1",
                  "faceted-search.advice.suggestion2",
                  "faceted-search.advice.suggestion3"
               ]
            }
         },
         {
            name: "alfresco/documentlibrary/AlfDocumentListInfiniteScroll"
         }
      ]
   }
};

// Put all components together
var main = {
   name: "alfresco/layout/VerticalWidgets",
   config: {
      baseClass: "side-margins",
      widgets: [
         accessMenu,
         {
            name: "alfresco/html/Spacer",
            config: {
               height: "4px"
            }
         },
         searchForm,
         {
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               widgets: [
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     align: "sidebar",
                     widthPx: 340,
                     config: {
                        widgets: [
                           sideBarMenu
                        ]
                     }
                  },
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        additionalCssClasses: "bottom-border",
                        widgets: [
                           searchResultsMenuBar
                        ]
                     }
                  }
               ]
            }
         },
         {
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               widgets: [
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     align: "sidebar",
                     widthPx: 340,
                     config: {
                        visibilityConfig: hideOnZeroResultsConfig,
                        widgets: facets
                     }
                  },
                  {
                     name: "alfresco/layout/VerticalWidgets",
                     config: {
                        widgets: [
                           searchDocLib
                        ]
                     }
                  }
               ]
            }
         }
      ]
   }
};

// Add a checkable menu for switching between Repository, All Sites and current site as necessary...
// If we're in a site, make sure add in the site as an option in the menu 
// Always add in "All Sites" and "Repository" options...
// Cloud will need to remove the "Repository" option via an extension...
// Need links rather than drop-down?

// TODO: We need to set the site as being the selected if it is included as a hash argument (Surf doesn't yet provide this information)
var scopeOptions = [];
if (page.url.templateArgs.site != null)
{
   var siteData = getSiteData();
   scopeOptions.push({
      id: "FCTSRCH_SET_SPECIFIC_SITE_SCOPE",
      name: "alfresco/menus/AlfCheckableMenuItem",
      config: {
         label: siteData.profile.title,
         value: page.url.templateArgs.site,
         group: "SEARCHLIST_SCOPE",
         publishTopic: "ALF_SEARCHLIST_SCOPE_SELECTION",
         checked: false,
         publishPayload: {
            label: siteData.profile.title,
            value: page.url.templateArgs.site
         }
      }
   });
}

scopeOptions.push({
   id: "FCTSRCH_SET_ALL_SITES_SCOPE",
   name: "alfresco/menus/AlfCheckableMenuItem",
   config: {
      label: msg.get("faceted-search.scope.allSites"),
      value: "ALL_SITES",
      group: "SEARCHLIST_SCOPE",
      publishTopic: "ALF_SEARCHLIST_SCOPE_SELECTION",
      checked: false,
      publishPayload: {
         label: msg.get("faceted-search.scope.allSites"),
         value: "ALL_SITES"
      }
   }
});
scopeOptions.push({
   id: "FCTSRCH_SET_REPO_SCOPE",
   name: "alfresco/menus/AlfCheckableMenuItem",
   config: {
      label: msg.get("faceted-search.scope.repository"),
      value: "REPO",
      group: "SEARCHLIST_SCOPE",
      publishTopic: "ALF_SEARCHLIST_SCOPE_SELECTION",
      checked: true,
      publishPayload: {
         label: msg.get("faceted-search.scope.repository"),
         value: "REPO"
      }
   }
});

var scopeSelection = {
   id: "FCTSRCH_TOP_MENU_BAR",
   name: "alfresco/layout/LeftAndRight",
   config: {
      widgets: [
         {
            name: "alfresco/html/Label",
            config: {
               label: msg.get("faceted-search.scope.label")
            }
         },
         {
            name: "alfresco/menus/AlfMenuBar",
            config: {
               widgets: [
                  {
                     id: "FCTSRCH_SCOPE_SELECTION_MENU",
                     name: "alfresco/menus/AlfMenuBarSelect",
                     config: {
                        selectionTopic: "ALF_SEARCHLIST_SCOPE_SELECTION",
                        widgets: [
                           {
                              id: "FCTSRCH_SCOPE_SELECTION_MENU_GROUP",
                              name: "alfresco/menus/AlfMenuGroup",
                              config: {
                                 widgets: scopeOptions
                              }
                           }
                        ]
                     }
                  }
               ]
            }
         }
      ]
   }
};

main.config.widgets.splice(2, 0, scopeSelection);

// Append services with those required for search
services.push("alfresco/services/NavigationService",
              "alfresco/services/SearchService",
              "alfresco/services/ActionService",
              "alfresco/services/DocumentService",
              "alfresco/dialogs/AlfDialogService"
              );

// Add in the search form and search doc lib...
widgets.push(main);

// Push services and widgets into the getFooterModel to return with a sticky footer wrapper
model.jsonModel = getFooterModel(services, widgets);
model.jsonModel.groupMemberships = user.properties["alfUserGroups"];