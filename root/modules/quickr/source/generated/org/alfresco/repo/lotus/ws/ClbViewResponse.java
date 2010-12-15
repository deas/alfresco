
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbViewResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbViewResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="view" type="{http://model.xsd.clb.content.ibm.com}ClbView"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbViewResponse", propOrder = {
    "view"
})
public class ClbViewResponse
    extends ClbResponse
{

    @XmlElement(required = true, nillable = true)
    protected ClbView view;

    /**
     * Gets the value of the view property.
     * 
     * @return
     *     possible object is
     *     {@link ClbView }
     *     
     */
    public ClbView getView() {
        return view;
    }

    /**
     * Sets the value of the view property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbView }
     *     
     */
    public void setView(ClbView value) {
        this.view = value;
    }

}
