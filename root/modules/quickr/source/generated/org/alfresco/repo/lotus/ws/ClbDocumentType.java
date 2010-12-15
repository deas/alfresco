
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ClbDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbDocumentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="propertySheetTypes" type="{http://model.xsd.clb.content.ibm.com}ClbPropertySheetType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="template" type="{http://model.xsd.clb.content.ibm.com}ClbDocument"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://model.xsd.content.ibm.com}BaseAttrs"/>
 *       &lt;attGroup ref="{http://model.xsd.content.ibm.com}DescriptionData"/>
 *       &lt;attGroup ref="{http://model.xsd.content.ibm.com}ContentAttrs"/>
 *       &lt;attribute name="versioning" type="{http://model.xsd.clb.content.ibm.com}ClbVersioning" />
 *       &lt;attribute name="approvalEnabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="approvalType" type="{http://model.xsd.clb.content.ibm.com}ClbDraftApprovalType" />
 *       &lt;attribute name="templateExtension" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="defaultExtension" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbDocumentType", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "propertySheetTypes",
    "template"
})
public class ClbDocumentType {

    protected List<ClbPropertySheetType> propertySheetTypes;
    @XmlElement(required = true, nillable = true)
    protected ClbDocument template;
    @XmlAttribute
    protected ClbVersioning versioning;
    @XmlAttribute(required = true)
    protected boolean approvalEnabled;
    @XmlAttribute
    protected ClbDraftApprovalType approvalType;
    @XmlAttribute
    protected String templateExtension;
    @XmlAttribute
    protected String defaultExtension;
    @XmlAttribute(required = true)
    protected String id;
    @XmlAttribute(required = true)
    protected String path;
    @XmlAttribute(required = true)
    protected boolean locked;
    @XmlAttribute
    protected String permissions;
    @XmlAttribute
    protected String displayLocation;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar systemCreated;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar systemLastModified;
    @XmlAttribute
    protected String description;
    @XmlAttribute
    protected String title;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar created;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastModified;
    @XmlAttribute(required = true)
    protected boolean hidden;
    @XmlAttribute
    protected String language;

    /**
     * Gets the value of the propertySheetTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propertySheetTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropertySheetTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbPropertySheetType }
     * 
     * 
     */
    public List<ClbPropertySheetType> getPropertySheetTypes() {
        if (propertySheetTypes == null) {
            propertySheetTypes = new ArrayList<ClbPropertySheetType>();
        }
        return this.propertySheetTypes;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocument }
     *     
     */
    public ClbDocument getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocument }
     *     
     */
    public void setTemplate(ClbDocument value) {
        this.template = value;
    }

    /**
     * Gets the value of the versioning property.
     * 
     * @return
     *     possible object is
     *     {@link ClbVersioning }
     *     
     */
    public ClbVersioning getVersioning() {
        return versioning;
    }

    /**
     * Sets the value of the versioning property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbVersioning }
     *     
     */
    public void setVersioning(ClbVersioning value) {
        this.versioning = value;
    }

    /**
     * Gets the value of the approvalEnabled property.
     * 
     */
    public boolean isApprovalEnabled() {
        return approvalEnabled;
    }

    /**
     * Sets the value of the approvalEnabled property.
     * 
     */
    public void setApprovalEnabled(boolean value) {
        this.approvalEnabled = value;
    }

    /**
     * Gets the value of the approvalType property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDraftApprovalType }
     *     
     */
    public ClbDraftApprovalType getApprovalType() {
        return approvalType;
    }

    /**
     * Sets the value of the approvalType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDraftApprovalType }
     *     
     */
    public void setApprovalType(ClbDraftApprovalType value) {
        this.approvalType = value;
    }

    /**
     * Gets the value of the templateExtension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemplateExtension() {
        return templateExtension;
    }

    /**
     * Sets the value of the templateExtension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemplateExtension(String value) {
        this.templateExtension = value;
    }

    /**
     * Gets the value of the defaultExtension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * Sets the value of the defaultExtension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultExtension(String value) {
        this.defaultExtension = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Gets the value of the locked property.
     * 
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the value of the locked property.
     * 
     */
    public void setLocked(boolean value) {
        this.locked = value;
    }

    /**
     * Gets the value of the permissions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPermissions() {
        return permissions;
    }

    /**
     * Sets the value of the permissions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPermissions(String value) {
        this.permissions = value;
    }

    /**
     * Gets the value of the displayLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayLocation() {
        return displayLocation;
    }

    /**
     * Sets the value of the displayLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisplayLocation(String value) {
        this.displayLocation = value;
    }

    /**
     * Gets the value of the systemCreated property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSystemCreated() {
        return systemCreated;
    }

    /**
     * Sets the value of the systemCreated property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSystemCreated(XMLGregorianCalendar value) {
        this.systemCreated = value;
    }

    /**
     * Gets the value of the systemLastModified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSystemLastModified() {
        return systemLastModified;
    }

    /**
     * Sets the value of the systemLastModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSystemLastModified(XMLGregorianCalendar value) {
        this.systemLastModified = value;
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

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

    /**
     * Gets the value of the lastModified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastModified() {
        return lastModified;
    }

    /**
     * Sets the value of the lastModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastModified(XMLGregorianCalendar value) {
        this.lastModified = value;
    }

    /**
     * Gets the value of the hidden property.
     * 
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the value of the hidden property.
     * 
     */
    public void setHidden(boolean value) {
        this.hidden = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

}
