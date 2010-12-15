
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbViewResultsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClbViewResultsResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://webservices.clb.content.ibm.com}ClbResponse">
 *       &lt;sequence>
 *         &lt;element name="columnFormats" type="{http://model.xsd.clb.content.ibm.com}ClbViewFormatColumn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="results" type="{http://model.xsd.clb.content.ibm.com}ClbViewResult" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClbViewResultsResponse", propOrder = {
    "columnFormats",
    "results"
})
public class ClbViewResultsResponse
    extends ClbResponse
{

    protected List<ClbViewFormatColumn> columnFormats;
    protected List<ClbViewResult> results;

    /**
     * Gets the value of the columnFormats property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the columnFormats property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColumnFormats().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbViewFormatColumn }
     * 
     * 
     */
    public List<ClbViewFormatColumn> getColumnFormats() {
        if (columnFormats == null) {
            columnFormats = new ArrayList<ClbViewFormatColumn>();
        }
        return this.columnFormats;
    }

    /**
     * Gets the value of the results property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the results property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResults().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ClbViewResult }
     * 
     * 
     */
    public List<ClbViewResult> getResults() {
        if (results == null) {
            results = new ArrayList<ClbViewResult>();
        }
        return this.results;
    }

}
