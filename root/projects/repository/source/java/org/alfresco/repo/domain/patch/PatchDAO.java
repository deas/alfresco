/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.repo.domain.patch;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.domain.avm.AVMNodeEntity;
import org.alfresco.repo.domain.contentdata.ContentDataDAO;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.apache.ibatis.session.ResultHandler;

/**
 * Additional DAO services for patches
 *
 * @author janv
 * @author Derek Hulley
 * @since 3.2
 */
public interface PatchDAO
{
    // AVM-related
    
    public long getAVMNodesCountWhereNewInStore();
    
    public List<AVMNodeEntity> getEmptyGUIDS(int count);
    
    public List<AVMNodeEntity> getNullVersionLayeredDirectories(int count);
    
    public List<AVMNodeEntity> getNullVersionLayeredFiles(int count);
    
    public long getMaxAvmNodeID();
    
    public List<Long> getAvmNodesWithOldContentProperties(Long minNodeId, Long maxNodeId);
    
    public int updateAVMNodesNullifyAcl(List<Long> nodeIds);
    
    public int updateAVMNodesSetAcl(long aclId, List<Long> nodeIds);
    
    // DM-related
    
    public long getMaxAdmNodeID();
    
    /**
     * Migrates DM content properties from the old V3.1 format (String-based {@link ContentData#toString()})
     * to the new V3.2 format (ID based storage using {@link ContentDataDAO}).
     * 
     * @param minNodeId         the inclusive node ID to limit the updates to
     * @param maxNodeId         the exclusive node ID to limit the updates to
     */
    public void updateAdmV31ContentProperties(Long minNodeId, Long maxNodeId);
    
    /**
     * Update all <b>alf_content_data</b> mimetype references.
     * 
     * @param oldMimetypeId     the ID to search for
     * @param newMimetypeId     the ID to change to
     * @return                  the number of rows affected
     */
    public int updateContentMimetypeIds(Long oldMimetypeId, Long newMimetypeId);
    
    /**
     * A callback handler for iterating over the string results
     */
    public interface StringHandler
    {
        void handle(String string);
    }
    
    /**
     * Add a <b>cm:sizeCurrent</b> property to person nodes that don't have it.
     */
    public int addSizeCurrentProp();
    
    // ACL-related
    
    /**
     * Get the max acl id
     * 
     * @return - max acl id
     */
    public long getMaxAclId();
    
    /**
     * How many DM nodes are there?
     * 
     * @return - the count
     */
    public long getDmNodeCount();
    
    /**
     * How many DM nodes are three with new ACls (to track patch progress)
     * 
     * @param above
     * @return - the count
     */
    public long getDmNodeCountWithNewACLs(Long above);
    
    public List<Long> selectAllAclIds();
    
    public List<Long> selectNonDanglingAclIds();
    
    public int deleteDanglingAces();
    
    public int deleteAcls(List<Long> aclIds);
    
    public int deleteAclMembersForAcls(List<Long> aclIds);
    
    /**
     * @return      Returns the names of authorities with incorrect CRC values
     */
    public List<String> getAuthoritiesWithNonUtf8Crcs();
    
    /**
     * @return                      Returns the number child association rows
     */
    public int getChildAssocCount();
    
    /**
     * 
     * @return                      Returns the maximum child assoc ID or <tt>0</tt> if there are none
     */
    Long getMaxChildAssocId();
    
    /**
     * The results map contains:
     * <pre>
     * <![CDATA[
        <result property="id" column="id" jdbcType="BIGINT" javaType="java.lang.Long"/>
        <result property="typeQNameId" column="type_qname_id" jdbcType="BIGINT" javaType="java.lang.Long"/>
        <result property="qnameNamespaceId" column="qname_ns_id" jdbcType="BIGINT" javaType="java.lang.Long"/>
        <result property="qnameLocalName" column="qname_localname" jdbcType="VARCHAR" javaType="java.lang.String"/>
        <result property="childNodeNameCrc" column="child_node_name_crc" jdbcType="BIGINT" javaType="java.lang.Long"/>
        <result property="qnameCrc" column="qname_crc" jdbcType="BIGINT" javaType="java.lang.Long"/>
        <result property="childNodeUuid" column="child_node_uuid" jdbcType="VARCHAR" javaType="java.lang.String"/>
        <result property="childNodeName" column="child_node_name" jdbcType="VARCHAR" javaType="java.lang.String"/>
       ]]>
     * </pre>
     * @param minAssocId            the minimum child assoc ID
     * @param stopAtAssocId         the child assoc ID to stop at i.e. once this ID has been reached,
     *                              pull back no results
     * @param rangeMultiplier       the ration of IDs to actual rows (how many IDs to select to get a row)
     * @param maxIdRange            the largest ID range to use for selects.  Normally, the ID range should be
     *                              allowed to grow in accordance with the general distribution of rows, but
     *                              if memory problems are encountered, then the range will need to be set down.
     * @param maxResults            the number of child associations to fetch
     * @return                      Returns child associations <b>that need fixing</b>
     */
    public List<Map<String, Object>> getChildAssocsForCrcFix(
            Long minAssocId,
            Long stopAtAssocId,
            long rangeMultiplier,
            long maxIdRange,
            int maxResults);
    
    public int updateChildAssocCrc(Long assocId, Long childNodeNameCrc, Long qnameCrc);
    
    /**
     * Query for a list of nodes that have a given type and share the same name pattern (SQL LIKE syntax)
     * 
     * @param typeQName             the node type
     * @param namePattern           the SQL LIKE pattern
     * @return                      Returns the node ID and node name
     */
    public List<Pair<NodeRef, String>> getNodesOfTypeWithNamePattern(QName typeQName, String namePattern);
    
    /**
     * Migrate old Tenant attributes (if any)
     */
    public void migrateOldAttrTenants(ResultHandler resultHandler);
    
    /**
     * Migrate old AVM Lock attributes (if any)
     */
    public void migrateOldAttrAVMLocks(ResultHandler resultHandler);
    
    /**
     * Migrate old Property-Backed Bean attributes (if any)
     */
    public void migrateOldAttrPropertyBackedBeans(ResultHandler resultHandler);
    
    /**
     * Migrate old Chaining User Registry Synchronizer attributes (if any)
     */
    public void migrateOldAttrChainingURS(ResultHandler resultHandler);
    
    /**
     * Drop old attribute alf_*attribute* tables
     */
    public void migrateOldAttrDropTables();
    
    /**
     * Get custom global attribute names (if any)
     */
    public List<String> getOldAttrCustomNames();
    
    /**
     * Get shared acls with inheritance issues
     */
    public List<Map<String, Object>> getSharedAclsThatDoNotInheritCorrectlyFromThePrimaryParent();
    
    /**
     * Get defining acls with inheritance issues
     */
    public List<Map<String, Object>> getDefiningAclsThatDoNotInheritCorrectlyFromThePrimaryParent();
    
    /**
     * Get acls that do not inherit from the primary parent.
     */
    public List<Map<String, Object>> getAclsThatInheritFromNonPrimaryParent();
    
    /**
     * Get acls that inherit with inheritance unset
     */
    public List<Map<String, Object>> getAclsThatInheritWithInheritanceUnset();
    
    /**
     * Get shared acls that do not inherit correctly from the defining acl
     */
    public List<Map<String, Object>> getSharedAclsThatDoNotInheritCorrectlyFromTheirDefiningAcl();
    
    /**
     * @param qnames                the qnames to search for
     * @return                      Returns a count of the number of nodes that have either of the aspects
     */
    public long getCountNodesWithAspects(Set<QName> qnames);
    
    /**
     * Find all the nodes ids with the given type
     * @param typeQNameId - the id of the type qname
     * @param minNodeId - min node id in the result set - inclusive
     * @param maxNodeId - max node id in the result set - exclusive
     * @return
     */
    public List<Long> getNodesByTypeQNameId(Long typeQNameId, Long minNodeId, Long maxNodeId);
    
    /**
     * Find all the nodes ids with the given type uri
     * @param uriId - the id of the type qname uri
     * @param minNodeId - min node id in the result set - inclusive
     * @param maxNodeId - max node id in the result set - exclusive
     * @return
     */
    public List<Long> getNodesByTypeUriId(Long uriId, Long minNodeId, Long maxNodeId);
    
    /**
     * Find all the nodes ids with the given aspect
     * @param aspectQNameId - the id of the aspect qname
     * @param minNodeId - min node id in the result set - inclusive
     * @param maxNodeId - max node id in the result set - exclusive
     * @return
     */
    public List<Long> getNodesByAspectQNameId(Long aspectQNameId, Long minNodeId, Long maxNodeId);
    
    /**
     * Find all the nodes ids with the given content property set with the given mimetype
     * @param mimetypeId - the id of the content data mimetype
     * @param minNodeId - min node id in the result set - inclusive
     * @param maxNodeId - max node id in the result set - exclusive
     * @return
     */
    public List<Long> getNodesByContentPropertyMimetypeId(Long mimetypeId, Long minNodeId, Long maxNodeId);
    
    /**
     * Gets the total number of nodes which match the given Type QName.
     * 
     * @param typeQName the qname to search for
     * @return count of nodes that match the typeQName
     */
    public long getCountNodesWithTypId(QName typeQName);
    
    /**
     * Finds folders of the shared surf-config (for all tenants):
     * <ul>
     * <li> company_home/sites/surf-config/components </li>
     * <li>company_home/sites/surf-config/pages </li>
     * <li>company_home/sites/surf-config/pages/user </li>
     * <li>company_home/sites/surf-config/pages/user{userId} </li>
     * </ul>
     * @param minNodeId - min node id in the result set - inclusive
     * @param maxNodeId - max node id in the result set - exclusive
     * @return list of children nodeRefs
     */
    public List<NodeRef> getChildrenOfTheSharedSurfConfigFolder(Long minNodeId, Long maxNodeId);

}
