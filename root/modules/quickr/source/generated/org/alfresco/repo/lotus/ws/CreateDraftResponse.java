
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
 *         &lt;element name="createDraftReturn" type="{http://webservices.clb.content.ibm.com}ClbDraftResponse"/>
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
    "createDraftReturn"
})
@XmlRootElement(name = "createDraftResponse")
public class CreateDraftResponse {

    @XmlElement(required = true)
    protected ClbDraftResponse createDraftReturn;

    /**
     * Gets the value of the createDraftReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDraftResponse }
     *     
     */
    public ClbDraftResponse getCreateDraftReturn() {
        return createDraftReturn;
    }

    /**
     * Sets the value of the createDraftReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDraftResponse }
     *     
     */
    public void setCreateDraftReturn(ClbDraftResponse value) {
        this.createDraftReturn = value;
    }

}
