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

package org.springframework.extensions.webscripts;

import java.awt.event.ActionEvent;

import javax.swing.WindowConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.debugger.Dim;
import org.mozilla.javascript.tools.debugger.ScopeProvider;
import org.mozilla.javascript.tools.debugger.SwingGui;
import org.mozilla.javascript.tools.shell.Global;


/**
 * Alfresco implementation of Rhino JavaScript debugger
 * 
 * @author davidc
 */
public class ScriptDebugger implements Runnable
{
    private static final Log logger = LogFactory.getLog(ScriptDebugger.class);
    
    private ContextFactory factory = null;
    private SwingGui gui = null;
    protected Dim dim = null;
    
    private String title = "";
    
    public void setTitle(String title)
    {
        this.title = title;
    }

    protected void initDebugger()
    {
        dim = new Dim();
    }
    
    /**
     * Start the Debugger
     */
    public void start()
    {
        if (logger.isDebugEnabled())
        {
            activate();
            show();
        }
    }

    /**
     * Activate the Debugger
     */
    public synchronized void activate()
    {
        factory = ContextFactory.getGlobal();
        Global global = new Global();
        global.init(factory);
        global.setIn(System.in);
        global.setOut(System.out);
        global.setErr(System.err);        
        initDebugger();
        ScopeProvider sp = new AlfrescoScopeProvider((Scriptable)global);
        dim.setScopeProvider(sp);
        gui = new AlfrescoGui(dim, getTitle(), this);
        gui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        gui.setExitAction(this);
    }
    
    protected String getTitle()
    {
        return this.title;
    }

    
    /**
     * Show the debugger
     */
    public synchronized void show()
    {
        if (!isActive())
        {
            activate();
        }
        
        dim.setBreakOnExceptions(true);
        dim.setBreak();
        dim.attachTo(factory);
        gui.pack();
        gui.setSize(600, 460);
        gui.setVisible(true);
    }
    

    /**
     * Hide the Debugger
     */
    public synchronized void hide()
    {
        if (isVisible())
        {
            dim.detach();
            gui.dispose();
        }
    }
    
    /**
     * Is Debugger visible?
     * 
     * @return
     */
    public boolean isVisible()
    {
        return isActive() && gui.isVisible();
    }
    
    /**
     * Is Debugger active?
     * 
     * @return
     */
    public boolean isActive()
    {
        return gui != null;
    }
    
    /**
     * Exit action.
     */
    public void run()
    {
        dim.detach();
        gui.dispose();
    }
    
    
    private static class AlfrescoGui extends SwingGui
    {
        private static final long serialVersionUID = 5053205080777378416L;
        private ScriptDebugger debugger;
        
        public AlfrescoGui(Dim dim, String title, ScriptDebugger debugger)
        {
            super(dim, title);
            this.debugger = debugger;
        }

        public void actionPerformed(ActionEvent e)
        {
            String cmd = e.getActionCommand();
            if (cmd.equals("Exit"))
            {
                debugger.hide();
            }
            else
            {
                super.actionPerformed(e);
            }
        }
    }
    
    
    public static class AlfrescoScopeProvider implements ScopeProvider
    {
        AlfrescoScopeProvider(Scriptable scope)
        {
            this.scope = scope;
        }
        
        /**
         * The scope object to expose
         */
        private Scriptable scope;
        
        /**
         * Returns the scope for script evaluations.
         */
        public Scriptable getScope()
        {
            return scope;
        }
    }
}
