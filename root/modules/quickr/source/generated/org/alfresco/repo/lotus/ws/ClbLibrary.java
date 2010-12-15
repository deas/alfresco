
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbLibrary complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbLibrary">
 *   &lt;complexContent>
 *     &lt;extension base="{http://model.xsd.clb.content.ibm.com}ClbFolder">
 *       &lt;sequence>
 *         &lt;element name="libraries" type="{http://model.xsd.clb.content.ibm.com}ClbLibrary" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="category" type="{http://model.xsd.clb.content.ibm.com}ClbCategoryType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbLibrary", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "libraries"
})
public class ClbLibrary
    extends ClbFolder
{

    protected List<ClbLibrary> libraries;
    @XmlAttribute
    protected ClbCategoryType category;

    /**
     * Gets the value of the libraries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the libraries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLibraries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbLibrary }
     * 
     * 
     */
    public List<ClbLibrary> getLibraries() {
        if (libraries == null) {
            libraries = new ArrayList<ClbLibrary>();
        }
        return this.libraries;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link ClbCategoryType }
     *     
     */
    public ClbCategoryType getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbCategoryType }
     *     
     */
    public void setCategory(ClbCategoryType value) {
        this.category = value;
    }

}
