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
    private static final String SELECT_FTI_BY_PARENT_NODEREF = "alfresco.filetransferinfo.select_FileTransferInfoByParentNodeRef";
    private static final String DELETE_FILE_TRANSFER_INFO_BY_NODEREF = "alfresco.filetransferinfo.delete_FileTransferInfoByNodeRef";
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
            String contentUrl)
    {
        FileTransferInfoEntity entity = new FileTransferInfoEntity();
        entity.setNodeRef(nodeRef);
        entity.setParent(parent);
        entity.setPath(path);
        entity.setContentName(content_name);
        entity.setContentUrl(contentUrl);
        template.insert(INSERT_FTI, entity);
        return entity;
    }

    public FileTransferInfoEntity findFileTransferInfoByNodeRef(String nodeRef)
    {

        FileTransferInfoEntity entity = new FileTransferInfoEntity();
        entity.setNodeRef(nodeRef);
        entity = (FileTransferInfoEntity) template.selectOne(SELECT_FTI_BY_NODEREF, entity);
        return entity;
    }

    public List<FileTransferInfoEntity> findFileTransferInfoByParentNodeRef(String nodeRef)
    {

        FileTransferInfoEntity entity = new FileTransferInfoEntity();
        entity.setParent(nodeRef);
        return (List<FileTransferInfoEntity>)template.selectList(SELECT_FTI_BY_PARENT_NODEREF, nodeRef);
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

}
