namespace AlfrescoWord2007
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

      #region Component Designer generated code

      /// <summary> 
      /// Required method for Designer support - do not modify 
      /// the contents of this method with the code editor.
      /// </summary>
      private void InitializeComponent()
      {
         this.components = new System.ComponentModel.Container();
         System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(AlfrescoPane));
         this.lnkShowConfiguration = new System.Windows.Forms.LinkLabel();
         this.pnlConfiguration = new System.Windows.Forms.Panel();
         this.lnkBackToBrowser = new System.Windows.Forms.LinkLabel();
         this.label7 = new System.Windows.Forms.Label();
         this.grpConfiguration = new System.Windows.Forms.GroupBox();
         this.btnDetailsOK = new System.Windows.Forms.Button();
         this.btnDetailsCancel = new System.Windows.Forms.Button();
         this.groupBox1 = new System.Windows.Forms.GroupBox();
         this.chkRememberAuth = new System.Windows.Forms.CheckBox();
         this.txtPassword = new System.Windows.Forms.TextBox();
         this.label8 = new System.Windows.Forms.Label();
         this.txtUsername = new System.Windows.Forms.TextBox();
         this.label1 = new System.Windows.Forms.Label();
         this.label6 = new System.Windows.Forms.Label();
         this.grpDetails = new System.Windows.Forms.GroupBox();
         this.txtCIFSServer = new System.Windows.Forms.TextBox();
         this.label5 = new System.Windows.Forms.Label();
         this.txtWebDAVURL = new System.Windows.Forms.TextBox();
         this.label4 = new System.Windows.Forms.Label();
         this.txtWebClientURL = new System.Windows.Forms.TextBox();
         this.label3 = new System.Windows.Forms.Label();
         this.tipGeneral = new System.Windows.Forms.ToolTip(this.components);
         this.tipMandatory = new System.Windows.Forms.ToolTip(this.components);
         this.tipOptional = new System.Windows.Forms.ToolTip(this.components);
         this.pnlWebBrowser = new System.Windows.Forms.Panel();
         this.webBrowser = new System.Windows.Forms.WebBrowser();
         this.pnlConfiguration.SuspendLayout();
         this.grpConfiguration.SuspendLayout();
         this.groupBox1.SuspendLayout();
         this.grpDetails.SuspendLayout();
         this.pnlWebBrowser.SuspendLayout();
         this.SuspendLayout();
         // 
         // lnkShowConfiguration
         // 
         this.lnkShowConfiguration.Location = new System.Drawing.Point(3, 657);
         this.lnkShowConfiguration.Name = "lnkShowConfiguration";
         this.lnkShowConfiguration.Size = new System.Drawing.Size(286, 30);
         this.lnkShowConfiguration.TabIndex = 6;
         this.lnkShowConfiguration.TabStop = true;
         this.lnkShowConfiguration.Text = "Click here to Configure the Alfresco Server URLs";
         this.lnkShowConfiguration.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
         this.lnkShowConfiguration.LinkClicked += new System.Windows.Forms.LinkLabelLinkClickedEventHandler(this.lnkShowConfiguration_LinkClicked);
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
         this.pnlConfiguration.Size = new System.Drawing.Size(292, 689);
         this.pnlConfiguration.TabIndex = 7;
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
         this.label7.Text = "Welcome to the Alfresco Add-In for Microsoft Office 2007";
         this.label7.TextAlign = System.Drawing.ContentAlignment.TopCenter;
         // 
         // grpConfiguration
         // 
         this.grpConfiguration.Controls.Add(this.btnDetailsOK);
         this.grpConfiguration.Controls.Add(this.btnDetailsCancel);
         this.grpConfiguration.Controls.Add(this.groupBox1);
         this.grpConfiguration.Controls.Add(this.label6);
         this.grpConfiguration.Controls.Add(this.grpDetails);
         this.grpConfiguration.Location = new System.Drawing.Point(0, 150);
         this.grpConfiguration.Name = "grpConfiguration";
         this.grpConfiguration.Size = new System.Drawing.Size(292, 474);
         this.grpConfiguration.TabIndex = 2;
         this.grpConfiguration.TabStop = false;
         this.grpConfiguration.Text = "Configuration";
         // 
         // btnDetailsOK
         // 
         this.btnDetailsOK.BackColor = System.Drawing.SystemColors.ButtonFace;
         this.btnDetailsOK.Location = new System.Drawing.Point(98, 440);
         this.btnDetailsOK.Name = "btnDetailsOK";
         this.btnDetailsOK.Size = new System.Drawing.Size(100, 23);
         this.btnDetailsOK.TabIndex = 8;
         this.btnDetailsOK.Text = "Save Settings";
         this.btnDetailsOK.UseVisualStyleBackColor = false;
         this.btnDetailsOK.Click += new System.EventHandler(this.btnDetailsOK_Click);
         this.btnDetailsOK.TextChanged += new System.EventHandler(this.btnDetailsOK_Click);
         // 
         // btnDetailsCancel
         // 
         this.btnDetailsCancel.BackColor = System.Drawing.SystemColors.ButtonFace;
         this.btnDetailsCancel.Location = new System.Drawing.Point(204, 440);
         this.btnDetailsCancel.Name = "btnDetailsCancel";
         this.btnDetailsCancel.Size = new System.Drawing.Size(75, 23);
         this.btnDetailsCancel.TabIndex = 7;
         this.btnDetailsCancel.Text = "Reset";
         this.btnDetailsCancel.UseVisualStyleBackColor = false;
         this.btnDetailsCancel.Click += new System.EventHandler(this.btnDetailsCancel_Click);
         this.btnDetailsCancel.TextChanged += new System.EventHandler(this.btnDetailsCancel_Click);
         // 
         // groupBox1
         // 
         this.groupBox1.Controls.Add(this.chkRememberAuth);
         this.groupBox1.Controls.Add(this.txtPassword);
         this.groupBox1.Controls.Add(this.label8);
         this.groupBox1.Controls.Add(this.txtUsername);
         this.groupBox1.Controls.Add(this.label1);
         this.groupBox1.Location = new System.Drawing.Point(12, 281);
         this.groupBox1.Name = "groupBox1";
         this.groupBox1.Size = new System.Drawing.Size(267, 153);
         this.groupBox1.TabIndex = 6;
         this.groupBox1.TabStop = false;
         this.groupBox1.Text = "Authentication";
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
         this.grpDetails.Controls.Add(this.txtCIFSServer);
         this.grpDetails.Controls.Add(this.label5);
         this.grpDetails.Controls.Add(this.txtWebDAVURL);
         this.grpDetails.Controls.Add(this.label4);
         this.grpDetails.Controls.Add(this.txtWebClientURL);
         this.grpDetails.Controls.Add(this.label3);
         this.grpDetails.Location = new System.Drawing.Point(12, 110);
         this.grpDetails.Name = "grpDetails";
         this.grpDetails.Size = new System.Drawing.Size(267, 165);
         this.grpDetails.TabIndex = 4;
         this.grpDetails.TabStop = false;
         this.grpDetails.Text = "Location";
         // 
         // txtCIFSServer
         // 
         this.txtCIFSServer.AutoCompleteMode = System.Windows.Forms.AutoCompleteMode.Suggest;
         this.txtCIFSServer.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.CustomSource;
         this.txtCIFSServer.Location = new System.Drawing.Point(9, 125);
         this.txtCIFSServer.Name = "txtCIFSServer";
         this.txtCIFSServer.Size = new System.Drawing.Size(251, 20);
         this.txtCIFSServer.TabIndex = 9;
         this.tipOptional.SetToolTip(this.txtCIFSServer, "The UNC path, or mapped drive, to the Alfresco CIFS server.");
         this.txtCIFSServer.TextChanged += new System.EventHandler(this.txtCIFSServer_TextChanged);
         // 
         // label5
         // 
         this.label5.AutoSize = true;
         this.label5.Location = new System.Drawing.Point(6, 109);
         this.label5.Name = "label5";
         this.label5.Size = new System.Drawing.Size(67, 13);
         this.label5.TabIndex = 8;
         this.label5.Text = "CIFS Server:";
         // 
         // txtWebDAVURL
         // 
         this.txtWebDAVURL.AutoCompleteMode = System.Windows.Forms.AutoCompleteMode.Suggest;
         this.txtWebDAVURL.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.CustomSource;
         this.txtWebDAVURL.Location = new System.Drawing.Point(9, 81);
         this.txtWebDAVURL.Name = "txtWebDAVURL";
         this.txtWebDAVURL.Size = new System.Drawing.Size(251, 20);
         this.txtWebDAVURL.TabIndex = 7;
         this.tipMandatory.SetToolTip(this.txtWebDAVURL, "The URL for the Alfresco Web Client.");
         this.txtWebDAVURL.TextChanged += new System.EventHandler(this.txtWebDAVURL_TextChanged);
         // 
         // label4
         // 
         this.label4.AutoSize = true;
         this.label4.Location = new System.Drawing.Point(6, 65);
         this.label4.Name = "label4";
         this.label4.Size = new System.Drawing.Size(80, 13);
         this.label4.TabIndex = 6;
         this.label4.Text = "WebDAV URL:";
         // 
         // txtWebClientURL
         // 
         this.txtWebClientURL.AutoCompleteMode = System.Windows.Forms.AutoCompleteMode.Suggest;
         this.txtWebClientURL.AutoCompleteSource = System.Windows.Forms.AutoCompleteSource.AllUrl;
         this.txtWebClientURL.Location = new System.Drawing.Point(9, 37);
         this.txtWebClientURL.Name = "txtWebClientURL";
         this.txtWebClientURL.Size = new System.Drawing.Size(251, 20);
         this.txtWebClientURL.TabIndex = 5;
         this.tipMandatory.SetToolTip(this.txtWebClientURL, "The URL for the Alfresco Web Client.");
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
         // pnlWebBrowser
         // 
         this.pnlWebBrowser.BackColor = System.Drawing.Color.White;
         this.pnlWebBrowser.Controls.Add(this.webBrowser);
         this.pnlWebBrowser.Dock = System.Windows.Forms.DockStyle.Fill;
         this.pnlWebBrowser.Location = new System.Drawing.Point(0, 0);
         this.pnlWebBrowser.Name = "pnlWebBrowser";
         this.pnlWebBrowser.Size = new System.Drawing.Size(292, 689);
         this.pnlWebBrowser.TabIndex = 8;
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
         this.webBrowser.WebBrowserShortcutsEnabled = false;
         this.webBrowser.Navigated += new System.Windows.Forms.WebBrowserNavigatedEventHandler(this.webBrowser_Navigated);
         // 
         // AlfrescoPane
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
         this.BackColor = System.Drawing.Color.White;
         this.Controls.Add(this.pnlConfiguration);
         this.Controls.Add(this.lnkShowConfiguration);
         this.Controls.Add(this.pnlWebBrowser);
         this.Name = "AlfrescoPane";
         this.Size = new System.Drawing.Size(292, 689);
         this.pnlConfiguration.ResumeLayout(false);
         this.grpConfiguration.ResumeLayout(false);
         this.groupBox1.ResumeLayout(false);
         this.groupBox1.PerformLayout();
         this.grpDetails.ResumeLayout(false);
         this.grpDetails.PerformLayout();
         this.pnlWebBrowser.ResumeLayout(false);
         this.ResumeLayout(false);

      }

      #endregion

      private System.Windows.Forms.LinkLabel lnkShowConfiguration;
      private System.Windows.Forms.Panel pnlConfiguration;
      private System.Windows.Forms.LinkLabel lnkBackToBrowser;
      private System.Windows.Forms.Label label7;
      private System.Windows.Forms.GroupBox grpConfiguration;
      private System.Windows.Forms.Button btnDetailsOK;
      private System.Windows.Forms.Button btnDetailsCancel;
      private System.Windows.Forms.GroupBox groupBox1;
      private System.Windows.Forms.CheckBox chkRememberAuth;
      private System.Windows.Forms.TextBox txtPassword;
      private System.Windows.Forms.Label label8;
      private System.Windows.Forms.TextBox txtUsername;
      private System.Windows.Forms.Label label1;
      private System.Windows.Forms.Label label6;
      private System.Windows.Forms.GroupBox grpDetails;
      private System.Windows.Forms.TextBox txtCIFSServer;
      private System.Windows.Forms.Label label5;
      private System.Windows.Forms.TextBox txtWebDAVURL;
      private System.Windows.Forms.Label label4;
      private System.Windows.Forms.TextBox txtWebClientURL;
      private System.Windows.Forms.Label label3;
      private System.Windows.Forms.ToolTip tipGeneral;
      private System.Windows.Forms.ToolTip tipMandatory;
      private System.Windows.Forms.ToolTip tipOptional;
      private System.Windows.Forms.Panel pnlWebBrowser;
      private System.Windows.Forms.WebBrowser webBrowser;
   }
}
