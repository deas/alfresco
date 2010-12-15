
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
 *         &lt;element name="libraryId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="libraryPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="categoryTypes" type="{http://model.xsd.clb.content.ibm.com}ClbCategoryType" maxOccurs="unbounded" minOccurs="0"/>
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
    "libraryId",
    "libraryPath",
    "categoryTypes"
})
@XmlRootElement(name = "getBusinessComponents")
public class GetBusinessComponents {

    @XmlElement(required = true, nillable = true)
    protected String libraryId;
    @XmlElement(required = true, nillable = true)
    protected String libraryPath;
    protected List<ClbCategoryType> categoryTypes;

    /**
     * Gets the value of the libraryId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibraryId() {
        return libraryId;
    }

    /**
     * Sets the value of the libraryId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibraryId(String value) {
        this.libraryId = value;
    }

    /**
     * Gets the value of the libraryPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibraryPath() {
        return libraryPath;
    }

    /**
     * Sets the value of the libraryPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibraryPath(String value) {
        this.libraryPath = value;
    }

    /**
     * Gets the value of the categoryTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the categoryTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCategoryTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbCategoryType }
     * 
     * 
     */
    public List<ClbCategoryType> getCategoryTypes() {
        if (categoryTypes == null) {
            categoryTypes = new ArrayList<ClbCategoryType>();
        }
        return this.categoryTypes;
    }

}
