
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
 *         &lt;element name="getDocumentTypeReturn" type="{http://webservices.clb.content.ibm.com}ClbDocumentTypeResponse"/>
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
    "getDocumentTypeReturn"
})
@XmlRootElement(name = "getDocumentTypeResponse")
public class GetDocumentTypeResponse {

    @XmlElement(required = true)
    protected ClbDocumentTypeResponse getDocumentTypeReturn;

    /**
     * Gets the value of the getDocumentTypeReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocumentTypeResponse }
     *     
     */
    public ClbDocumentTypeResponse getGetDocumentTypeReturn() {
        return getDocumentTypeReturn;
    }

    /**
     * Sets the value of the getDocumentTypeReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocumentTypeResponse }
     *     
     */
    public void setGetDocumentTypeReturn(ClbDocumentTypeResponse value) {
        this.getDocumentTypeReturn = value;
    }

}
