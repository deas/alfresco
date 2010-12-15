
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="libraryId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="libraryPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extensionFilter" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="docTypeOption" type="{http://webservices.clb.content.ibm.com}ClbDocTypeOption"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "libraryId",
    "libraryPath",
    "extensionFilter",
    "docTypeOption"
})
@XmlRootElement(name = "getDocumentTypes")
public class GetDocumentTypes {

    @XmlElement(required = true, nillable = true)
    protected String libraryId;
    @XmlElement(required = true, nillable = true)
    protected String libraryPath;
    @XmlElement(required = true, nillable = true)
    protected String extensionFilter;
    @XmlElement(required = true, nillable = true)
    protected ClbDocTypeOption docTypeOption;

    /**
     * Gets the value of the libraryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibraryId() {
        return libraryId;
    }

    /**
     * Sets the value of the libraryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibraryId(String value) {
        this.libraryId = value;
    }

    /**
     * Gets the value of the libraryPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibraryPath() {
        return libraryPath;
    }

    /**
     * Sets the value of the libraryPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibraryPath(String value) {
        this.libraryPath = value;
    }

    /**
     * Gets the value of the extensionFilter property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionFilter() {
        return extensionFilter;
    }

    /**
     * Sets the value of the extensionFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionFilter(String value) {
        this.extensionFilter = value;
    }

    /**
     * Gets the value of the docTypeOption property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocTypeOption }
     *     
     */
    public ClbDocTypeOption getDocTypeOption() {
        return docTypeOption;
    }

    /**
     * Sets the value of the docTypeOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocTypeOption }
     *     
     */
    public void setDocTypeOption(ClbDocTypeOption value) {
        this.docTypeOption = value;
    }

}
