
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
 *         &lt;element name="getBusinessComponentReturn" type="{http://webservices.clb.content.ibm.com}ClbLibraryResponse"/>
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
    "getBusinessComponentReturn"
})
@XmlRootElement(name = "getBusinessComponentResponse")
public class GetBusinessComponentResponse {

    @XmlElement(required = true)
    protected ClbLibraryResponse getBusinessComponentReturn;

    /**
     * Gets the value of the getBusinessComponentReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbLibraryResponse }
     *     
     */
    public ClbLibraryResponse getGetBusinessComponentReturn() {
        return getBusinessComponentReturn;
    }

    /**
     * Sets the value of the getBusinessComponentReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbLibraryResponse }
     *     
     */
    public void setGetBusinessComponentReturn(ClbLibraryResponse value) {
        this.getBusinessComponentReturn = value;
    }

}
