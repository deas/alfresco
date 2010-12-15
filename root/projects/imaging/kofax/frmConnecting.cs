using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Resources;

namespace KofaxAlfrescoRelease_v1
{

    public partial class frmConnecting : Form
    {

        private System.Windows.Forms.Label lblWait;
        private ResourceManager oRM;

        private frmAlfrescoSetUp m_oSetup;
        
        //***********************************************************
		//
		// Method:		Constructor
		// Purpose:		Initializes member variables.
		//
		//************************************************************/
        internal frmConnecting(frmAlfrescoSetUp oSetup)
		{
			m_oSetup = oSetup;
			
			oRM = new ResourceManager("frmWait", System.Reflection.Assembly.GetExecutingAssembly());
			InitializeComponent();
		}


        //**************************************************************
        // Function:	LoadSettings
        // Purpose:		Starts the waiting form by setting the mouse
        //				pointer and showing the form.
        // Input:		None
        // Output:		None
        //*************************************************************/
        internal void LoadSettings()
        {
            try
            {
                //Mouse pointer to hourglass
                this.Cursor = Cursors.WaitCursor;

                //Load and display the form
                this.Show();  
                
                LoadControl();

                //Return the pointer to normal once the form has been activated
                this.Cursor = Cursors.IBeam;
            }
            catch (Exception e)
            {
            }

        }

        //**************************************************************
        // Function:	frmWait_Load
        // Purpose:		Called when the Load event is fired.
        // Input:		sender-		The sender of the event
        //				e-			The event arguments
        // Output:		None
        //*************************************************************/
        private void LoadControl()
        {
            try
            {

                //After the wait for is displayed, load settings
                //and then hide the waiting form
                this.Refresh();

                //Initialize controls in the Release Setup form
                //				m_oSetup.LoadControls();

                this.Hide();

            }
            catch (Exception e)
            {
            }
            finally
            {
                //Hide and unload the form once the loading of controls has finalized
                this.Close();

                //This is disposing of the resources
                this.Dispose();
            }
        }

    }
}