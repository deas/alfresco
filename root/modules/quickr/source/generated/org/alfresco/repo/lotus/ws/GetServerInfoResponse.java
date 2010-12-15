
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
 *         &lt;element name="getServerInfoReturn" type="{http://webservices.clb.content.ibm.com}ServerInfoResponse"/>
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
    "getServerInfoReturn"
})
@XmlRootElement(name = "getServerInfoResponse")
public class GetServerInfoResponse {

    @XmlElement(required = true)
    protected ServerInfoResponse getServerInfoReturn;

    /**
     * Gets the value of the getServerInfoReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ServerInfoResponse }
     *     
     */
    public ServerInfoResponse getGetServerInfoReturn() {
        return getServerInfoReturn;
    }

    /**
     * Sets the value of the getServerInfoReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServerInfoResponse }
     *     
     */
    public void setGetServerInfoReturn(ServerInfoResponse value) {
        this.getServerInfoReturn = value;
    }

}
