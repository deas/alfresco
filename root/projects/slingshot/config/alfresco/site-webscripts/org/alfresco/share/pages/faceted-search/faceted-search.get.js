<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-footer.lib.js">

/* *********************************************************************************
 *                                                                                 *
 * GET ALL USER PREFERENCES                                                        *
 *                                                                                 *
 ***********************************************************************************/
function getUserPreferences() {
   var userPreferences = {};
   var prefs = JSON.parse(preferences.value);
   return prefs
}

var userPreferences = getUserPreferences();
var viewRendererName =  "simple";
try
{
   viewRendererName = userPreferences.org.alfresco.share.searchList.viewRendererName;
}
catch(e)
{
   // No action. Ignore when view preference hasn't been set-up
}

// Get Search sorting configuration from share-config
var sortConfig = config.scoped["Search"]["sorting"];

// Get the initial header services and widgets...
var services = getHeaderServices(),
    widgets = getHeaderModel(msg.get("faceted-search.page.title"));

// Scope the model IDs
var rootWidgetId = "FCTSRCH_";

// Insert a configuration page link if the user has the appropriate permissions...
if (_processedUserData.groups["GROUP_ALFRESCO_ADMINISTRATORS"] == true ||
    _processedUserData.groups["GROUP_SEARCH_ADMINISTRATORS"] == true ||
    _processedUserData.isNetworkAdmin == true)
{
   var titleMenu = widgetUtils.findObject(widgets, "id", "HEADER_TITLE_MENU");
   var searchConfigMenuItem = {
      id: "FCTSRCH_CONFIG_PAGE_LINK",
      name: "alfresco/menus/AlfMenuBarItem",
      config: {
         label: msg.get("faceted-search-config.label"),
         title: msg.get("faceted-search.config.link"),
         // iconAltText: msg.get("faceted-search.config.link"),
         // iconClass: "alf-configure-icon",
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
   titleMenu.config.widgets.splice(0, 0, searchConfigMenuItem);
}

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

// Headings
var headingForSearchForm = {
   name: "alfresco/html/Heading",
   config: {
      level: 2,
      label: msg.get("faceted-search.heading.search-form"),
      isHidden: true
   }
};
var headingForFacetMenu = {
   name: "alfresco/html/Heading",
   config: {
      level: 2,
      label: msg.get("faceted-search.heading.facet-menu"),
      isHidden: true
   }
};
var headingForSortMenu = {
   name: "alfresco/html/Heading",
   config: {
      level: 2,
      label: msg.get("faceted-search.heading.sort-menu"),
      isHidden: true
   }
};
var headingForResultsList = {
   name: "alfresco/html/Heading",
   config: {
      level: 2,
      label: msg.get("faceted-search.heading.search-results-list"),
      isHidden: true
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
      textBoxCssClasses: "long hiddenlabel",
      textBoxLabel: msg.get("faceted-search.search-form.search-field-label")
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

// Make a request to the Repository to get the configured facets to use in search...
var rawFacets = [];
var result = remote.call("/api/solr/facet-config");
if (result.status.code == status.STATUS_OK)
{
   rawFacets = JSON.parse(result).facets;
}

// Iterate over the list of facets and create an array of widgets for each one.
// Only the enableincludeFacetd facets will be included and if any are scoped this will be
// taken into account...
var facets = [];
rawFacets.forEach(function(facet, index, rawFacets) {

   if (facet.isEnabled === true)
   {
      var includeFacet = true;

      // If we're in the context of a site and there is site scoping defined for
      // the current facet then we need to check that the current site is within
      // the list of scoped sites...
      if (facet.scope === "SCOPED_SITES")
      {
         includeFacet = false;
         var siteId = page.url.templateArgs.site;
         if (siteId != null && facet.scopedSites != null && facet.scopedSites.length != null)
         {
            for (var i=0; i<facet.scopedSites.length; i++)
            {
               if (facet.scopedSites[i] === siteId)
               {
                  includeFacet = true;
                  break;
               }
            }
         }
      }

      // If the facet passes all scoping criteria then it should be included...
      if (includeFacet === true)
      {
         var blockIncludeFacetRequest = (facet.customProperties != null && 
                                         facet.customProperties.blockIncludeFacetRequest != null &&
                                         facet.customProperties.blockIncludeFacetRequest.value === "true");
         
         var facet = {
            id: "FCTSRCH_" + facet.filterID,
            name: facet.displayControl,
            config: {
               label: msg.get(facet.displayName),
               facetQName: facet.facetQName,
               sortBy: facet.sortBy,
               maxFilters: facet.maxFilters,
               hitThreshold: facet.hitThreshold,
               minFilterValueLength: facet.minFilterValueLength,
               useHash: false,
               headingLevel: 3,
               blockIncludeFacetRequest: blockIncludeFacetRequest
            }
         };
         facets.push(facet);
      }
   }
});

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
   name: "alfresco/documentlibrary/AlfToolbar",
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
         headingForSortMenu,
         {
            name: "alfresco/menus/AlfMenuBar",
            align: "right",
            id: "FCTSRCH_SEARCH_LIST_MENU_BAR",
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
                  sortMenu,
                  {
                     name: "alfresco/menus/AlfMenuBarPopup",
                     config: {
                        iconClass: "alf-configure-icon",
                        widgets: [
                           {
                              id: "DOCLIB_CONFIG_MENU_VIEW_SELECT_GROUP",
                              name: "alfresco/documentlibrary/AlfViewSelectionGroup"
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

// Build the searchDocLib model
var searchDocLib = {
   id: "FCTSRCH_SEARCH_RESULTS_LIST",
   name: "alfresco/documentlibrary/AlfSearchList",
   config: {
      viewPreferenceProperty: "org.alfresco.share.searchList.viewRendererName",
      view: viewRendererName,
      waitForPageWidgets: true,
      useHash: true,
      hashVarsForUpdate: [
         "searchTerm",
         "facetFilters",
         "sortField",
         "sortAscending",
         "allSites",
         "repo",
         "searchScope",
         "query"
      ],
      selectedScope: "REPO",
      useInfiniteScroll: true,
      siteId: null, // Don't set the siteId initially (we don't want to do a site search on first load)
      rootNode: null,
      repo: true,
      additionalControlsTarget: "FCTSRCH_RESULTS_MENU_BAR",
      widgets: [
         {
            id: "FCTSRCH_SEARCH_ADVICE_NO_RESULTS",
            name: "alfresco/documentlibrary/views/AlfSearchListView",
            config: {
               searchAdviceTitle: "faceted-search.advice.title",
               searchAdvice: [
                  "faceted-search.advice.suggestion1",
                  "faceted-search.advice.suggestion2",
                  "faceted-search.advice.suggestion3"
               ],
               a11yCaption: msg.get("faceted-search.results.caption"),
               a11yCaptionClass: "hiddenAccessible",
               widgetsForHeader: [
                  {
                     name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                     config: {
                        label: msg.get("faceted-search.results.heading.thumbnail"),
                        class: "hiddenAccessible",
                        a11yScope: "col"
                     }
                  },
                  {
                     name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                     config: {
                        label: msg.get("faceted-search.results.heading.details"),
                        class: "hiddenAccessible",
                        a11yScope: "col"
                     }
                  },
                  {
                     name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                     config: {
                        label: msg.get("faceted-search.results.heading.actions"),
                        class: "hiddenAccessible",
                        a11yScope: "col"
                     }
                  }
               ]
            }
         },
         {
            name: "alfresco/documentlibrary/views/AlfGalleryView",
            config: {
               showNextLink: true,
               nextLinkLabel: msg.get("faceted-search.show-more-results.label"),
               widgets: [
                  {
                     name: "alfresco/search/SearchGalleryThumbnail",
                     config: {
                        widgetsForSelectBar: [
                           {
                              name: "alfresco/renderers/MoreInfo",
                              align: "right",
                              config: {
                                 xhrRequired: true
                              }
                           }
                        ],
                        publishTopic: "ALF_NAVIGATE_TO_PAGE"
                     }
                  }
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
   id: "FCTSRCH_MAIN_VERTICAL_STACK",
   name: "alfresco/layout/VerticalWidgets",
   config: {
      baseClass: "side-margins",
      widgets: [
         {
            name: "alfresco/html/Spacer",
            config: {
               height: "4px"
            }
         },
         headingForSearchForm,
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
         headingForFacetMenu,
         {
            name: "alfresco/layout/HorizontalWidgets",
            config: {
               widgets: [
                  {
                     id: "FCTSRCH_SEARCH_FACET_LIST",
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
                           headingForResultsList,
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
              "alfresco/dialogs/AlfDialogService",
              "alfresco/services/PreferenceService");

// Add in the search form and search doc lib...
widgets.unshift(accessMenu);
widgets.push(main);

// Push services and widgets into the getFooterModel to return with a sticky footer wrapper
model.jsonModel = getFooterModel(services, widgets);
model.jsonModel.groupMemberships = user.properties["alfUserGroups"];