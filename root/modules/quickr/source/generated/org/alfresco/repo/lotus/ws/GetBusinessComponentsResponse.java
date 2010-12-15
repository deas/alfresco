
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
 *         &lt;element name="getBusinessComponentsReturn" type="{http://webservices.clb.content.ibm.com}ClbLibrariesResponse"/>
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
    "getBusinessComponentsReturn"
})
@XmlRootElement(name = "getBusinessComponentsResponse")
public class GetBusinessComponentsResponse {

    @XmlElement(required = true)
    protected ClbLibrariesResponse getBusinessComponentsReturn;

    /**
     * Gets the value of the getBusinessComponentsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbLibrariesResponse }
     *     
     */
    public ClbLibrariesResponse getGetBusinessComponentsReturn() {
        return getBusinessComponentsReturn;
    }

    /**
     * Sets the value of the getBusinessComponentsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbLibrariesResponse }
     *     
     */
    public void setGetBusinessComponentsReturn(ClbLibrariesResponse value) {
        this.getBusinessComponentsReturn = value;
    }

}
