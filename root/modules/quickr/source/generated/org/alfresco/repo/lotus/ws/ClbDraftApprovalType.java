
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbDraftApprovalType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbDraftApprovalType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Parallel"/>
 *     &lt;enumeration value="Serial"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbDraftApprovalType", namespace = "http://model.xsd.clb.content.ibm.com")
@XmlEnum
public enum ClbDraftApprovalType {

    @XmlEnumValue("Parallel")
    PARALLEL("Parallel"),
    @XmlEnumValue("Serial")
    SERIAL("Serial");
    private final String value;

    ClbDraftApprovalType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClbDraftApprovalType fromValue(String v) {
        for (ClbDraftApprovalType c: ClbDraftApprovalType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
