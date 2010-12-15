
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbViewResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbViewResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="columns" type="{http://model.xsd.clb.content.ibm.com}ClbColumn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="document" type="{http://model.xsd.clb.content.ibm.com}ClbDocument"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbViewResult", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "columns",
    "document"
})
public class ClbViewResult {

    protected List<ClbColumn> columns;
    @XmlElement(required = true)
    protected ClbDocument document;

    /**
     * Gets the value of the columns property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the columns property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumns().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbColumn }
     * 
     * 
     */
    public List<ClbColumn> getColumns() {
        if (columns == null) {
            columns = new ArrayList<ClbColumn>();
        }
        return this.columns;
    }

    /**
     * Gets the value of the document property.
     * 
     * @return
     *     possible object is
     *     {@link ClbDocument }
     *     
     */
    public ClbDocument getDocument() {
        return document;
    }

    /**
     * Sets the value of the document property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClbDocument }
     *     
     */
    public void setDocument(ClbDocument value) {
        this.document = value;
    }

}
