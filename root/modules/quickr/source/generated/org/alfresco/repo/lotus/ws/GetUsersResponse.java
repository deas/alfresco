
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
 *         &lt;element name="getUsersReturn" type="{http://webservices.clb.content.ibm.com}UsersResponse"/>
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
    "getUsersReturn"
})
@XmlRootElement(name = "getUsersResponse")
public class GetUsersResponse {

    @XmlElement(required = true)
    protected UsersResponse getUsersReturn;

    /**
     * Gets the value of the getUsersReturn property.
     * 
     * @return
     *     possible object is
     *     {@link UsersResponse }
     *     
     */
    public UsersResponse getGetUsersReturn() {
        return getUsersReturn;
    }

    /**
     * Sets the value of the getUsersReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link UsersResponse }
     *     
     */
    public void setGetUsersReturn(UsersResponse value) {
        this.getUsersReturn = value;
    }

}
