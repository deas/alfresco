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
package org.alfresco.repo.transfer.fsr;

import java.io.Serializable;

public class TransferStatusEntity
{
    private Long id;
    private String transferId;
    private Integer currentPos;
    private Integer endPos;
    private Serializable error;
    private String status;

    public TransferStatusEntity()
    {
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTransferId()
    {
        return transferId;
    }

    public void setTransferId(String transferId)
    {
        this.transferId = transferId;
    }

    public Integer getCurrentPos()
    {
        return currentPos;
    }

    public void setCurrentPos(Integer currentPos)
    {
        this.currentPos = currentPos;
    }

    public Integer getEndPos()
    {
        return endPos;
    }

    public void setEndPos(Integer endPos)
    {
        this.endPos = endPos;
    }

    public Serializable getError()
    {
        return error;
    }

    public void setError(Serializable error)
    {
        this.error = error;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
