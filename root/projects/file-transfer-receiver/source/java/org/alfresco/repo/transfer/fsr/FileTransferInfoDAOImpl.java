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


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

public class FileTransferInfoDAOImpl implements FileTransferInfoDAO
{

    private static final String INSERT_FTI = "alfresco.filetransfer.insert.insert_FileTransferInfo";
    private static final String SELECT_FTI_BY_NODEREF = "alfresco.filetransferinfo.select_FileTransferInfoByNodeRef";
    private static final String UPDATE_FTI_BY_NODEREF = "alfresco.filetransferinfo.update_FileTransferInfoByNodeRef";
    private static final String UPDATE_PATH_BY_PARENT = "alfresco.filetransferinfo.update_PathByParent";
    private static final String SELECT_FTI_BY_PARENT_NODEREF = "alfresco.filetransferinfo.select_FileTransferInfoByParentNodeRef";
    private static final String DELETE_FILE_TRANSFER_INFO_BY_NODEREF = "alfresco.filetransferinfo.delete_FileTransferInfoByNodeRef";
    private static final String INSERT_FTNR = "alfresco.filetransfer.insert.insert_FileTransferNodeRename";
    private static final String DELETE_FILE_TRANSFER_RENAME_BY_ID = "alfresco.filetransferinfo.delete_FileTransferNodeRenameByTransferId";
    private static final String SELECT_FILE_TRANSFER_RENAME_BY_TRANSFER_ID = "alfresco.filetransferinfo.select_FileTransferRenameByTransferId";
    private SqlSessionTemplate template;

    public final void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate)
    {
        this.template = sqlSessionTemplate;
    }

    public FileTransferInfoEntity createFileTransferInfo(
            String nodeRef,
            String parent,
            String path,
            String content_name,
            String contentUrl,
            boolean isFolder,
            String sourceRepoId)
    {
        FileTransferInfoEntity entity = new FileTransferInfoEntity();
        entity.setNodeRef(nodeRef);
        entity.setParent(parent);
        entity.setPath(path);
        entity.setContentName(content_name);
        entity.setContentUrl(contentUrl);
        entity.setFolder(isFolder);
        entity.setSourceRepoId(sourceRepoId);
        template.insert(INSERT_FTI, entity);
        return entity;
    }

    public FileTransferInfoEntity findFileTransferInfoByNodeRef(String nodeRef)
    {

        FileTransferInfoEntity entity = new FileTransferInfoEntity();
        entity.setNodeRef(nodeRef);
        entity = template.selectOne(SELECT_FTI_BY_NODEREF, entity);
        return entity;
    }

    @SuppressWarnings("unchecked")
    public List<FileTransferInfoEntity> findFileTransferInfoByParentNodeRef(String nodeRef)
    {

        FileTransferInfoEntity entity = new FileTransferInfoEntity();
        entity.setParent(nodeRef);
        return template.selectList(SELECT_FTI_BY_PARENT_NODEREF, nodeRef);
    }

    public void updateFileTransferInfoByNodeRef(FileTransferInfoEntity modifiedEntity)
    {
        template.update(UPDATE_FTI_BY_NODEREF,modifiedEntity);
    }

    public void deleteFileTransferInfoByNodeRef(String nodeRef)
    {
        Map<String, Object> params = new HashMap<String, Object>(5);
        params.put("nodeRef", nodeRef);
        template.delete(DELETE_FILE_TRANSFER_INFO_BY_NODEREF, params);
    }

    public FileTransferNodeRenameEntity createFileTransferNodeRenameEntity(String noderef, String transferId, String newName)
    {
        FileTransferNodeRenameEntity newNameEntity = new FileTransferNodeRenameEntity();
        newNameEntity.setRenamedNodeRef(noderef);
        newNameEntity.setTransferId(transferId);
        newNameEntity.setNewName(newName);
        template.insert(INSERT_FTNR, newNameEntity);
        return newNameEntity;
    }

    public void deleteNodeRenameByTransferIdAndNodeRef(String transferId, String nodeRef)
    {
        Map<String, Object> params = new HashMap<String, Object>(5);
        params.put("transferId", transferId);
        params.put("renamedNodeRef", nodeRef);
        template.delete(DELETE_FILE_TRANSFER_RENAME_BY_ID, params);
    }

    @SuppressWarnings("unchecked")
    public List<FileTransferNodeRenameEntity> findFileTransferNodeRenameEntityByTransferId(String transferId)
    {
        FileTransferNodeRenameEntity newNameEntity = new FileTransferNodeRenameEntity();
        newNameEntity.setTransferId(transferId);
        return template.selectList(SELECT_FILE_TRANSFER_RENAME_BY_TRANSFER_ID, newNameEntity);
    }

    @Override
    public void updatePathOfChildren(String parentId, String newPath)
    {
        Map<String, Object> params = new HashMap<String, Object>(5);
        params.put("parent", parentId);
        params.put("path", newPath);
        template.update(UPDATE_PATH_BY_PARENT, params);
    }

}
