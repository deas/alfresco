/// Author:    Manfung Chan
/// Version:   v1.0

using System;
using System.Collections.Generic;
using System.Text;

namespace KofaxAlfrescoRelease_v1
{
    class ReleaseConstants
    {
        public static readonly String SEPERATOR = "<|>";
        public static readonly String CONTENT_TYPE = "CT";
        public static readonly String ASPECT = "A";

        // Custom property names
        public static readonly String CUSTOM_USERNAME = "UserName";
        public static readonly String CUSTOM_PASSWORD = "Password";
        public static readonly String CUSTOM_REPOSITORY = "Repository";
        public static readonly String CUSTOM_LOCATION = "Location";
        public static readonly String CUSTOM_LOCATION_UUID = "LocationUuid";
        public static readonly String CUSTOM_ASPECTS = "Aspects";
        public static readonly String CUSTOM_IMAGE = "Image";
        public static readonly String CUSTOM_OCR = "OCR";
        public static readonly String CUSTOM_PDF = "PDF";
        public static readonly String CUSTOM_CONTENT_TYPE = "ContentType";

        // mime types
        public static readonly String MIME_TYPE_TIFF = "image/tiff";
        public static readonly String MIME_TYPE_TEXT = "text/plain";
        public static readonly String MIME_TYPE_PDF = "application/pdf";

        // Error messages
        public static readonly String ERR_CONNECTION_FAIL = "Error connecting, please check username, password and repository are correct.";
        public static readonly String ERR_CHECK_CONNECTION = "Error, please check connection to repository";
        public static readonly String ERR_CONNECTING_TO_REPO = "Error connecting to repository: ";
        public static readonly String ERR_NODE_DELETED = "Alfresco DDocument deleted. ";
        public static readonly String ERR_NOT_NODE_DELETED = "Alfresco document not deleted. ";

        // validation mess
        public static readonly String VALIDATE_LOCATION = "Please select a destination";
        public static readonly String VALIDATE_CONTENT_TYPE = "Please select a content type";
        public static readonly String VALIDATE_USER_NAME = "Please enter a user name";
        public static readonly String VALIDATE_PASSWORD = "Please enter a password";
        public static readonly String VALIDATE_REPOSITORY = "Please enter the alfresco repository";
        public static readonly String VALIDATE_DOCUMENT_CONTENT = "Please select a document content";
        public static readonly String VALIDATE_MISSING_MANDATORY_FIELDS = "The following mandatory fields are missing: \n\n";

        // Relase Constant
        public static readonly int MAX_FILE_SIZE = 1000000;

    }
}
