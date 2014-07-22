/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts.ui.common;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

public class ConstantMethodBinding extends MethodBinding implements StateHolder
{
   private String outcome = null;
   private boolean transientFlag = false;

   public ConstantMethodBinding()
   {
   }

   public ConstantMethodBinding(String yourOutcome)
   {
      outcome = yourOutcome;
   }

   public Object invoke(FacesContext context, Object params[])
   {
      return outcome;
   }

   public Class getType(FacesContext context)
   {
      return String.class;
   }

   public Object saveState(FacesContext context)
   {
      return outcome;
   }

   public void restoreState(FacesContext context, Object state)
   {
      outcome = (String) state;
   }

   public boolean isTransient()
   {
      return (this.transientFlag);
   }

   public void setTransient(boolean transientFlag)
   {
      this.transientFlag = transientFlag;
   }
}
