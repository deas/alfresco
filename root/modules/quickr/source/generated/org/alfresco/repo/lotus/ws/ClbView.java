
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbView complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbView">
 *   &lt;complexContent>
 *     &lt;extension base="{http://model.xsd.content.ibm.com}Content">
 *       &lt;sequence>
 *         &lt;element name="types" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="formatXml" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="folderId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="shared" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="formatStyle" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbView", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "types",
    "formatXml"
})
public class ClbView
    extends Content
{

    protected List<String> types;
    @XmlElement(required = true, nillable = true)
    protected String formatXml;
    @XmlAttribute(required = true)
    protected String folderId;
    @XmlAttribute(required = true)
    protected boolean shared;
    @XmlAttribute(required = true)
    protected String formatStyle;

    /**
     * Gets the value of the types property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the types property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTypes() {
        if (types == null) {
            types = new ArrayList<String>();
        }
        return this.types;
    }

    /**
     * Gets the value of the formatXml property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormatXml() {
        return formatXml;
    }

    /**
     * Sets the value of the formatXml property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormatXml(String value) {
        this.formatXml = value;
    }

    /**
     * Gets the value of the folderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderId() {
        return folderId;
    }

    /**
     * Sets the value of the folderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderId(String value) {
        this.folderId = value;
    }

    /**
     * Gets the value of the shared property.
     * 
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * Sets the value of the shared property.
     * 
     */
    public void setShared(boolean value) {
        this.shared = value;
    }

    /**
     * Gets the value of the formatStyle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormatStyle() {
        return formatStyle;
    }

    /**
     * Sets the value of the formatStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormatStyle(String value) {
        this.formatStyle = value;
    }

}
