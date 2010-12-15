
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="path" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="downloadOption" type="{http://webservices.clb.content.ibm.com}ClbDownloadOption"/>
 *         &lt;element name="serviceOptions" type="{http://webservices.clb.content.ibm.com}ClbServiceOption" maxOccurs="unbounded" minOccurs="0"/>
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
    "id",
    "path",
    "downloadOption",
    "serviceOptions"
})
@XmlRootElement(name = "getDocument")
public class GetDocument {

    @XmlElement(required = true, nillable = true)
    protected String id;
    @XmlElement(required = true, nillable = true)
    protected String path;
    @XmlElement(required = true, nillable = true)
    protected ClbDownloadOption downloadOption;
    protected List<ClbServiceOption> serviceOptions;

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
     * Gets the value of the downloadOption property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDownloadOption }
     *     
     */
    public ClbDownloadOption getDownloadOption() {
        return downloadOption;
    }

    /**
     * Sets the value of the downloadOption property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDownloadOption }
     *     
     */
    public void setDownloadOption(ClbDownloadOption value) {
        this.downloadOption = value;
    }

    /**
     * Gets the value of the serviceOptions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serviceOptions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServiceOptions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbServiceOption }
     * 
     * 
     */
    public List<ClbServiceOption> getServiceOptions() {
        if (serviceOptions == null) {
            serviceOptions = new ArrayList<ClbServiceOption>();
        }
        return this.serviceOptions;
    }

}
