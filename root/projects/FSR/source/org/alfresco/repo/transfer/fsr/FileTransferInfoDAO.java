package org.alfresco.repo.transfer.fsr;

import java.util.List;

import org.alfresco.util.Pair;



public interface FileTransferInfoDAO
{
   FileTransferInfoEntity createFileTransferInfo(String nodeRef, String parent, String path, String content_name, String contentUrl );

   FileTransferInfoEntity findFileTransferInfoByNodeRef(String nodeRef);

   List<FileTransferInfoEntity> findFileTransferInfoByParentNodeRef(String nodeRef);

   void updateFileTransferInfoByNodeRef(FileTransferInfoEntity modifiedEntity);

   void deleteFileTransferInfoByNodeRef(String nodeRef);

}
