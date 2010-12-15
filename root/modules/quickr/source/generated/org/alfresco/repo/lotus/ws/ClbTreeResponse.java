
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbTreeResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbTreeResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="root" type="{http://model.xsd.clb.content.ibm.com}ClbTreeFolder"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbTreeResponse", propOrder = {
    "root"
})
public class ClbTreeResponse
    extends ClbResponse
{

    @XmlElement(required = true, nillable = true)
    protected ClbTreeFolder root;

    /**
     * Gets the value of the root property.
     * 
     * @return
     *     possible object is
     *     {@link ClbTreeFolder }
     *     
     */
    public ClbTreeFolder getRoot() {
        return root;
    }

    /**
     * Sets the value of the root property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbTreeFolder }
     *     
     */
    public void setRoot(ClbTreeFolder value) {
        this.root = value;
    }

}
