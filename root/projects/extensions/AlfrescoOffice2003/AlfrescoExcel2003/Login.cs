/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace AlfrescoExcel2003
{
   public partial class Login : Form
   {
      public Login()
      {
         InitializeComponent();
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

      private void Login_Activated(object sender, EventArgs e)
      {
         if (txtUsername.Text.Length == 0)
         {
            this.ActiveControl = txtUsername;
         }
         else
         {
            this.ActiveControl = txtPassword;
         }
      }
   }
}