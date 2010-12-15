
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
 *         &lt;element name="getLockedDocumentsReturn" type="{http://webservices.clb.content.ibm.com}ClbDocumentsResponse"/>
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
    "getLockedDocumentsReturn"
})
@XmlRootElement(name = "getLockedDocumentsResponse")
public class GetLockedDocumentsResponse {

    @XmlElement(required = true)
    protected ClbDocumentsResponse getLockedDocumentsReturn;

    /**
     * Gets the value of the getLockedDocumentsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocumentsResponse }
     *     
     */
    public ClbDocumentsResponse getGetLockedDocumentsReturn() {
        return getLockedDocumentsReturn;
    }

    /**
     * Sets the value of the getLockedDocumentsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocumentsResponse }
     *     
     */
    public void setGetLockedDocumentsReturn(ClbDocumentsResponse value) {
        this.getLockedDocumentsReturn = value;
    }

}
