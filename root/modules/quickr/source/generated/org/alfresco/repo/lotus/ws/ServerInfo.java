
package org.alfresco.repo.lotus.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServerInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServerInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serverProperties" type="{http://webservices.clb.content.ibm.com}Property" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="providerCapabilities" type="{http://webservices.clb.content.ibm.com}Property" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="locale" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="utcOffset" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="securityEnabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServerInfo", propOrder = {
    "serverProperties",
    "providerCapabilities"
})
public class ServerInfo {

    protected List<Property> serverProperties;
    protected List<Property> providerCapabilities;
    @XmlAttribute
    protected String version;
    @XmlAttribute
    protected String locale;
    @XmlAttribute
    protected Integer utcOffset;
    @XmlAttribute(required = true)
    protected boolean securityEnabled;

    /**
     * Gets the value of the serverProperties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serverProperties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServerProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Property }
     * 
     * 
     */
    public List<Property> getServerProperties() {
        if (serverProperties == null) {
            serverProperties = new ArrayList<Property>();
        }
        return this.serverProperties;
    }

    /**
     * Gets the value of the providerCapabilities property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the providerCapabilities property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProviderCapabilities().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Property }
     * 
     * 
     */
    public List<Property> getProviderCapabilities() {
        if (providerCapabilities == null) {
            providerCapabilities = new ArrayList<Property>();
        }
        return this.providerCapabilities;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the locale property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the value of the locale property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocale(String value) {
        this.locale = value;
    }

    /**
     * Gets the value of the utcOffset property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUtcOffset() {
        return utcOffset;
    }

    /**
     * Sets the value of the utcOffset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUtcOffset(Integer value) {
        this.utcOffset = value;
    }

    /**
     * Gets the value of the securityEnabled property.
     * 
     */
    public boolean isSecurityEnabled() {
        return securityEnabled;
    }

    /**
     * Sets the value of the securityEnabled property.
     * 
     */
    public void setSecurityEnabled(boolean value) {
        this.securityEnabled = value;
    }

}
