/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.alfresco.share.enums;

/**
 * Cmis Binding Type + Version Enum.
 * 
 * @author Meenal Bhave
 */

public enum CMISBinding
{
    // WEBSERVICES("webservices"),
    // CUSTOM("custom"),
    ATOMPUB10("atompub1.0"),
    ATOMPUB11("atompub1.1"),
    BROWSER11("browser1.1") ;

    private final String value;

    CMISBinding(String v)
    {
        value = v;
    }

    public String value()
    {
        return value;
    }

    public static CMISBinding fromValue(String v)
    {
        for (CMISBinding c : CMISBinding.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
