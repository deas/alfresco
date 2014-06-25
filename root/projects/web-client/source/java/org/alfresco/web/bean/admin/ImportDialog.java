/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.web.bean.admin;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.springframework.extensions.surf.util.I18NUtil;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.executer.ImporterActionExecuter;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.FileUploadBean;
import org.alfresco.web.bean.dialog.BaseDialogBean;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.ReportedException;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.repo.component.UICharsetSelector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Backing bean implementation for the Import dialog.
 * 
 * @author gavinc
 */
public class ImportDialog extends BaseDialogBean
{
   private static final long serialVersionUID = -8563911447832447065L;

   private static final Log logger = LogFactory.getLog(ImportDialog.class);

   private static final String DEFAULT_OUTCOME = "dialog:close";
   
   private static final String MSG_ERROR = "error_import";
   private static final String ERR_DUPLICATE_NAME = "system.err.duplicate_name";
   private static final String MSG_ERROR_NO_FILE = "error_import_no_file";
   private static final String MSG_ERROR_EMPTY_FILE = "error_import_empty_file";
   private static final String MSG_OK = "ok";
   private static final String MSG_IMPORT_TITLE = "import_title";
   private final static String MSG_LEFT_QUOTE = "left_qoute";
   private final static String MSG_RIGHT_QUOTE = "right_quote";
   
   transient private ActionService actionService;
   transient private ContentService contentService;
   transient private MimetypeService mimetypeService;
   
   private File file;
   private String fileName;
   private String encoding = "UTF-8";
   private boolean runInBackground = true;
   private boolean highByteZip = false;
   List<SelectItem> encodings;
   
   /**
    * Performs the import operation using the current state of the bean
    * 
    * @return The outcome
    */
   public String performImport(final FacesContext context, String outcome)
   {
      
      
      if (logger.isDebugEnabled())
         logger.debug("Called import for file: " + this.file);
      
      if (this.file != null && this.file.exists())
      {
         // check the file actually has contents
         if (this.file.length() > 0)
         {
            try
            {
      
               RetryingTransactionHelper txnHelper = Repository.getRetryingTransactionHelper(context);
               RetryingTransactionCallback<Object> callback = new RetryingTransactionCallback<Object>()
               {
                  public Object execute() throws Throwable
                  {
                     // first of all we need to add the uploaded ACP/ZIP file to the repository
                     NodeRef acpNodeRef = addFileToRepository(context);
                     
                     // build the action params map based on the bean's current state
                     Map<String, Serializable> params = new HashMap<String, Serializable>(2, 1.0f);
                     params.put(ImporterActionExecuter.PARAM_DESTINATION_FOLDER, browseBean.getActionSpace().getNodeRef());
                     params.put(ImporterActionExecuter.PARAM_ENCODING, encoding);
                     
                     // build the action to execute
                     Action action = getActionService().createAction(ImporterActionExecuter.NAME, params);
                     if (action instanceof ImporterActionExecuter)
                     {
                         ((ImporterActionExecuter)action).setHighByteZip(highByteZip);
                     }
                     action.setExecuteAsynchronously(runInBackground);
                     
                     // execute the action on the ACP file
                     getActionService().executeAction(action, acpNodeRef);
                     
                     if (logger.isDebugEnabled())
                     {
                        logger.debug("Executed import action with action params of " + params);
                     }
                     return null;
                  }
               };
               txnHelper.doInTransaction(callback);
               
               // reset the bean
               reset();
            }
            catch (Throwable e)
            {
               if (e instanceof DuplicateChildNodeNameException)
               {
                  String name = ((DuplicateChildNodeNameException)e).getName();
                  String err_mess = MessageFormat.format(I18NUtil.getMessage(ERR_DUPLICATE_NAME), name);
                  Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
                        FacesContext.getCurrentInstance(), MSG_ERROR), err_mess), e);
               }
               else
               {
                  Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
                        FacesContext.getCurrentInstance(), MSG_ERROR), e.toString()), e);
               }
               outcome = null;
               ReportedException.throwIfNecessary(e);
            }
         }
         else
         {
            Utils.addErrorMessage(Application.getMessage(FacesContext.getCurrentInstance(), MSG_ERROR_EMPTY_FILE));
            outcome = null;
         }
      }
      else
      {
         Utils.addErrorMessage(Application.getMessage(FacesContext.getCurrentInstance(), MSG_ERROR_NO_FILE));
         outcome = null;
      }
      
      return outcome;
   }
   
   /**
    * Action called when the dialog is cancelled, just resets the bean's state
    * 
    * @return The outcome
    */
   public String cancel()
   {
      reset();
      
      return DEFAULT_OUTCOME;
   }
   
   /**
    * Resets the dialog state back to the default
    */
   public void reset()
   {
      this.file = null;
      this.fileName = null;
      this.runInBackground = true;
      
      // delete the temporary file we uploaded earlier
      if (this.file != null)
      {
         this.file.delete();
      }
      
      // remove the file upload bean from the session
      FacesContext ctx = FacesContext.getCurrentInstance();
      ctx.getExternalContext().getSessionMap().remove(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
   }
   
   /**
    * @return Returns the message to display when a file has been uploaded
    */
   public String getFileUploadSuccessMsg()
   {
      String msg = Application.getMessage(FacesContext.getCurrentInstance(), "file_upload_success");
      return MessageFormat.format(msg, new Object[] {Utils.encode(getFileName())});
   }
   
   /**
    * @return Returns the name of the file
    */
   public String getFileName()
   {
      // try and retrieve the file and filename from the file upload bean
      // representing the file we previously uploaded.
      FacesContext ctx = FacesContext.getCurrentInstance();
      FileUploadBean fileBean = (FileUploadBean)ctx.getExternalContext().getSessionMap().
         get(FileUploadBean.FILE_UPLOAD_BEAN_NAME);
      if (fileBean != null)
      {
         this.fileName = fileBean.getFileName();
         this.file = fileBean.getFile();
      }
      
      return this.fileName;
   }
   
   /**
    * @param fileName The name of the file
    */
   public void setFileName(String fileName)
   {
       //do nothing required for JSF workflow
   }

   /**
    * Returns the encoding to use for the export
    *  
    * @return The encoding
    */
 /*  public String getEncoding()
   {
      return this.encoding;
   }*/

   /**
    * Sets the encoding to use for the export package
    * 
    * @param encoding The encoding
    */
/*   public void setEncoding(String encoding)
   {
      this.encoding = encoding;
   }*/

   /**
    * Determines whether the import should run in the background
    * 
    * @return true means the import will run in the background 
    */
   public boolean getRunInBackground()
   {
      return this.runInBackground;
   }

   /**
    * Determines whether the import will run in the background
    * 
    * @param runInBackground true to run the import in the background
    */
   public void setRunInBackground(boolean runInBackground)
   {
      this.runInBackground = runInBackground;
   }
   
   /**
    * @return the highByteZip encoding switch
    */
   public boolean getHighByteZip()
   {
      return this.highByteZip;
   }

   /**
    * @param highByteZip the encoding switch for high-byte ZIP filenames to set
    */
   public void setHighByteZip(boolean highByteZip)
   {
      this.highByteZip = highByteZip;
   }
   
   /**
    * Sets the action service
    * 
    * @param actionService  the action service
    */
   public void setActionService(ActionService actionService)
   {
      this.actionService = actionService;
   }
   
   protected ActionService getActionService()
   {
      if (actionService == null)
      {
         actionService = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getActionService();
      }
      return actionService;
   }
   
   /**
    * Sets the content service
    * 
    * @param contentService the content service
    */
   public void setContentService(ContentService contentService)
   {
      this.contentService = contentService;
   }
   
   protected ContentService getContentService()
   {
      if (contentService == null)
      {
         contentService = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getContentService();
      }
      return contentService;
   }
   
   /**
    * Sets the mimetype sevice
    * 
    * @param mimetypeService the mimetype service
    */
   public void setMimetypeService(MimetypeService mimetypeService)
   {
      this.mimetypeService = mimetypeService;
   }
   
   protected MimetypeService getMimetypeService()
   {
      if (mimetypeService == null)
      {
         mimetypeService = Repository.getServiceRegistry(FacesContext.getCurrentInstance()).getMimetypeService();
      }
      return mimetypeService;
   }
   
   /**
    * Adds the uploaded ACP/ZIP file to the repository
    *  
    * @param context Faces context
    * @return NodeRef representing the ACP/ZIP file in the repository
    */
   private NodeRef addFileToRepository(FacesContext context)
   {
      // set the name for the new node
      Map<QName, Serializable> contentProps = new HashMap<QName, Serializable>(1);
      contentProps.put(ContentModel.PROP_NAME, this.fileName);
      
      // create the node to represent the zip file
      String assocName = QName.createValidLocalName(this.fileName);
      ChildAssociationRef assocRef = this.getNodeService().createNode(
           this.browseBean.getActionSpace().getNodeRef(), ContentModel.ASSOC_CONTAINS,
           QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, assocName),
           ContentModel.TYPE_CONTENT, contentProps);
      
      NodeRef acpNodeRef = assocRef.getChildRef();
      
      // apply the titled aspect to behave in the web client
      String mimetype = this.getMimetypeService().guessMimetype(this.fileName);
      Map<QName, Serializable> titledProps = new HashMap<QName, Serializable>(2, 1.0f);
      titledProps.put(ContentModel.PROP_TITLE, this.fileName);
      titledProps.put(ContentModel.PROP_DESCRIPTION,
            MimetypeMap.MIMETYPE_ACP.equals(mimetype) ?
               Application.getMessage(context, "import_acp_description") :
               Application.getMessage(context, "import_zip_description"));
      this.getNodeService().addAspect(acpNodeRef, ContentModel.ASPECT_TITLED, titledProps);
      
      // add the content to the node
      ContentWriter writer = this.getContentService().getWriter(acpNodeRef, ContentModel.PROP_CONTENT, true);
      writer.setEncoding("UTF-8");
      writer.setMimetype(mimetype);
      writer.putContent(this.file);
      
      return acpNodeRef;
   }

   @Override
   protected String finishImpl(FacesContext context, String outcome) throws Exception
   {
      return performImport(context,outcome);
   }
   
   @Override
   public boolean getFinishButtonDisabled()
   {
      return getFileName() == null;
   }
   
   @Override
   public String getFinishButtonLabel()
   {
      return Application.getMessage(FacesContext.getCurrentInstance(), MSG_OK);
   }
   
   @Override
   public String getContainerTitle()
   {
       FacesContext fc = FacesContext.getCurrentInstance();
       String name = Application.getMessage(fc, MSG_LEFT_QUOTE)
       + browseBean.getActionSpace().getName()
       + Application.getMessage(fc, MSG_RIGHT_QUOTE);
       return MessageFormat.format(Application.getMessage(fc, MSG_IMPORT_TITLE), name);
   }
   
   /**
    * @return  Returns the encoding currently selected
    */
   public String getEncoding()
   {
      if (encoding == null)
      {
         ConfigService configSvc = Application.getConfigService(FacesContext.getCurrentInstance());
         Config config = configSvc.getConfig("Import Dialog");
         if (config != null)
         {
            ConfigElement defaultEncCfg = config.getConfigElement("default-encoding");
            if (defaultEncCfg != null)
            {
               String value = defaultEncCfg.getValue();
               if (value != null)
               {
                  encoding = value.trim();
               }
            }
         }
         if (encoding == null || encoding.length() == 0)
         {
            // if not configured, set to a sensible default for most character sets
            encoding = "UTF-8";
         }
      }
      return encoding;
   }

   /**
    * @param encoding   the document's encoding
    */
   public void setEncoding(String encoding)
   {
      this.encoding = encoding;
   }
   
   public List<SelectItem> getEncodings()
   {
       if ((this.encodings == null) || (Application.isDynamicConfig(FacesContext.getCurrentInstance())))
       {
           FacesContext context = FacesContext.getCurrentInstance();
           
           this.encodings = new ArrayList<SelectItem>(3);
           
           ConfigService svc = Application.getConfigService(context);
           Config cfg = svc.getConfig("Import Dialog");
           if (cfg != null)
           {
               ConfigElement typesCfg = cfg.getConfigElement("encodings");
               if (typesCfg != null)
               {
                   for (ConfigElement child : typesCfg.getChildren())
                   {
                       String encoding = child.getAttribute("name");
                       if (encoding != null)
                       {
                           this.encodings.add(new SelectItem(encoding, encoding));
                       }
                   }
               }
               else
               {
                   logger.warn("Could not find 'encodings' configuration element");
               }
           }
           else
           {
               encodings = UICharsetSelector.getCharsetEncodingList();
           }
       }
      
       return this.encodings;
   }
}
