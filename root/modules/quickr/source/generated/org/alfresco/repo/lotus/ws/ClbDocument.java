
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
 * <p>Java class for ClbDocument complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbDocument">
 *   &lt;complexContent>
 *     &lt;extension base="{http://model.xsd.clb.content.ibm.com}ClbData">
 *       &lt;sequence>
 *         &lt;element name="documentType" type="{http://model.xsd.clb.content.ibm.com}ClbDocumentType"/>
 *         &lt;element name="drafts" type="{http://model.xsd.clb.content.ibm.com}ClbDraft" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://model.xsd.content.ibm.com}ManagedData"/>
 *       &lt;attribute name="url" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbDocument", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "documentType",
    "drafts"
})
public class ClbDocument
    extends ClbData
{

    @XmlElement(required = true, nillable = true)
    protected ClbDocumentType documentType;
    protected List<ClbDraft> drafts;
    @XmlAttribute
    protected String url;
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
     * Gets the value of the drafts property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the drafts property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDrafts().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbDraft }
     * 
     * 
     */
    public List<ClbDraft> getDrafts() {
        if (drafts == null) {
            drafts = new ArrayList<ClbDraft>();
        }
        return this.drafts;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
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
