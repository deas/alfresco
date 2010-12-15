
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbDocTypeOption.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbDocTypeOption">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LibraryScope"/>
 *     &lt;enumeration value="All"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbDocTypeOption")
@XmlEnum
public enum ClbDocTypeOption {

    @XmlEnumValue("LibraryScope")
    LIBRARY_SCOPE("LibraryScope"),
    @XmlEnumValue("All")
    ALL("All");
    private final String value;

    ClbDocTypeOption(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClbDocTypeOption fromValue(String v) {
        for (ClbDocTypeOption c: ClbDocTypeOption.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
