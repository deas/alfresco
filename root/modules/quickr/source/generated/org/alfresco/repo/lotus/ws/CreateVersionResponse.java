
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
 *         &lt;element name="createVersionReturn" type="{http://webservices.clb.content.ibm.com}ClbVersionResponse"/>
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
    "createVersionReturn"
})
@XmlRootElement(name = "createVersionResponse")
public class CreateVersionResponse {

    @XmlElement(required = true)
    protected ClbVersionResponse createVersionReturn;

    /**
     * Gets the value of the createVersionReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbVersionResponse }
     *     
     */
    public ClbVersionResponse getCreateVersionReturn() {
        return createVersionReturn;
    }

    /**
     * Sets the value of the createVersionReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbVersionResponse }
     *     
     */
    public void setCreateVersionReturn(ClbVersionResponse value) {
        this.createVersionReturn = value;
    }

}
