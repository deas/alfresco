
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
 *         &lt;element name="getPrivateViewsReturn" type="{http://webservices.clb.content.ibm.com}ClbViewsResponse"/>
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
    "getPrivateViewsReturn"
})
@XmlRootElement(name = "getPrivateViewsResponse")
public class GetPrivateViewsResponse {

    @XmlElement(required = true)
    protected ClbViewsResponse getPrivateViewsReturn;

    /**
     * Gets the value of the getPrivateViewsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbViewsResponse }
     *     
     */
    public ClbViewsResponse getGetPrivateViewsReturn() {
        return getPrivateViewsReturn;
    }

    /**
     * Sets the value of the getPrivateViewsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbViewsResponse }
     *     
     */
    public void setGetPrivateViewsReturn(ClbViewsResponse value) {
        this.getPrivateViewsReturn = value;
    }

}
