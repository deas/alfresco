
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
 *         &lt;element name="createFolderReturn" type="{http://webservices.clb.content.ibm.com}ClbFolderResponse"/>
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
    "createFolderReturn"
})
@XmlRootElement(name = "createFolderResponse")
public class CreateFolderResponse {

    @XmlElement(required = true)
    protected ClbFolderResponse createFolderReturn;

    /**
     * Gets the value of the createFolderReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbFolderResponse }
     *     
     */
    public ClbFolderResponse getCreateFolderReturn() {
        return createFolderReturn;
    }

    /**
     * Sets the value of the createFolderReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbFolderResponse }
     *     
     */
    public void setCreateFolderReturn(ClbFolderResponse value) {
        this.createFolderReturn = value;
    }

}
