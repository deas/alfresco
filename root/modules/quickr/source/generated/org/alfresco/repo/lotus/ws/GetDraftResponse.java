
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
 *         &lt;element name="getDraftReturn" type="{http://webservices.clb.content.ibm.com}ClbDraftResponse"/>
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
    "getDraftReturn"
})
@XmlRootElement(name = "getDraftResponse")
public class GetDraftResponse {

    @XmlElement(required = true)
    protected ClbDraftResponse getDraftReturn;

    /**
     * Gets the value of the getDraftReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDraftResponse }
     *     
     */
    public ClbDraftResponse getGetDraftReturn() {
        return getDraftReturn;
    }

    /**
     * Sets the value of the getDraftReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDraftResponse }
     *     
     */
    public void setGetDraftReturn(ClbDraftResponse value) {
        this.getDraftReturn = value;
    }

}
