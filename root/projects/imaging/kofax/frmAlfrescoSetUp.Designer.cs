namespace KofaxAlfrescoRelease_v1
{
    using Alfresco;
    partial class frmAlfrescoSetUp
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {

            if (disposing && (components != null))
            {
                AuthenticationUtils.endSession();
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.lblUserName = new System.Windows.Forms.Label();
            this.UserName = new System.Windows.Forms.TextBox();
            this.lblPassword = new System.Windows.Forms.Label();
            this.Password = new System.Windows.Forms.TextBox();
            this.lblRepository = new System.Windows.Forms.Label();
            this.Repository = new System.Windows.Forms.ComboBox();
            this.btnConnect = new System.Windows.Forms.Button();
            this.lblLocation = new System.Windows.Forms.Label();
            this.btnCancel = new System.Windows.Forms.Button();
            this.btnSave = new System.Windows.Forms.Button();
            this.Location = new System.Windows.Forms.TextBox();
            this.lbApect = new System.Windows.Forms.Label();
            this.ContentType = new System.Windows.Forms.ComboBox();
            this.lbContentModel = new System.Windows.Forms.Label();
            this.gbFields = new System.Windows.Forms.GroupBox();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.panel1 = new System.Windows.Forms.Panel();
            this.lblMandatory9 = new System.Windows.Forms.Label();
            this.lblMandatory8 = new System.Windows.Forms.Label();
            this.lblMandatory7 = new System.Windows.Forms.Label();
            this.lblMandatory6 = new System.Windows.Forms.Label();
            this.lblMandatory5 = new System.Windows.Forms.Label();
            this.lblMandatory4 = new System.Windows.Forms.Label();
            this.lblMandatory3 = new System.Windows.Forms.Label();
            this.lblMandatory2 = new System.Windows.Forms.Label();
            this.lblMandatory1 = new System.Windows.Forms.Label();
            this.IndexField9 = new System.Windows.Forms.ComboBox();
            this.IndexField8 = new System.Windows.Forms.ComboBox();
            this.IndexField7 = new System.Windows.Forms.ComboBox();
            this.DestinationField1 = new System.Windows.Forms.TextBox();
            this.IndexField6 = new System.Windows.Forms.ComboBox();
            this.vScrollBar1 = new System.Windows.Forms.VScrollBar();
            this.IndexField5 = new System.Windows.Forms.ComboBox();
            this.IndexField4 = new System.Windows.Forms.ComboBox();
            this.IndexField3 = new System.Windows.Forms.ComboBox();
            this.DestinationField9 = new System.Windows.Forms.TextBox();
            this.DestinationField8 = new System.Windows.Forms.TextBox();
            this.DestinationField7 = new System.Windows.Forms.TextBox();
            this.DestinationField6 = new System.Windows.Forms.TextBox();
            this.DestinationField5 = new System.Windows.Forms.TextBox();
            this.DestinationField4 = new System.Windows.Forms.TextBox();
            this.DestinationField3 = new System.Windows.Forms.TextBox();
            this.IndexField2 = new System.Windows.Forms.ComboBox();
            this.DestinationField2 = new System.Windows.Forms.TextBox();
            this.IndexField1 = new System.Windows.Forms.ComboBox();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.Aspects = new System.Windows.Forms.CheckedListBox();
            this.bnApply = new System.Windows.Forms.Button();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.locationPanel = new System.Windows.Forms.Panel();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.label7 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.PDF = new System.Windows.Forms.ComboBox();
            this.OCR = new System.Windows.Forms.ComboBox();
            this.Image = new System.Windows.Forms.ComboBox();
            this.label3 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.lbBatchClass = new System.Windows.Forms.Label();
            this.lbDocumentClass = new System.Windows.Forms.Label();
            this.btnReset = new System.Windows.Forms.Button();
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.aboutToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.gbFields.SuspendLayout();
            this.panel1.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            this.groupBox2.SuspendLayout();
            this.menuStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // lblUserName
            // 
            this.lblUserName.AutoEllipsis = true;
            this.lblUserName.AutoSize = true;
            this.lblUserName.Location = new System.Drawing.Point(18, 25);
            this.lblUserName.Name = "lblUserName";
            this.lblUserName.Size = new System.Drawing.Size(60, 13);
            this.lblUserName.TabIndex = 0;
            this.lblUserName.Text = "User Name";
            // 
            // UserName
            // 
            this.UserName.Location = new System.Drawing.Point(81, 22);
            this.UserName.Name = "UserName";
            this.UserName.Size = new System.Drawing.Size(125, 20);
            this.UserName.TabIndex = 1;
            this.UserName.Text = "admin";
            // 
            // lblPassword
            // 
            this.lblPassword.AutoSize = true;
            this.lblPassword.Location = new System.Drawing.Point(241, 25);
            this.lblPassword.Name = "lblPassword";
            this.lblPassword.Size = new System.Drawing.Size(53, 13);
            this.lblPassword.TabIndex = 2;
            this.lblPassword.Text = "Password";
            // 
            // Password
            // 
            this.Password.Location = new System.Drawing.Point(300, 22);
            this.Password.Name = "Password";
            this.Password.PasswordChar = '*';
            this.Password.Size = new System.Drawing.Size(125, 20);
            this.Password.TabIndex = 3;
            this.Password.Text = "admin";
            // 
            // lblRepository
            // 
            this.lblRepository.AutoSize = true;
            this.lblRepository.Location = new System.Drawing.Point(18, 56);
            this.lblRepository.Name = "lblRepository";
            this.lblRepository.Size = new System.Drawing.Size(57, 13);
            this.lblRepository.TabIndex = 4;
            this.lblRepository.Text = "Repository";
            // 
            // Repository
            // 
            this.Repository.DisplayMember = "test";
            this.Repository.FormattingEnabled = true;
            this.Repository.Items.AddRange(new object[] {
            "http://localhost:8080/alfresco"});
            this.Repository.Location = new System.Drawing.Point(81, 53);
            this.Repository.Name = "Repository";
            this.Repository.Size = new System.Drawing.Size(279, 21);
            this.Repository.TabIndex = 5;
            this.Repository.ValueMember = "test";
            // 
            // btnConnect
            // 
            this.btnConnect.Location = new System.Drawing.Point(366, 51);
            this.btnConnect.Name = "btnConnect";
            this.btnConnect.Size = new System.Drawing.Size(59, 23);
            this.btnConnect.TabIndex = 6;
            this.btnConnect.Text = "Connect";
            this.btnConnect.UseVisualStyleBackColor = true;
            this.btnConnect.Click += new System.EventHandler(this.btnConnect_Click);
            // 
            // lblLocation
            // 
            this.lblLocation.AutoSize = true;
            this.lblLocation.Location = new System.Drawing.Point(18, 94);
            this.lblLocation.Name = "lblLocation";
            this.lblLocation.Size = new System.Drawing.Size(60, 13);
            this.lblLocation.TabIndex = 7;
            this.lblLocation.Text = "Destination";
            // 
            // btnCancel
            // 
            this.btnCancel.Location = new System.Drawing.Point(313, 650);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(75, 23);
            this.btnCancel.TabIndex = 8;
            this.btnCancel.Text = "Cancel";
            this.btnCancel.UseVisualStyleBackColor = true;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            // 
            // btnSave
            // 
            this.btnSave.Location = new System.Drawing.Point(151, 650);
            this.btnSave.Name = "btnSave";
            this.btnSave.Size = new System.Drawing.Size(75, 23);
            this.btnSave.TabIndex = 9;
            this.btnSave.Text = "OK";
            this.btnSave.UseVisualStyleBackColor = true;
            this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
            // 
            // Location
            // 
            this.Location.Location = new System.Drawing.Point(81, 91);
            this.Location.Name = "Location";
            this.Location.ReadOnly = true;
            this.Location.Size = new System.Drawing.Size(344, 20);
            this.Location.TabIndex = 11;
            // 
            // lbApect
            // 
            this.lbApect.AutoSize = true;
            this.lbApect.Location = new System.Drawing.Point(13, 53);
            this.lbApect.Name = "lbApect";
            this.lbApect.Size = new System.Drawing.Size(45, 13);
            this.lbApect.TabIndex = 15;
            this.lbApect.Text = "Aspects";
            // 
            // ContentType
            // 
            this.ContentType.Enabled = false;
            this.ContentType.FormattingEnabled = true;
            this.ContentType.Location = new System.Drawing.Point(95, 18);
            this.ContentType.Name = "ContentType";
            this.ContentType.Size = new System.Drawing.Size(290, 21);
            this.ContentType.Sorted = true;
            this.ContentType.TabIndex = 14;
            this.ContentType.SelectedIndexChanged += new System.EventHandler(this.ContentType_SelectedIndexChanged);
            // 
            // lbContentModel
            // 
            this.lbContentModel.AutoSize = true;
            this.lbContentModel.Location = new System.Drawing.Point(13, 26);
            this.lbContentModel.Name = "lbContentModel";
            this.lbContentModel.Size = new System.Drawing.Size(71, 13);
            this.lbContentModel.TabIndex = 13;
            this.lbContentModel.Text = "Content Type";
            // 
            // gbFields
            // 
            this.gbFields.Controls.Add(this.label2);
            this.gbFields.Controls.Add(this.label1);
            this.gbFields.Controls.Add(this.panel1);
            this.gbFields.Location = new System.Drawing.Point(11, 237);
            this.gbFields.Name = "gbFields";
            this.gbFields.Size = new System.Drawing.Size(417, 291);
            this.gbFields.TabIndex = 14;
            this.gbFields.TabStop = false;
            this.gbFields.Text = "Fields";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(201, 21);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(94, 13);
            this.label2.TabIndex = 2;
            this.label2.Text = "Ascent Index Field";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(19, 21);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(126, 13);
            this.label1.TabIndex = 1;
            this.label1.Text = "Alfresco Destination Field";
            // 
            // panel1
            // 
            this.panel1.Controls.Add(this.lblMandatory9);
            this.panel1.Controls.Add(this.lblMandatory8);
            this.panel1.Controls.Add(this.lblMandatory7);
            this.panel1.Controls.Add(this.lblMandatory6);
            this.panel1.Controls.Add(this.lblMandatory5);
            this.panel1.Controls.Add(this.lblMandatory4);
            this.panel1.Controls.Add(this.lblMandatory3);
            this.panel1.Controls.Add(this.lblMandatory2);
            this.panel1.Controls.Add(this.lblMandatory1);
            this.panel1.Controls.Add(this.IndexField9);
            this.panel1.Controls.Add(this.IndexField8);
            this.panel1.Controls.Add(this.IndexField7);
            this.panel1.Controls.Add(this.DestinationField1);
            this.panel1.Controls.Add(this.IndexField6);
            this.panel1.Controls.Add(this.vScrollBar1);
            this.panel1.Controls.Add(this.IndexField5);
            this.panel1.Controls.Add(this.IndexField4);
            this.panel1.Controls.Add(this.IndexField3);
            this.panel1.Controls.Add(this.DestinationField9);
            this.panel1.Controls.Add(this.DestinationField8);
            this.panel1.Controls.Add(this.DestinationField7);
            this.panel1.Controls.Add(this.DestinationField6);
            this.panel1.Controls.Add(this.DestinationField5);
            this.panel1.Controls.Add(this.DestinationField4);
            this.panel1.Controls.Add(this.DestinationField3);
            this.panel1.Controls.Add(this.IndexField2);
            this.panel1.Controls.Add(this.DestinationField2);
            this.panel1.Controls.Add(this.IndexField1);
            this.panel1.Location = new System.Drawing.Point(11, 37);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(400, 242);
            this.panel1.TabIndex = 0;
            // 
            // lblMandatory9
            // 
            this.lblMandatory9.AutoSize = true;
            this.lblMandatory9.Location = new System.Drawing.Point(1, 213);
            this.lblMandatory9.Name = "lblMandatory9";
            this.lblMandatory9.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory9.TabIndex = 26;
            this.lblMandatory9.Text = "*";
            this.lblMandatory9.Visible = false;
            // 
            // lblMandatory8
            // 
            this.lblMandatory8.AutoSize = true;
            this.lblMandatory8.Location = new System.Drawing.Point(2, 187);
            this.lblMandatory8.Name = "lblMandatory8";
            this.lblMandatory8.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory8.TabIndex = 25;
            this.lblMandatory8.Text = "*";
            this.lblMandatory8.Visible = false;
            // 
            // lblMandatory7
            // 
            this.lblMandatory7.AutoSize = true;
            this.lblMandatory7.Location = new System.Drawing.Point(1, 162);
            this.lblMandatory7.Name = "lblMandatory7";
            this.lblMandatory7.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory7.TabIndex = 24;
            this.lblMandatory7.Text = "*";
            this.lblMandatory7.Visible = false;
            // 
            // lblMandatory6
            // 
            this.lblMandatory6.AutoSize = true;
            this.lblMandatory6.Location = new System.Drawing.Point(1, 136);
            this.lblMandatory6.Name = "lblMandatory6";
            this.lblMandatory6.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory6.TabIndex = 23;
            this.lblMandatory6.Text = "*";
            this.lblMandatory6.Visible = false;
            // 
            // lblMandatory5
            // 
            this.lblMandatory5.AutoSize = true;
            this.lblMandatory5.Location = new System.Drawing.Point(1, 110);
            this.lblMandatory5.Name = "lblMandatory5";
            this.lblMandatory5.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory5.TabIndex = 22;
            this.lblMandatory5.Text = "*";
            this.lblMandatory5.Visible = false;
            // 
            // lblMandatory4
            // 
            this.lblMandatory4.AutoSize = true;
            this.lblMandatory4.Location = new System.Drawing.Point(1, 84);
            this.lblMandatory4.Name = "lblMandatory4";
            this.lblMandatory4.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory4.TabIndex = 21;
            this.lblMandatory4.Text = "*";
            this.lblMandatory4.Visible = false;
            // 
            // lblMandatory3
            // 
            this.lblMandatory3.AutoSize = true;
            this.lblMandatory3.Location = new System.Drawing.Point(2, 58);
            this.lblMandatory3.Name = "lblMandatory3";
            this.lblMandatory3.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory3.TabIndex = 20;
            this.lblMandatory3.Text = "*";
            this.lblMandatory3.Visible = false;
            // 
            // lblMandatory2
            // 
            this.lblMandatory2.AutoSize = true;
            this.lblMandatory2.Location = new System.Drawing.Point(1, 31);
            this.lblMandatory2.Name = "lblMandatory2";
            this.lblMandatory2.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory2.TabIndex = 19;
            this.lblMandatory2.Text = "*";
            this.lblMandatory2.Visible = false;
            // 
            // lblMandatory1
            // 
            this.lblMandatory1.AutoSize = true;
            this.lblMandatory1.Location = new System.Drawing.Point(1, 6);
            this.lblMandatory1.Name = "lblMandatory1";
            this.lblMandatory1.Size = new System.Drawing.Size(11, 13);
            this.lblMandatory1.TabIndex = 3;
            this.lblMandatory1.Text = "*";
            this.lblMandatory1.Visible = false;
            // 
            // IndexField9
            // 
            this.IndexField9.FormattingEnabled = true;
            this.IndexField9.Location = new System.Drawing.Point(193, 210);
            this.IndexField9.Name = "IndexField9";
            this.IndexField9.Size = new System.Drawing.Size(180, 21);
            this.IndexField9.Sorted = true;
            this.IndexField9.TabIndex = 18;
            this.IndexField9.Visible = false;
            this.IndexField9.SelectedIndexChanged += new System.EventHandler(this.IndexField9_SelectedIndexChanged);
            // 
            // IndexField8
            // 
            this.IndexField8.FormattingEnabled = true;
            this.IndexField8.Location = new System.Drawing.Point(193, 184);
            this.IndexField8.Name = "IndexField8";
            this.IndexField8.Size = new System.Drawing.Size(180, 21);
            this.IndexField8.Sorted = true;
            this.IndexField8.TabIndex = 17;
            this.IndexField8.Visible = false;
            this.IndexField8.SelectedIndexChanged += new System.EventHandler(this.IndexField8_SelectedIndexChanged);
            // 
            // IndexField7
            // 
            this.IndexField7.FormattingEnabled = true;
            this.IndexField7.Location = new System.Drawing.Point(193, 158);
            this.IndexField7.Name = "IndexField7";
            this.IndexField7.Size = new System.Drawing.Size(180, 21);
            this.IndexField7.Sorted = true;
            this.IndexField7.TabIndex = 16;
            this.IndexField7.Visible = false;
            this.IndexField7.SelectedIndexChanged += new System.EventHandler(this.IndexField7_SelectedIndexChanged);
            // 
            // DestinationField1
            // 
            this.DestinationField1.Location = new System.Drawing.Point(11, 4);
            this.DestinationField1.Name = "DestinationField1";
            this.DestinationField1.ReadOnly = true;
            this.DestinationField1.Size = new System.Drawing.Size(170, 20);
            this.DestinationField1.TabIndex = 2;
            this.DestinationField1.Visible = false;
            // 
            // IndexField6
            // 
            this.IndexField6.FormattingEnabled = true;
            this.IndexField6.Location = new System.Drawing.Point(193, 132);
            this.IndexField6.Name = "IndexField6";
            this.IndexField6.Size = new System.Drawing.Size(180, 21);
            this.IndexField6.Sorted = true;
            this.IndexField6.TabIndex = 15;
            this.IndexField6.Visible = false;
            this.IndexField6.SelectedIndexChanged += new System.EventHandler(this.IndexField6_SelectedIndexChanged);
            // 
            // vScrollBar1
            // 
            this.vScrollBar1.Location = new System.Drawing.Point(376, 0);
            this.vScrollBar1.Maximum = 9;
            this.vScrollBar1.Name = "vScrollBar1";
            this.vScrollBar1.Size = new System.Drawing.Size(17, 239);
            this.vScrollBar1.TabIndex = 0;
            this.vScrollBar1.Visible = false;
            this.vScrollBar1.Scroll += new System.Windows.Forms.ScrollEventHandler(this.vScrollBar1_Scroll);
            // 
            // IndexField5
            // 
            this.IndexField5.FormattingEnabled = true;
            this.IndexField5.Location = new System.Drawing.Point(193, 106);
            this.IndexField5.Name = "IndexField5";
            this.IndexField5.Size = new System.Drawing.Size(180, 21);
            this.IndexField5.Sorted = true;
            this.IndexField5.TabIndex = 14;
            this.IndexField5.Visible = false;
            this.IndexField5.SelectedIndexChanged += new System.EventHandler(this.IndexField5_SelectedIndexChanged);
            // 
            // IndexField4
            // 
            this.IndexField4.FormattingEnabled = true;
            this.IndexField4.Location = new System.Drawing.Point(193, 80);
            this.IndexField4.Name = "IndexField4";
            this.IndexField4.Size = new System.Drawing.Size(180, 21);
            this.IndexField4.Sorted = true;
            this.IndexField4.TabIndex = 13;
            this.IndexField4.Visible = false;
            this.IndexField4.SelectedIndexChanged += new System.EventHandler(this.IndexField4_SelectedIndexChanged);
            // 
            // IndexField3
            // 
            this.IndexField3.FormattingEnabled = true;
            this.IndexField3.Location = new System.Drawing.Point(193, 54);
            this.IndexField3.Name = "IndexField3";
            this.IndexField3.Size = new System.Drawing.Size(180, 21);
            this.IndexField3.Sorted = true;
            this.IndexField3.TabIndex = 12;
            this.IndexField3.Visible = false;
            this.IndexField3.SelectedIndexChanged += new System.EventHandler(this.IndexField3_SelectedIndexChanged);
            // 
            // DestinationField9
            // 
            this.DestinationField9.Location = new System.Drawing.Point(10, 211);
            this.DestinationField9.Name = "DestinationField9";
            this.DestinationField9.ReadOnly = true;
            this.DestinationField9.Size = new System.Drawing.Size(170, 20);
            this.DestinationField9.TabIndex = 11;
            this.DestinationField9.Visible = false;
            // 
            // DestinationField8
            // 
            this.DestinationField8.Location = new System.Drawing.Point(11, 185);
            this.DestinationField8.Name = "DestinationField8";
            this.DestinationField8.ReadOnly = true;
            this.DestinationField8.Size = new System.Drawing.Size(170, 20);
            this.DestinationField8.TabIndex = 10;
            this.DestinationField8.Visible = false;
            // 
            // DestinationField7
            // 
            this.DestinationField7.Location = new System.Drawing.Point(10, 159);
            this.DestinationField7.Name = "DestinationField7";
            this.DestinationField7.ReadOnly = true;
            this.DestinationField7.Size = new System.Drawing.Size(170, 20);
            this.DestinationField7.TabIndex = 9;
            this.DestinationField7.Visible = false;
            // 
            // DestinationField6
            // 
            this.DestinationField6.Location = new System.Drawing.Point(11, 133);
            this.DestinationField6.Name = "DestinationField6";
            this.DestinationField6.ReadOnly = true;
            this.DestinationField6.Size = new System.Drawing.Size(170, 20);
            this.DestinationField6.TabIndex = 8;
            this.DestinationField6.Visible = false;
            // 
            // DestinationField5
            // 
            this.DestinationField5.Location = new System.Drawing.Point(11, 107);
            this.DestinationField5.Name = "DestinationField5";
            this.DestinationField5.ReadOnly = true;
            this.DestinationField5.Size = new System.Drawing.Size(170, 20);
            this.DestinationField5.TabIndex = 7;
            this.DestinationField5.Visible = false;
            // 
            // DestinationField4
            // 
            this.DestinationField4.Location = new System.Drawing.Point(10, 81);
            this.DestinationField4.Name = "DestinationField4";
            this.DestinationField4.ReadOnly = true;
            this.DestinationField4.Size = new System.Drawing.Size(170, 20);
            this.DestinationField4.TabIndex = 6;
            this.DestinationField4.Visible = false;
            // 
            // DestinationField3
            // 
            this.DestinationField3.Location = new System.Drawing.Point(10, 55);
            this.DestinationField3.Name = "DestinationField3";
            this.DestinationField3.ReadOnly = true;
            this.DestinationField3.Size = new System.Drawing.Size(170, 20);
            this.DestinationField3.TabIndex = 5;
            this.DestinationField3.Visible = false;
            // 
            // IndexField2
            // 
            this.IndexField2.FormattingEnabled = true;
            this.IndexField2.Location = new System.Drawing.Point(193, 28);
            this.IndexField2.Name = "IndexField2";
            this.IndexField2.Size = new System.Drawing.Size(180, 21);
            this.IndexField2.Sorted = true;
            this.IndexField2.TabIndex = 4;
            this.IndexField2.Visible = false;
            this.IndexField2.SelectedIndexChanged += new System.EventHandler(this.IndexField2_SelectedIndexChanged);
            // 
            // DestinationField2
            // 
            this.DestinationField2.Location = new System.Drawing.Point(10, 29);
            this.DestinationField2.Name = "DestinationField2";
            this.DestinationField2.ReadOnly = true;
            this.DestinationField2.Size = new System.Drawing.Size(170, 20);
            this.DestinationField2.TabIndex = 3;
            this.DestinationField2.Visible = false;
            // 
            // IndexField1
            // 
            this.IndexField1.FormattingEnabled = true;
            this.IndexField1.Location = new System.Drawing.Point(193, 3);
            this.IndexField1.Name = "IndexField1";
            this.IndexField1.Size = new System.Drawing.Size(180, 21);
            this.IndexField1.Sorted = true;
            this.IndexField1.TabIndex = 1;
            this.IndexField1.Visible = false;
            this.IndexField1.SelectedIndexChanged += new System.EventHandler(this.IndexField1_SelectedIndexChanged);
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.Aspects);
            this.groupBox1.Controls.Add(this.ContentType);
            this.groupBox1.Controls.Add(this.lbContentModel);
            this.groupBox1.Controls.Add(this.lbApect);
            this.groupBox1.Location = new System.Drawing.Point(11, 16);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(417, 115);
            this.groupBox1.TabIndex = 15;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Document Model";
            // 
            // Aspects
            // 
            this.Aspects.CheckOnClick = true;
            this.Aspects.Enabled = false;
            this.Aspects.FormattingEnabled = true;
            this.Aspects.Location = new System.Drawing.Point(95, 53);
            this.Aspects.Name = "Aspects";
            this.Aspects.Size = new System.Drawing.Size(289, 49);
            this.Aspects.Sorted = true;
            this.Aspects.TabIndex = 22;
            this.Aspects.SelectedIndexChanged += new System.EventHandler(this.Aspects_SelectedIndexChanged);
            // 
            // bnApply
            // 
            this.bnApply.Location = new System.Drawing.Point(394, 650);
            this.bnApply.Name = "bnApply";
            this.bnApply.Size = new System.Drawing.Size(75, 23);
            this.bnApply.TabIndex = 16;
            this.bnApply.Text = "Apply";
            this.bnApply.UseVisualStyleBackColor = true;
            this.bnApply.Click += new System.EventHandler(this.bnApply_Click);
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Location = new System.Drawing.Point(12, 84);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(453, 560);
            this.tabControl1.TabIndex = 17;
            // 
            // tabPage1
            // 
            this.tabPage1.Controls.Add(this.locationPanel);
            this.tabPage1.Controls.Add(this.lblUserName);
            this.tabPage1.Controls.Add(this.btnConnect);
            this.tabPage1.Controls.Add(this.UserName);
            this.tabPage1.Controls.Add(this.Location);
            this.tabPage1.Controls.Add(this.lblLocation);
            this.tabPage1.Controls.Add(this.Repository);
            this.tabPage1.Controls.Add(this.lblPassword);
            this.tabPage1.Controls.Add(this.lblRepository);
            this.tabPage1.Controls.Add(this.Password);
            this.tabPage1.Location = new System.Drawing.Point(4, 22);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(445, 534);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "Repository";
            this.tabPage1.UseVisualStyleBackColor = true;
            // 
            // locationPanel
            // 
            this.locationPanel.BackColor = System.Drawing.SystemColors.InactiveCaptionText;
            this.locationPanel.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.locationPanel.Location = new System.Drawing.Point(21, 131);
            this.locationPanel.Name = "locationPanel";
            this.locationPanel.Size = new System.Drawing.Size(404, 330);
            this.locationPanel.TabIndex = 13;
            // 
            // tabPage2
            // 
            this.tabPage2.Controls.Add(this.groupBox2);
            this.tabPage2.Controls.Add(this.gbFields);
            this.tabPage2.Controls.Add(this.groupBox1);
            this.tabPage2.Location = new System.Drawing.Point(4, 22);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(445, 534);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "Document Details";
            this.tabPage2.UseVisualStyleBackColor = true;
            // 
            // groupBox2
            // 
            this.groupBox2.Controls.Add(this.label7);
            this.groupBox2.Controls.Add(this.label6);
            this.groupBox2.Controls.Add(this.label5);
            this.groupBox2.Controls.Add(this.PDF);
            this.groupBox2.Controls.Add(this.OCR);
            this.groupBox2.Controls.Add(this.Image);
            this.groupBox2.Location = new System.Drawing.Point(11, 137);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(417, 94);
            this.groupBox2.TabIndex = 16;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Document Content";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(13, 66);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(58, 13);
            this.label7.TabIndex = 24;
            this.label7.Text = "Kofax PDF";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(13, 43);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(73, 13);
            this.label6.TabIndex = 23;
            this.label6.Text = "OCR Full Text";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(13, 20);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(36, 13);
            this.label5.TabIndex = 22;
            this.label5.Text = "Image";
            // 
            // PDF
            // 
            this.PDF.Enabled = false;
            this.PDF.FormattingEnabled = true;
            this.PDF.Location = new System.Drawing.Point(95, 63);
            this.PDF.Name = "PDF";
            this.PDF.Size = new System.Drawing.Size(289, 21);
            this.PDF.TabIndex = 5;
            // 
            // OCR
            // 
            this.OCR.Enabled = false;
            this.OCR.FormattingEnabled = true;
            this.OCR.Location = new System.Drawing.Point(95, 40);
            this.OCR.Name = "OCR";
            this.OCR.Size = new System.Drawing.Size(289, 21);
            this.OCR.TabIndex = 4;
            // 
            // Image
            // 
            this.Image.FormattingEnabled = true;
            this.Image.Items.AddRange(new object[] {
            ""});
            this.Image.Location = new System.Drawing.Point(95, 17);
            this.Image.Name = "Image";
            this.Image.Size = new System.Drawing.Size(289, 21);
            this.Image.TabIndex = 3;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(13, 36);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(66, 13);
            this.label3.TabIndex = 18;
            this.label3.Text = "Batch Class:";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(13, 58);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(87, 13);
            this.label4.TabIndex = 19;
            this.label4.Text = "Document Class:";
            // 
            // lbBatchClass
            // 
            this.lbBatchClass.AutoSize = true;
            this.lbBatchClass.Location = new System.Drawing.Point(112, 36);
            this.lbBatchClass.Name = "lbBatchClass";
            this.lbBatchClass.Size = new System.Drawing.Size(0, 13);
            this.lbBatchClass.TabIndex = 20;
            // 
            // lbDocumentClass
            // 
            this.lbDocumentClass.AutoSize = true;
            this.lbDocumentClass.Location = new System.Drawing.Point(112, 58);
            this.lbDocumentClass.Name = "lbDocumentClass";
            this.lbDocumentClass.Size = new System.Drawing.Size(0, 13);
            this.lbDocumentClass.TabIndex = 21;
            // 
            // btnReset
            // 
            this.btnReset.Location = new System.Drawing.Point(232, 650);
            this.btnReset.Name = "btnReset";
            this.btnReset.Size = new System.Drawing.Size(75, 23);
            this.btnReset.TabIndex = 22;
            this.btnReset.Text = "Reset";
            this.btnReset.UseVisualStyleBackColor = true;
            this.btnReset.Click += new System.EventHandler(this.btnReset_Click);
            // 
            // menuStrip1
            // 
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.aboutToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.RightToLeft = System.Windows.Forms.RightToLeft.Yes;
            this.menuStrip1.Size = new System.Drawing.Size(482, 24);
            this.menuStrip1.TabIndex = 23;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // aboutToolStripMenuItem
            // 
            this.aboutToolStripMenuItem.Name = "aboutToolStripMenuItem";
            this.aboutToolStripMenuItem.Size = new System.Drawing.Size(48, 20);
            this.aboutToolStripMenuItem.Text = "About";
            this.aboutToolStripMenuItem.Click += new System.EventHandler(this.aboutToolStripMenuItem_Click);
            // 
            // frmAlfrescoSetUp
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(482, 685);
            this.Controls.Add(this.btnReset);
            this.Controls.Add(this.lbDocumentClass);
            this.Controls.Add(this.lbBatchClass);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.tabControl1);
            this.Controls.Add(this.bnApply);
            this.Controls.Add(this.btnSave);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.menuStrip1);
            this.Name = "frmAlfrescoSetUp";
            this.Text = "Alfresco Release Setup";
            this.Load += new System.EventHandler(this.frmAlfrescoSetUp_Load);
            this.gbFields.ResumeLayout(false);
            this.gbFields.PerformLayout();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage1.PerformLayout();
            this.tabPage2.ResumeLayout(false);
            this.groupBox2.ResumeLayout(false);
            this.groupBox2.PerformLayout();
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label lblUserName;
        private System.Windows.Forms.TextBox UserName;
        private System.Windows.Forms.Label lblPassword;
        private System.Windows.Forms.TextBox Password;
        private System.Windows.Forms.Label lblRepository;
        private System.Windows.Forms.ComboBox Repository;
        private System.Windows.Forms.Button btnConnect;
        private System.Windows.Forms.Label lblLocation;
        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.Button btnSave;
        private System.Windows.Forms.TextBox Location;
        private System.Windows.Forms.GroupBox gbFields;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label lbContentModel;
        private System.Windows.Forms.Label lbApect;
        private System.Windows.Forms.ComboBox ContentType;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.ComboBox IndexField9;
        private System.Windows.Forms.ComboBox IndexField8;
        private System.Windows.Forms.ComboBox IndexField7;
        private System.Windows.Forms.ComboBox IndexField6;
        private System.Windows.Forms.ComboBox IndexField5;
        private System.Windows.Forms.ComboBox IndexField4;
        private System.Windows.Forms.ComboBox IndexField3;
        private System.Windows.Forms.TextBox DestinationField9;
        private System.Windows.Forms.TextBox DestinationField8;
        private System.Windows.Forms.TextBox DestinationField7;
        private System.Windows.Forms.TextBox DestinationField6;
        private System.Windows.Forms.TextBox DestinationField5;
        private System.Windows.Forms.TextBox DestinationField4;
        private System.Windows.Forms.TextBox DestinationField3;
        private System.Windows.Forms.ComboBox IndexField2;
        private System.Windows.Forms.TextBox DestinationField2;
        private System.Windows.Forms.TextBox DestinationField1;
        private System.Windows.Forms.ComboBox IndexField1;
        private System.Windows.Forms.VScrollBar vScrollBar1;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Button bnApply;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.TabPage tabPage2;
        private System.Windows.Forms.Panel locationPanel;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label lbBatchClass;
        private System.Windows.Forms.Label lbDocumentClass;
        private System.Windows.Forms.Label lblMandatory2;
        private System.Windows.Forms.Label lblMandatory1;
        private System.Windows.Forms.Label lblMandatory9;
        private System.Windows.Forms.Label lblMandatory8;
        private System.Windows.Forms.Label lblMandatory7;
        private System.Windows.Forms.Label lblMandatory6;
        private System.Windows.Forms.Label lblMandatory5;
        private System.Windows.Forms.Label lblMandatory4;
        private System.Windows.Forms.Label lblMandatory3;
        private System.Windows.Forms.CheckedListBox Aspects;
        private System.Windows.Forms.ComboBox OCR;
        private System.Windows.Forms.ComboBox Image;
        private System.Windows.Forms.ComboBox PDF;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Button btnReset;
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem aboutToolStripMenuItem;
    }
}