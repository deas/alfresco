
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbDocumentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbDocumentResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="document" type="{http://model.xsd.clb.content.ibm.com}ClbDocument"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbDocumentResponse", propOrder = {
    "document"
})
public class ClbDocumentResponse
    extends ClbResponse
{

    @XmlElement(required = true, nillable = true)
    protected ClbDocument document;

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocument }
     *     
     */
    public ClbDocument getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocument }
     *     
     */
    public void setDocument(ClbDocument value) {
        this.document = value;
    }

}
