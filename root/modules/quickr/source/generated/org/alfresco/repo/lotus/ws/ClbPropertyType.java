
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="styles" type="{http://model.xsd.clb.content.ibm.com}ClbStyleType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="labels" type="{http://model.xsd.clb.content.ibm.com}ClbLabelType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="options" type="{http://model.xsd.clb.content.ibm.com}ClbOptionType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="defaultValues" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="dataType" type="{http://model.xsd.clb.content.ibm.com}ClbDataType" />
 *       &lt;attribute name="indexable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="multiple" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="readOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="searchable" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="maxLength" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="nodeSet" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="propertyId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="propertyName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbPropertyType", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "styles",
    "labels",
    "options",
    "defaultValues"
})
public class ClbPropertyType {

    protected List<ClbStyleType> styles;
    protected List<ClbLabelType> labels;
    protected List<ClbOptionType> options;
    protected List<String> defaultValues;
    @XmlAttribute
    protected ClbDataType dataType;
    @XmlAttribute
    protected Boolean indexable;
    @XmlAttribute
    protected Boolean multiple;
    @XmlAttribute
    protected Boolean readOnly;
    @XmlAttribute
    protected Boolean required;
    @XmlAttribute
    protected Boolean searchable;
    @XmlAttribute
    protected Long maxLength;
    @XmlAttribute
    protected String nodeSet;
    @XmlAttribute(required = true)
    protected String propertyId;
    @XmlAttribute
    protected String propertyName;

    /**
     * Gets the value of the styles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the styles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStyles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbStyleType }
     * 
     * 
     */
    public List<ClbStyleType> getStyles() {
        if (styles == null) {
            styles = new ArrayList<ClbStyleType>();
        }
        return this.styles;
    }

    /**
     * Gets the value of the labels property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the labels property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLabels().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbLabelType }
     * 
     * 
     */
    public List<ClbLabelType> getLabels() {
        if (labels == null) {
            labels = new ArrayList<ClbLabelType>();
        }
        return this.labels;
    }

    /**
     * Gets the value of the options property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the options property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOptions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbOptionType }
     * 
     * 
     */
    public List<ClbOptionType> getOptions() {
        if (options == null) {
            options = new ArrayList<ClbOptionType>();
        }
        return this.options;
    }

    /**
     * Gets the value of the defaultValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the defaultValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDefaultValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDefaultValues() {
        if (defaultValues == null) {
            defaultValues = new ArrayList<String>();
        }
        return this.defaultValues;
    }

    /**
     * Gets the value of the dataType property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDataType }
     *     
     */
    public ClbDataType getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDataType }
     *     
     */
    public void setDataType(ClbDataType value) {
        this.dataType = value;
    }

    /**
     * Gets the value of the indexable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIndexable() {
        return indexable;
    }

    /**
     * Sets the value of the indexable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndexable(Boolean value) {
        this.indexable = value;
    }

    /**
     * Gets the value of the multiple property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultiple() {
        return multiple;
    }

    /**
     * Sets the value of the multiple property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultiple(Boolean value) {
        this.multiple = value;
    }

    /**
     * Gets the value of the readOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the value of the readOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReadOnly(Boolean value) {
        this.readOnly = value;
    }

    /**
     * Gets the value of the required property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRequired() {
        return required;
    }

    /**
     * Sets the value of the required property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequired(Boolean value) {
        this.required = value;
    }

    /**
     * Gets the value of the searchable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSearchable() {
        return searchable;
    }

    /**
     * Sets the value of the searchable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSearchable(Boolean value) {
        this.searchable = value;
    }

    /**
     * Gets the value of the maxLength property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the value of the maxLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxLength(Long value) {
        this.maxLength = value;
    }

    /**
     * Gets the value of the nodeSet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeSet() {
        return nodeSet;
    }

    /**
     * Sets the value of the nodeSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeSet(String value) {
        this.nodeSet = value;
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
     * Gets the value of the propertyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Sets the value of the propertyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyName(String value) {
        this.propertyName = value;
    }

}
