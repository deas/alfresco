
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Permission.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Permission">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="AddChild"/>
 *     &lt;enumeration value="Delete"/>
 *     &lt;enumeration value="Edit"/>
 *     &lt;enumeration value="View"/>
 *     &lt;enumeration value="GrantAccess"/>
 *     &lt;enumeration value="Delegate"/>
 *     &lt;enumeration value="LockOverride"/>
 *     &lt;enumeration value="AddFolder"/>
 *     &lt;enumeration value="EditFolder"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Permission", namespace = "http://model.xsd.content.ibm.com")
@XmlEnum
public enum Permission {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("AddChild")
    ADD_CHILD("AddChild"),
    @XmlEnumValue("Delete")
    DELETE("Delete"),
    @XmlEnumValue("Edit")
    EDIT("Edit"),
    @XmlEnumValue("View")
    VIEW("View"),
    @XmlEnumValue("GrantAccess")
    GRANT_ACCESS("GrantAccess"),
    @XmlEnumValue("Delegate")
    DELEGATE("Delegate"),
    @XmlEnumValue("LockOverride")
    LOCK_OVERRIDE("LockOverride"),
    @XmlEnumValue("AddFolder")
    ADD_FOLDER("AddFolder"),
    @XmlEnumValue("EditFolder")
    EDIT_FOLDER("EditFolder");
    private final String value;

    Permission(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Permission fromValue(String v) {
        for (Permission c: Permission.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
