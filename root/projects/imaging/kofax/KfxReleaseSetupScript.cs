/// Author:    Manfung Chan
/// Version:   v1.0

using System;
using System.Windows.Forms;
using System.Runtime.InteropServices;

//*******************************************************************************
//
// Class:      KfxReleaseSetupScript - KfxReleaseSetupScript.cs
// Purpose:     ReleaseSetup
//
// (c) Copyright 2002 Kofax Image Products.
// All rights reserved.
//
//*******************************************************************************
namespace KofaxAlfrescoRelease_v1
{

	using AscentRelease;	
	
	//Make the interface dual type so it exposes the methods in OLE view 
	//(otherwise can be seen from unmanaged code)
    [GuidAttribute("5F5E61B2-0B35-4616-BABE-3DCDC12D5E11")]
	[InterfaceType(ComInterfaceType.InterfaceIsDual)]
	public interface IKfxReleaseSetupScript
	{
		KfxReturnValue OpenScript();
		KfxReturnValue RunUI();
		KfxReturnValue CloseScript();

		KfxReturnValue ActionEvent(KfxActionValue oActionID, string strData1, string strData2);

		ReleaseSetupData SetupData {get; set;}
	}

    [GuidAttribute("EA3402CC-AFAC-4056-8F50-EA788E23E1CE")]
	[ClassInterface(ClassInterfaceType.None)]
	public class KfxReleaseSetupScript : IKfxReleaseSetupScript
	{
		private ReleaseSetupData releaseSetUpData;	//This is the ReleaseSetupData object that is passed 
												//into the VB COM server from Ascent Capture and then
												//to this class. It holds all settings to be saved for
												//setup.

        //The flag to know whether or not to run the UI. Used in the action events.        
        private bool showUI = false;
                                                

		//*******************************************************************************
		//
		// Method:      Constructor
		// Purpose:     Initalize variables
		//
		//******************************************************************************
		public KfxReleaseSetupScript()
		{

		}


		//*******************************************************************************
		// Property (Get/Set):		SetupData
		// Purpose:					This is needed if we are calling the release script 
		//							directly from Ascent Capture. It's also used to pass
		//							the RSD object from the VB COM server.
		//******************************************************************************
		public ReleaseSetupData SetupData
		{
			get
			{
				return this.releaseSetUpData;
			}
			set
			{
				this.releaseSetUpData = value;
			}
		}

		//*********************************************************
		// Function:	OpenScript()
		// Scope:		internal
		// Overview:	Script initialization point.  Perform any necessary
		//				initialization here. Such initialization can include
		//				allocating necessary resources, logging in to the
		//				external data source, etc.
		// Params:		none
		// Returns:		KFX_REL_SUCCESS, KFX_REL_ERROR
		// Called By:	Called once by the Release Setup Controller when the
		//				script is loaded and before a call to "RunUI" or
		//				"ActionEvent" is made.
		//*********************************************************
		public KfxReturnValue OpenScript()
		{
            
            return AscentRelease.KfxReturnValue.KFX_REL_SUCCESS;

		}


		//*********************************************************
		// Function:	RunUI()
		// Scope:		internal
		// Overview:	User interface display point.  This method
		//				is called by the Release Setup Controller
		//				to display the setup dialog specific to this
		//				script.  The SetupData object will be initialized
		//				by the time that this method is called.
		// Params:		none
		// Returns:		KFX_REL_SUCCESS, KFX_REL_ERROR, KFX_REL_STOPPED
		// Called By:	Called once by the Release Setup Controller.
		//**********************************************************
		public KfxReturnValue RunUI()
		{
            try
            {
                frmAlfrescoSetUp frmSetup = new frmAlfrescoSetUp(this.releaseSetUpData);

                //If the user cancels, the return value will be KFX_REL_STOPPED
                return frmSetup.ShowForm();

            }
            catch (Exception e)
            {
                return AscentRelease.KfxReturnValue.KFX_REL_ERROR;
            }

		}


		//*********************************************************
		// Function:	CloseScript()
		// Scope:		internal
		// Overview:	SetupScript release point.  Perform any necessary
		//				cleanup such as logging out of the data
		//				source, release any resources, etc.
		// Params:		none
		// Returns:		One of the following:
		//				KFX_REL_SUCCESS, KFX_REL_ERROR
		// Called By:	Administration.  Called once just
		//				before the script object is released.
		//**********************************************************
		public KfxReturnValue CloseScript()
		{
			try
			{
                GC.Collect();
                GC.WaitForPendingFinalizers();

                //make sure all of the instances of SetupData are released.
                // **** LOOK AT THIS IN MORE DETAIL  **********
                while (System.Runtime.InteropServices.Marshal.ReleaseComObject(this.releaseSetUpData) > 0)
                { }

                this.releaseSetUpData = null;
                return AscentRelease.KfxReturnValue.KFX_REL_SUCCESS;
			}
			catch(Exception e)
			{				
			
				return AscentRelease.KfxReturnValue.KFX_REL_ERROR;
			}
			
		}


		//*********************************************************
		// Function:	ActionEvent()
		// Scope:		internal
		// Overview:	Called by the setup controller to invoke a specific
		//				action for the script to respond to.
		//				Refer to the documentation for a list of actions and
		//				their associated parameters.
		// Params:		ActionID - ID of the action to perform
		//				strData1 - Action parameter 1
		//				strData2 - Action parameter 2
		// Returns:		One of the following:
		//				KFX_REL_SUCCESS, KFX_REL_ERROR, KFX_REL_STOPPED
		// Called By:	Called once by the Release Setup Controller to perform
		//**********************************************************
		public KfxReturnValue ActionEvent(AscentRelease.KfxActionValue oActionID, string strData1, string strData2)
		{
			try
			{

                switch (oActionID)
                {
                    case KfxActionValue.KFX_REL_BATCHCLASS_RENAME:
                        {
                            break;
                        }
                    case KfxActionValue.KFX_REL_BATCHFIELD_DELETE:
                        {
                            break;
                        }
                    case KfxActionValue.KFX_REL_BATCHFIELD_INSERT:
                        {

                            break;
                        }
                    case KfxActionValue.KFX_REL_BATCHFIELD_RENAME:
                        {

                            break;
                        }
                    case KfxActionValue.KFX_REL_DOCCLASS_RENAME:
                        {
                            break;
                        }
                    case KfxActionValue.KFX_REL_END:
                        {
                            //Last action event fired. Handle showing UI if necessary.
                            if (showUI)
                                RunUI();

                            break;
                        }
                    case KfxActionValue.KFX_REL_IMPORT:
                        {
                             RunUI();

                            break;
                        }
                    case KfxActionValue.KFX_REL_INDEXFIELD_DELETE:
                        {
                            showUI = ReleaseUtils.OnIndexFieldDelete(strData1, releaseSetUpData);

                            break;
                        }
                    case KfxActionValue.KFX_REL_INDEXFIELD_INSERT:
                        {
                            showUI = true;
                            break;
                        }
                    case KfxActionValue.KFX_REL_INDEXFIELD_RENAME:
                        {
                            ReleaseUtils.OnIndexFieldRename(strData1, strData2, releaseSetUpData);
                            break;
                        }
                    case KfxActionValue.KFX_REL_PUBLISH_CHECK:
                        {
                            break;
                        }
                    case KfxActionValue.KFX_REL_RELEASESETUP_DELETE:
                        {
                            break;
                        }
                    case KfxActionValue.KFX_REL_START:
                        {
 
                            break;
                        }
                    case KfxActionValue.KFX_REL_UNDEFINED_ACTION:
                        {
 
                            break;
                        }
                    case KfxActionValue.KFX_REL_UPGRADE:
                        {
                            return AscentRelease.KfxReturnValue.KFX_REL_UNSUPPORTED;
                        }
                    default:
                        {
                            break;
                        }
                }

				return AscentRelease.KfxReturnValue.KFX_REL_SUCCESS;
			}
			catch(Exception e)
			{				
				
				return AscentRelease.KfxReturnValue.KFX_REL_ERROR;
			}
		}
	}
}
