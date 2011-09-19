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

import org.mybatis.spring.SqlSessionTemplate;

/**
 * 
 * @author Brian
 * @since 4.0
 */
public class TransferStatusDAOImpl implements TransferStatusDAO
{
    private static final String TRANSFER_STATUS_NAMESPACE = "alfresco.transferstatus";

    private static final String INSERT_TRANSFER_STATUS = TRANSFER_STATUS_NAMESPACE + ".insert_TransferStatus";
    private static final String SELECT_TRANSFER_STATUS_BY_TRANSFER_ID = TRANSFER_STATUS_NAMESPACE + ".select_TransferStatusByTransferId";
    private static final String UPDATE_TRANSFER_STATUS = TRANSFER_STATUS_NAMESPACE + ".update_TransferStatus";
    private static final String DELETE_TRANSFER_STATUS = TRANSFER_STATUS_NAMESPACE + ".delete_TransferStatus";

    private SqlSessionTemplate template;

    public final void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate)
    {
        this.template = sqlSessionTemplate;
    }

    @Override
    public TransferStatusEntity createTransferStatus(String transferId, Integer currentPos, Integer endPos, String status,
            Serializable error)
    {
        TransferStatusEntity entity = new TransferStatusEntity();
        entity.setTransferId(transferId);
        entity.setCurrentPos(currentPos);
        entity.setEndPos(endPos);
        entity.setStatus(status);
        entity.setError(error);
        template.insert(INSERT_TRANSFER_STATUS, entity);
        return entity;
    }

    @Override
    public void delete(TransferStatusEntity statusEntity)
    {
        template.delete(DELETE_TRANSFER_STATUS, statusEntity);
    }

    @Override
    public TransferStatusEntity findByTransferId(String transferId)
    {
        return (TransferStatusEntity)template.selectOne(SELECT_TRANSFER_STATUS_BY_TRANSFER_ID, transferId);
    }

    @Override
    public void update(TransferStatusEntity statusEntity)
    {
        template.update(UPDATE_TRANSFER_STATUS, statusEntity);
    }

}
