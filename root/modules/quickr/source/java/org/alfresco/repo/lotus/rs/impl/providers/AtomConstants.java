/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
* This file is part of Alfresco
*
* Alfresco is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.repo.lotus.rs.impl.providers;

import org.apache.abdera.util.Constants;
import org.dom4j.QName;

/**
 * @author EugeneZh
 */
public interface AtomConstants
{
    // namespaces

    String ATOM_NS_PREFIX = "atom";

    // elements

    QName SERVICE = QName.get(Constants.LN_SERVICE, Constants.APP_PREFIX, Constants.APP_NS);

    QName WORKSPACE = QName.get(Constants.LN_WORKSPACE, Constants.APP_PREFIX, Constants.APP_NS);

    QName COLLECTION = QName.get(Constants.LN_COLLECTION, Constants.APP_PREFIX, Constants.APP_NS);

    QName ACCEPT = QName.get(Constants.LN_ACCEPT, Constants.APP_PREFIX, Constants.APP_NS);

    // attributes

    QName TITLE_ATTRIBUTE = QName.get(Constants.LN_TITLE);

    QName HREF_ATTRIBUTE = QName.get(Constants.LN_HREF);

    // freemarker

    String TEMPLATE_ENCODING = "utf-8";
}
