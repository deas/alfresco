
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
 *         &lt;element name="moveDocumentReturn" type="{http://webservices.clb.content.ibm.com}ClbMoveResponse"/>
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
    "moveDocumentReturn"
})
@XmlRootElement(name = "moveDocumentResponse")
public class MoveDocumentResponse {

    @XmlElement(required = true)
    protected ClbMoveResponse moveDocumentReturn;

    /**
     * Gets the value of the moveDocumentReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbMoveResponse }
     *     
     */
    public ClbMoveResponse getMoveDocumentReturn() {
        return moveDocumentReturn;
    }

    /**
     * Sets the value of the moveDocumentReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbMoveResponse }
     *     
     */
    public void setMoveDocumentReturn(ClbMoveResponse value) {
        this.moveDocumentReturn = value;
    }

}
