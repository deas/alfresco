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
package org.alfresco.solr.tracker;

/**
 * @author Andy
 */
public class TrackerStats
{
    IncrementalStats modelTimes = new IncrementalStats(1000000);

    IncrementalStats aclTxTimes = new IncrementalStats(1000000);

    IncrementalStats txTimes = new IncrementalStats(1000000);

    IncrementalStats txDocs = new IncrementalStats(1);

    IncrementalStats docTransformationTimes = new IncrementalStats(1000000);

    /**
     * @return the modelTimes
     */
    public IncrementalStats getModelTimes()
    {
        return modelTimes;
    }

    /**
     * @return the aclTxTimes
     */
    public IncrementalStats getAclTxTimes()
    {
        return aclTxTimes;
    }

    /**
     * @return the txTimes
     */
    public IncrementalStats getTxTimes()
    {
        return txTimes;
    }

    /**
     * @return the txDocs
     */
    public IncrementalStats getTxDocs()
    {
        return txDocs;
    }

    /**
     * @return the docTransformationTimes
     */
    public IncrementalStats getDocTransformationTimes()
    {
        return docTransformationTimes;
    }

    public double getMeanModelSyncTime()
    {
        return modelTimes.getMean();
    }

    public double getMeanTxIndexTime()
    {
        return txTimes.getMean();
    }

    public double getMeanDocsPerTx()
    {
        return txDocs.getMean();
    }

    static class IncrementalStats
    {
        int scale;

        IncrementalStats(int scale)
        {
            this.scale = scale;
        }

        double[] moments = new double[5];
      
        double min = 0D;
        
        double max = 0D;

        void add(double xUnscaled)
        {
            double x = xUnscaled / scale;
            if((moments[0] == 0) || (x > max))
            {
                max = x;
            }
            if((moments[0] == 0L) || (x < min))
            {
                min = x;
            }
            double n = moments[0];
            double nPlus1 = n + 1;
            double n2 = n*n;
            double d = (moments[1] - x)/nPlus1;
            double d2 = d*d;
            double d3 = d2*d;
            double n_nPlus1 = n / nPlus1;

            moments[4] += 4*d*moments[3] + 6*d2*moments[2] + (1 + n*n2)*d2*d2;
            moments[4] *= n_nPlus1;
            
            moments[3] += 3*d*moments[2] + (1-n2)*d3;
            moments[3] *= n_nPlus1;
            
            moments[2] += (1+n)*d2;
            moments[2] *= n_nPlus1;
            
            moments[1] -= d;
            
            moments[0] = nPlus1;
        }

        long getN()
        {
            return (long)moments[0];
        }

        double getMin()
        {
            return min;
        }
        
        double getMax()
        {
            return max;
        }
        
        double getMean()
        {
            return moments[1];
        }

        double getVarience()
        {
            if (moments[0] > 1)
            {
                return moments[2] * moments[0] / (moments[0] - 1);
            }
            else
            {
                return Double.NaN;
            }
        }

        double getStandardDeviation()
        {
            return Math.sqrt(getVarience());
        }

        
        double getSkew()
        {
            if (moments[0] > 2)
            {
                double v = getVarience();
                return moments[3]*moments[0]*moments[0] / (Math.sqrt(v)*v*(moments[0]-1)*(moments[0]-2));
            }
            else
            {
                return Double.NaN;
            }
        }                                                                                                                                                                     
        double getKurtosis()
        {
            if (moments[0] > 3)
            {
                double div =(moments[0] - 2) * (moments[0] - 3);
                double nMinus1 = moments[0] - 1;
                double v = getVarience();
                double z = ((moments[4] * moments[0] * moments[0] * (moments[0] + 1) )/(v*v*nMinus1));
                z -= 3*nMinus1*nMinus1;
                z /= div;
                return z;
            }
            else
            {
                return Double.NaN;
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return "IncrementalStats [getN()="
                    + getN() + ", getMin()=" + getMin() + ", getMax()=" + getMax() + ", getMean()=" + getMean() + ", getVarience()=" + getVarience() + ", getStandardDeviation()="
                    + getStandardDeviation() + ", getSkew()=" + getSkew() + ", getKurtosis()=" + getKurtosis() + "]";
        }

        

        
    }

    /**
     * @param l
     */
    public void addModelTime(long time)
    {
        modelTimes.add(time);
    }

    /**
     * @param l
     */
    public void addAclTxTime(long time)
    {
        aclTxTimes.add(time);
    }

    /**
     * @param l
     */
    public void addTxTime(long time)
    {
        txTimes.add(time);
    }

    /**
     * @param size
     */
    public void addTxDocs(int size)
    {
        txDocs.add(size);
    }

    /**
     * @param l
     */
    public void addDocTransformationTime(long time)
    {
        docTransformationTimes.add(time);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "TrackerStats [modelTimes="
                + modelTimes + ", aclTxTimes=" + aclTxTimes + ", txTimes=" + txTimes + ", txDocs=" + txDocs + ", docTransformationTimes=" + docTransformationTimes + "]";
    }

}
