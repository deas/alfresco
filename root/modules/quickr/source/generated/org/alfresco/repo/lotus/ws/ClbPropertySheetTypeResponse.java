
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbPropertySheetTypeResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbPropertySheetTypeResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="propertySheetType" type="{http://model.xsd.clb.content.ibm.com}ClbPropertySheetType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbPropertySheetTypeResponse", propOrder = {
    "propertySheetType"
})
public class ClbPropertySheetTypeResponse
    extends ClbResponse
{

    @XmlElement(required = true, nillable = true)
    protected ClbPropertySheetType propertySheetType;

    /**
     * Gets the value of the propertySheetType property.
     * 
     * @return
     *     possible object is
     *     {@link ClbPropertySheetType }
     *     
     */
    public ClbPropertySheetType getPropertySheetType() {
        return propertySheetType;
    }

    /**
     * Sets the value of the propertySheetType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbPropertySheetType }
     *     
     */
    public void setPropertySheetType(ClbPropertySheetType value) {
        this.propertySheetType = value;
    }

}
