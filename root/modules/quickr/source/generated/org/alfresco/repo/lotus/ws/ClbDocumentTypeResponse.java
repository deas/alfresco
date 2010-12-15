
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbDocumentTypeResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbDocumentTypeResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="documentType" type="{http://model.xsd.clb.content.ibm.com}ClbDocumentType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbDocumentTypeResponse", propOrder = {
    "documentType"
})
public class ClbDocumentTypeResponse
    extends ClbResponse
{

    @XmlElement(required = true, nillable = true)
    protected ClbDocumentType documentType;

    /**
     * Gets the value of the documentType property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocumentType }
     *     
     */
    public ClbDocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the value of the documentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocumentType }
     *     
     */
    public void setDocumentType(ClbDocumentType value) {
        this.documentType = value;
    }

}
