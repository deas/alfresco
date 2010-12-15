
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
 *         &lt;element name="getSubmittedDraftsReturn" type="{http://webservices.clb.content.ibm.com}ClbDraftsResponse"/>
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
    "getSubmittedDraftsReturn"
})
@XmlRootElement(name = "getSubmittedDraftsResponse")
public class GetSubmittedDraftsResponse {

    @XmlElement(required = true)
    protected ClbDraftsResponse getSubmittedDraftsReturn;

    /**
     * Gets the value of the getSubmittedDraftsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDraftsResponse }
     *     
     */
    public ClbDraftsResponse getGetSubmittedDraftsReturn() {
        return getSubmittedDraftsReturn;
    }

    /**
     * Sets the value of the getSubmittedDraftsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDraftsResponse }
     *     
     */
    public void setGetSubmittedDraftsReturn(ClbDraftsResponse value) {
        this.getSubmittedDraftsReturn = value;
    }

}
