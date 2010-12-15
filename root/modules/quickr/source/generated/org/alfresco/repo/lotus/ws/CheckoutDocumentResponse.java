
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
 *         &lt;element name="checkoutDocumentReturn" type="{http://webservices.clb.content.ibm.com}ClbDocumentResponse"/>
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
    "checkoutDocumentReturn"
})
@XmlRootElement(name = "checkoutDocumentResponse")
public class CheckoutDocumentResponse {

    @XmlElement(required = true)
    protected ClbDocumentResponse checkoutDocumentReturn;

    /**
     * Gets the value of the checkoutDocumentReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocumentResponse }
     *     
     */
    public ClbDocumentResponse getCheckoutDocumentReturn() {
        return checkoutDocumentReturn;
    }

    /**
     * Sets the value of the checkoutDocumentReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocumentResponse }
     *     
     */
    public void setCheckoutDocumentReturn(ClbDocumentResponse value) {
        this.checkoutDocumentReturn = value;
    }

}
