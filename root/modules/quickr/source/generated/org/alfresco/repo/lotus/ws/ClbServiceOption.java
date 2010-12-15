
package org.alfresco.repo.lotus.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClbServiceOption.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ClbServiceOption">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="IncludeDrafts"/>
 *     &lt;enumeration value="IncludeApproveDrafts"/>
 *     &lt;enumeration value="IncludeSubmittedDrafts"/>
 *     &lt;enumeration value="IncludeReferences"/>
 *     &lt;enumeration value="IncludePermissions"/>
 *     &lt;enumeration value="IncludePropertySheets"/>
 *     &lt;enumeration value="ResolveLockOwner"/>
 *     &lt;enumeration value="RetrieveDownloadURL"/>
 *     &lt;enumeration value="RetrieveViewURL"/>
 *     &lt;enumeration value="IncludeFolderChildren"/>
 *     &lt;enumeration value="RetrieveByIdOrAbsPath"/>
 *     &lt;enumeration value="RetrievePathRelativeToLibraryId"/>
 *     &lt;enumeration value="ResolveDefaultDocumentType"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ClbServiceOption")
@XmlEnum
public enum ClbServiceOption {

    @XmlEnumValue("None")
    NONE("None"),
    @XmlEnumValue("IncludeDrafts")
    INCLUDE_DRAFTS("IncludeDrafts"),
    @XmlEnumValue("IncludeApproveDrafts")
    INCLUDE_APPROVE_DRAFTS("IncludeApproveDrafts"),
    @XmlEnumValue("IncludeSubmittedDrafts")
    INCLUDE_SUBMITTED_DRAFTS("IncludeSubmittedDrafts"),
    @XmlEnumValue("IncludeReferences")
    INCLUDE_REFERENCES("IncludeReferences"),
    @XmlEnumValue("IncludePermissions")
    INCLUDE_PERMISSIONS("IncludePermissions"),
    @XmlEnumValue("IncludePropertySheets")
    INCLUDE_PROPERTY_SHEETS("IncludePropertySheets"),
    @XmlEnumValue("ResolveLockOwner")
    RESOLVE_LOCK_OWNER("ResolveLockOwner"),
    @XmlEnumValue("RetrieveDownloadURL")
    RETRIEVE_DOWNLOAD_URL("RetrieveDownloadURL"),
    @XmlEnumValue("RetrieveViewURL")
    RETRIEVE_VIEW_URL("RetrieveViewURL"),
    @XmlEnumValue("IncludeFolderChildren")
    INCLUDE_FOLDER_CHILDREN("IncludeFolderChildren"),
    @XmlEnumValue("RetrieveByIdOrAbsPath")
    RETRIEVE_BY_ID_OR_ABS_PATH("RetrieveByIdOrAbsPath"),
    @XmlEnumValue("RetrievePathRelativeToLibraryId")
    RETRIEVE_PATH_RELATIVE_TO_LIBRARY_ID("RetrievePathRelativeToLibraryId"),
    @XmlEnumValue("ResolveDefaultDocumentType")
    RESOLVE_DEFAULT_DOCUMENT_TYPE("ResolveDefaultDocumentType");
    private final String value;

    ClbServiceOption(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ClbServiceOption fromValue(String v) {
        for (ClbServiceOption c: ClbServiceOption.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
