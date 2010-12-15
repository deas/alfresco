
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
 *         &lt;element name="getPropertySheetTypeReturn" type="{http://webservices.clb.content.ibm.com}ClbPropertySheetTypeResponse"/>
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
    "getPropertySheetTypeReturn"
})
@XmlRootElement(name = "getPropertySheetTypeResponse")
public class GetPropertySheetTypeResponse {

    @XmlElement(required = true)
    protected ClbPropertySheetTypeResponse getPropertySheetTypeReturn;

    /**
     * Gets the value of the getPropertySheetTypeReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbPropertySheetTypeResponse }
     *     
     */
    public ClbPropertySheetTypeResponse getGetPropertySheetTypeReturn() {
        return getPropertySheetTypeReturn;
    }

    /**
     * Sets the value of the getPropertySheetTypeReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbPropertySheetTypeResponse }
     *     
     */
    public void setGetPropertySheetTypeReturn(ClbPropertySheetTypeResponse value) {
        this.getPropertySheetTypeReturn = value;
    }

}
