
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
 *         &lt;element name="cancelDocumentReturn" type="{http://webservices.clb.content.ibm.com}ClbCancelDocumentResponse"/>
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
    "cancelDocumentReturn"
})
@XmlRootElement(name = "cancelDocumentResponse")
public class CancelDocumentResponse {

    @XmlElement(required = true)
    protected ClbCancelDocumentResponse cancelDocumentReturn;

    /**
     * Gets the value of the cancelDocumentReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbCancelDocumentResponse }
     *     
     */
    public ClbCancelDocumentResponse getCancelDocumentReturn() {
        return cancelDocumentReturn;
    }

    /**
     * Sets the value of the cancelDocumentReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbCancelDocumentResponse }
     *     
     */
    public void setCancelDocumentReturn(ClbCancelDocumentResponse value) {
        this.cancelDocumentReturn = value;
    }

}
