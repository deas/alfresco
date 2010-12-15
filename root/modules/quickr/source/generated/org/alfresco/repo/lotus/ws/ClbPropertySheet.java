
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbPropertySheet complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbPropertySheet">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dynamicLongs" type="{http://model.xsd.clb.content.ibm.com}ClbDynamicLongValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dynamicDates" type="{http://model.xsd.clb.content.ibm.com}ClbDynamicDateValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dynamicDoubles" type="{http://model.xsd.clb.content.ibm.com}ClbDynamicDoubleValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dynamicBooleans" type="{http://model.xsd.clb.content.ibm.com}ClbDynamicBooleanValue" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dynamicStrings" type="{http://model.xsd.clb.content.ibm.com}ClbDynamicStringValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://model.xsd.content.ibm.com}NamedData"/>
 *       &lt;attGroup ref="{http://model.xsd.content.ibm.com}DescriptionData"/>
 *       &lt;attribute name="propertySheetTypeId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="extracted" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbPropertySheet", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "dynamicLongs",
    "dynamicDates",
    "dynamicDoubles",
    "dynamicBooleans",
    "dynamicStrings"
})
public class ClbPropertySheet {

    protected List<ClbDynamicLongValue> dynamicLongs;
    protected List<ClbDynamicDateValue> dynamicDates;
    protected List<ClbDynamicDoubleValue> dynamicDoubles;
    protected List<ClbDynamicBooleanValue> dynamicBooleans;
    protected List<ClbDynamicStringValue> dynamicStrings;
    @XmlAttribute(required = true)
    protected String propertySheetTypeId;
    @XmlAttribute(required = true)
    protected boolean extracted;
    @XmlAttribute
    protected String label;
    @XmlAttribute
    protected String description;
    @XmlAttribute
    protected String title;

    /**
     * Gets the value of the dynamicLongs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dynamicLongs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDynamicLongs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbDynamicLongValue }
     * 
     * 
     */
    public List<ClbDynamicLongValue> getDynamicLongs() {
        if (dynamicLongs == null) {
            dynamicLongs = new ArrayList<ClbDynamicLongValue>();
        }
        return this.dynamicLongs;
    }

    /**
     * Gets the value of the dynamicDates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dynamicDates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDynamicDates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbDynamicDateValue }
     * 
     * 
     */
    public List<ClbDynamicDateValue> getDynamicDates() {
        if (dynamicDates == null) {
            dynamicDates = new ArrayList<ClbDynamicDateValue>();
        }
        return this.dynamicDates;
    }

    /**
     * Gets the value of the dynamicDoubles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dynamicDoubles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDynamicDoubles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbDynamicDoubleValue }
     * 
     * 
     */
    public List<ClbDynamicDoubleValue> getDynamicDoubles() {
        if (dynamicDoubles == null) {
            dynamicDoubles = new ArrayList<ClbDynamicDoubleValue>();
        }
        return this.dynamicDoubles;
    }

    /**
     * Gets the value of the dynamicBooleans property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dynamicBooleans property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDynamicBooleans().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbDynamicBooleanValue }
     * 
     * 
     */
    public List<ClbDynamicBooleanValue> getDynamicBooleans() {
        if (dynamicBooleans == null) {
            dynamicBooleans = new ArrayList<ClbDynamicBooleanValue>();
        }
        return this.dynamicBooleans;
    }

    /**
     * Gets the value of the dynamicStrings property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dynamicStrings property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDynamicStrings().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbDynamicStringValue }
     * 
     * 
     */
    public List<ClbDynamicStringValue> getDynamicStrings() {
        if (dynamicStrings == null) {
            dynamicStrings = new ArrayList<ClbDynamicStringValue>();
        }
        return this.dynamicStrings;
    }

    /**
     * Gets the value of the propertySheetTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertySheetTypeId() {
        return propertySheetTypeId;
    }

    /**
     * Sets the value of the propertySheetTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertySheetTypeId(String value) {
        this.propertySheetTypeId = value;
    }

    /**
     * Gets the value of the extracted property.
     * 
     */
    public boolean isExtracted() {
        return extracted;
    }

    /**
     * Sets the value of the extracted property.
     * 
     */
    public void setExtracted(boolean value) {
        this.extracted = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
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

}
