
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
 * <p>Java class for ClbDraft complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbDraft">
 *   &lt;complexContent>
 *     &lt;extension base="{http://model.xsd.clb.content.ibm.com}ClbData">
 *       &lt;sequence>
 *         &lt;element name="documentType" type="{http://model.xsd.clb.content.ibm.com}ClbDocumentType"/>
 *         &lt;element name="approvers" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="approvalStates" type="{http://model.xsd.clb.content.ibm.com}ClbDraftApprovalState" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://model.xsd.content.ibm.com}ManagedData"/>
 *       &lt;attribute name="expandGroups" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="approvalEnabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="approvalType" type="{http://model.xsd.clb.content.ibm.com}ClbDraftApprovalType" />
 *       &lt;attribute name="submitted" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbDraft", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "documentType",
    "approvers",
    "approvalStates"
})
public class ClbDraft
    extends ClbData
{

    @XmlElement(required = true, nillable = true)
    protected ClbDocumentType documentType;
    protected List<String> approvers;
    protected List<ClbDraftApprovalState> approvalStates;
    @XmlAttribute(required = true)
    protected boolean expandGroups;
    @XmlAttribute(required = true)
    protected boolean approvalEnabled;
    @XmlAttribute
    protected ClbDraftApprovalType approvalType;
    @XmlAttribute(required = true)
    protected boolean submitted;
    @XmlAttribute
    protected Long dataLength;
    @XmlAttribute
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataLastModified;
    @XmlAttribute
    protected String dataMimeType;

    /**
     * Gets the value of the documentType property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocumentType }
     *     
     */
    public ClbDocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the value of the documentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocumentType }
     *     
     */
    public void setDocumentType(ClbDocumentType value) {
        this.documentType = value;
    }

    /**
     * Gets the value of the approvers property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the approvers property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApprovers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getApprovers() {
        if (approvers == null) {
            approvers = new ArrayList<String>();
        }
        return this.approvers;
    }

    /**
     * Gets the value of the approvalStates property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the approvalStates property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApprovalStates().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbDraftApprovalState }
     * 
     * 
     */
    public List<ClbDraftApprovalState> getApprovalStates() {
        if (approvalStates == null) {
            approvalStates = new ArrayList<ClbDraftApprovalState>();
        }
        return this.approvalStates;
    }

    /**
     * Gets the value of the expandGroups property.
     * 
     */
    public boolean isExpandGroups() {
        return expandGroups;
    }

    /**
     * Sets the value of the expandGroups property.
     * 
     */
    public void setExpandGroups(boolean value) {
        this.expandGroups = value;
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
     * Gets the value of the submitted property.
     * 
     */
    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * Sets the value of the submitted property.
     * 
     */
    public void setSubmitted(boolean value) {
        this.submitted = value;
    }

    /**
     * Gets the value of the dataLength property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDataLength() {
        return dataLength;
    }

    /**
     * Sets the value of the dataLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDataLength(Long value) {
        this.dataLength = value;
    }

    /**
     * Gets the value of the dataLastModified property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataLastModified() {
        return dataLastModified;
    }

    /**
     * Sets the value of the dataLastModified property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataLastModified(XMLGregorianCalendar value) {
        this.dataLastModified = value;
    }

    /**
     * Gets the value of the dataMimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataMimeType() {
        return dataMimeType;
    }

    /**
     * Sets the value of the dataMimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataMimeType(String value) {
        this.dataMimeType = value;
    }

}
