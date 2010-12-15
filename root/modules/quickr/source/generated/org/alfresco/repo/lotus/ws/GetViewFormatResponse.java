
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
 *         &lt;element name="getViewFormatReturn" type="{http://webservices.clb.content.ibm.com}ClbViewFormatResponse"/>
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
    "getViewFormatReturn"
})
@XmlRootElement(name = "getViewFormatResponse")
public class GetViewFormatResponse {

    @XmlElement(required = true)
    protected ClbViewFormatResponse getViewFormatReturn;

    /**
     * Gets the value of the getViewFormatReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbViewFormatResponse }
     *     
     */
    public ClbViewFormatResponse getGetViewFormatReturn() {
        return getViewFormatReturn;
    }

    /**
     * Sets the value of the getViewFormatReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbViewFormatResponse }
     *     
     */
    public void setGetViewFormatReturn(ClbViewFormatResponse value) {
        this.getViewFormatReturn = value;
    }

}
