
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
 *         &lt;element name="getDocumentsReturn" type="{http://webservices.clb.content.ibm.com}ClbDocumentsResponse"/>
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
    "getDocumentsReturn"
})
@XmlRootElement(name = "getDocumentsResponse")
public class GetDocumentsResponse {

    @XmlElement(required = true)
    protected ClbDocumentsResponse getDocumentsReturn;

    /**
     * Gets the value of the getDocumentsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocumentsResponse }
     *     
     */
    public ClbDocumentsResponse getGetDocumentsReturn() {
        return getDocumentsReturn;
    }

    /**
     * Sets the value of the getDocumentsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocumentsResponse }
     *     
     */
    public void setGetDocumentsReturn(ClbDocumentsResponse value) {
        this.getDocumentsReturn = value;
    }

}
