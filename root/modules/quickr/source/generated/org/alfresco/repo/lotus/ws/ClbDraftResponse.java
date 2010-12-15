
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbDraftResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbDraftResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="draft" type="{http://model.xsd.clb.content.ibm.com}ClbDraft"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbDraftResponse", propOrder = {
    "draft"
})
public class ClbDraftResponse
    extends ClbResponse
{

    @XmlElement(required = true, nillable = true)
    protected ClbDraft draft;

    /**
     * Gets the value of the draft property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDraft }
     *     
     */
    public ClbDraft getDraft() {
        return draft;
    }

    /**
     * Sets the value of the draft property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDraft }
     *     
     */
    public void setDraft(ClbDraft value) {
        this.draft = value;
    }

}
