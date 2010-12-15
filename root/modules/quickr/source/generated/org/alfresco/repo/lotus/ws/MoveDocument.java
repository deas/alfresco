
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
 *         &lt;element name="srcId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="srcPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "srcId",
    "srcPath",
    "destId",
    "destPath"
})
@XmlRootElement(name = "moveDocument")
public class MoveDocument {

    @XmlElement(required = true, nillable = true)
    protected String srcId;
    @XmlElement(required = true, nillable = true)
    protected String srcPath;
    @XmlElement(required = true, nillable = true)
    protected String destId;
    @XmlElement(required = true, nillable = true)
    protected String destPath;

    /**
     * Gets the value of the srcId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcId() {
        return srcId;
    }

    /**
     * Sets the value of the srcId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcId(String value) {
        this.srcId = value;
    }

    /**
     * Gets the value of the srcPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcPath() {
        return srcPath;
    }

    /**
     * Sets the value of the srcPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcPath(String value) {
        this.srcPath = value;
    }

    /**
     * Gets the value of the destId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestId() {
        return destId;
    }

    /**
     * Sets the value of the destId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestId(String value) {
        this.destId = value;
    }

    /**
     * Gets the value of the destPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestPath() {
        return destPath;
    }

    /**
     * Sets the value of the destPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestPath(String value) {
        this.destPath = value;
    }

}
