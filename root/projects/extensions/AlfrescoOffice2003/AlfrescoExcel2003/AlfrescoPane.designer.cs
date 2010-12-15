namespace AlfrescoExcel2003
{
    partial class AlfrescoPane
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
           this.components = new System.ComponentModel.Container();
           System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AlfrescoPane));
           this.pnlConfiguration = new System.Windows.Forms.Panel();
           this.lnkBackToBrowser = new System.Windows.Forms.LinkLabel();
           this.label7 = new System.Windows.Forms.Label();
           this.grpConfiguration = new System.Windows.Forms.GroupBox();
           this.btnDetailsOK = new System.Windows.Forms.Button();
           this.btnDetailsCancel = new System.Windows.Forms.Button();
           this.grpAuthentication = new System.Windows.Forms.GroupBox();
           this.chkRememberAuth = new System.Windows.Forms.CheckBox();
           this.txtPassword = new System.Windows.Forms.TextBox();
           this.label8 = new System.Windows.Forms.Label();
           this.txtUsername = new System.Windows.Forms.TextBox();
           this.label1 = new System.Windows.Forms.Label();
           this.label6 = new System.Windows.Forms.Label();
           this.grpDetails = new System.Windows.Forms.GroupBox();
           this.chkUseCIFS = new System.Windows.Forms.CheckBox();
           this.txtCIFSServer = new System.Windows.Forms.TextBox();
           this.txtWebClientURL = new System.Windows.Forms.TextBox();
           this.label3 = new System.Windows.Forms.Label();
           this.pnlWebBrowser = new System.Windows.Forms.Panel();
           this.lnkShowConfiguration = new System.Windows.Forms.LinkLabel();
           this.webBrowser = new System.Windows.Forms.WebBrowser();
           this.tipGeneral = new System.Windows.Forms.ToolTip(this.components);
           this.tipMandatory = new System.Windows.Forms.ToolTip(this.components);
           this.tipOptional = new System.Windows.Forms.ToolTip(this.components);
           this.pnlConfiguration.SuspendLayout();
           this.grpConfiguration.SuspendLayout();
           this.grpAuthentication.SuspendLayout();
           this.grpDetails.SuspendLayout();
           this.pnlWebBrowser.SuspendLayout();
           this.SuspendLayout();
           // 
           // pnlConfiguration
           // 
           this.pnlConfiguration.BackColor = System.Drawing.Color.White;
           this.pnlConfiguration.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("pnlConfiguration.BackgroundImage")));
           this.pnlConfiguration.BackgroundImageLayout = System.Windows.Forms.ImageLayout.None;
           this.pnlConfiguration.Controls.Add(this.lnkBackToBrowser);
           this.pnlConfiguration.Controls.Add(this.label7);
           this.pnlConfiguration.Controls.Add(this.grpConfiguration);
           this.pnlConfiguration.Dock = System.Windows.Forms.DockStyle.Fill;
           this.pnlConfiguration.Location = new System.Drawing.Point(0, 0);
           this.pnlConfiguration.Name = "pnlConfiguration";
           this.pnlConfiguration.Size = new System.Drawing.Size(292, 686);
           this.pnlConfiguration.TabIndex = 2;
           // 
           // lnkBackToBrowser
           // 
           this.lnkBackToBrowser.Location = new System.Drawing.Point(3, 657);
           this.lnkBackToBrowser.Name = "lnkBackToBrowser";
           this.lnkBackToBrowser.Size = new System.Drawing.Size(286, 30);
           this.lnkBackToBrowser.TabIndex = 4;
           this.lnkBackToBrowser.TabStop = true;
           this.lnkBackToBrowser.Text = "Click here to return to the Office Web Client view";
           this.lnkBackToBrowser.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
           this.lnkBackToBrowser.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lnkBackToBrowser_LinkClicked);
           // 
           // label7
           // 
           this.label7.Font = new System.Drawing.Font("Trebuchet MS", 12F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
           this.label7.ForeColor = System.Drawing.SystemColors.ControlDarkDark;
           this.label7.Location = new System.Drawing.Point(4, 77);
           this.label7.Name = "label7";
           this.label7.Size = new System.Drawing.Size(285, 46);
           this.label7.TabIndex = 3;
           this.label7.Text = "Welcome to the Alfresco Add-In for Microsoft Office 2003";
           this.label7.TextAlign = System.Drawing.ContentAlignment.TopCenter;
           // 
           // grpConfiguration
           // 
           this.grpConfiguration.Controls.Add(this.btnDetailsOK);
           this.grpConfiguration.Controls.Add(this.btnDetailsCancel);
           this.grpConfiguration.Controls.Add(this.grpAuthentication);
           this.grpConfiguration.Controls.Add(this.label6);
           this.grpConfiguration.Controls.Add(this.grpDetails);
           this.grpConfiguration.Location = new System.Drawing.Point(0, 150);
           this.grpConfiguration.Name = "grpConfiguration";
           this.grpConfiguration.Size = new System.Drawing.Size(292, 504);
           this.grpConfiguration.TabIndex = 2;
           this.grpConfiguration.TabStop = false;
           this.grpConfiguration.Text = "Configuration";
           // 
           // btnDetailsOK
           // 
           this.btnDetailsOK.BackColor = System.Drawing.SystemColors.ButtonFace;
           this.btnDetailsOK.Location = new System.Drawing.Point(98, 467);
           this.btnDetailsOK.Name = "btnDetailsOK";
           this.btnDetailsOK.Size = new System.Drawing.Size(100, 28);
           this.btnDetailsOK.TabIndex = 8;
           this.btnDetailsOK.Text = "Save Settings";
           this.btnDetailsOK.UseVisualStyleBackColor = false;
           this.btnDetailsOK.Click += new System.EventHandler(this.btnDetailsOK_Click);
           // 
           // btnDetailsCancel
           // 
           this.btnDetailsCancel.BackColor = System.Drawing.SystemColors.ButtonFace;
           this.btnDetailsCancel.Location = new System.Drawing.Point(204, 467);
           this.btnDetailsCancel.Name = "btnDetailsCancel";
           this.btnDetailsCancel.Size = new System.Drawing.Size(75, 28);
           this.btnDetailsCancel.TabIndex = 7;
           this.btnDetailsCancel.Text = "Reset";
           this.btnDetailsCancel.UseVisualStyleBackColor = false;
           this.btnDetailsCancel.Click += new System.EventHandler(this.btnDetailsCancel_Click);
           // 
           // grpAuthentication
           // 
           this.grpAuthentication.Controls.Add(this.chkRememberAuth);
           this.grpAuthentication.Controls.Add(this.txtPassword);
           this.grpAuthentication.Controls.Add(this.label8);
           this.grpAuthentication.Controls.Add(this.txtUsername);
           this.grpAuthentication.Controls.Add(this.label1);
           this.grpAuthentication.Location = new System.Drawing.Point(12, 273);
           this.grpAuthentication.Name = "grpAuthentication";
           this.grpAuthentication.Size = new System.Drawing.Size(267, 153);
           this.grpAuthentication.TabIndex = 6;
           this.grpAuthentication.TabStop = false;
           this.grpAuthentication.Text = "Authentication (Leave blank for NTLM)";
           // 
           // chkRememberAuth
           // 
           this.chkRememberAuth.AutoSize = true;
           this.chkRememberAuth.Location = new System.Drawing.Point(9, 119);
           this.chkRememberAuth.Name = "chkRememberAuth";
           this.chkRememberAuth.Size = new System.Drawing.Size(180, 17);
           this.chkRememberAuth.TabIndex = 4;
           this.chkRememberAuth.Text = "Remember authentication details";
           this.chkRememberAuth.UseVisualStyleBackColor = true;
           // 
           // txtPassword
           // 
           this.txtPassword.Location = new System.Drawing.Point(9, 83);
           this.txtPassword.Name = "txtPassword";
           this.txtPassword.PasswordChar = '*';
           this.txtPassword.Size = new System.Drawing.Size(251, 20);
           this.txtPassword.TabIndex = 3;
           this.tipOptional.SetToolTip(this.txtPassword, "Enter your Alfresco password here if you want to be automatically logged-on.");
           this.txtPassword.TextChanged += new System.EventHandler(this.txtPassword_TextChanged);
           // 
           // label8
           // 
           this.label8.AutoSize = true;
           this.label8.Location = new System.Drawing.Point(6, 66);
           this.label8.Name = "label8";
           this.label8.Size = new System.Drawing.Size(53, 13);
           this.label8.TabIndex = 2;
           this.label8.Text = "Password";
           // 
           // txtUsername
           // 
           this.txtUsername.Location = new System.Drawing.Point(9, 37);
           this.txtUsername.Name = "txtUsername";
           this.txtUsername.Size = new System.Drawing.Size(251, 20);
           this.txtUsername.TabIndex = 1;
           this.tipOptional.SetToolTip(this.txtUsername, "Enter your Alfresco username here if you want to be automatically logged-on.");
           this.txtUsername.TextChanged += new System.EventHandler(this.txtUsername_TextChanged);
           // 
           // label1
           // 
           this.label1.AutoSize = true;
           this.label1.Location = new System.Drawing.Point(6, 21);
           this.label1.Name = "label1";
           this.label1.Size = new System.Drawing.Size(55, 13);
           this.label1.TabIndex = 0;
           this.label1.Text = "Username";
           // 
           // label6
           // 
           this.label6.Location = new System.Drawing.Point(7, 20);
           this.label6.Name = "label6";
           this.label6.Size = new System.Drawing.Size(273, 87);
           this.label6.TabIndex = 5;
           this.label6.Text = resources.GetString("label6.Text");
           // 
           // grpDetails
           // 
           this.grpDetails.Controls.Add(this.chkUseCIFS);
           this.grpDetails.Controls.Add(this.txtCIFSServer);
           this.grpDetails.Controls.Add(this.txtWebClientURL);
           this.grpDetails.Controls.Add(this.label3);
           this.grpDetails.Location = new System.Drawing.Point(12, 110);
           this.grpDetails.Name = "grpDetails";
           this.grpDetails.Size = new System.Drawing.Size(267, 146);
           this.grpDetails.TabIndex = 4;
           this.grpDetails.TabStop = false;
           this.grpDetails.Text = "Location";
           // 
           // chkUseCIFS
           // 
           this.chkUseCIFS.AutoSize = true;
           this.chkUseCIFS.Location = new System.Drawing.Point(9, 75);
           this.chkUseCIFS.Name = "chkUseCIFS";
           this.chkUseCIFS.Size = new System.Drawing.Size(131, 17);
           this.chkUseCIFS.TabIndex = 11;
           this.chkUseCIFS.Text = "Use CIFS Connection:";
           this.chkUseCIFS.UseVisualStyleBackColor = true;
           this.chkUseCIFS.CheckedChanged += new System.EventHandler(this.chkUseCIFS_CheckedChanged);
           // 
           // txtCIFSServer
           // 
           this.txtCIFSServer.AutoCompleteMode = System.Windows.Forms.AutoCompleteMode.Suggest;
           this.txtCIFSServer.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.CustomSource;
           this.txtCIFSServer.Location = new System.Drawing.Point(29, 98);
           this.txtCIFSServer.Name = "txtCIFSServer";
           this.txtCIFSServer.Size = new System.Drawing.Size(231, 20);
           this.txtCIFSServer.TabIndex = 9;
           this.tipOptional.SetToolTip(this.txtCIFSServer, "The UNC path, or mapped drive, to the Alfresco CIFS server.\r\ne.g. \\\\myserver_a\\al" +
                   "fresco\\");
           this.txtCIFSServer.TextChanged += new System.EventHandler(this.txtCIFSServer_TextChanged);
           // 
           // txtWebClientURL
           // 
           this.txtWebClientURL.AutoCompleteMode = System.Windows.Forms.AutoCompleteMode.Suggest;
           this.txtWebClientURL.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.AllUrl;
           this.txtWebClientURL.Location = new System.Drawing.Point(9, 37);
           this.txtWebClientURL.Name = "txtWebClientURL";
           this.txtWebClientURL.Size = new System.Drawing.Size(251, 20);
           this.txtWebClientURL.TabIndex = 5;
           this.tipMandatory.SetToolTip(this.txtWebClientURL, "The URL for the Alfresco Web Client.\r\ne.g. http://myserver:8080/alfresco/");
           this.txtWebClientURL.TextChanged += new System.EventHandler(this.txtWebClientURL_TextChanged);
           // 
           // label3
           // 
           this.label3.AutoSize = true;
           this.label3.Location = new System.Drawing.Point(6, 21);
           this.label3.Name = "label3";
           this.label3.Size = new System.Drawing.Size(87, 13);
           this.label3.TabIndex = 4;
           this.label3.Text = "Web Client URL:";
           // 
           // pnlWebBrowser
           // 
           this.pnlWebBrowser.BackColor = System.Drawing.Color.White;
           this.pnlWebBrowser.Controls.Add(this.lnkShowConfiguration);
           this.pnlWebBrowser.Controls.Add(this.webBrowser);
           this.pnlWebBrowser.Dock = System.Windows.Forms.DockStyle.Fill;
           this.pnlWebBrowser.Location = new System.Drawing.Point(0, 0);
           this.pnlWebBrowser.Name = "pnlWebBrowser";
           this.pnlWebBrowser.Size = new System.Drawing.Size(292, 686);
           this.pnlWebBrowser.TabIndex = 5;
           // 
           // lnkShowConfiguration
           // 
           this.lnkShowConfiguration.Location = new System.Drawing.Point(3, 657);
           this.lnkShowConfiguration.Name = "lnkShowConfiguration";
           this.lnkShowConfiguration.Size = new System.Drawing.Size(286, 30);
           this.lnkShowConfiguration.TabIndex = 5;
           this.lnkShowConfiguration.TabStop = true;
           this.lnkShowConfiguration.Text = "Click here to Configure the Alfresco Server URLs";
           this.lnkShowConfiguration.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
           this.lnkShowConfiguration.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lnkShowConfiguration_LinkClicked);
           // 
           // webBrowser
           // 
           this.webBrowser.AllowWebBrowserDrop = false;
           this.webBrowser.Dock = System.Windows.Forms.DockStyle.Top;
           this.webBrowser.Location = new System.Drawing.Point(0, 0);
           this.webBrowser.MinimumSize = new System.Drawing.Size(20, 20);
           this.webBrowser.Name = "webBrowser";
           this.webBrowser.ScrollBarsEnabled = false;
           this.webBrowser.Size = new System.Drawing.Size(292, 654);
           this.webBrowser.TabIndex = 1;
           this.webBrowser.Navigated += new System.Windows.Forms.WebBrowserNavigatedEventHandler(this.webBrowser_Navigated);
           // 
           // tipGeneral
           // 
           this.tipGeneral.AutoPopDelay = 5000;
           this.tipGeneral.InitialDelay = 500;
           this.tipGeneral.ReshowDelay = 100;
           this.tipGeneral.ToolTipTitle = "Alfresco";
           // 
           // tipMandatory
           // 
           this.tipMandatory.AutoPopDelay = 5000;
           this.tipMandatory.InitialDelay = 500;
           this.tipMandatory.ReshowDelay = 100;
           this.tipMandatory.ToolTipTitle = "Mandatory Setting";
           // 
           // tipOptional
           // 
           this.tipOptional.AutoPopDelay = 5000;
           this.tipOptional.InitialDelay = 500;
           this.tipOptional.ReshowDelay = 100;
           this.tipOptional.ToolTipTitle = "Optional Setting";
           // 
           // AlfrescoPane
           // 
           this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
           this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
           this.ClientSize = new System.Drawing.Size(292, 686);
           this.Controls.Add(this.pnlConfiguration);
           this.Controls.Add(this.pnlWebBrowser);
           this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
           this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
           this.MaximizeBox = false;
           this.MinimizeBox = false;
           this.Name = "AlfrescoPane";
           this.ShowInTaskbar = false;
           this.Text = "Alfresco";
           this.TopMost = true;
           this.Load += new System.EventHandler(this.AlfrescoPane_Load);
           this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.AlfrescoPane_FormClosing);
           this.pnlConfiguration.ResumeLayout(false);
           this.grpConfiguration.ResumeLayout(false);
           this.grpAuthentication.ResumeLayout(false);
           this.grpAuthentication.PerformLayout();
           this.grpDetails.ResumeLayout(false);
           this.grpDetails.PerformLayout();
           this.pnlWebBrowser.ResumeLayout(false);
           this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel pnlConfiguration;
       private System.Windows.Forms.GroupBox grpConfiguration;
       private System.Windows.Forms.Label label6;
       private System.Windows.Forms.GroupBox grpDetails;
       private System.Windows.Forms.TextBox txtCIFSServer;
       private System.Windows.Forms.TextBox txtWebClientURL;
       private System.Windows.Forms.Label label3;
       private System.Windows.Forms.Label label7;
       private System.Windows.Forms.ToolTip tipGeneral;
       private System.Windows.Forms.ToolTip tipMandatory;
       private System.Windows.Forms.ToolTip tipOptional;
       private System.Windows.Forms.LinkLabel lnkBackToBrowser;
       private System.Windows.Forms.Button btnDetailsOK;
       private System.Windows.Forms.Button btnDetailsCancel;
       private System.Windows.Forms.GroupBox grpAuthentication;
       private System.Windows.Forms.Label label8;
       private System.Windows.Forms.TextBox txtUsername;
       private System.Windows.Forms.Label label1;
       private System.Windows.Forms.CheckBox chkRememberAuth;
       private System.Windows.Forms.TextBox txtPassword;
       private System.Windows.Forms.Panel pnlWebBrowser;
       private System.Windows.Forms.LinkLabel lnkShowConfiguration;
       private System.Windows.Forms.WebBrowser webBrowser;
       private System.Windows.Forms.CheckBox chkUseCIFS;
    }
}