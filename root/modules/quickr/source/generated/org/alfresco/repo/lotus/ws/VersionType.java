
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VersionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VersionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Name"/>
 *     &lt;enumeration value="Label"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "VersionType", namespace = "http://model.xsd.content.ibm.com")
@XmlEnum
public enum VersionType {

    @XmlEnumValue("Name")
    NAME("Name"),
    @XmlEnumValue("Label")
    LABEL("Label");
    private final String value;

    VersionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VersionType fromValue(String v) {
        for (VersionType c: VersionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
