
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbFolder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbFolder">
 *   &lt;complexContent>
 *     &lt;extension base="{http://model.xsd.clb.content.ibm.com}ClbData">
 *       &lt;sequence>
 *         &lt;element name="folders" type="{http://model.xsd.clb.content.ibm.com}ClbFolder" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="documents" type="{http://model.xsd.clb.content.ibm.com}ClbDocument" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbFolder", namespace = "http://model.xsd.clb.content.ibm.com", propOrder = {
    "folders",
    "documents"
})
@XmlSeeAlso({
    ClbLibrary.class
})
public class ClbFolder
    extends ClbData
{

    protected List<ClbFolder> folders;
    protected List<ClbDocument> documents;

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
     * {@link ClbFolder }
     * 
     * 
     */
    public List<ClbFolder> getFolders() {
        if (folders == null) {
            folders = new ArrayList<ClbFolder>();
        }
        return this.folders;
    }

    /**
     * Gets the value of the documents property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documents property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocuments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbDocument }
     * 
     * 
     */
    public List<ClbDocument> getDocuments() {
        if (documents == null) {
            documents = new ArrayList<ClbDocument>();
        }
        return this.documents;
    }

}
