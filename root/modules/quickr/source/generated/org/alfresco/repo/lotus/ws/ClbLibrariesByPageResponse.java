
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbLibrariesByPageResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbLibrariesByPageResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbLibrariesResponse">
 *       &lt;sequence>
 *         &lt;element name="paginationInfo" type="{http://webservices.clb.content.ibm.com}PaginationInfo"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbLibrariesByPageResponse", propOrder = {
    "paginationInfo"
})
public class ClbLibrariesByPageResponse
    extends ClbLibrariesResponse
{

    @XmlElement(required = true)
    protected PaginationInfo paginationInfo;

    /**
     * Gets the value of the paginationInfo property.
     * 
     * @return
     *     possible object is
     *     {@link PaginationInfo }
     *     
     */
    public PaginationInfo getPaginationInfo() {
        return paginationInfo;
    }

    /**
     * Sets the value of the paginationInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaginationInfo }
     *     
     */
    public void setPaginationInfo(PaginationInfo value) {
        this.paginationInfo = value;
    }

}
