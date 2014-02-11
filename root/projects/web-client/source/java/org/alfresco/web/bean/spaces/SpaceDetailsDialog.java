/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.bean.spaces;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.model.ApplicationModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.servlet.GuestTemplateContentServlet;
import org.alfresco.web.bean.BaseDetailsBean;
import org.alfresco.web.bean.TemplateSupportBean;
import org.alfresco.web.bean.dialog.NavigationSupport;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.NodeListUtils;
import org.alfresco.web.ui.common.NodePropertyComparator;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.Utils.URLMode;
import org.alfresco.web.ui.common.component.UIActionLink;

/**
 * Backing bean provided access to the details of a Space
 * 
 * @author Kevin Roast
 */
public class SpaceDetailsDialog extends BaseDetailsBean implements NavigationSupport
{
   private static final long serialVersionUID = -6066782024875635443L;
   
   private static final String MSG_HAS_FOLLOWING_CATEGORIES = "has_following_categories_space";
   private static final String MSG_NO_CATEGORIES_APPLIED = "no_categories_applied_space";
   private static final String MSG_ERROR_ASPECT_CLASSIFY = "error_aspect_classify_space";
   private static final String MSG_DETAILS_OF = "details_of";
   private static final String MSG_LOCATION = "location";
   private final static String MSG_CLOSE = "close";
   private final static String MSG_LEFT_QUOTE = "left_qoute";
   private final static String MSG_RIGHT_QUOTE = "right_quote";
   
   /** RSS Template ID */
   private String rssTemplate;

   // ------------------------------------------------------------------------------
   // Construction
   
   /**
    * Default constructor
    */
   public SpaceDetailsDialog()
   {
      super();
      
      // initial state of some panels that don't use the default
      panels.put("rules-panel", false);
      panels.put("dashboard-panel", false);
   }
   
   
   // ------------------------------------------------------------------------------
   // Bean property getters and setters 
   
   /**
    * Returns the Node this bean is currently representing
    * 
    * @return The Node
    */
   public Node getNode()
   {
      return this.browseBean.getActionSpace();
   }
   
   /**
    * Returns the Space this bean is currently representing
    * 
    * @return The Space Node
    */
   public Node getSpace()
   {
      return getNode();
   }
   
   /**
    * Returns the URL to access the browse page for the current node
    * 
    * @return The bookmark URL
    */
   public String getBrowseUrl()
   {
      return Utils.generateURL(FacesContext.getCurrentInstance(), getNode(), URLMode.BROWSE);
   }
   
   /**
    * Resolve the actual document Node from any Link object that may be proxying it
    * 
    * @return current document Node or document Node resolved from any Link object
    */
   protected Node getLinkResolvedNode()
   {
      Node space = getSpace();
      if (ApplicationModel.TYPE_FOLDERLINK.equals(space.getType()))
      {
         NodeRef destRef = (NodeRef)space.getProperties().get(ContentModel.PROP_LINK_DESTINATION);
         if (getNodeService().exists(destRef))
         {
            space = new Node(destRef);
         }
      }
      return space;
   }
   
   /**
    * Returns a model for use by a template on the Space Details page.
    * 
    * @return model containing current current space info.
    */
   @SuppressWarnings("unchecked")
   public Map getTemplateModel()
   {
      HashMap model = new HashMap(1, 1.0f);

      model.put("space", getSpace().getNodeRef());
      model.put(TemplateService.KEY_IMAGE_RESOLVER, imageResolver);
      
      return model;
   }
   
   /**
    * @see org.alfresco.web.bean.BaseDetailsBean#getPropertiesPanelId()
    */
   protected String getPropertiesPanelId()
   {
      return "space-props";
   }
   
   
   // ------------------------------------------------------------------------------
   // Action event handlers
   
   /**
    * Navigates to next item in the list of Spaces
    */
   public void nextItem(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         NodeRef currNodeRef = new NodeRef(Repository.getStoreRef(), id);
         List<Node> nodes = this.browseBean.getParentNodes(currNodeRef);
         Node next = null;
         if (nodes.size() > 1)
         {
            String currentSortColumn = this.browseBean.getSpacesRichList().getCurrentSortColumn();
            if (currentSortColumn != null)
            {
                boolean currentSortDescending = this.browseBean.getSpacesRichList().isCurrentSortDescending();
                Collections.sort(nodes, new NodePropertyComparator(currentSortColumn, !currentSortDescending));
            }
            next = NodeListUtils.nextItem(nodes, id);
            this.browseBean.setupSpaceAction(next.getId(), false);
         }
         if (next == null)
         {
            Node currNode = new Node(currNodeRef);
            this.navigator.setupDispatchContext(currNode);
         }
      }
   }
   
   /**
    * Navigates to the previous item in the list Spaces
    */
   public void previousItem(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         NodeRef currNodeRef = new NodeRef(Repository.getStoreRef(), id);
         List<Node> nodes = this.browseBean.getParentNodes(currNodeRef);
         Node previous = null;
         if (nodes.size() > 1)
         {
            String currentSortColumn = this.browseBean.getSpacesRichList().getCurrentSortColumn();
            if (currentSortColumn != null)
            {
                boolean currentSortDescending = this.browseBean.getSpacesRichList().isCurrentSortDescending();
                Collections.sort(nodes, new NodePropertyComparator(currentSortColumn, !currentSortDescending));
            }
            previous = NodeListUtils.previousItem(nodes, id);
            this.browseBean.setupSpaceAction(previous.getId(), false);
         }
         if (previous == null)
         {
            Node currNode = new Node(currNodeRef);
            this.navigator.setupDispatchContext(currNode);
         }
      }
   }
   
   /**
    * Action handler to clear the current Space properties before returning to the browse screen,
    * as the user may have modified the properties! 
    */
   public String cancel()
   {
      this.navigator.resetCurrentNodeProperties();
      return super.cancel();
   }
   
   // ------------------------------------------------------------------------------
   // Categorised Details
   
   /**
    * Determines whether the current space has any categories applied
    * 
    * @return true if the document has categories attached
    */
   public boolean isCategorised()
   {
      return getSpace().hasAspect(ContentModel.ASPECT_GEN_CLASSIFIABLE);
   }
   
   /**
    * Returns a list of objects representing the categories applied to the 
    * current space
    *  
    * @return List of categories
    */
   public String getCategoriesOverviewHTML()
   {
      String html = null;
      
      if (isCategorised())
      {
         // we know for now that the general classifiable aspect only will be
         // applied so we can retrive the categories property direclty
         Collection<NodeRef> categories = (Collection<NodeRef>)getNodeService().getProperty(
                 getSpace().getNodeRef(), ContentModel.PROP_CATEGORIES);
         
         if (categories == null || categories.size() == 0)
         {
            html = Application.getMessage(FacesContext.getCurrentInstance(), MSG_NO_CATEGORIES_APPLIED);
         }
         else
         {
            StringBuilder builder = new StringBuilder(Application.getMessage(FacesContext.getCurrentInstance(), 
                  MSG_HAS_FOLLOWING_CATEGORIES));
            
            builder.append("<ul>");
            for (NodeRef ref : categories)
            {
               if (getNodeService().exists(ref))
               {
                  builder.append("<li>");
                  builder.append(Repository.getNameForNode(getNodeService(), ref));
                  builder.append("</li>");
               }
            }
            builder.append("</ul>");
            
            html = builder.toString();
         }
      }
      
      return html;
   }
   
   /**
    * Applies the classifiable aspect to the current document
    */
   public void applyClassifiable()
   {
      UserTransaction tx = null;
      
      try
      {
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // add the general classifiable aspect to the node
         getNodeService().addAspect(getSpace().getNodeRef(), ContentModel.ASPECT_GEN_CLASSIFIABLE, null);
         
         // commit the transaction
         tx.commit();
         
         // reset the state of the current document
         getSpace().reset();
      }
      catch (Throwable e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), MSG_ERROR_ASPECT_CLASSIFY), e.getMessage()), e);
      }
   }
   
   /**
    * Returns whether the current sapce is locked
    * 
    * @return true if the document is checked out
    */
   public boolean isLocked()
   {
      return getSpace().isLocked();
   }
   
   /**
    * @return true if the current space has an RSS feed applied
    */
   public boolean isRSSFeed()
   {
      return hasRSSFeed(getSpace());
   }
   
   /**
    * @return true if the current space has an RSS feed applied
    */
   public static boolean hasRSSFeed(Node space)
   {
      return (space.hasAspect(ApplicationModel.ASPECT_FEEDSOURCE) &&
              space.getProperties().get(ApplicationModel.PROP_FEEDTEMPLATE) != null);
   }
   
   /**
    * @return RSS Feed URL for the current space
    */
   public String getRSSFeedURL()
   {
      return buildRSSFeedURL(getSpace());
   }
   
   /**
    * Build URL for an RSS space based on the 'feedsource' aspect property.
    *  
    * @param space  Node to build RSS template URL for
    *  
    * @return URL for the RSS feed for a space
    */
   public static String buildRSSFeedURL(Node space)
   {
      // build RSS feed template URL from selected template and the space NodeRef and
      // add the guest=true URL parameter - this is required for no login access and
      // add the mimetype=text/xml URL parameter - required to return correct stream type
      return GuestTemplateContentServlet.generateURL(space.getNodeRef(),
                (NodeRef)space.getProperties().get(ApplicationModel.PROP_FEEDTEMPLATE))
                    + "/rss.xml?mimetype=text%2Fxml%3Bcharset=utf-8";
   }

   /**
    * @return Returns the current RSS Template ID.
    */
   public String getRSSTemplate()
   {
      // return current template if it exists
      NodeRef ref = (NodeRef)getNode().getProperties().get(ApplicationModel.PROP_FEEDTEMPLATE);
      return ref != null ? ref.getId() : this.rssTemplate;
   }

   /**
    * @param rssTemplate The RSS Template Id to set.
    */
   public void setRSSTemplate(String rssTemplate)
   {
      this.rssTemplate = rssTemplate;
   }
   
   /**
    * Action handler to apply the selected RSS Template and FeedSource aspect to the current Space
    */
   public void applyRSSTemplate(ActionEvent event)
   {
      if (this.rssTemplate != null && this.rssTemplate.equals(TemplateSupportBean.NO_SELECTION) == false)
      {
         try
         {
            // apply the feedsource aspect if required 
            if (getNode().hasAspect(ApplicationModel.ASPECT_FEEDSOURCE) == false)
            {
               getNodeService().addAspect(getNode().getNodeRef(), ApplicationModel.ASPECT_FEEDSOURCE, null);
            }
            
            // get the selected template Id from the Template Picker
            NodeRef templateRef = new NodeRef(Repository.getStoreRef(), this.rssTemplate);
            
            // set the template NodeRef into the templatable aspect property
            getNodeService().setProperty(getNode().getNodeRef(), ApplicationModel.PROP_FEEDTEMPLATE, templateRef); 
            
            // reset node details for next refresh of details page
            getNode().reset();
         }
         catch (Exception e)
         {
            Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
                  FacesContext.getCurrentInstance(), Repository.ERROR_GENERIC), e.getMessage()), e);
         }
      }
   }
   
   /**
    * Action handler to remove a RSS template from the current Space
    */
   public void removeRSSTemplate(ActionEvent event)
   {
      try
      {
         // clear template property
         getNodeService().setProperty(getNode().getNodeRef(), ApplicationModel.PROP_FEEDTEMPLATE, null);
         getNodeService().removeAspect(getNode().getNodeRef(), ApplicationModel.ASPECT_FEEDSOURCE);
         
         // reset node details for next refresh of details page
         getNode().reset();
      }
      catch (Exception e)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), Repository.ERROR_GENERIC), e.getMessage()), e);
      }
   }


   @Override
   protected String finishImpl(FacesContext context, String outcome) throws Exception
   {
      return null;
   }

   public String getCurrentItemId()
   {
      return getId();
   }

   public String getCancelButtonLabel()
   {
      return Application.getMessage(FacesContext.getCurrentInstance(), MSG_CLOSE);
   }

   @Override
   public String getContainerSubTitle()
   {
      return Application.getMessage(FacesContext.getCurrentInstance(), MSG_LOCATION) + ": " + 
             getSpace().getNodePath().toDisplayPath(getNodeService(), getPermissionService());
   }

   public String getContainerTitle()
   {
       FacesContext fc = FacesContext.getCurrentInstance();
       return Application.getMessage(fc, MSG_DETAILS_OF) + " " + Application.getMessage(fc, MSG_LEFT_QUOTE) + getName() + Application.getMessage(fc, MSG_RIGHT_QUOTE);
   }
   
   public String getOutcome(){
      return "dialog:close:dialog:showSpaceDetails";
   }
   
}
