namespace KofaxAlfrescoRelease_v1
{
    partial class frmConnecting
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(frmConnecting));
            this.lbConnecting = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // lbConnecting
            // 
            resources.ApplyResources(this.lbConnecting, "lbConnecting");
            this.lbConnecting.Name = "lbConnecting";
            // 
            // frmConnecting
            // 
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Inherit;
            resources.ApplyResources(this, "$this");
            this.ControlBox = false;
            this.Controls.Add(this.lbConnecting);
            this.MinimizeBox = false;
            this.Name = "frmConnecting";
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Label lbConnecting;
    }
}