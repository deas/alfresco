
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
 *         &lt;element name="getDocumentURLReturn" type="{http://webservices.clb.content.ibm.com}ClbContentURLResponse"/>
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
    "getDocumentURLReturn"
})
@XmlRootElement(name = "getDocumentURLResponse")
public class GetDocumentURLResponse {

    @XmlElement(required = true)
    protected ClbContentURLResponse getDocumentURLReturn;

    /**
     * Gets the value of the getDocumentURLReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbContentURLResponse }
     *     
     */
    public ClbContentURLResponse getGetDocumentURLReturn() {
        return getDocumentURLReturn;
    }

    /**
     * Sets the value of the getDocumentURLReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbContentURLResponse }
     *     
     */
    public void setGetDocumentURLReturn(ClbContentURLResponse value) {
        this.getDocumentURLReturn = value;
    }

}
