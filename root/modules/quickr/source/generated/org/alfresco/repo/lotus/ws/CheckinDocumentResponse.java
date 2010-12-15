
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
 *         &lt;element name="checkinDocumentReturn" type="{http://webservices.clb.content.ibm.com}ClbCheckinResponse"/>
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
    "checkinDocumentReturn"
})
@XmlRootElement(name = "checkinDocumentResponse")
public class CheckinDocumentResponse {

    @XmlElement(required = true)
    protected ClbCheckinResponse checkinDocumentReturn;

    /**
     * Gets the value of the checkinDocumentReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbCheckinResponse }
     *     
     */
    public ClbCheckinResponse getCheckinDocumentReturn() {
        return checkinDocumentReturn;
    }

    /**
     * Sets the value of the checkinDocumentReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbCheckinResponse }
     *     
     */
    public void setCheckinDocumentReturn(ClbCheckinResponse value) {
        this.checkinDocumentReturn = value;
    }

}
