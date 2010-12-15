
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
 *         &lt;element name="submitDraftReturn" type="{http://webservices.clb.content.ibm.com}ClbResponse"/>
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
    "submitDraftReturn"
})
@XmlRootElement(name = "submitDraftResponse")
public class SubmitDraftResponse {

    @XmlElement(required = true)
    protected ClbResponse submitDraftReturn;

    /**
     * Gets the value of the submitDraftReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbResponse }
     *     
     */
    public ClbResponse getSubmitDraftReturn() {
        return submitDraftReturn;
    }

    /**
     * Sets the value of the submitDraftReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbResponse }
     *     
     */
    public void setSubmitDraftReturn(ClbResponse value) {
        this.submitDraftReturn = value;
    }

}
