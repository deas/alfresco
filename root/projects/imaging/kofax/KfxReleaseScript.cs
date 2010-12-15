/// Author:    Manfung Chan
/// Version:   v1.0

using System;
using System.Windows.Forms;
using System.Runtime.InteropServices;
using System.IO;
using Alfresco;
using Alfresco.RepositoryWebService;
using Alfresco.ContentWebService;
using Microsoft.Web.Services3;
using Microsoft.Web.Services3.Security;
using Microsoft.Web.Services3.Security.Tokens;
using Microsoft.Web.Services3.Security.Utility;
using System.Collections;

//*******************************************************************************
//
// Class:      KfxReleaseScript - KfxReleaseScript.cs
// Purpose:     Release
//
// Author: Manfung Chan
// 
//
//******************************************************************************
namespace KofaxAlfrescoRelease_v1
{
	//ReleaseData object set by the release controller.
	//This object is to be used during the document release
	//process as it will contain the document data and the
	//external data source information defined during the
	//setup process.

	//Make the interface dual type so it exposes the methods in OLE view 
	//(otherwise can be seen from unmanaged code)
	[GuidAttribute("E100C9F5-2CD9-4ce1-9C05-44D4013F5EAD")]
	[InterfaceType(ComInterfaceType.InterfaceIsDual)]
	public interface IKfxReleaseScript
	{
		AscentRelease.ReleaseData DocumentData {get;set;}
		AscentRelease.KfxReturnValue CloseScript();
		AscentRelease.KfxReturnValue OpenScript();
		AscentRelease.KfxReturnValue ReleaseDoc();
	}

    [GuidAttribute("F1CF5EB4-9691-41bc-BB01-1D23ABE0C255")]
	[ClassInterface(ClassInterfaceType.None)]
	public class KfxReleaseScript : IKfxReleaseScript
	{
        internal AscentRelease.ReleaseData releaseData;        
        private Alfresco.RepositoryWebService.Store spacesStore;
        private RepositoryService repoService;
        private String locationUuid;
        private String imageContentProp;
        private String ocrContentProp;
        private String pdfContentProp;
        private String contentType;


		//*******************************************************************************
		//
		// Method:      Constructor
		// Purpose:     Initalize variables
		//
		//******************************************************************************
		public KfxReleaseScript()
		{
		    
		}        

		//*******************************************************************************
		// Property (Get/Set):		DocumentData
		// Purpose:					This is needed if we are calling the release script 
		//							directly from Ascent Capture. It's also used to pass
		//							the RD object from the VB COM server.
		//******************************************************************************
		public AscentRelease.ReleaseData DocumentData
		{
            get
            {
                return this.releaseData;
            }
            set
            {
                this.releaseData = value;
            }
		}

		//**********************************************************
		// Function:	CloseScript()
		// Scope:		internal
		// Overview:	Script release point.  Perform any necessary
		//				cleanup such as logging out of the data
		//				source, release any resources, etc.
		// Params:		none
		// Returns:		One of the following:
		//				KFX_REL_SUCCESS, KFX_REL_ERROR
		// Called By:	The Batch Release Manager.  Called once just
		//				before the script object is released.
		//**********************************************************
		public AscentRelease.KfxReturnValue CloseScript()
		{
            // End the Alfresco session
            try
            {
                AuthenticationUtils.endSession();
                return AscentRelease.KfxReturnValue.KFX_REL_SUCCESS;
            }
            catch (Exception)
            {
                return AscentRelease.KfxReturnValue.KFX_REL_ERROR;
            }
		}

		//**********************************************************
		// Function:	OpenScript()
		// Scope:		internal
		// Overview:	Script initialization point.  Perform any
		//				necessary initialization such as logging
		//				in to a remote data source, allocated any
		//				necessary resources, etc.
		// Params:		none
		// Returns:		One of the following:
		//				KFX_REL_SUCCESS, KFX_REL_ERROR
		// Called By:	The Batch Release Manager.  Called once
		//				when the script object is loaded.
		//**********************************************************
		public AscentRelease.KfxReturnValue OpenScript()
		{
            // Start the Alfresco session
            try
            {

                string repository = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_REPOSITORY);
                WebServiceFactory.setEndpointAddress(repository);

                string userName = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_USERNAME);
                string password = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_PASSWORD);
                AuthenticationUtils.startSession(userName, password);
                this.repoService = WebServiceFactory.getRepositoryService();

                // the uuid of the location to be saved
                this.locationUuid = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_LOCATION_UUID);

                this.contentType = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_CONTENT_TYPE);

                this.imageContentProp = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_IMAGE);
                this.ocrContentProp = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_OCR);
                this.pdfContentProp = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_PDF);

                // Initialise the reference to the spaces store
                this.spacesStore = new Alfresco.RepositoryWebService.Store();
                spacesStore.scheme = Alfresco.RepositoryWebService.StoreEnum.workspace;
                spacesStore.address = "SpacesStore";

                return AscentRelease.KfxReturnValue.KFX_REL_SUCCESS;
            }
            catch (Exception)
            {
                return AscentRelease.KfxReturnValue.KFX_REL_ERROR;
            }            				
		}
	
		//**********************************************************
		// Function:	ReleaseDoc()
		// Scope:		internal
		// Overview:	Document release point.  Use the ReleaseData
		//				object to release the current document's data
		//				to the external data source.
		// Params:		none
		// Returns:		One of the following: KFX_REL_SUCCESS,
		//				KFX_REL_ERROR, KFX_REL_DOCCLASSERROR,
		//				KFX_REL_QUEUED
		// Called By:	The Batch Release Manager.  Called once for each
		//				document to be released.
		//**********************************************************
		public AscentRelease.KfxReturnValue ReleaseDoc()
		{
            try
            {
                
                String name = ReleaseUtils.getLinkValue(this.releaseData.Values, ReleaseConstants.CONTENT_TYPE + Alfresco.Constants.PROP_NAME);
                if (name == null || name.Equals(""))
                {
                    // node name is null or ""
                    Log log = new Log();
                    log.ErrorLog(".\\log\\", "KfxReleaseScript method ReleaseDoc - Content name is empty, check index fields in Release setup or the Content Type properties in Alfresco", "");
                    return AscentRelease.KfxReturnValue.KFX_REL_ERROR;
                }
                String location = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_LOCATION);

                // get properties and aspects list
                int i = 0;
                ArrayList aspectFields = new ArrayList();
                ArrayList properties = new ArrayList();
                NamedValue nameValue = new NamedValue();
                // if its a content type field then create a NamedValue otherwise
                // store in the aspect array to be used later on
                foreach (AscentRelease.Value oValue in releaseData.Values)
                {

                    String value = oValue.Value;
                    String destination = oValue.Destination;
                    if (destination.StartsWith(ReleaseConstants.CONTENT_TYPE))
                    {
                        // content type field
                        nameValue = new NamedValue();

                        // need to remove the prefix
                        int start = ReleaseConstants.CONTENT_TYPE.Length;
                        nameValue.name = destination.Substring(start, (destination.Length - start));
                        nameValue.value = value;
                        nameValue.isMultiValue = false;
                        properties.Add(nameValue);
                    }
                    else
                    {
                        // aspect field
                        aspectFields.Add(oValue);
                    }
                }
                
                // create the CML
                bool isNew = true;
                String existingUuid = this.getNodeUuidFromLocation(this.locationUuid, name);
                CML cml;

                // flag to delete refnode if there is an error uploading the content
                bool deleteOnErrFlag = true; 
                if (existingUuid.Equals(""))
                {
                    // new content to upload
                    cml = this.getCMLCreate((NamedValue[])properties.ToArray(typeof(NamedValue)));
                }
                else
                {
                    // update, set deleteFlag to false for updating node Content
                    deleteOnErrFlag = false;
                    cml = this.getCMLUpdate(existingUuid, (NamedValue[])properties.ToArray(typeof(NamedValue)));
                    isNew = false;

                }

                // create any aspects 
                CMLAddAspect[] cmlAspects = null;
                String aspects = ReleaseUtils.getCustomProperty(this.releaseData.CustomProperties, ReleaseConstants.CUSTOM_ASPECTS);
                AscentRelease.Value aspectValue;
                if (aspects != null)
                {
                    // seperate the aspects string
                    String[] aspectNames = ReleaseUtils.SplitByString(aspects, ReleaseConstants.SEPERATOR);
                    String aspectName;

                    cmlAspects = new CMLAddAspect[aspectNames.Length];
                    // for each aspect create a aspect CML
                    for (i = 0; i < aspectNames.Length; i++)
                    {
                        aspectName = aspectNames[i];

                        // create the aspect CML
                        CMLAddAspect addAspect = new CMLAddAspect();

                        addAspect.aspect = aspectName;

                        if (isNew)
                        {
                            addAspect.where_id = "1";
                        }
                        else
                        {
                            // use  predicate

                            Alfresco.RepositoryWebService.Reference reference = new Alfresco.RepositoryWebService.Reference();
                            reference.store = this.spacesStore;
                            reference.uuid = existingUuid;

                            Alfresco.RepositoryWebService.Predicate pred = new Alfresco.RepositoryWebService.Predicate();
                            pred.Items = new Alfresco.RepositoryWebService.Reference[] { reference };
                            addAspect.where = pred;
                        }

                        ArrayList aspectProperties = new ArrayList();
                        // add the aspect fields for this aspect
                        for (int j = 0; j < aspectFields.Count; j++)
                        {
                            // loop thru the aspects fields and add the relevent fields
                            aspectValue = (AscentRelease.Value)aspectFields[j];
                            String destination = aspectValue.Destination;

                            String prefix = ReleaseConstants.ASPECT + ReleaseConstants.SEPERATOR + aspectName;

                            if (destination.StartsWith(prefix))
                            {
                                // content type field
                                nameValue = new NamedValue();

                                // need to remove the prefix
                                int start = prefix.Length;
                                nameValue.name = destination.Substring(start, (destination.Length - start));
                                nameValue.value = aspectValue.Value;
                                nameValue.isMultiValue = false;
                                aspectProperties.Add(nameValue);

                            }
                        }
                        addAspect.property = (NamedValue[])aspectProperties.ToArray(typeof(NamedValue));
                        cmlAspects[i] = addAspect;
                    }
                }

                // add any aspects
                if (cmlAspects != null && cmlAspects.Length > 0)
                {                    
                    cml.addAspect = cmlAspects;
                }                

                UpdateResult[] updateResult = this.repoService.update(cml);

                if (!this.writeNewContent(updateResult[0].destination, deleteOnErrFlag))
                {
                    // failed to update content, deleted
                    return AscentRelease.KfxReturnValue.KFX_REL_ERROR;
                }

                return AscentRelease.KfxReturnValue.KFX_REL_SUCCESS;

            }
            catch (Exception e)
            {
                Log log = new Log();
                log.ErrorLog(".\\log\\", "KfxReleaseScript method ReleaseDoc " + e.Message, e.StackTrace);

                return AscentRelease.KfxReturnValue.KFX_REL_ERROR;
            }
		}

        private bool writeNewContent(Alfresco.RepositoryWebService.Reference rwsRef, bool deleteOnErrFlag)
        {
            bool success = true;
            try
            {
                // write the content
                if (this.imageContentProp != null)
                {

                    try
                    {
                        foreach (AscentRelease.ImageFile image in releaseData.ImageFiles)
                        {

                            String filePath = image.FileName;
                            // get the file and check its size
                            FileInfo imageFile = new FileInfo(filePath);

                            if (imageFile.Length > ReleaseConstants.MAX_FILE_SIZE)
                            {
                                // get the smaller file
                                filePath = filePath.Insert(filePath.LastIndexOf("."), "t");
                            }

                            this.writeContentType(filePath, rwsRef, this.imageContentProp, ReleaseConstants.MIME_TYPE_TIFF);
                            break;

                        }
                    }
                    catch (Exception e)
                    {
                        Log log = new Log();
                        log.ErrorLog(".\\log\\", "KfxReleaseScript method writeNewContent " + e.Message, e.StackTrace);
                        return false;
                    }

                }

                if (this.ocrContentProp != null)
                {
                    this.writeContentType(releaseData.TextFilePath, rwsRef,this.ocrContentProp, ReleaseConstants.MIME_TYPE_TEXT);
                }

                if (this.pdfContentProp != null)
                {
                    this.writeContentType(releaseData.KofaxPDFFileName, rwsRef,this.pdfContentProp, ReleaseConstants.MIME_TYPE_PDF);
                }
                releaseData.RepositoryDocumentID = rwsRef.uuid;
            }
            catch (Exception ex)
            {
                String errorMessage = ReleaseConstants.ERR_NOT_NODE_DELETED;
                if (deleteOnErrFlag)
                {
                    this.deleteReference(rwsRef);
                    errorMessage = ReleaseConstants.ERR_NODE_DELETED;
                }
                Log log = new Log();
                log.ErrorLog(".\\log\\", "KfxReleaseScript method writeNewContent " + ex.Message, ex.StackTrace);
                success = false;
            }

            return success;
        }


        private void writeContentType(String filePath, Alfresco.RepositoryWebService.Reference rwsRef,
            String property, String mimetype)
        {

            Alfresco.ContentWebService.Reference newContentNode = new Alfresco.ContentWebService.Reference();
            newContentNode.path = rwsRef.path;
            newContentNode.uuid = rwsRef.uuid;

            Alfresco.ContentWebService.Store cwsStore = new Alfresco.ContentWebService.Store();
            cwsStore.address = "SpacesStore";
            spacesStore.scheme = Alfresco.RepositoryWebService.StoreEnum.workspace;
            newContentNode.store = cwsStore;

            // Open the file and convert to byte array 
            FileStream inputStream = new FileStream(filePath, FileMode.Open);

            int bufferSize = (int)inputStream.Length;
            byte[] bytes = new byte[bufferSize];
            inputStream.Read(bytes, 0, bufferSize);
            inputStream.Close();

            ContentFormat contentFormat = new ContentFormat();
            contentFormat.mimetype = mimetype;
            WebServiceFactory.getContentService().write(newContentNode, property, bytes, contentFormat);

        }

        private CML getCMLCreate(NamedValue[] properties)
        {

            string qName = "ASCENT_" + releaseData.UniqueDocumentID.ToString();

            // get the parent reference
            Alfresco.RepositoryWebService.ParentReference parentReference = new Alfresco.RepositoryWebService.ParentReference();
            parentReference.store = spacesStore;
            parentReference.uuid = this.locationUuid;

            parentReference.associationType = Constants.ASSOC_CONTAINS;
            // set node content type
            parentReference.childName = this.contentType;

            // Create the CML create object
            CMLCreate create = new CMLCreate();
            create.parent = parentReference;
            create.id = "1";

            // set the contenttype
            create.type = this.contentType;
            create.property = properties;                        

            // Create and execute the cml statement
            CML cml = new CML();
            cml.create = new CMLCreate[] { create };

            return cml;

        }

        private CML getCMLUpdate(String uuid, NamedValue[] properties)
        {

            // ************ test ***********8
            /*
            for (int i = 0; i < properties.Length; i++)
            {

                NamedValue name = properties[i];
                if (name.name.Equals(Alfresco.Constants.PROP_DESCRIPTION))
                {
                    MessageBox.Show("Gotcha");
                    name.value = "UPDATE";
                    properties[i] = name;
                }
            }
            */

            Alfresco.RepositoryWebService.Reference reference = new Alfresco.RepositoryWebService.Reference();
            reference.store = this.spacesStore;
            reference.uuid = uuid;
           
            CMLUpdate cmlUpdate = new CMLUpdate();
            Alfresco.RepositoryWebService.Predicate pred = new Alfresco.RepositoryWebService.Predicate();
            pred.Items = new Alfresco.RepositoryWebService.Reference[] { reference };
            cmlUpdate.where = pred;
            cmlUpdate.property = properties;

            // Create and execute the cml statement
            CML cml = new CML();
            cml.update = new CMLUpdate[] { cmlUpdate };

            return cml;
            
        }

        private String getNodeUuidFromLocation(String spaceUuid, String name)
        {
            String uuid = "";
            // Create a query object
            Alfresco.RepositoryWebService.Query query = new Alfresco.RepositoryWebService.Query();
            query.language = Alfresco.RepositoryWebService.QueryLanguageEnum.lucene;

            String search = "+PARENT:\"workspace://SpacesStore/" + spaceUuid + "\" +@cm\\:name:\"" + name + "\"";
            query.statement = search;
            // Initialise the reference to the spaces store
            Alfresco.RepositoryWebService.Store spacesStore = new Alfresco.RepositoryWebService.Store();
            spacesStore.scheme = Alfresco.RepositoryWebService.StoreEnum.workspace;
            spacesStore.address = "SpacesStore";

            QueryResult result = this.repoService.query(spacesStore, query, true);

            if (result.resultSet.rows != null)
            {
                uuid = result.resultSet.rows[0].node.id;
                
            }
            return uuid;
        }

        private bool isNewContent(String spaceUuid, String name)
        {
            bool isNew = true;
            // Create a query object
            Alfresco.RepositoryWebService.Query query = new Alfresco.RepositoryWebService.Query();
            query.language = Alfresco.RepositoryWebService.QueryLanguageEnum.lucene;

            // Create a query object
            String search = "+PARENT:\"workspace://SpacesStore/" + spaceUuid + "\" +@cm\\:name:\"" + name + "\"";

            query.statement = search;
            // Initialise the reference to the spaces store
            Alfresco.RepositoryWebService.Store spacesStore = new Alfresco.RepositoryWebService.Store();
            spacesStore.scheme = Alfresco.RepositoryWebService.StoreEnum.workspace;
            spacesStore.address = "SpacesStore";

            QueryResult result = this.repoService.query(spacesStore, query, true);

            if (result.resultSet.rows != null)
            {
                isNew = false;
            }
            return isNew;
        }

        private void deleteReference(Alfresco.RepositoryWebService.Reference reference)
        {
           
            Alfresco.RepositoryWebService.Predicate where = new Alfresco.RepositoryWebService.Predicate();
            where.Items = new Alfresco.RepositoryWebService.Reference[] { reference };

            CMLDelete cmlDelete = new CMLDelete();
            cmlDelete.where = where;

            CML cml = new CML();
            cml.delete = new CMLDelete[] { cmlDelete };
            this.repoService.update(cml);

        }
	}        

}
