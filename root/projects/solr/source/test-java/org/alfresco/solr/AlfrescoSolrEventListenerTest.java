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
package org.alfresco.solr;

import java.util.LinkedList;

import org.alfresco.solr.AlfrescoSolrEventListener.CacheMatch;
import org.alfresco.solr.AlfrescoSolrEventListener.CacheSection;
import org.alfresco.solr.AlfrescoSolrEventListener.Delete;
import org.alfresco.solr.AlfrescoSolrEventListener.Match;
import org.alfresco.solr.AlfrescoSolrEventListener.Merge;
import org.alfresco.solr.AlfrescoSolrEventListener.MergeAndNew;
import org.alfresco.solr.AlfrescoSolrEventListener.New;

import junit.framework.TestCase;

/**
 * @author Andy
 */
public class AlfrescoSolrEventListenerTest extends TestCase
{
    
    static AlfrescoSolrEventListener asel = new AlfrescoSolrEventListener(null);
    
    
    SimpleCacheSection[] start = new SimpleCacheSection[] 
                                                        { 
            new SimpleCacheSection(1,  0, 10, 10,  0),
            new SimpleCacheSection(2, 10, 11, 10,  0),
            new SimpleCacheSection(3, 21, 12, 10,  0), 
            new SimpleCacheSection(4, 33, 13, 10,  0), 
            new SimpleCacheSection(5, 46, 10,  0,  0), 
            new SimpleCacheSection(6, 56, 10, 10,  0),
                                                        };
            
     SimpleCacheSection[] startWithDeletions = new SimpleCacheSection[] 
                                                                { 
                    new SimpleCacheSection(1,  0, 10, 10,  0),
                    new SimpleCacheSection(2, 10, 11, 10,  1),
                    new SimpleCacheSection(3, 21, 12, 10,  0), 
                    new SimpleCacheSection(4, 33, 13, 10,  1), 
                    new SimpleCacheSection(5, 46, 10,  0,  0), 
                    new SimpleCacheSection(6, 56, 10, 10,  1),
                                                        };

    
    SimpleCacheSection[][] mergeResults = new SimpleCacheSection[][]
                                                                   {
            {
                new SimpleCacheSection(1,  0, 10, 10, 0), 
                new SimpleCacheSection(2, 10, 11, 10, 0),
                new SimpleCacheSection(3, 21, 12, 10, 0), 
                new SimpleCacheSection(4, 33, 13, 10, 0), 
                new SimpleCacheSection(7, 46, 10, 10, 0) 
            },
            {
                new SimpleCacheSection(1,  0, 10, 10, 0),
                new SimpleCacheSection(2, 10, 11, 10, 0), 
                new SimpleCacheSection(3, 21, 12, 10, 0), 
                new SimpleCacheSection(7, 33, 20, 20, 0) 
            },
            {
                new SimpleCacheSection(1,  0, 10, 10, 0),
                new SimpleCacheSection(2, 10, 11, 10, 0), 
                new SimpleCacheSection(7, 21, 30, 30, 0) 
            },
            {
                new SimpleCacheSection(1,  0, 10, 10, 0),
                new SimpleCacheSection(7, 10, 40, 40, 0) 
            },
            {
                new SimpleCacheSection(7,  0, 50, 50, 0) 
            },
            {
                new SimpleCacheSection(7,  0, 20, 20,  0), 
                new SimpleCacheSection(3, 20, 12, 10,  0), 
                new SimpleCacheSection(4, 32, 13, 10,  0), 
                new SimpleCacheSection(5, 45, 10,  0,  0), 
                new SimpleCacheSection(6, 55, 10, 10,  0),
            },
            {
                new SimpleCacheSection(7,  0, 30, 30,  0), 
                new SimpleCacheSection(4, 30, 13, 10,  0), 
                new SimpleCacheSection(5, 43, 10,  0,  0), 
                new SimpleCacheSection(6, 53, 10, 10,  0),
            },
            {
                new SimpleCacheSection(7,  0, 40, 40,  0), 
                new SimpleCacheSection(5, 40, 10,  0,  0), 
                new SimpleCacheSection(6, 50, 10, 10,  0),
            },
            {
                new SimpleCacheSection(7,  0, 40, 40,  0), 
                new SimpleCacheSection(6, 40, 10, 10,  0),
            },
            {
                new SimpleCacheSection(1,  0, 10, 10,  0),
                new SimpleCacheSection(2, 10, 11, 10,  0),
                new SimpleCacheSection(7, 21, 20, 20,  0), 
                new SimpleCacheSection(5, 41, 10,  0,  0), 
                new SimpleCacheSection(6, 51, 10, 10,  0),
            },
            {
                new SimpleCacheSection(1,  0, 10, 10,  0),
                new SimpleCacheSection(7, 10, 30, 30,  0),
                new SimpleCacheSection(6, 40, 10, 10,  0),
            },
            {
                new SimpleCacheSection(7,  0, 20, 20,  0),
                new SimpleCacheSection(3, 20, 12, 10,  0), 
                new SimpleCacheSection(4, 32, 13, 10,  0), 
                new SimpleCacheSection(8, 45, 10, 10,  0), 
            },
            {
                new SimpleCacheSection(7,  0, 20, 20,  0),
                new SimpleCacheSection(8, 20, 30, 30,  0),
            },
            {
                new SimpleCacheSection(7,  0, 30, 30,  0),
                new SimpleCacheSection(8, 30, 20, 20,  0),
            },
            {
                new SimpleCacheSection(7,  0, 40, 40,  0),
                new SimpleCacheSection(8, 40, 10, 10,  0),
            },
            {
                new SimpleCacheSection(7,  0, 20, 20,  0),
                new SimpleCacheSection(8, 20, 20, 20,  0), 
                new SimpleCacheSection(9, 40, 10, 10,  0), 
            },
           
            
                                                                   };
            
    CacheMatch[][] mergeOperations = new CacheMatch[][]
                                                     {
            {
                new Match(10, 10),
                new Match(11, 10),
                new Match(12, 10),
                new Match(13, 10),
                new Merge(10, 10),
            },
            {
                new Match(10, 10),
                new Match(11, 10),
                new Match(12, 10),
                new Merge(20, 20)
            },
            {
                new Match(10, 10),
                new Match(11, 10),
                new Merge(30, 30),
            },
            {
                new Match(10, 10),
                new Merge(40, 40),
            },
            {
                new Merge(50, 50),
            },
            {
                new Merge(20, 20),
                new Match(12, 10),
                new Match(13, 10),
                new Match(10, 0),
                new Match(10, 10),
            },
            {
                new Merge(30, 30),
                new Match(13, 10),
                new Match(10, 0),
                new Match(10, 10),
            },
            {
                new Merge(40, 40),
                new Match(10, 0),
                new Match(10, 10),
            },
            {
                new Merge(40, 40),
                new Match(10, 10)
            },
            {
                new Match(10, 10),
                new Match(11, 10),
                new Merge(20, 20),
                new Match(10, 0),
                new Match(10, 10)
            },
            {
                new Match(10, 10),
                new Merge(30, 30),
                new Match(10, 10)
            },
            {
                new Merge(20, 20),
                new Match(12, 10),
                new Match(13, 10),
                new Merge(10, 10)
            },
            {
                new Merge(20, 20),
                new Merge(30, 30)
            },
            {
                new Merge(30, 30),
                new Merge(20, 20)
            },
            {
                new Merge(40, 40),
                new Merge(10, 10)
            },
            {
                new Merge(20, 20),
                new Merge(20, 20),
                new Merge(10, 10)
            },
            
                                                     };
    
    
    SimpleCacheSection[][] deleteResults = new SimpleCacheSection[][]
                                                                   {
            {
                new SimpleCacheSection(1,  0, 10, 10,  0),
                new SimpleCacheSection(2, 10, 11, 9,  0),
                new SimpleCacheSection(3, 21, 12, 10,  0), 
                new SimpleCacheSection(4, 33, 13, 9,  0), 
                new SimpleCacheSection(5, 46, 10, 0,  0), 
                new SimpleCacheSection(6, 56, 10, 9,  0),
            },
            {
                new SimpleCacheSection(1,  0, 10, 10,  0),
                new SimpleCacheSection(2, 10, 11, 9,  0),
                new SimpleCacheSection(3, 21, 12, 10,  0), 
                new SimpleCacheSection(7, 33, 18, 18,  0), 
            },
           
                                                                   };
    
    CacheMatch[][] deleteOperations = new CacheMatch[][]
                                                      {
             {
                 new Match(10, 10),
                 new Delete(11, 9),
                 new Match(12, 10),
                 new Delete(13, 9),
                 new Match(10, 0),
                 new Delete(10, 9),
             },
             {
                 new Match(10, 10),
                 new Delete(11, 9),
                 new Match(12, 10),
                 new Merge(18, 18)
             },
             
                                                      };
    
    
    SimpleCacheSection[][] deleteAndNewResults = new SimpleCacheSection[][]
                                                                    {
             {
                 new SimpleCacheSection(1,  0, 10, 10,  0),
                 new SimpleCacheSection(2, 10, 11, 9,  0),
                 new SimpleCacheSection(3, 21, 12, 10,  0), 
                 new SimpleCacheSection(4, 33, 13, 9,  0), 
                 new SimpleCacheSection(5, 46, 10, 0,  0), 
                 new SimpleCacheSection(6, 56, 10, 9,  0),
             },
             {
                 new SimpleCacheSection(1,  0, 10, 10,  0),
                 new SimpleCacheSection(2, 10, 11, 9,  0),
                 new SimpleCacheSection(3, 21, 12, 10,  0), 
                 new SimpleCacheSection(4, 33, 13, 9,  0), 
                 new SimpleCacheSection(5, 46, 10, 0,  0), 
                 new SimpleCacheSection(6, 56, 10, 9,  0),
                 new SimpleCacheSection(7, 66, 10, 10,  0),
                 new SimpleCacheSection(8, 76, 10, 9,  0),
             },
             {
                 new SimpleCacheSection(1,  0, 10, 10,  0),
                 new SimpleCacheSection(2, 10, 11, 9,  0),
                 new SimpleCacheSection(3, 21, 12, 10,  0), 
                 new SimpleCacheSection(7, 33, 18, 18,  0), 
             },
             {
                 new SimpleCacheSection(1,  0, 10, 10,  0),
                 new SimpleCacheSection(2, 10, 11, 9,  0),
                 new SimpleCacheSection(3, 21, 12, 10,  0), 
                 new SimpleCacheSection(7, 33, 18, 18,  0), 
                 new SimpleCacheSection(8, 47, 20, 2,  0),
                 new SimpleCacheSection(9, 67, 10, 10,  0), 
             },
             {
                 new SimpleCacheSection(10,  0, 29, 29,  0),
                 new SimpleCacheSection(7, 29, 18, 18,  0), 
                 new SimpleCacheSection(8, 47, 20, 2,  0),
                 new SimpleCacheSection(9, 67, 10, 10,  0), 
             },
             {
                 new SimpleCacheSection(1,  0, 10, 10,  0),
                 new SimpleCacheSection(2, 10, 11, 9,  0),
                 new SimpleCacheSection(3, 21, 12, 10,  0), 
                 new SimpleCacheSection(7, 33, 20, 20,  0), 
             },
             {
                 new SimpleCacheSection(1,  0, 10, 10,  0),
                 new SimpleCacheSection(2, 10, 11, 9,  0),
                 new SimpleCacheSection(3, 21, 12, 10,  0), 
                 new SimpleCacheSection(7, 33, 20, 20,  0), 
                 new SimpleCacheSection(8, 47, 20, 2,  0),
                 new SimpleCacheSection(9, 67, 10, 10,  0), 
             },
             {
                 new SimpleCacheSection(7,  0, 19, 19,  0),
                 new SimpleCacheSection(8, 19, 10, 10,  0), 
                 new SimpleCacheSection(9, 29, 9, 9,  0), 
                 new SimpleCacheSection(10, 38, 10, 10,  0),
             },
                                                                    };
    CacheMatch[][] deleteAndNewOperations = new CacheMatch[][]
                                                       {
              {
                  new Match(10, 10),
                  new Delete(11, 9),
                  new Match(12, 10),
                  new Delete(13, 9),
                  new Match(10, 0),
                  new Delete(10, 10),
              },
              {
                  new Match(10, 10),
                  new Delete(11, 9),
                  new Match(12, 10),
                  new Delete(13, 9),
                  new Match(10, 0),
                  new Delete(10, 9),
                  asel.new New(10, 10),
                  asel.new New(10, 10),
              },
              {
                  new Match(10, 10),
                  new Delete(11, 9),
                  new Match(12, 10),
                  new Merge(18, 18)
              },
              {
                  new Match(10, 10),
                  new Delete(11, 9),
                  new Match(12, 10),
                  new Merge(18, 18),
                  asel.new New(20, 20),
                  asel.new New(10, 20),
              },
              {
                  new Merge(29, 29),
                  new Merge(18, 18),
                  asel.new New(20, 20),
                  asel.new New(10, 10),
              },
              {
                  new Match(10, 10),
                  new Delete(11, 9),
                  new Match(12, 10),
                  asel.new MergeAndNew(20, 20)
              },
              {
                  new Match(10, 10),
                  new Delete(11, 9),
                  new Match(12, 10),
                  asel.new MergeAndNew(20, 20),
                  asel.new New(20, 20),
                  asel.new New(10, 10),
              },
              {
                  new Merge(19, 19),
                  new Merge(10, 10),
                  new Merge(9, 9),
                  asel.new MergeAndNew(10, 10),
              },
                                                       };
     
    
    
    public void testMerges()
    {
        
        for(int i = 0; i < mergeResults.length; i ++)
        {
            System.out.println("Test "+i);
            SimpleCacheSection[] after = mergeResults[i];
            
            LinkedList<CacheMatch> operations = asel.buildCacheUpdateOperations(false, start, after, null);
            CacheMatch[] expectedOperations = mergeOperations[i];
            
            assertEquals(expectedOperations.length, operations.size());
            
            for (int c = 0; c < expectedOperations.length; c++)
            {
                System.out.println(c);
                System.out.println("\t"+operations.get(c).getFinalCacheSize());
                System.out.println("\t"+expectedOperations[c].getFinalCacheSize());
                assertTrue(operations.get(c).getClass().isAssignableFrom(expectedOperations[c].getClass()));
                assertTrue(operations.get(c).getFinalCacheSize() == expectedOperations[c].getFinalCacheSize());
                assertTrue(operations.get(c).getFinalDocCount() == expectedOperations[c].getFinalDocCount());
            }
            
        }
    }
    
    public void testWithDeletes()
    {
        
        for(int i = 0; i < deleteResults.length; i ++)
        {
            System.out.println("Test "+i);
            SimpleCacheSection[] after = deleteResults[i];
            
            LinkedList<CacheMatch> operations = asel.buildCacheUpdateOperations(false, startWithDeletions, after, null);
            CacheMatch[] expectedOperations = deleteOperations[i];
            
            assertEquals(expectedOperations.length, operations.size());
            
            for (int c = 0; c < expectedOperations.length; c++)
            {
                System.out.println(c);
                System.out.println("\t"+operations.get(c).getFinalCacheSize());
                System.out.println("\t"+expectedOperations[c].getFinalCacheSize());
                assertTrue(operations.get(c).getClass().isAssignableFrom(expectedOperations[c].getClass()));
                assertTrue(operations.get(c).getFinalCacheSize() == expectedOperations[c].getFinalCacheSize());
            }
            
        }
    }
    
    public void testWithDeletesAndNew()
    {
      
        for(int i = 0; i < deleteAndNewResults.length; i ++)
        {
            System.out.println("Test "+i);
            SimpleCacheSection[] after = deleteAndNewResults[i];
            
            LinkedList<CacheMatch> operations = asel.buildCacheUpdateOperations(true, startWithDeletions, after, null);
            CacheMatch[] expectedOperations = deleteAndNewOperations[i];
            
            assertEquals(expectedOperations.length, operations.size());
            
            for (int c = 0; c < expectedOperations.length; c++)
            {
                System.out.println(c);
                System.out.println("\t"+operations.get(c).getFinalCacheSize());
                System.out.println("\t"+expectedOperations[c].getFinalCacheSize());
                System.out.println("\t"+operations.get(c).toString());
                assertTrue(operations.get(c).getClass().isAssignableFrom(expectedOperations[c].getClass()));
                assertTrue(operations.get(c).getFinalCacheSize() == expectedOperations[c].getFinalCacheSize());
            }
            
        }
    }
    
    public void testAllMatch()
    {
        for (int i = 0; i < 100; i++)
        {
            testAllMatchAndNewSections(i, 0, 10);
        }
    }

    public void testAllMatchAndNewSections()
    {
        for (int i = 0; i < 100; i++)
        {
            for (int j = 0; i < 100; i++)
            {
                testAllMatchAndNewSections(i, j, 10);
            }
        }
    }

    private void testAllMatchAndNewSections(int count, int newCount, int docCount)
    {

        SimpleCacheSection[] before = new SimpleCacheSection[count];
        SimpleCacheSection[] after = new SimpleCacheSection[count + newCount];

        for (int i = 0; i < count; i++)
        {
            before[i] = new SimpleCacheSection(i, i * docCount, docCount, docCount, 0);
            after[i] = new SimpleCacheSection(i, i * docCount, docCount, docCount, 0);
        }
        for (int i = 0; i < newCount; i++)
        {
            after[i] = new SimpleCacheSection(i, (count + i) * docCount, docCount, docCount, 0);
        }

        LinkedList<CacheMatch> operations = asel.buildCacheUpdateOperations(true, before, after, null);

        for (int i = 0; i < count; i++)
        {
            assertTrue(operations.get(i) instanceof Match);
        }
        for (int i = 0; i < newCount; i++)
        {
            assertTrue(operations.get(count + i) instanceof New);
        }

    }

    private static class SimpleCacheSection implements CacheSection
    {
        int id;

        int start;

        int length;

        int docCount;

        int newDeletions = 0;

        SimpleCacheSection(int id, int start, int length, int docCount, int newDeletions)
        {
            this.id = id;
            this.start = start;
            this.length = length;
            this.docCount = docCount;
            this.newDeletions = newDeletions;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            return result;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SimpleCacheSection other = (SimpleCacheSection) obj;
            if (id != other.id)
                return false;
            return true;
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getDocCount()
         */
        @Override
        public int getDocCount()
        {
            return docCount;
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#addDeletion(int)
         */
        @Override
        public void addDeletion(int doc)
        {

        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getDeletionsCount()
         */
        @Override
        public int getDeletionsCount()
        {
            return length - docCount;
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getLength()
         */
        @Override
        public int getLength()
        {
            return length;
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getNewDeletionsCount()
         */
        @Override
        public int getNewDeletionsCount()
        {
            return newDeletions;
        }

        /*
         * (non-Javadoc)
         * @see org.alfresco.solr.AlfrescoSolrEventListener.CacheSection#getStart()
         */
        @Override
        public int getStart()
        {
            return start;
        }

    }
}
