
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
 *         &lt;element name="createDocumentReturn" type="{http://webservices.clb.content.ibm.com}ClbDocumentResponse"/>
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
    "createDocumentReturn"
})
@XmlRootElement(name = "createDocumentResponse")
public class CreateDocumentResponse {

    @XmlElement(required = true)
    protected ClbDocumentResponse createDocumentReturn;

    /**
     * Gets the value of the createDocumentReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocumentResponse }
     *     
     */
    public ClbDocumentResponse getCreateDocumentReturn() {
        return createDocumentReturn;
    }

    /**
     * Sets the value of the createDocumentReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocumentResponse }
     *     
     */
    public void setCreateDocumentReturn(ClbDocumentResponse value) {
        this.createDocumentReturn = value;
    }

}
