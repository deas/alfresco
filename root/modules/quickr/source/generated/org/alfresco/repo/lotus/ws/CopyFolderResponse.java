
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
 *         &lt;element name="copyFolderReturn" type="{http://webservices.clb.content.ibm.com}ClbCopyResponse"/>
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
    "copyFolderReturn"
})
@XmlRootElement(name = "copyFolderResponse")
public class CopyFolderResponse {

    @XmlElement(required = true)
    protected ClbCopyResponse copyFolderReturn;

    /**
     * Gets the value of the copyFolderReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbCopyResponse }
     *     
     */
    public ClbCopyResponse getCopyFolderReturn() {
        return copyFolderReturn;
    }

    /**
     * Sets the value of the copyFolderReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbCopyResponse }
     *     
     */
    public void setCopyFolderReturn(ClbCopyResponse value) {
        this.copyFolderReturn = value;
    }

}
