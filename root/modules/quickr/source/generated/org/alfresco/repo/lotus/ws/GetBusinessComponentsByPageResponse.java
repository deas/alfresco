
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
 *         &lt;element name="getBusinessComponentsByPageReturn" type="{http://webservices.clb.content.ibm.com}ClbLibrariesByPageResponse"/>
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
    "getBusinessComponentsByPageReturn"
})
@XmlRootElement(name = "getBusinessComponentsByPageResponse")
public class GetBusinessComponentsByPageResponse {

    @XmlElement(required = true)
    protected ClbLibrariesByPageResponse getBusinessComponentsByPageReturn;

    /**
     * Gets the value of the getBusinessComponentsByPageReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbLibrariesByPageResponse }
     *     
     */
    public ClbLibrariesByPageResponse getGetBusinessComponentsByPageReturn() {
        return getBusinessComponentsByPageReturn;
    }

    /**
     * Sets the value of the getBusinessComponentsByPageReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbLibrariesByPageResponse }
     *     
     */
    public void setGetBusinessComponentsByPageReturn(ClbLibrariesByPageResponse value) {
        this.getBusinessComponentsByPageReturn = value;
    }

}
