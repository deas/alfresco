/// Author:    Manfung Chan
/// Version:   v1.0

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Alfresco;
using Alfresco.RepositoryWebService;
using KofaxAlfrescoRelease_v1;

namespace KofaxAlfrescoRelease_v1
{
    public partial class frmTreeViewBrowse : Form
    {
        private Alfresco.RepositoryWebService.Store spacesStore;
        private RepositoryService repoService;
        private frmAlfrescoSetUp parentForm;
        private TreeNode preTreeNode;
        private String selectedUuid;

        public RepositoryService RepoService
        {
            set { repoService = value; }
        }

        public frmTreeViewBrowse(String selectUuid)
        {
            
            try
            {
                
                InitializeComponent();
                this.selectedUuid = selectUuid;
                // Initialise the reference to the spaces store
                this.spacesStore = new Alfresco.RepositoryWebService.Store();
                this.spacesStore.scheme = Alfresco.RepositoryWebService.StoreEnum.workspace;
                this.spacesStore.address = "SpacesStore";

                // Load the images in an ImageList.
                ImageList myImageList = new ImageList();

//                myImageList.Images.Add(Image.FromFile("C:\\Temp\\ftv2folderclosed.gif"));
//                myImageList.Images.Add(Image.FromFile("C:\\Temp\\ftv2folderopen.gif"));

                myImageList.Images.Add(KofaxAlfrescoRelease_v1.Properties.Resources.ftv2folderclosed);
                myImageList.Images.Add(KofaxAlfrescoRelease_v1.Properties.Resources.ftv2folderopen);

                // Assign the ImageList to the TreeView.
                treeView1.ImageList = myImageList;

                treeView1.ImageIndex = 0;
                treeView1.SelectedImageIndex = 1;
            }
            catch (Exception e)
            {
                Log log = new Log();
                log.ErrorLog(".\\log\\", "constructor frmTreeViewBrowse " + e.Message, e.StackTrace);
            }

        }

        /// <summary>
        /// The form load event handler
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Browse_Load(object sender, EventArgs e)
        {
            this.initializeRootFolder();
            this.parentForm = (frmAlfrescoSetUp)this.Owner;
        }

        private void initializeRootFolder()
        {
            try
            {
                // Suppress repainting the TreeView until all the objects have been created.
                this.treeView1.BeginUpdate();

                // get the root position, Company Home

                TreeNode rootNode = new TreeNode();

                Alfresco.RepositoryWebService.Reference reference = new Alfresco.RepositoryWebService.Reference();
                reference.store = this.spacesStore;
                reference.path = "/app:company_home";

                // Create a query object
                Query query = new Query();
                query.language = QueryLanguageEnum.lucene;
                query.statement = "Path:\"/\" AND @cm\\:title:\"Company Home\"";

                QueryResult result = this.repoService.query(this.spacesStore, query, true);
                string name = null;
                if (result.resultSet.rows != null)
                {
                    // construct root node
                    ResultSetRow row = result.resultSet.rows[0];
                    foreach (NamedValue namedValue in row.columns)
                    {
                        if (namedValue.name.Contains("title") == true)
                        {
                            name = namedValue.value;
                            rootNode.Text = name;
                            rootNode.Name = name;
                        }
                    }
                    rootNode.Tag = row.node;
                    if (this.selectedUuid.Equals(row.node.id))
                    {
                        this.setInitNode(rootNode);
                    }
                }
                // add the root node to the tree view
                this.treeView1.Nodes.AddRange(new System.Windows.Forms.TreeNode[] { rootNode });
                try
                {
                    this.buildTree(rootNode, reference);
                }
                catch (Exception e)
                {
                    Log log = new Log();
                    log.ErrorLog(".\\log\\", "frmTreeViewBrowse method initializeRootFolder calling buildtree recursion method " + e.Message, e.StackTrace);
                }
                

                // Begin repainting the TreeView.
                this.treeView1.EndUpdate();
            }
            catch (Exception ex)
            {
                Log log = new Log();
                log.ErrorLog(".\\log\\", "frmTreeViewBrowse method initializeRootFolder " + ex.Message, ex.StackTrace);
            }

            

        }

        private void setInitNode(TreeNode node)
        {

            node.BackColor = SystemColors.Highlight;
            node.ForeColor = SystemColors.HighlightText;

            // traverse back up the nodes and expand
            TreeNode parent = node.Parent;
            while (parent != null)
            {
                parent.Expand();
                parent = parent.Parent;
            }

            this.preTreeNode = node;
            
        }

        /// <summary>
        /// Constructs and adds a child node to the tree view at the parentNode supplied
        /// </summary>
        private TreeNode addChildNode(TreeNode parentNode, String name, ResultSetRowNode rsrNode)
        {

            TreeNode node = new TreeNode(name);
            node.Text = name;
            node.Tag = rsrNode;
            parentNode.Nodes.Add(node);
            return node;
        }
        
        private void buildTree(TreeNode parentNode, Reference childReference)
        {
            try
            {
                // Query for the children of the reference
                QueryResult result = this.repoService.queryChildren(childReference);
                if (result.resultSet.rows != null)
                {
                    foreach (ResultSetRow row in result.resultSet.rows)
                    {
                        // only interested in folders
                        if (row.node.type.Contains("folder") == true)
                        {
                            foreach (NamedValue namedValue in row.columns)
                            {
                                if (namedValue.name.Contains("name") == true)
                                {
                                    // add a node to the tree view
                                    TreeNode node = this.addChildNode(parentNode, namedValue.value, row.node);

                                    // Create the reference for the node selected
                                    Alfresco.RepositoryWebService.Reference reference = new Alfresco.RepositoryWebService.Reference();
                                    reference.store = this.spacesStore;
                                    reference.uuid = row.node.id;

                                    if (this.selectedUuid.Equals(reference.uuid))
                                    {
                                        this.setInitNode(node);
                                    }

                                    // add the child node
                                    buildTree(node, reference);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Log log = new Log();
                log.ErrorLog(".\\log\\", "frmTreeViewBrowse method buildTree(" + childReference.uuid + ") " + e.Message, e.StackTrace);
            }


        }

        private void treeView1_AfterSelect(object sender, TreeViewEventArgs e)
        {
            ResultSetRowNode node = (ResultSetRowNode)this.treeView1.SelectedNode.Tag;
            this.parentForm.LocationUuid = node.id;
            this.parentForm.LocationName = treeView1.SelectedNode.FullPath;
            TreeNode selectedNode = this.treeView1.SelectedNode;
            
            selectedNode.BackColor = SystemColors.Highlight;
            selectedNode.ForeColor = SystemColors.HighlightText;

            if (this.preTreeNode != null) 
            {
                this.preTreeNode.BackColor = Color.Empty;
                this.preTreeNode.ForeColor = Color.Empty;
            }
            this.preTreeNode = treeView1.SelectedNode;
        }


    }
}