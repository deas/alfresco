
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbCancelDocumentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbCancelDocumentResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;attribute name="documentDeleted" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbCancelDocumentResponse")
public class ClbCancelDocumentResponse
    extends ClbResponse
{

    @XmlAttribute(required = true)
    protected boolean documentDeleted;

    /**
     * Gets the value of the documentDeleted property.
     * 
     */
    public boolean isDocumentDeleted() {
        return documentDeleted;
    }

    /**
     * Sets the value of the documentDeleted property.
     * 
     */
    public void setDocumentDeleted(boolean value) {
        this.documentDeleted = value;
    }

}
