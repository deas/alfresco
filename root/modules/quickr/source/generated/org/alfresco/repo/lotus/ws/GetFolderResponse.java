
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
 *         &lt;element name="getFolderReturn" type="{http://webservices.clb.content.ibm.com}ClbFolderResponse"/>
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
    "getFolderReturn"
})
@XmlRootElement(name = "getFolderResponse")
public class GetFolderResponse {

    @XmlElement(required = true)
    protected ClbFolderResponse getFolderReturn;

    /**
     * Gets the value of the getFolderReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbFolderResponse }
     *     
     */
    public ClbFolderResponse getGetFolderReturn() {
        return getFolderReturn;
    }

    /**
     * Sets the value of the getFolderReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbFolderResponse }
     *     
     */
    public void setGetFolderReturn(ClbFolderResponse value) {
        this.getFolderReturn = value;
    }

}
