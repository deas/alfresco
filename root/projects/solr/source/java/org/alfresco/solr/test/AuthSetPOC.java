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
package org.alfresco.solr.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.lucene.util.OpenBitSet;

/**
 * @author Andy
 */
public class AuthSetPOC
{

    public static void main(String[] args)
    {
        int docCount = 10000000;

        int aclCount = 100000;

        int authCount = 100000;

        int authsPerAcl = 500;

        HashSet<Long> publicAcls = new HashSet<Long>(aclCount);

        OpenBitSet publicDocs = new OpenBitSet(docCount);

        HashMap<String, HashSet<Long>> authSpecificAcls = new HashMap<String, HashSet<Long>>(authCount,1f);

        long[][] aclToDocs = new long[aclCount][2];

        int docsPerAcl = docCount / aclCount;

        for (long i = 0; i < aclCount; i++)
        {
            Long aclId = i;
            if (i % 2 == 0)
            {
                publicAcls.add(aclId);
                for (long j = i * docsPerAcl; j < ((i + 1) * docsPerAcl); j++)
                {
                    publicDocs.set(j);
                }
            }

            else
            {
            for (long j = i; j < i + authsPerAcl; j++)
            {
                String auth = "AUTH-" + (j % authCount);
                HashSet<Long> authAcls = authSpecificAcls.get(auth);
                if (authAcls == null)
                {
                    authAcls = new HashSet<Long>(authsPerAcl*aclCount/authCount, 1f);
                    authSpecificAcls.put(auth, authAcls);
                }
                authAcls.add(aclId);
            }
            }

            aclToDocs[(int) i][0] = i * docsPerAcl;
            aclToDocs[(int) i][1] = ((i + 1) * docsPerAcl) - 1;
        }

        long start = System.nanoTime();

        int testCount = 100;

        Random r = new Random(213);

        OpenBitSet set = new OpenBitSet(docCount);
        set.union(publicDocs);

        HashSet<Long> toAdd = new HashSet<Long>();
        for (int l = 0; l < testCount; l++)
        {
            String auth = "AUTH-" + r.nextInt(authCount - 1);
            HashSet<Long> specific = authSpecificAcls.get(auth);
            if(specific != null)
            {
                toAdd.addAll(specific);
            }
        }

        for (Long acl : toAdd)
        {
            for (long i = aclToDocs[acl.intValue()][0]; i <= aclToDocs[acl.intValue()][0]; i++)
            {
                set.set(i);
            }
        }

        long end = System.nanoTime();

        System.out.println("In " + ((end - start) / 1e9));
        
        start = System.nanoTime();
        
        
        long s = -1;
        int count = 0;
        while( (s = set.nextSetBit(s+1)) != -1)
        {
            count++;
        }
        
        end = System.nanoTime();
        
        System.out.println("Scan " + count + " " +  ((end - start) / 1e9));
    }
}
