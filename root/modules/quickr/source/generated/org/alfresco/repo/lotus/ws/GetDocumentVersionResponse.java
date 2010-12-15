
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
 *         &lt;element name="getDocumentVersionReturn" type="{http://webservices.clb.content.ibm.com}ClbDocumentResponse"/>
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
    "getDocumentVersionReturn"
})
@XmlRootElement(name = "getDocumentVersionResponse")
public class GetDocumentVersionResponse {

    @XmlElement(required = true)
    protected ClbDocumentResponse getDocumentVersionReturn;

    /**
     * Gets the value of the getDocumentVersionReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocumentResponse }
     *     
     */
    public ClbDocumentResponse getGetDocumentVersionReturn() {
        return getDocumentVersionReturn;
    }

    /**
     * Sets the value of the getDocumentVersionReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocumentResponse }
     *     
     */
    public void setGetDocumentVersionReturn(ClbDocumentResponse value) {
        this.getDocumentVersionReturn = value;
    }

}
