
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbDataType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbDataType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="String"/>
 *     &lt;enumeration value="Double"/>
 *     &lt;enumeration value="Long"/>
 *     &lt;enumeration value="DateTime"/>
 *     &lt;enumeration value="Boolean"/>
 *     &lt;enumeration value="Reference"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbDataType", namespace = "http://model.xsd.clb.content.ibm.com")
@XmlEnum
public enum ClbDataType {

    @XmlEnumValue("String")
    STRING("String"),
    @XmlEnumValue("Double")
    DOUBLE("Double"),
    @XmlEnumValue("Long")
    LONG("Long"),
    @XmlEnumValue("DateTime")
    DATE_TIME("DateTime"),
    @XmlEnumValue("Boolean")
    BOOLEAN("Boolean"),
    @XmlEnumValue("Reference")
    REFERENCE("Reference");
    private final String value;

    ClbDataType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClbDataType fromValue(String v) {
        for (ClbDataType c: ClbDataType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
