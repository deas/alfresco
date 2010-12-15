/// Author:    Manfung Chan
/// Version:   v1.0

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Collections;  

namespace KofaxAlfrescoRelease_v1
{
    using AscentRelease;
    using Alfresco;
    using Alfresco.RepositoryWebService;
    using Microsoft.Web.Services3.Security.Tokens;
    using Alfresco.ContentWebService;

    public partial class frmAlfrescoSetUp : Form
    {

        private const int MAX_FILEDS = 9;

        private ReleaseSetupData releaseSetUpData;
        private bool m_bCancel;
        private ComboBox[] indexFieldsArray = new ComboBox[9];
        private TextBox[] destinationFieldsArray = new TextBox[9];
        private Label[] mandatoryFieldsArray = new Label[9];

        private ArrayList releaseLinks;
        private ArrayList selectedAspects = new ArrayList();
        private int numFieldsDisplayed = 0;
        private int currentIndexStartPos = 0;
        private int destinationFieldCount = 0;
        private AscentRelease.IndexFields indexFields;
        private bool updateReleaseLink = true;

        private frmTreeViewBrowse browse = null;
        private RepositoryService repoService;

        // set by the TreeView form
        private String locationUuid;

        // sets the Location field on this form
        public String LocationName
        {
            set
            {
                this.Location.Text = value;
            }
        }

        // used to to store the uuid of the folder
        public String LocationUuid
        {
            set { locationUuid = value; }
        }


        //**************************************************************************
        // Property (Get/Set):		SetupData
        // Purpose:					This will get the release setup data object.
        //**************************************************************************
        internal ReleaseSetupData SetupData
        {
            get
            {
                return this.releaseSetUpData;
            }
        }

        //**************************************************************************
        // Property (Get/Set):		Cancelled
        // Purpose:					This will get or set the value of m_bCancel.
        //**************************************************************************
        internal bool Cancelled
        {
            get { return m_bCancel; }
            set { m_bCancel = value; }
        }

        public frmAlfrescoSetUp()
        {
            InitializeComponent();
        }

        //***********************************************************
		//
		// Method:		Constructor
		// Purpose:		Initializes member variables.
		//
		//************************************************************/
        internal frmAlfrescoSetUp(ReleaseSetupData releaseSetUpData)
		{
            this.releaseSetUpData = releaseSetUpData;
            InitializeComponent();

		}

        private void frmAlfrescoSetUp_Load(object sender, EventArgs e)
        {
            this.lbBatchClass.Text = this.releaseSetUpData.BatchClassName;
            this.lbDocumentClass.Text = this.releaseSetUpData.DocClassName;
            // put fields into an array for easy access by index number
            this.destinationFieldsArray[0] = this.DestinationField1;
            this.destinationFieldsArray[1] = this.DestinationField2;
            this.destinationFieldsArray[2] = this.DestinationField3;
            this.destinationFieldsArray[3] = this.DestinationField4;
            this.destinationFieldsArray[4] = this.DestinationField5;
            this.destinationFieldsArray[5] = this.DestinationField6;
            this.destinationFieldsArray[6] = this.DestinationField7;
            this.destinationFieldsArray[7] = this.DestinationField8;
            this.destinationFieldsArray[8] = this.DestinationField9;

            this.indexFieldsArray[0] = this.IndexField1;
            this.indexFieldsArray[1] = this.IndexField2;
            this.indexFieldsArray[2] = this.IndexField3;
            this.indexFieldsArray[3] = this.IndexField4;
            this.indexFieldsArray[4] = this.IndexField5;
            this.indexFieldsArray[5] = this.IndexField6;
            this.indexFieldsArray[6] = this.IndexField7;
            this.indexFieldsArray[7] = this.IndexField8;
            this.indexFieldsArray[8] = this.IndexField9;

            this.mandatoryFieldsArray[0] = this.lblMandatory1;
            this.mandatoryFieldsArray[1] = this.lblMandatory2;
            this.mandatoryFieldsArray[2] = this.lblMandatory3;
            this.mandatoryFieldsArray[3] = this.lblMandatory4;
            this.mandatoryFieldsArray[4] = this.lblMandatory5;
            this.mandatoryFieldsArray[5] = this.lblMandatory6;
            this.mandatoryFieldsArray[6] = this.lblMandatory7;
            this.mandatoryFieldsArray[7] = this.lblMandatory8;
            this.mandatoryFieldsArray[8] = this.lblMandatory9;

            // get the index fields
            this.indexFields = this.releaseSetUpData.IndexFields;

            this.initializeDocumentContent();
            this.initializeRepositoryFields();
            this.initializeReleaseLinks();
            
        }

        //**************************************************************
        // Function:	InitializeRepositoryFields
        // Purpose:		initialise the repository fields
        //
        // Input:		None
        //
        // Output:		None
        //*************************************************************/
        private void initializeRepositoryFields()
        {
            try
            {
                //load the existing values, 0 == existing, -1 == new
                if (this.releaseSetUpData.New == 0)
                {
                    // loading an existing doc, get the current repository data
                    CustomProperties properties = this.releaseSetUpData.CustomProperties;
                    this.UserName.Text = ReleaseUtils.getCustomProperty(properties, ReleaseConstants.CUSTOM_USERNAME);
                    this.Repository.Text = ReleaseUtils.getCustomProperty(properties, ReleaseConstants.CUSTOM_REPOSITORY);
                    this.Location.Text = ReleaseUtils.getCustomProperty(properties, ReleaseConstants.CUSTOM_LOCATION);
                    this.locationUuid = ReleaseUtils.getCustomProperty(properties, ReleaseConstants.CUSTOM_LOCATION_UUID);

                    WebServiceFactory.setEndpointAddress(this.Repository.Text);
                    AuthenticationUtils.startSession(this.UserName.Text, this.Password.Text);
                    if (this.browse == null || this.browse.IsDisposed)
                    {
                        try
                        {
                            // set the selected node in the tree
                            this.displayLocationTree(this.locationUuid);                            

                        }
                        catch (Exception e)
                        {

                            MessageBox.Show("Error displaying workspaces ");

                            Log log = new Log();
                            log.ErrorLog(".\\log\\", "frmAlfrescoSetUp method initializeRepositoryFields " + e.Message, e.StackTrace);

                        }
                        finally
                        {
                            this.Cursor = Cursors.Default;
                        }
                    }
                    
                    // set and display the content types
                    String contentType = ReleaseUtils.getCustomProperty(properties, ReleaseConstants.CUSTOM_CONTENT_TYPE);
                    this.setContentTypes(contentType);

                    // load the document content
                    ComboItem item;
                    String image = ReleaseUtils.getCustomProperty(properties, ReleaseConstants.CUSTOM_IMAGE);
                    if (image != null)
                    {
                        ComboBox.ObjectCollection items = this.Image.Items;
                        // start from 1 as it contains an empty first element
                        for (int i = 1; i < items.Count; i++)
                        {
                            item = (ComboItem)items[i];
                            if (item.ItemData.Equals(image))
                            {
                                this.Image.SelectedIndex = i;
                            }
                        }
                    }

                    String ocr = ReleaseUtils.getCustomProperty(properties, ReleaseConstants.CUSTOM_OCR);
                    if (ocr != null)
                    {
                        ComboBox.ObjectCollection items = this.OCR.Items;
                        // start from 1 as it contains an empty first element
                        for (int i = 1; i < items.Count; i++)
                        {
                            item = (ComboItem)items[i];
                            if (item.ItemData.Equals(ocr))
                            {
                                this.OCR.SelectedIndex = i;
                            }
                        }
                    }

                    String pdf = ReleaseUtils.getCustomProperty(properties, ReleaseConstants.CUSTOM_PDF);
                    if (pdf != null)
                    {
                        ComboBox.ObjectCollection items = this.PDF.Items;
                        // start from 1 as it contains an empty first element
                        for (int i = 1; i < items.Count; i++)
                        {
                            item = (ComboItem)items[i];
                            if (item.ItemData.Equals(pdf))
                            {
                                this.PDF.SelectedIndex = i;
                            }
                        }
                    }

                    // set the aspect options
                    ListBox.ObjectCollection aspectsItems = this.Aspects.Items;
                    
                    String aspectValue = "";

                    for (int j = 0; j < aspectsItems.Count; j++)
                    {

                        // for each item see if there is a custom property saved
                        item = (ComboItem)aspectsItems[j];
                        aspectValue = ReleaseUtils.getCustomProperty(properties, item.ItemData);
                        if (aspectValue != null && !aspectValue.Equals(""))
                        {
                            // set checkbox
                            this.Aspects.SetItemChecked(j, true);
                            this.Aspects.SelectedIndex = j;
                            // add the aspects to an array for later reference
                            this.selectedAspects.Add(aspectValue);

                        }
                    }

                    this.Aspects.Enabled = true;
                    
                }

            }
            catch (Exception e)
            {
                MessageBox.Show(ReleaseConstants.ERR_CHECK_CONNECTION);
                AuthenticationUtils.endSession();
                Log log = new Log();
                log.ErrorLog(".\\log\\", "frmAlfrescoSetUp method initializeRepositoryFields " + e.Message, e.StackTrace);
            }
        }


        //**************************************************************
        // Function:	initializeDocumentContent
        // Purpose:		sets the Document Content details, checks if PDF and
        //              ORC modules has been added to the ( -1 == enabled, 0 ==  NOT)
        // Input:		None
        //              
        // Output:		None
        //*************************************************************/
        private void initializeDocumentContent()
        {

            try
            {
                CustomProperties properties = this.releaseSetUpData.CustomProperties;
                
                // check if OCR has been enabled on the Ascent Document Class
                if (this.releaseSetUpData.TextFileEnabled == -1)
                {
                    
                    this.OCR.Enabled = true;
                    this.Image.Enabled = true;

                }
                // check if PDF has been enabled on the Ascent Document Class
                if (this.releaseSetUpData.KofaxPDFDocClassEnabled == -1)
                {

                    this.PDF.Enabled = true;
                    this.Image.Enabled = true;
                }

            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.StackTrace);
            }
        }

        //**************************************************************
        // Function:	initializeReleaseLinks
        // Purpose:		Sets the releaselinks if it is not a new release setup
        //              
        // Input:		None
        //              
        // Output:		None
        //*************************************************************/
        private void initializeReleaseLinks()
        {
            try
            {
                //load the existing values, 0 == existing, -1 == new
                if (this.releaseSetUpData.New == 0)
                {
                    if (this.releaseSetUpData.Links.Count > 0)
                    {
                        // for each ascent Link
                        // get the releaseLink and set the index field, 
                        AscentRelease.Links links = this.releaseSetUpData.Links;
                        ReleaseLink releaseLink;

                        foreach (AscentRelease.Link link in this.releaseSetUpData.Links)
                        {
                            // get the release link where the IndexFieldname == link name
                            releaseLink = this.getReleaseLinkByDestination(link.Destination);

                            if (releaseLink != null)
                            {
                                releaseLink.IndexFieldName = link.Source;
                            }
                        }
                        // display fields
                        this.displayDestinations(0);
                    }
                }
            }
            catch (Exception e)
            {
                MessageBox.Show("Error " + e.StackTrace);

            }
        }

        private ReleaseLink getReleaseLinkByDestination(String destination)
        {
            ReleaseLink releaseLink;
            for (int i = 0; i < this.releaseLinks.Count; i++)
            {
                releaseLink = (ReleaseLink)this.releaseLinks[i];
                if (releaseLink.Destination.Equals(destination))
                {
                    return releaseLink;
                }
            }
            // not found
            return null;
        }


        //**************************************************************
        // Function:	ShowForm
        // Purpose:		Display the Release Setup form to the user 
        //				(also displaying the frmWait)
        //
        // Input:		None
        //
        // Output:		The return value KfxReturnValue success/failure/Stopped
        //*************************************************************/
        internal KfxReturnValue ShowForm()
        {
            try
            {

                frmConnecting oWait = new frmConnecting(this);
                oWait.LoadSettings();

                DialogResult oResult = this.ShowDialog();

                if (oResult == DialogResult.Cancel)
                {
                    m_bCancel = true;

                    this.Close();
                }

                //If the user has canceled the UI, we must return KFX_REL_STOPPED
                if (!m_bCancel)
                {
                    return KfxReturnValue.KFX_REL_SUCCESS;
                }
                else
                {
                    return KfxReturnValue.KFX_REL_STOPPED;
                }

            }
            catch (Exception)
            {
                return KfxReturnValue.KFX_REL_ERROR;
            }
            finally
            {
                //Unload the form
                this.Close();
                this.Dispose(true);
                this.releaseSetUpData = null;


            }
        }

        //**************************************************************
        // Function:	ConnectButton_Click
        // Purpose:		event handler for the connect button
        //              validates the repository connection and populates the
        //              other fields dependent ont he select repository, eg content
        //              model, aspects etc
        // Input:		object sender, EventArgs e
        //
        // Output:		None
        //*************************************************************/
        private void btnConnect_Click(object sender, EventArgs e)
        {

            if (this.validateRepositoryConnection())
            {

                // Display a wait cursor while testing connection
                Cursor.Current = Cursors.WaitCursor;
                try
                {
                           
                    AuthenticationUtils.endSession();
                    
                    WebServiceFactory.setEndpointAddress(this.Repository.Text);                    
                    AuthenticationUtils.startSession(this.UserName.Text, this.Password.Text);

                    if (AuthenticationUtils.IsSessionValid)
                    {

                        if (this.browse == null || this.browse.IsDisposed)
                        {
                            try
                            {
                                this.displayLocationTree("");
                                // get the content types and aspects
                                this.setContentTypes(null);
                            }
                            catch (Exception ex)
                            {
                                MessageBox.Show("Error displaying folder spaces " + ex.StackTrace);
                                Log log = new Log();
                                log.ErrorLog(".\\log\\", "frmAlfrescoSetUp method btnConnect_Click " + ex.Message, ex.StackTrace);                                

                                if (this.browse != null)
                                {
                                    this.browse.Dispose();
                                }
                            }
                            finally
                            {
                                this.Cursor = Cursors.Default;
                            }
                        }

                    }
                    else
                    {
                        this.resetForm();
                        MessageBox.Show(ReleaseConstants.ERR_CONNECTION_FAIL);                        
                    }
                }
                catch (Exception)
                {
                    this.resetForm();
                    MessageBox.Show(ReleaseConstants.ERR_CONNECTION_FAIL);
                }
                Cursor.Current = Cursors.Default;
            }
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            AuthenticationUtils.endSession();
            this.Close();
        }

        private void displayLocationTree(String selectedUuid) 
        {
            try
            {
                this.repoService = WebServiceFactory.getRepositoryService();
                this.browse = new frmTreeViewBrowse(selectedUuid);
                this.browse.Owner = this;
                this.browse.RepoService = this.repoService;
                this.Cursor = Cursors.WaitCursor;
                this.browse.TopLevel = false;
                this.locationPanel.Controls.Add(browse);
                this.browse.Location = new Point(-18, -37);
                this.browse.Show();
            }
            catch (Exception e)
            {
                MessageBox.Show("Error displaying repository spaces");
                Log log = new Log();
                log.ErrorLog(".\\log\\", "frmAlfrescoSetUp method displayLocationTree " + e.Message, e.StackTrace);
            }


        }

        private void ContentType_SelectedIndexChanged(object sender, EventArgs e)
        {
            try
            {
                // clear any checked aspects
                this.clearAspects();

                // get the destination fields for the content type
                // and create new releaselinks
                this.releaseLinks = new ArrayList();
                // get the ClassDef assigned to the comboItem
                ComboItem comboItem = (ComboItem)this.ContentType.SelectedItem;
                Alfresco.DictionaryServiceWebService.ClassDefinition classDefinition = (Alfresco.DictionaryServiceWebService.ClassDefinition)comboItem.ItemTag;
                Alfresco.DictionaryServiceWebService.PropertyDefinition[] properties = classDefinition.properties;

                int propLength = 0;
                if (properties != null)
                {
                    int offset = 0;
                    foreach (Alfresco.DictionaryServiceWebService.PropertyDefinition prop in properties)
                    {
                        // dont add content type fields, these are for the tiff, ocr and pdf fields
                        String dataType = prop.dataType;

                        if (dataType.EndsWith("content"))
                        {
                            // add this to the document content options
                            ComboItem contentComboItem = new ComboItem(prop.title, prop.name);
                            if (this.OCR.Enabled || this.PDF.Enabled)
                            {

                                if (this.OCR.Items.Count == 0)
                                {
                                    this.OCR.Items.Add("");
                                    this.PDF.Items.Add("");
                                }

                                this.OCR.Items.Add(contentComboItem);
                                this.PDF.Items.Add(contentComboItem);
                            }
                            else
                            {
                                if (this.Image.Items.Count == 0)
                                {
                                    this.Image.Items.Add("");
                                }
                            }

                            this.Image.Items.Add(contentComboItem);

                            offset++;
                        }
                        else
                        {
                            String title = prop.title;
                            if (title != null && !title.Equals(""))
                            {
                                this.addReleaseLink(title, ReleaseConstants.CONTENT_TYPE + prop.name, dataType, prop.mandatory);
                            }
                            else
                            {
                                // dont add 
                                offset++;
                            }
                        }
                    }
                    propLength = properties.Length - offset;

                }

                if (propLength < this.numFieldsDisplayed)
                {
                    // hide the extra fields
                    for (int i = propLength; i < this.numFieldsDisplayed; i++)
                    {
                        this.removeFields(i);
                    }
                }

                this.destinationFieldCount = propLength;

                if (this.destinationFieldCount <= MAX_FILEDS)
                {
                    this.numFieldsDisplayed = this.destinationFieldCount;
                }
                else
                {
                    this.numFieldsDisplayed = MAX_FILEDS;
                    // turn on the scroll bar and set the max scroll size
                    this.vScrollBar1.Maximum = this.destinationFieldCount;
                    this.vScrollBar1.Visible = true;
                }

                // display the new links and enable the aspects
                this.Aspects.Enabled = true;
                this.displayDestinations(0);
            }
            catch (Exception ex)
            {
                Log log = new Log();
                log.ErrorLog(".\\log\\", "frmAlfrescoSetUp method ContentType_SelectedIndexChanged " + ex.Message, ex.StackTrace);
            }
            
        }

        // add a releaselink and the index options based on field type
        private ReleaseLink addReleaseLink(String displayDestName, String destination, String type, bool mandatory)
        {

            ReleaseLink releaseLink = new ReleaseLink(displayDestName, destination, type);
            try
            {
               
                // get the options based on field type
                ArrayList options = new ArrayList();
                options.Add("");
                foreach (AscentRelease.IndexField index in this.indexFields)
                {

                    if (ReleaseUtils.equalsType(index.Type, type))
                    {
                        // add
                        options.Add(index.Name);
                    }
                }
                releaseLink.IndexOptions = options;

                if (mandatory)
                {
                    releaseLink.Mandatory = true;
                }

                this.releaseLinks.Add(releaseLink);

            }
            catch (Exception ex)
            {

                Log log = new Log();
                log.ErrorLog(".\\log\\", "frmAlfrescoSetUp method addReleaseLink " + ex.Message, ex.StackTrace);

            }
            return releaseLink;
        }

        private void setAspectsOptions(ComboItem[] items) {

            this.Aspects.Items.Clear();
            this.Aspects.Items.AddRange(items);
            this.Aspects.SelectedIndex = -1;
            this.Aspects.Enabled = true;

        }

        //**************************************************************
        // Function:	displayIndexes
        // Purpose:		Displays the index and destination fields at a start 
        //              position
        // Input:		startPos - the start position of the releaseLinks
        //
        // Output:		None
        //*************************************************************/
        private void displayDestinations(int startPos)
        {
            // display the index fields from the new start pos
            for (int i = 0; i < this.numFieldsDisplayed; i++)
            {
                ReleaseLink l = (ReleaseLink)releaseLinks[i + startPos];
                displayDestination((ReleaseLink)releaseLinks[i + startPos], i);
            }
        }

        private void displayDestinations(int startPos, int exemptPos)
        {
            // display the index fields from the new start pos
            for (int i = 0; i < this.numFieldsDisplayed; i++)
            {
                if (i != startPos)
                {
                    this.displayDestination((ReleaseLink)releaseLinks[i + startPos], i);
                }
            }
        }

        //**************************************************************
        // Function:	displayDestination
        // Purpose:		displays an index and destination field
        //              
        // Input:		Releaselink link - contains the link details
        //              int displayField - the field position on the form
        // Output:		None
        //*************************************************************/
        internal void displayDestination(ReleaseLink link, int displayField)
        {
            this.updateReleaseLink = false;
            TextBox textBox = this.destinationFieldsArray[displayField];
            textBox.Text = link.DisplayDestinationName;
            textBox.Visible = true;

            // check if mandatory
            Label label = this.mandatoryFieldsArray[displayField];
            if (link.Mandatory)
            {                
                label.Visible = true;
            }
            else
            {
                label.Visible = false;
            }

            ComboBox comboBox = this.indexFieldsArray[displayField];
            comboBox.SelectedIndex = -1;
            // get the index options 
            ArrayList options = link.IndexOptions;
            comboBox.Items.Clear();
            comboBox.Items.AddRange(options.ToArray());

            comboBox.Tag = link;
            comboBox.Visible = true;

            String indexValue = link.IndexFieldName;
            if (indexValue != null && !indexValue.Equals("") && options != null && options.Count > 0)
            {
                // find the selectedIndex position in the combobox options     
                comboBox.SelectedIndex = options.IndexOf(indexValue);
            }

            this.updateReleaseLink = true;
            
        }

        private void setContentTypes(String contentType)
        {

            // set the content model type and aspects options
            this.ContentType.Items.Clear();
            ComboItem item;
            ComboItem selectedItem = null;

            // only get the document types of subtype Content
            Alfresco.DictionaryServiceWebService.ClassPredicate classPredicate = new Alfresco.DictionaryServiceWebService.ClassPredicate();
            classPredicate.names = new String[] { "cm:content" };
            classPredicate.followSubClass = true;

            Alfresco.DictionaryServiceWebService.ClassDefinition[] classDefinitions = WebServiceFactory.getDictionaryService().getClasses(classPredicate, null);
            foreach (Alfresco.DictionaryServiceWebService.ClassDefinition classDefinition in classDefinitions)
            {

                string displayLabel = classDefinition.title;
                if (displayLabel != null && displayLabel.Trim().Length != 0)
                {
                    item = new ComboItem(displayLabel, classDefinition.name);
                    item.ItemTag = classDefinition;
                    if (classDefinition.isAspect)
                    {
                        this.Aspects.Items.Add(item);
                    }
                    else
                    {
                        
                        this.ContentType.Items.Add(item);
                        
                        if (contentType != null && contentType.Equals(classDefinition.name))
                        {
                            // this is the current selected item
                            selectedItem = item;                            
                        }
                    }
                }
            }

            if (selectedItem != null)
            {
                this.ContentType.SelectedItem = selectedItem;
            }
            
            this.ContentType.Enabled = true;
        }

        private void vScrollBar1_Scroll(object sender, ScrollEventArgs e)
        {
            int pos = this.vScrollBar1.Value;
            // display the index fields staring from the new pos
            this.currentIndexStartPos = pos;
            this.displayDestinations(pos);
        }

        //**************************************************************
        // Function:	setIndex
        // Purpose:		Saves the index value in the releaseLink
        //              at the form position + currentIndexStartPos
        // Input:		int pos - the destination field position on the form
        //              String value - the actual value to be saved
        // Output:		None
        //*************************************************************/
        private void setIndexInReleaseLink(int pos, String value)
        {
            if (this.updateReleaseLink)
            {
                // get the correct position in the array
                ReleaseLink link = (ReleaseLink)releaseLinks[pos + this.currentIndexStartPos];
                link.IndexFieldName = value;
            }            
        }     

        private void IndexField1_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField1.SelectedItem;
            this.indexedChanged(index, 0);
        }

        private void IndexField2_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField2.SelectedItem;
            this.indexedChanged(index, 1);
        }

        private void IndexField3_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField3.SelectedItem;
            this.indexedChanged(index, 2);
        }

        private void IndexField4_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField4.SelectedItem;
            this.indexedChanged(index, 3);            
        }

        private void IndexField5_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField5.SelectedItem;
            this.indexedChanged(index, 4);
        }

        private void IndexField6_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField6.SelectedItem;
            this.indexedChanged(index, 5);
        }

        private void IndexField7_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField7.SelectedItem;
            this.indexedChanged(index, 6);
        }

        private void IndexField8_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField8.SelectedItem;
            this.indexedChanged(index, 7);
        }

        private void IndexField9_SelectedIndexChanged(object sender, EventArgs e)
        {
            String index = (String)IndexField9.SelectedItem;
            this.indexedChanged(index, 8);
        }

        // add the selected option to the release link and
        // remove the option from other index field options
        private void indexedChanged(String index, int fieldPos)
        {

            if (index != null)
            {
                // get the current value and add the option back in
                String prevIndex = this.getIndexFromReleaseLink(fieldPos);

                if (prevIndex != null && !prevIndex.Equals(""))
                {
                    // add the index option
                    this.addIndexOption(prevIndex);
                }

                this.setIndexInReleaseLink(fieldPos, index);
                /*
                // remove the current option
                if (!index.Equals(""))
                {
                    this.removeIndexOption(index, fieldPos);
                }
                */
            }
        }

        private void bnApply_Click(object sender, EventArgs e)
        {
            // Validate Data
            if (this.validate())
            {
                this.save();
            }
        }

        //**************************************************************
        // Function:	getIndexFromReleaseLink
        // Purpose:		get the curent Index value in the release link
        //              
        // Input:		int pos - the releaselink position
        //
        // Output:		String - the index value
        //*************************************************************/
        private String getIndexFromReleaseLink(int pos)
        {
            ReleaseLink link = (ReleaseLink)releaseLinks[pos + this.currentIndexStartPos];
            return link.IndexFieldName;     
        }

        //**************************************************************
        // Function:	addIndexOption
        // Purpose:		add index value to the rest of the other
        //              drop down list in the index field
        // Input:		None
        //
        // Output:		None
        //*************************************************************/
        private void addIndexOption(String index)
        {
            // loop thru the releaselinks and update the
            // options
            ArrayList options = new ArrayList();
            ArrayList removedOptions = new ArrayList();
            foreach (ReleaseLink link in this.releaseLinks)
            {

                options = link.IndexOptions;
                removedOptions = link.RemovedIndexes;

                if (!options.Contains(index) && removedOptions.Contains(index))
                {
                    options.Add(index);
                    link.IndexOptions = options;
                }                
            }

            ReleaseLink releaseLink;
            ComboBox comboBox;
            // now for the fields on display
            // now update the fields on displayed            
            for (int i = 0; i < this.numFieldsDisplayed; i++)
            {
                
                comboBox = this.indexFieldsArray[i];
                
                releaseLink = (ReleaseLink)comboBox.Tag;
                if (releaseLink != null)
                {
                    removedOptions = releaseLink.RemovedIndexes;
                    // if this is an index removed below add it back
                    if (removedOptions.Contains(index))
                    {

                        // update the tag release link
                        comboBox.Items.Add(index);
                        removedOptions.Remove(index);
                        releaseLink.RemovedIndexes = removedOptions;
                        comboBox.Tag = releaseLink;
                    }
                }
            }
        }

        //**************************************************************
        // Function:	removeIndexOption
        // Purpose:		Remove the selected index value from the rest
        //              of the other drop down list in the index field
        // Input:		String index - the index value to be removed
        //              int exemptPos - the position of the exempt 
        //                              field on display
        // Output:		bool  : false - invalid data
        //*************************************************************/
        private void removeIndexOption(String index, int exemptPos)
        {
            // loop thru the releaselinks and update the
            // options
            ArrayList options = new ArrayList();
            ArrayList removedOptions = new ArrayList();
            int i = 0;
            foreach (ReleaseLink link in this.releaseLinks)
            {
                options = link.IndexOptions;
                // dont remove from the current releaseLink
                if (i != exemptPos + this.currentIndexStartPos && options.Contains(index))
                {

                    // only remove if its in the options
                    options.Remove(index);
                    link.IndexOptions = options;

                    // store the removed index for future reference if added back
                    removedOptions = link.RemovedIndexes;
                    removedOptions.Add(index);
                    link.RemovedIndexes = removedOptions;
                }
                i++;
            }

            // now update the fields on displayed
            ReleaseLink releaseLink;
            ComboBox comboBox;
            for (i = 0; i < this.numFieldsDisplayed; i++)
            {

                if (i != exemptPos)
                {
                    // remove from the tag data
                    comboBox = this.indexFieldsArray[i];
                    if (comboBox.Items.IndexOf(index) != -1)
                    {

                        releaseLink = (ReleaseLink)comboBox.Tag;
                        comboBox.Items.Remove(index);
                        options = releaseLink.RemovedIndexes;
                        options.Add(index);
                        releaseLink.RemovedIndexes = options;
                        comboBox.Tag = releaseLink;
                    }
                }
            }
        }

        private void btnSave_Click(object sender, EventArgs e)
        {

            // Validate Data
            if (this.validate())
            {
                this.save();
                this.Close();
            }
        }

        //**************************************************************
        // Function:	validate
        // Purpose:		validates the data
        //
        // Input:		None
        //
        // Output:		bool  : false - invalid data
        //*************************************************************/
        private bool validate()
        {

            bool valid = true;

            // validates the repository connection fields first
            // before validating the other fields
            if (valid = this.validateRepositoryConnection())
            {
                if (this.Location.Text.Equals(""))
                {
                    this.tabControl1.SelectedTab = this.tabPage1;
                    MessageBox.Show(ReleaseConstants.VALIDATE_LOCATION);
                    valid = false;
                }
                else if (this.ContentType.Text.Equals(""))
                {
                    this.tabControl1.SelectedTab = this.tabPage2;
                    MessageBox.Show(ReleaseConstants.VALIDATE_CONTENT_TYPE);                    
                    this.ContentType.Focus();
                    valid = false;
                }
                else if (!this.validateDocumentContent())
                {
                    return false;
                }
                else
                {
                    valid = this.validateMandatoryFields();
                }
            }

            return valid;

        }

        //**************************************************************
        // Function:	validateRepositoryConnection
        // Purpose:		validates the repository connection fields
        //
        // Input:		None
        //
        // Output:		bool  : false - invalid data
        //*************************************************************/
        private bool validateRepositoryConnection()
        {
            bool valid = true;

            String userName = this.UserName.Text;
            String password = this.Password.Text;
            String repository = this.Repository.Text;

            if (userName.Equals(""))
            {
                this.tabControl1.SelectedTab = this.tabPage1;
                MessageBox.Show(ReleaseConstants.VALIDATE_USER_NAME);
                this.UserName.Focus();
                valid = false;
            }
            else if (password.Equals(""))
            {
                this.tabControl1.SelectedTab = this.tabPage1;
                MessageBox.Show(ReleaseConstants.VALIDATE_PASSWORD);
                this.Password.Focus();
                valid = false;
            }
            else if (repository.Equals(""))
            {
                this.tabControl1.SelectedTab = this.tabPage1;
                MessageBox.Show(ReleaseConstants.VALIDATE_REPOSITORY);
                this.Repository.Focus();
                valid = false;
            }

            return valid;
        }

        private bool validateDocumentContent()
        {
            bool valid = true;
            String image = this.Image.Text;
            String ocr = this.OCR.Text;
            String pdf = this.PDF.Text;
            if (image == null || image.Equals(""))
            {
                // check the ocr and pdf
                if (ocr.Equals("") && pdf.Equals(""))
                {
                    MessageBox.Show(ReleaseConstants.VALIDATE_DOCUMENT_CONTENT);
                    this.tabControl1.SelectedTab = this.tabPage2;
                    this.Image.Focus();
                    valid = false;
                }
                else if (pdf.Equals(ocr))
                {
                    MessageBox.Show("OCR and PDF document contents are the same");
                    this.tabControl1.SelectedTab = this.tabPage2;
                    this.OCR.Focus();
                    valid = false;
                }  
            }
            else
            {
                // check for duplicates
                if (image.Equals(ocr))
                {
                    MessageBox.Show("Image and OCR document contents are the same");
                    this.tabControl1.SelectedTab = this.tabPage2;
                    this.Image.Focus();
                    valid = false;
                }
                else if (image.Equals(pdf))
                {
                    MessageBox.Show("Image and PDF document contents are the same");
                    this.tabControl1.SelectedTab = this.tabPage2;
                    this.Image.Focus();
                    valid = false;
                }
                else if (pdf.Equals(ocr) && !pdf.Equals(""))
                {
                    MessageBox.Show("OCR and PDF document contents are the same");
                    this.tabControl1.SelectedTab = this.tabPage2;
                    this.OCR.Focus();
                    valid = false;
                }
            }

            return valid;
        }

        //**************************************************************
        // Function:	validateMandatoryFields
        // Purpose:		validates mandatory fields has been selected
        //              
        // Input:		None
        //
        // Output:		bool  : false - invalid data
        //*************************************************************/
        private bool validateMandatoryFields()
        {
            bool isValid = true;
            ArrayList missing = new ArrayList();
            String index = "";
            foreach (ReleaseLink link in this.releaseLinks)
            {
                if (link.Mandatory)
                {
                    // check index has been assigned
                    index = link.IndexFieldName;
                    if (index == null || index.Equals(""))
                    {
                        missing.Add(link.DisplayDestinationName);
                        isValid = false;
                    }                    
                }
            }

            if (!isValid)
            {

                this.tabControl1.SelectedTab = this.tabPage2;
                String mess = ReleaseConstants.VALIDATE_MISSING_MANDATORY_FIELDS;
                foreach (String field in missing)
                {
                    mess += field + "\n";
                }
                MessageBox.Show(mess);
            }

            return isValid;

        }

        //**************************************************************
        // Function:	save
        // Purpose:		Saves the data
        //              
        // Input:		None
        //
        // Output:		None
        //*************************************************************/
        private void save()
        {
            try
            {

                //we want to clear out the Custom Properties and Links so 
                //we don't get dup errors
                this.releaseSetUpData.Links.RemoveAll();
                this.releaseSetUpData.CustomProperties.RemoveAll();

                // save the repository, user name and password to the ReleaseSetUpData object
                // as a custom property to be accessed in the openScript method
                this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_USERNAME, this.UserName.Text);
                this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_PASSWORD, this.Password.Text);
                this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_REPOSITORY, this.Repository.Text);
                // save the location data
                this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_LOCATION, this.Location.Text);
                this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_LOCATION_UUID, this.locationUuid);

                // document content to be saved
                ComboItem comboItem;
                if (!this.Image.Text.Equals(""))
                {
                    comboItem = (ComboItem)this.Image.SelectedItem;
                    this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_IMAGE, comboItem.ItemData);
                }
                if (!this.OCR.Text.Equals(""))
                {
                    comboItem = (ComboItem)this.OCR.SelectedItem;
                    this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_OCR, comboItem.ItemData);
                }
                if (!this.PDF.Text.Equals(""))
                {
                    comboItem = (ComboItem)this.PDF.SelectedItem;
                    this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_PDF, comboItem.ItemData);
                }
 
                // save the content type
                comboItem = (ComboItem)this.ContentType.SelectedItem;
                this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_CONTENT_TYPE, comboItem.ItemData);
                
                // save the aspects
                ListBox.ObjectCollection aspectsItems = this.Aspects.Items;
                int aspectSize = aspectsItems.Count;
                String aspects = "";
                ComboItem item;
                for (int j = 0; j < aspectsItems.Count; j++)
                {
                    if (this.Aspects.GetItemChecked(j))
                    {

                        item = (ComboItem)aspectsItems[j];
                        String aspect = item.ItemData;
                        aspects += aspect + ReleaseConstants.SEPERATOR;
                        this.releaseSetUpData.CustomProperties.Add(aspect, aspect);
                    }
                }
                if (!aspects.Equals(""))
                {
                    // remove the last seperator
                    aspects = aspects.Substring(0, aspects.Length - ReleaseConstants.SEPERATOR.Length);
                    // save all the aspects in 1 custom property
                    this.releaseSetUpData.CustomProperties.Add(ReleaseConstants.CUSTOM_ASPECTS, aspects);
                }

                // Create "index - destinations" release data links
                ReleaseLink link;
                for (int i = 0; i < this.destinationFieldCount; i++)
                {
                    link = (ReleaseLink)this.releaseLinks[i];
                    String index = link.IndexFieldName;
                    String destination = link.Destination;
                    if (index != null && !index.Equals(""))
                    {                        
                        this.releaseSetUpData.Links.Add(index, KfxLinkSourceType.KFX_REL_INDEXFIELD,
                                    destination);
                    }
                }

                //FIX SPR#27062
                GC.Collect();
                GC.WaitForPendingFinalizers();
                this.releaseSetUpData.Apply();

            }
            catch (Exception ex)
            {
                MessageBox.Show("Error saving release script");
                Log log = new Log();
                log.ErrorLog(".\\log\\", "frmAlfrescoSetUp method save " + ex.Message, ex.StackTrace);
            }
        }

        private void Aspects_SelectedIndexChanged(object sender, EventArgs e)
        {
            // get the selected item and find its position within the checklistbox
            ComboItem comboItem = (ComboItem)this.Aspects.SelectedItem;
            String aspect = comboItem.ToString();

            int i = this.Aspects.FindString(aspect);
            ReleaseLink[] aspectReleaseLinks;
            if (this.Aspects.GetItemChecked(i))
            {
                // only add the aspests if it hasn't been added before, this is handle the double click bug
                // to prevent duplicates
                if (this.selectedAspects.IndexOf(aspect) == -1)
                {
                    
                    // get any extra fields with the new aspect
                    aspectReleaseLinks = this.addFieldsFromAspect(aspect,
                        (Alfresco.DictionaryServiceWebService.ClassDefinition)comboItem.ItemTag);

                    if (aspectReleaseLinks.Length > 0)
                    {
                        // attach the array of aspect release links to the 
                        // selected check box item
                        comboItem.Releaselinks = aspectReleaseLinks;

                    }
                    // store the aspect so that it's not added again
                    this.selectedAspects.Add(aspect);
                }
            }
            else
            {
                // only remove the aspests if it has been added before
                if (this.selectedAspects.IndexOf(aspect) != -1)
                {
                    // remove the aspect fields
                    aspectReleaseLinks = comboItem.Releaselinks;
                    if (aspectReleaseLinks != null)
                    {
                        this.removeReleaseLinks(aspectReleaseLinks);
                    }
                    this.selectedAspects.Remove(aspect);
                }

               
            }
        }

        //**************************************************************
        // Function:	addFieldsFromAspect
        // Purpose:		gets the aspect fields and redraw the fields
        //              
        // Input:		String aspect   -   aspect name
        //              ClassDefinition classDefinition - class def of the aspect
        // Output:		an array of the new release links created for the aspect
        //*************************************************************/
        private ReleaseLink[] addFieldsFromAspect(String aspect,
            Alfresco.DictionaryServiceWebService.ClassDefinition classDefinition)
        {

            Alfresco.DictionaryServiceWebService.PropertyDefinition[] properties = classDefinition.properties;
            ArrayList aspectReleaseLinks = new ArrayList();
            int propLength = 0;
            if (properties != null)
            {
                int offset = 0;
                foreach (Alfresco.DictionaryServiceWebService.PropertyDefinition prop in properties)
                {
                    // dont add content type fields, these are for the tiff, ocr and pdf fields
                    String dataType = prop.dataType;

                    if (dataType.EndsWith("content"))
                    {
                        // do something with the document content fields
                        offset++;
                    }
                    else
                    {

                        String title = prop.title;
                        if (title != null && !title.Equals(""))
                        {
                            aspectReleaseLinks.Add(this.addReleaseLink(title + " (" + aspect + ")", ReleaseConstants.ASPECT + ReleaseConstants.SEPERATOR + classDefinition.name + prop.name, dataType, prop.mandatory));
                        }
                        else
                        {
                            // dont add 
                            offset++;
                        }
                    }
                }
                propLength = properties.Length - offset;
            }
            this.destinationFieldCount = this.destinationFieldCount + propLength;
            if (this.destinationFieldCount < MAX_FILEDS)
            {
                this.numFieldsDisplayed = this.destinationFieldCount;
                this.vScrollBar1.Visible = false;
            }
            else
            {
                numFieldsDisplayed = MAX_FILEDS;
                // turn on the scroll bar and set the max scroll size
                this.vScrollBar1.Maximum = this.destinationFieldCount;
                this.vScrollBar1.Visible = true;
            }
            this.displayDestinations(this.currentIndexStartPos);
            return (ReleaseLink[])aspectReleaseLinks.ToArray(typeof(ReleaseLink));
        }

        //**************************************************************
        // Function:	removeReleaseLinks
        // Purpose:		removes the releaseLinks passed in
        //              
        // Input:		ReleaseLink[] linksToRemove - the ReleaseLinks to be removed
        //
        // Output:		none
        //*************************************************************/
        private void removeReleaseLinks(ReleaseLink[] linksToRemove)
        {
            
            foreach (ReleaseLink link in linksToRemove)
            {
                this.releaseLinks.Remove(link);
            }
            int removeCount = linksToRemove.Length;
            this.destinationFieldCount = this.destinationFieldCount - removeCount;
            if (this.destinationFieldCount < MAX_FILEDS)
            {
                this.numFieldsDisplayed = this.destinationFieldCount;
                // need to remove and hide the extra fields                
                for (int i = this.numFieldsDisplayed; i < MAX_FILEDS; i++)
                {
                    this.removeFields(i);
                }
                this.vScrollBar1.Visible = false;
            }

            this.displayDestinations(0);
        }

        //**************************************************************
        // Function:	removeFields
        // Purpose:		hides the field mappings at position provided
        //              
        // Input:		int displayField - the field set position
        //
        // Output:		none
        //*************************************************************/
        private void removeFields(int displayField)
        {
            TextBox textBox = this.destinationFieldsArray[displayField];
            textBox.Text = "";
            textBox.Visible = false;
            Label label = this.mandatoryFieldsArray[displayField];
            label.Visible = false;
            ComboBox comboBox = this.indexFieldsArray[displayField];
            comboBox.Visible = false;
             
        }

        private void clearAspects()
        {

            for ( int i = 0; i < this.Aspects.Items.Count; i++) 
            {
                if (this.Aspects.GetItemChecked(i))
                {
                    this.Aspects.SetItemChecked(i, false);
                }
            }
        }

        private void btnReset_Click(object sender, EventArgs e)
        {
            this.reset(true);            
        }

        // pass in reset option
        private void reset(bool fullReset)
        {
            if (this.releaseSetUpData.New == 0)
            {
                releaseLinks = new ArrayList();
                selectedAspects = new ArrayList();
                this.Location.ResetText();
                this.resetComboBox(this.ContentType, false);
                this.Aspects.Items.Clear();
                this.Aspects.ResetText();
                this.Aspects.Enabled = false;
                this.resetComboBox(this.Image);
                this.resetComboBox(this.OCR);
                this.resetComboBox(this.PDF);
                this.initializeDocumentContent();
                this.initializeRepositoryFields();
                this.initializeReleaseLinks();
            }
            else
            {
                if (fullReset)
                {
                    this.resetRepository();
                }
                this.resetForm();
            }
        }

        private void resetForm()
        {
            if (this.browse != null)
            {
                this.browse.Dispose();
                this.locationPanel.Controls.Remove(this.browse);
            }

            this.Location.ResetText();
            this.resetComboBox(this.ContentType, false);
            this.Aspects.Items.Clear();
            this.Aspects.ResetText();
            this.Aspects.Enabled = false;
            this.resetComboBox(this.Image);
            this.resetComboBox(this.OCR);
            this.resetComboBox(this.PDF);
            
            releaseLinks = new ArrayList();
            selectedAspects = new ArrayList();
            this.numFieldsDisplayed = 0;
            this.currentIndexStartPos = 0;
            this.destinationFieldCount = 0;

            for (int i = 0; i < MAX_FILEDS; i++)
            {
                this.removeFields(i);
            }
            
        }

        private void resetRepository()
        {
            this.UserName.ResetText();
            this.Password.ResetText();
            this.Repository.ResetText();
        }

        private void resetComboBox(ComboBox comboBox, bool enabled)
        {
            this.resetComboBox(comboBox);
            comboBox.Enabled = enabled;
        }

        private void resetComboBox(ComboBox comboBox)
        {
            comboBox.ResetText();
            comboBox.Items.Clear();
        }

        private void aboutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show("Kofax to Alfresco Release Script V1.0");
        }



    }
}