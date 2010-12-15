
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbErrorType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbErrorType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AccessDenied"/>
 *     &lt;enumeration value="ItemNotFound"/>
 *     &lt;enumeration value="ItemExists"/>
 *     &lt;enumeration value="DocumentNotLocked"/>
 *     &lt;enumeration value="DocumentAlreadyLocked"/>
 *     &lt;enumeration value="DraftExists"/>
 *     &lt;enumeration value="DraftSubmitted"/>
 *     &lt;enumeration value="UnsupportedVersion"/>
 *     &lt;enumeration value="UnsupportedOperation"/>
 *     &lt;enumeration value="OnlyDocumentsCanBeLocked"/>
 *     &lt;enumeration value="ExtensionChangeNotAllowed"/>
 *     &lt;enumeration value="VersionLabelNotFound"/>
 *     &lt;enumeration value="ConstraintViolation"/>
 *     &lt;enumeration value="FixedFolder"/>
 *     &lt;enumeration value="InvalidTypeForOperation"/>
 *     &lt;enumeration value="InvalidPath"/>
 *     &lt;enumeration value="InvalidPathSegmentLength"/>
 *     &lt;enumeration value="GeneralInformation"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbErrorType")
@XmlEnum
public enum ClbErrorType {

    @XmlEnumValue("AccessDenied")
    ACCESS_DENIED("AccessDenied"),
    @XmlEnumValue("ItemNotFound")
    ITEM_NOT_FOUND("ItemNotFound"),
    @XmlEnumValue("ItemExists")
    ITEM_EXISTS("ItemExists"),
    @XmlEnumValue("DocumentNotLocked")
    DOCUMENT_NOT_LOCKED("DocumentNotLocked"),
    @XmlEnumValue("DocumentAlreadyLocked")
    DOCUMENT_ALREADY_LOCKED("DocumentAlreadyLocked"),
    @XmlEnumValue("DraftExists")
    DRAFT_EXISTS("DraftExists"),
    @XmlEnumValue("DraftSubmitted")
    DRAFT_SUBMITTED("DraftSubmitted"),
    @XmlEnumValue("UnsupportedVersion")
    UNSUPPORTED_VERSION("UnsupportedVersion"),
    @XmlEnumValue("UnsupportedOperation")
    UNSUPPORTED_OPERATION("UnsupportedOperation"),
    @XmlEnumValue("OnlyDocumentsCanBeLocked")
    ONLY_DOCUMENTS_CAN_BE_LOCKED("OnlyDocumentsCanBeLocked"),
    @XmlEnumValue("ExtensionChangeNotAllowed")
    EXTENSION_CHANGE_NOT_ALLOWED("ExtensionChangeNotAllowed"),
    @XmlEnumValue("VersionLabelNotFound")
    VERSION_LABEL_NOT_FOUND("VersionLabelNotFound"),
    @XmlEnumValue("ConstraintViolation")
    CONSTRAINT_VIOLATION("ConstraintViolation"),
    @XmlEnumValue("FixedFolder")
    FIXED_FOLDER("FixedFolder"),
    @XmlEnumValue("InvalidTypeForOperation")
    INVALID_TYPE_FOR_OPERATION("InvalidTypeForOperation"),
    @XmlEnumValue("InvalidPath")
    INVALID_PATH("InvalidPath"),
    @XmlEnumValue("InvalidPathSegmentLength")
    INVALID_PATH_SEGMENT_LENGTH("InvalidPathSegmentLength"),
    @XmlEnumValue("GeneralInformation")
    GENERAL_INFORMATION("GeneralInformation");
    private final String value;

    ClbErrorType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClbErrorType fromValue(String v) {
        for (ClbErrorType c: ClbErrorType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
