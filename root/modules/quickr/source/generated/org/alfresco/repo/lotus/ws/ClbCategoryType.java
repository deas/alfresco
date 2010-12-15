
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbCategoryType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbCategoryType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DocumentManager"/>
 *     &lt;enumeration value="PersonalDocumentManager"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbCategoryType", namespace = "http://model.xsd.clb.content.ibm.com")
@XmlEnum
public enum ClbCategoryType {

    @XmlEnumValue("DocumentManager")
    DOCUMENT_MANAGER("DocumentManager"),
    @XmlEnumValue("PersonalDocumentManager")
    PERSONAL_DOCUMENT_MANAGER("PersonalDocumentManager");
    private final String value;

    ClbCategoryType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClbCategoryType fromValue(String v) {
        for (ClbCategoryType c: ClbCategoryType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
