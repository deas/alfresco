
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
 *         &lt;element name="executeViewReturn" type="{http://webservices.clb.content.ibm.com}ClbViewResultsResponse"/>
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
    "executeViewReturn"
})
@XmlRootElement(name = "executeViewResponse")
public class ExecuteViewResponse {

    @XmlElement(required = true)
    protected ClbViewResultsResponse executeViewReturn;

    /**
     * Gets the value of the executeViewReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ClbViewResultsResponse }
     *     
     */
    public ClbViewResultsResponse getExecuteViewReturn() {
        return executeViewReturn;
    }

    /**
     * Sets the value of the executeViewReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbViewResultsResponse }
     *     
     */
    public void setExecuteViewReturn(ClbViewResultsResponse value) {
        this.executeViewReturn = value;
    }

}
