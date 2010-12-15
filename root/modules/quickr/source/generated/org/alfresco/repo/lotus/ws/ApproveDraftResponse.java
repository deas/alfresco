
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
 *         &lt;element name="approveDraftReturn" type="{http://webservices.clb.content.ibm.com}ClbApproveDraftResponse"/>
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
    "approveDraftReturn"
})
@XmlRootElement(name = "approveDraftResponse")
public class ApproveDraftResponse {

    @XmlElement(required = true)
    protected ClbApproveDraftResponse approveDraftReturn;

    /**
     * Gets the value of the approveDraftReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbApproveDraftResponse }
     *     
     */
    public ClbApproveDraftResponse getApproveDraftReturn() {
        return approveDraftReturn;
    }

    /**
     * Sets the value of the approveDraftReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbApproveDraftResponse }
     *     
     */
    public void setApproveDraftReturn(ClbApproveDraftResponse value) {
        this.approveDraftReturn = value;
    }

}
