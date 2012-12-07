/*
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
package org.alfresco.filesys.repo.rules;

import java.util.ArrayList;
import java.util.Date;

import org.alfresco.filesys.repo.ResultCallback;
import org.alfresco.filesys.repo.rules.commands.CloseFileCommand;
import org.alfresco.filesys.repo.rules.commands.CompoundCommand;
import org.alfresco.filesys.repo.rules.commands.CopyContentCommand;
import org.alfresco.filesys.repo.rules.commands.DeleteFileCommand;
import org.alfresco.filesys.repo.rules.commands.RestoreFileCommand;
import org.alfresco.filesys.repo.rules.operations.CloseFileOperation;
import org.alfresco.filesys.repo.rules.operations.MoveFileOperation;
import org.alfresco.filesys.repo.rules.operations.RenameFileOperation;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport.TxnReadState;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * First case of this is Mac Mountain Lion Preview application.
 * and then a new copy of the file put into place.
 * 
 * a) DeleteOnClose fileA
 * b) Close fileA
 * c) Rename whatever fileA
 * 
 * This rule will kick in and ...
 * 
 */
class ScenarioDeleteOnCloseRenameInstance implements ScenarioInstance
{
    private static Log logger = LogFactory.getLog(ScenarioDeleteOnCloseRenameInstance.class);
      
    private Date startTime = new Date();
    
    /**
     * Timeout in ms.  Default 30 seconds.
     */
    private long timeout = 30000;
    
    private boolean isComplete = false;
    
    private Ranking ranking = Ranking.HIGH;
    
    private NodeRef originalNodeRef = null;
    
    enum InternalState
    {
        NONE,
        LOOK_FOR_RENAME
    } ;
    
    InternalState state = InternalState.NONE;
    
    String name;
    
    /**
     * Evaluate the next operation
     * @param operation
     */
    public Command evaluate(Operation operation)
    {                
        /**
         * Anti-pattern : timeout
         */
        Date now = new Date();
        if(now.getTime() > startTime.getTime() + getTimeout())
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Instance timed out");
            }
            isComplete = true;
            return null;
        }
        
        switch (state)
        {
            case NONE:
                if(operation instanceof CloseFileOperation)
                {
                    CloseFileOperation c = (CloseFileOperation)operation;
                    this.name = c.getName();
                    logger.debug("New scenario initialised for file " + name);
                    state = InternalState.LOOK_FOR_RENAME;
                    
                    ArrayList<Command> commands = new ArrayList<Command>();
                    ArrayList<Command> postCommitCommands = new ArrayList<Command>();
                    ArrayList<Command> postErrorCommands = new ArrayList<Command>();
                    commands.add(new CloseFileCommand(c.getName(), c.getNetworkFile(), c.getRootNodeRef(), c.getPath()));
                    postCommitCommands.add(newDeleteFileCallbackCommand());
                    return new CompoundCommand(commands, postCommitCommands, postErrorCommands);  
                }
                break;
                
            case LOOK_FOR_RENAME:
                if(operation instanceof RenameFileOperation)
                {
                    RenameFileOperation r = (RenameFileOperation)operation;
                    if(name.equals(r.getTo()))
                    {
                        logger.debug("Delete on close Rename shuffle - fire!");
                       
                        if(originalNodeRef != null)
                        {
                            /**
                             * Shuffle is as follows
                             * a) Copy content from File to File~
                             * b) Delete File
                             * c) Rename File~ to File
                             */
                            ArrayList<Command> commands = new ArrayList<Command>();
                            RestoreFileCommand r1 = new RestoreFileCommand(r.getTo(), r.getRootNodeRef(), r.getToPath(), 0, originalNodeRef);
                            CopyContentCommand copyContent = new CopyContentCommand(r.getFrom(), r.getTo(), r.getRootNodeRef(), r.getFromPath(), r.getToPath());
                            DeleteFileCommand d1 = new DeleteFileCommand(r.getFrom(), r.getRootNodeRef(), r.getFromPath()); 
                        
                            commands.add(r1);
                            commands.add(copyContent);
                            commands.add(d1);
                            logger.debug("Scenario complete");
                            isComplete = true;
                            return new CompoundCommand(commands);
                        }
                        else
                        {
                            logger.debug("Scenario complete");
                            isComplete = true;
                            return null;
                        }
                    }
                }
                
                
                if(operation instanceof MoveFileOperation)
                {
                    MoveFileOperation r = (MoveFileOperation)operation;
                    if(name.equals(r.getTo()))
                    {
                        logger.debug("Delete on close Rename shuffle - fire!");
                       
                        if(originalNodeRef != null)
                        {
                            /**
                             * Shuffle is as follows
                             * a) Copy content from File to File~
                             * b) Delete File
                             * c) Rename File~ to File
                             */
                            ArrayList<Command> commands = new ArrayList<Command>();
                            RestoreFileCommand r1 = new RestoreFileCommand(r.getTo(), r.getRootNodeRef(), r.getToPath(), 0, originalNodeRef);
                            CopyContentCommand copyContent = new CopyContentCommand(r.getFrom(), r.getTo(), r.getRootNodeRef(), r.getFromPath(), r.getToPath());
                            DeleteFileCommand d1 = new DeleteFileCommand(r.getFrom(), r.getRootNodeRef(), r.getFromPath()); 
                        
                            commands.add(r1);
                            commands.add(copyContent);
                            commands.add(d1);
                            logger.debug("Scenario complete");
                            isComplete = true;
                            return new CompoundCommand(commands);
                        }
                        else
                        {
                            logger.debug("Scenario complete");
                            isComplete = true;
                            return null;
                        }
                    }
                }
        }
             
        return null;
    }
    
    @Override
    public boolean isComplete()
    {
        return isComplete;
    }
    
    public String toString()
    {
        return "ScenarioDeleteOnCloseRenameShuffleInstance name:" + name ;
    }

    public void setTimeout(long timeout)
    {
        this.timeout = timeout;
    }

    public long getTimeout()
    {
        return timeout;
    }
    
    @Override
    public Ranking getRanking()
    {
        return ranking;
    }
    
    public void setRanking(Ranking ranking)
    {
        this.ranking = ranking;
    }
    
    /**
     * Called for delete file.
     */
    private ResultCallback newDeleteFileCallbackCommand()
    {
        return new ResultCallback()
        {
            @Override
            public void execute(Object result)
            {
                if(result instanceof NodeRef)
                {
                    logger.debug("got node ref of deleted node");
                    originalNodeRef = (NodeRef)result;
                }
            }

            @Override
            public TxnReadState getTransactionRequired()
            {
                return TxnReadState.TXN_NONE;
            }   
        };
    }

}

