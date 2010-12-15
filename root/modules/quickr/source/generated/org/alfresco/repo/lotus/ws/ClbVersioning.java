
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbVersioning.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbVersioning">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="EXPLICIT"/>
 *     &lt;enumeration value="IMPLICIT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbVersioning", namespace = "http://model.xsd.clb.content.ibm.com")
@XmlEnum
public enum ClbVersioning {

    NONE,
    EXPLICIT,
    IMPLICIT;

    public String value() {
        return name();
    }

    public static ClbVersioning fromValue(String v) {
        return valueOf(v);
    }

}
