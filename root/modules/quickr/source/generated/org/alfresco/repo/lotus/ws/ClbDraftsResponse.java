
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbDraftsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbDraftsResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="drafts" type="{http://model.xsd.clb.content.ibm.com}ClbDraft" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbDraftsResponse", propOrder = {
    "drafts"
})
public class ClbDraftsResponse
    extends ClbResponse
{

    protected List<ClbDraft> drafts;

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

}
