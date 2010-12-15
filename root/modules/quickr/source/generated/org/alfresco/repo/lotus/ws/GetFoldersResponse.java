
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
 *         &lt;element name="getFoldersReturn" type="{http://webservices.clb.content.ibm.com}ClbFoldersResponse"/>
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
    "getFoldersReturn"
})
@XmlRootElement(name = "getFoldersResponse")
public class GetFoldersResponse {

    @XmlElement(required = true)
    protected ClbFoldersResponse getFoldersReturn;

    /**
     * Gets the value of the getFoldersReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbFoldersResponse }
     *     
     */
    public ClbFoldersResponse getGetFoldersReturn() {
        return getFoldersReturn;
    }

    /**
     * Sets the value of the getFoldersReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbFoldersResponse }
     *     
     */
    public void setGetFoldersReturn(ClbFoldersResponse value) {
        this.getFoldersReturn = value;
    }

}
