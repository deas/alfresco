
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
 *         &lt;element name="getTreeReturn" type="{http://webservices.clb.content.ibm.com}ClbTreeResponse"/>
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
    "getTreeReturn"
})
@XmlRootElement(name = "getTreeResponse")
public class GetTreeResponse {

    @XmlElement(required = true)
    protected ClbTreeResponse getTreeReturn;

    /**
     * Gets the value of the getTreeReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbTreeResponse }
     *     
     */
    public ClbTreeResponse getGetTreeReturn() {
        return getTreeReturn;
    }

    /**
     * Sets the value of the getTreeReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbTreeResponse }
     *     
     */
    public void setGetTreeReturn(ClbTreeResponse value) {
        this.getTreeReturn = value;
    }

}
