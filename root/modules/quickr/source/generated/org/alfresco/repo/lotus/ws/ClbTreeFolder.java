
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbTreeFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbTreeFolder">
 *   &lt;complexContent>
 *     &lt;extension base="{http://model.xsd.clb.content.ibm.com}ClbTreeItem">
 *       &lt;sequence>
 *         &lt;element name="items" type="{http://model.xsd.clb.content.ibm.com}ClbTreeItem" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="folders" type="{http://model.xsd.clb.content.ibm.com}ClbTreeFolder" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="childCount" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbTreeFolder", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "items",
    "folders"
})
public class ClbTreeFolder
    extends ClbTreeItem
{

    protected List<ClbTreeItem> items;
    protected List<ClbTreeFolder> folders;
    @XmlAttribute(required = true)
    protected int childCount;

    /**
     * Gets the value of the items property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the items property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbTreeItem }
     * 
     * 
     */
    public List<ClbTreeItem> getItems() {
        if (items == null) {
            items = new ArrayList<ClbTreeItem>();
        }
        return this.items;
    }

    /**
     * Gets the value of the folders property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folders property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolders().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbTreeFolder }
     * 
     * 
     */
    public List<ClbTreeFolder> getFolders() {
        if (folders == null) {
            folders = new ArrayList<ClbTreeFolder>();
        }
        return this.folders;
    }

    /**
     * Gets the value of the childCount property.
     * 
     */
    public int getChildCount() {
        return childCount;
    }

    /**
     * Sets the value of the childCount property.
     * 
     */
    public void setChildCount(int value) {
        this.childCount = value;
    }

}
