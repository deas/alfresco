
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbContentURLMode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbContentURLMode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Download"/>
 *     &lt;enumeration value="View"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbContentURLMode", namespace = "http://model.xsd.clb.content.ibm.com")
@XmlEnum
public enum ClbContentURLMode {

    @XmlEnumValue("Download")
    DOWNLOAD("Download"),
    @XmlEnumValue("View")
    VIEW("View");
    private final String value;

    ClbContentURLMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClbContentURLMode fromValue(String v) {
        for (ClbContentURLMode c: ClbContentURLMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
