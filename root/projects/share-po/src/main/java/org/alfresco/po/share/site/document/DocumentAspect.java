/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import org.apache.commons.lang3.StringUtils;

/**
 * Different Aspects of Documents.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public enum DocumentAspect
{
    CLASSIFIABLE("Classifiable"),
    VERSIONABLE("Versionable"),
    AUDIO("Audio"),
    INDEX_CONTROL("Index Control"),
    COMPLIANCEABLE("Complianceable"),
    DUBLIN_CORE("Dublin Core"),
    EFFECTIVITY("Effectivity"),
    SUMMARIZABLE("Summarizable"),
    TEMPLATABLE("Templatable"),
    EMAILED("Emailed"),
    ALIASABLE_EMAIL("Aliasable (Email)"),
    TAGGABLE("Taggable"),
    INLINE_EDITABLE("Inline Editable"),
    GOOGLE_DOCS_EDITABLE("Google Docs Editable"),
    GEOGRAPHIC("Geographic"),
    EXIF("EXIF"),
    RESTRICTABLE("Restrictable");

    private String value;

    private DocumentAspect(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

    /**
     * Find the {@link DocumentAspect} based it is name.
     * 
     * @param name - Aspect's Name
     * @return {@link DocumentAspect}
     * @throws Exception - Throws {@link Exception} if not able to find
     */
    public static DocumentAspect getAspect(String name) throws Exception
    {
        if (StringUtils.isEmpty(name))
        {
            throw new UnsupportedOperationException("Name can't null or empty, It is required.");
        }
        for (DocumentAspect aspect : DocumentAspect.values())
        {
            if (aspect.value != null && aspect.value.equalsIgnoreCase(name.trim()))
            {
                return aspect;
            }
        }
        throw new IllegalArgumentException("Not able to find the Document Aspect for given name : " + name);
    }
}
