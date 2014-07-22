/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.webscripts;


/**
 * Represents a MediaType as described at
 * 
 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
 */
public class MediaType
{
    private String type;
    private String subtype;
    private String params = "";
    private float quality = 1.0f;
    

    /**
     * Construct
     * 
     * @param  mediatype  string representation of mediatype e.g. text/html;level=1;q=0.8
     */
    public MediaType(String mediatype)
    {
        if (mediatype == null || mediatype.length() == 0)
        {
            throw new WebScriptException("Invalid mediatype: " + mediatype);
        }
        String[] parts = mediatype.split("/");
        if (parts.length != 2)
        {
            throw new WebScriptException("Invalid mediatype: " + mediatype + " does not consists of type and subtype");
        }
        
        this.type = parts[0].trim();

        int paramsIdx = parts[1].indexOf(';');
        if (paramsIdx == -1)
        {
            // subtype only has been specified
            this.subtype = parts[1];
        }
        else
        {
            String params = parts[1].substring(paramsIdx);
            int qualityIdx = params.lastIndexOf(";");
            String[] qualityParts = params.substring(qualityIdx +1).split("=");
            if (qualityParts.length == 2 && qualityParts[0].trim().equals("q"))
            {
                // mediatype includes quality factor (and potentially accept-params)
                this.subtype = parts[1].substring(0, paramsIdx).replace(" ", "");
                this.quality = new Float(qualityParts[1].trim()).floatValue();
                if (qualityIdx > 0)
                {
                    this.params = params.substring(1, qualityIdx).replace(" ", "");
                }
            }
            else
            {
                // mediatype includes accept-params only
                this.subtype = parts[1].substring(0, paramsIdx).replace(" ", "");
                this.params = params.substring(1).replace(" ", "");
            }
        }
    }
    
    /**
     * @return  type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @return  subtype
     */
    public String getSubtype()
    {
        return subtype;
    }

    /**
     * @return  params
     */
    public String getParams()
    {
        return params;
    }

    /**
     * @return  quality factory
     */
    public float getQuality()
    {
        return quality;
    }
    
    /**
     * Compare to another media type
     * 
     * @param to  media type to compare to
     * @return  score representing how close a match the compared media types are
     */
    public float compare(MediaType to)
    {
        boolean typeWildcard = to.type.equals("*");
        boolean subtypeWildcard = to.subtype.equals("*");
        boolean paramsWildcard = to.params.length() == 0;
        if (typeWildcard || type.equals(to.type))
        {
            if (subtypeWildcard || subtype.equals(to.subtype))
            {
                if (paramsWildcard || params.equals(to.params))
                {
                    return (typeWildcard ? 0.0f : 10.0f) + (subtypeWildcard ? 0.0f : 100.0f) + (paramsWildcard ? 0.0f : 1000f) + to.quality;
                }
            }
        }
        return 0.0f;
    }
    
    /**
     * @return  string representation of media type in the format of type/subtype[;params]
     */
    private String toMediaType()
    {
        return type + "/" + subtype + (params.length() == 0 ? "" : ";" + params);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return toMediaType().hashCode();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other)
    {
        if (other == null)
        {
            return false;
        }
        if (other instanceof MediaType)
        {
            return this.type.equals(((MediaType)other).type) && this.subtype.equals(((MediaType)other).subtype) &&
                   this.params.equals(((MediaType)other).params);
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return toMediaType() + ";q=" + quality;
    }

    
    
    /**
     * Simple exampe usage
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        MediaType one = new MediaType("*/*");
        System.out.println(one.toString());
        MediaType two = new MediaType("a/b");
        System.out.println(two.toString());
        MediaType three = new MediaType("a/*");
        System.out.println(three.toString());
        MediaType four = new MediaType("a/*;b=1");
        System.out.println(four.toString());
        MediaType five = new MediaType("a/* ; b=1");
        System.out.println(five.toString());
        System.out.println(four.equals(five));
        MediaType six = new MediaType("a/*;q=0.1");
        System.out.println(six.toString());
        MediaType seven = new MediaType("a/*;q = 0.1");
        System.out.println(seven.toString());
        MediaType eight = new MediaType("a/*; q = 0.1");
        System.out.println(eight.toString());
        MediaType nine = new MediaType("a/*;b=1; q = 0.1");
        System.out.println(nine.toString());
        MediaType ten = new MediaType("a/*;");
        System.out.println(ten.toString());
        MediaType eleven = new MediaType("a/*;b=1;c=2;q=1.0");
        System.out.println(eleven.toString());
        
        MediaType textHtmlLevel1 = new MediaType("text/html;level=1");
        MediaType textHtml = new MediaType("text/html");
        MediaType textPlain = new MediaType("text/plain");
        MediaType imageJpeg = new MediaType("image/jpeg");
        MediaType textHtmlLevel2 = new MediaType("text/html;level=2");
        MediaType textHtmlLevel3 = new MediaType("text/html;level=3");
        
        MediaType acceptTextStar = new MediaType("text/*;q=0.3");
        MediaType acceptTextHtml = new MediaType("text/html;q=0.7");
        MediaType acceptTextHtmlLevel1 = new MediaType("text/html;level=1");
        MediaType acceptTextHtmlLevel2 = new MediaType("text/html;level=2;q=0.4");
        MediaType acceptStarStar = new MediaType("*/*;q=0.5");
        

        MediaType[] negotiated = new MediaType[] {textHtmlLevel1, textHtml, textPlain, imageJpeg, textHtmlLevel2, textHtmlLevel3};
        MediaType[] accept = new MediaType[] {acceptTextStar, acceptTextHtml, acceptTextHtmlLevel1, acceptTextHtmlLevel2, acceptStarStar};

        for (MediaType neg : negotiated)
        {
            System.out.println("Testing " + neg.toMediaType());
            float q = 0.0f;
            for (MediaType acc : accept)
            {
                float accq = neg.compare(acc);
                System.out.println(" Compare to " + acc.toString() + " = " + accq);
                if (accq > q)
                {
                    q = accq;
                }
            }
            System.out.println(" Result = " + q);
        }
    }
    
}
