using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace AlfrescoWord2007
{
   public partial class Login : Form
   {
      public Login()
      {
         InitializeComponent();
         if (txtUsername.Text.Length == 0)
         {
            txtUsername.Focus();
         }
         else
         {
            txtPassword.Focus();
         }
      }

      public string Username
      {
         get
         {
            return txtUsername.Text;
         }
         set
         {
            txtUsername.Text = value;
         }
      }

      public string Password
      {
         get
         {
            return txtPassword.Text;
         }
         set
         {
            txtPassword.Text = value;
         }
      }
   }
}