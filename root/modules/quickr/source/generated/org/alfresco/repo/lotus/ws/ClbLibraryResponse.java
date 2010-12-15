
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbLibraryResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbLibraryResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="library" type="{http://model.xsd.clb.content.ibm.com}ClbLibrary"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbLibraryResponse", propOrder = {
    "library"
})
public class ClbLibraryResponse
    extends ClbResponse
{

    @XmlElement(required = true, nillable = true)
    protected ClbLibrary library;

    /**
     * Gets the value of the library property.
     * 
     * @return
     *     possible object is
     *     {@link ClbLibrary }
     *     
     */
    public ClbLibrary getLibrary() {
        return library;
    }

    /**
     * Sets the value of the library property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbLibrary }
     *     
     */
    public void setLibrary(ClbLibrary value) {
        this.library = value;
    }

}
