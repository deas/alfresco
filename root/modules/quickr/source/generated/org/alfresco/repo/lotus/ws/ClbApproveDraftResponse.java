
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbApproveDraftResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbApproveDraftResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;attribute name="draftPublished" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbApproveDraftResponse")
public class ClbApproveDraftResponse
    extends ClbResponse
{

    @XmlAttribute(required = true)
    protected boolean draftPublished;

    /**
     * Gets the value of the draftPublished property.
     * 
     */
    public boolean isDraftPublished() {
        return draftPublished;
    }

    /**
     * Sets the value of the draftPublished property.
     * 
     */
    public void setDraftPublished(boolean value) {
        this.draftPublished = value;
    }

}
