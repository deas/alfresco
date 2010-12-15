
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbViewFormatColumn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbViewFormatColumn">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="propertyType" type="{http://model.xsd.clb.content.ibm.com}ClbPropertyType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="columnId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="title" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="propertyId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="propertySheetId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sortOrder" use="required" type="{http://model.xsd.clb.content.ibm.com}ClbSortOrder" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbViewFormatColumn", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "propertyType"
})
public class ClbViewFormatColumn {

    @XmlElement(required = true)
    protected ClbPropertyType propertyType;
    @XmlAttribute(required = true)
    protected String columnId;
    @XmlAttribute(required = true)
    protected String title;
    @XmlAttribute(required = true)
    protected String propertyId;
    @XmlAttribute(required = true)
    protected String propertySheetId;
    @XmlAttribute(required = true)
    protected ClbSortOrder sortOrder;

    /**
     * Gets the value of the propertyType property.
     * 
     * @return
     *     possible object is
     *     {@link ClbPropertyType }
     *     
     */
    public ClbPropertyType getPropertyType() {
        return propertyType;
    }

    /**
     * Sets the value of the propertyType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbPropertyType }
     *     
     */
    public void setPropertyType(ClbPropertyType value) {
        this.propertyType = value;
    }

    /**
     * Gets the value of the columnId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColumnId() {
        return columnId;
    }

    /**
     * Sets the value of the columnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColumnId(String value) {
        this.columnId = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the propertyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyId() {
        return propertyId;
    }

    /**
     * Sets the value of the propertyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyId(String value) {
        this.propertyId = value;
    }

    /**
     * Gets the value of the propertySheetId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertySheetId() {
        return propertySheetId;
    }

    /**
     * Sets the value of the propertySheetId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertySheetId(String value) {
        this.propertySheetId = value;
    }

    /**
     * Gets the value of the sortOrder property.
     * 
     * @return
     *     possible object is
     *     {@link ClbSortOrder }
     *     
     */
    public ClbSortOrder getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the value of the sortOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbSortOrder }
     *     
     */
    public void setSortOrder(ClbSortOrder value) {
        this.sortOrder = value;
    }

}
