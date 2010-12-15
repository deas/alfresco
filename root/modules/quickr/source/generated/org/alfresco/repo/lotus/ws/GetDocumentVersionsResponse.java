
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
 *         &lt;element name="getDocumentVersionsReturn" type="{http://webservices.clb.content.ibm.com}ClbVersionsResponse"/>
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
    "getDocumentVersionsReturn"
})
@XmlRootElement(name = "getDocumentVersionsResponse")
public class GetDocumentVersionsResponse {

    @XmlElement(required = true)
    protected ClbVersionsResponse getDocumentVersionsReturn;

    /**
     * Gets the value of the getDocumentVersionsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbVersionsResponse }
     *     
     */
    public ClbVersionsResponse getGetDocumentVersionsReturn() {
        return getDocumentVersionsReturn;
    }

    /**
     * Sets the value of the getDocumentVersionsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbVersionsResponse }
     *     
     */
    public void setGetDocumentVersionsReturn(ClbVersionsResponse value) {
        this.getDocumentVersionsReturn = value;
    }

}
