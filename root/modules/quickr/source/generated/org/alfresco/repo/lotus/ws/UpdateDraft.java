
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
 *         &lt;element name="draft" type="{http://model.xsd.clb.content.ibm.com}ClbDraft"/>
 *         &lt;element name="createDocument" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "draft",
    "createDocument"
})
@XmlRootElement(name = "updateDraft")
public class UpdateDraft {

    @XmlElement(required = true)
    protected ClbDraft draft;
    protected boolean createDocument;

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

    /**
     * Gets the value of the createDocument property.
     * 
     */
    public boolean isCreateDocument() {
        return createDocument;
    }

    /**
     * Sets the value of the createDocument property.
     * 
     */
    public void setCreateDocument(boolean value) {
        this.createDocument = value;
    }

}
