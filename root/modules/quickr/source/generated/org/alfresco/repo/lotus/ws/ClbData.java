
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbData">
 *   &lt;complexContent>
 *     &lt;extension base="{http://model.xsd.content.ibm.com}Content">
 *       &lt;sequence>
 *         &lt;element name="propertySheets" type="{http://model.xsd.clb.content.ibm.com}ClbPropertySheet" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="propertySheetTypes" type="{http://model.xsd.clb.content.ibm.com}ClbPropertySheetType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://model.xsd.clb.content.ibm.com}ClbControlData"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbData", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "propertySheets",
    "propertySheetTypes"
})
@XmlSeeAlso({
    ClbDocument.class,
    ClbDraft.class,
    ClbFolder.class
})
public class ClbData
    extends Content
{

    protected List<ClbPropertySheet> propertySheets;
    protected List<ClbPropertySheetType> propertySheetTypes;
    @XmlAttribute
    protected String currentState;

    /**
     * Gets the value of the propertySheets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propertySheets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropertySheets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbPropertySheet }
     * 
     * 
     */
    public List<ClbPropertySheet> getPropertySheets() {
        if (propertySheets == null) {
            propertySheets = new ArrayList<ClbPropertySheet>();
        }
        return this.propertySheets;
    }

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
     * Gets the value of the currentState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrentState() {
        return currentState;
    }

    /**
     * Sets the value of the currentState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrentState(String value) {
        this.currentState = value;
    }

}
