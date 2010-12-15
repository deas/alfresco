
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="error" type="{http://webservices.clb.content.ibm.com}ClbError"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbResponse", propOrder = {
    "error"
})
@XmlSeeAlso({
    ClbDocumentResponse.class,
    ClbFolderResponse.class,
    ClbCopyResponse.class,
    ClbApproveDraftResponse.class,
    UserResponse.class,
    ClbViewResponse.class,
    ClbDraftResponse.class,
    ClbFoldersResponse.class,
    UsersResponse.class,
    ClbVersionResponse.class,
    ClbLibraryResponse.class,
    ClbLibrariesResponse.class,
    ClbViewFormatResponse.class,
    ClbDocumentTypesResponse.class,
    ClbCancelDocumentResponse.class,
    ClbViewResultsResponse.class,
    ClbDraftsResponse.class,
    ServerInfoResponse.class,
    ClbDocumentTypeResponse.class,
    ClbMoveResponse.class,
    ClbDocumentsResponse.class,
    ClbViewsResponse.class,
    ClbPropertySheetTypeResponse.class,
    ClbContentURLResponse.class,
    ClbVersionsResponse.class,
    ClbTreeResponse.class,
    ClbCheckinResponse.class
})
public class ClbResponse {

    @XmlElement(required = true, nillable = true)
    protected ClbError error;

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link ClbError }
     *     
     */
    public ClbError getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbError }
     *     
     */
    public void setError(ClbError value) {
        this.error = value;
    }

}
