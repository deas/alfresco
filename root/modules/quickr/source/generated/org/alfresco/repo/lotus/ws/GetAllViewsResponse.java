
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
 *         &lt;element name="getAllViewsReturn" type="{http://webservices.clb.content.ibm.com}ClbViewsResponse"/>
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
    "getAllViewsReturn"
})
@XmlRootElement(name = "getAllViewsResponse")
public class GetAllViewsResponse {

    @XmlElement(required = true)
    protected ClbViewsResponse getAllViewsReturn;

    /**
     * Gets the value of the getAllViewsReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbViewsResponse }
     *     
     */
    public ClbViewsResponse getGetAllViewsReturn() {
        return getAllViewsReturn;
    }

    /**
     * Sets the value of the getAllViewsReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbViewsResponse }
     *     
     */
    public void setGetAllViewsReturn(ClbViewsResponse value) {
        this.getAllViewsReturn = value;
    }

}
