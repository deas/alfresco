package org.alfresco.service.cmr.search;

/**
 * Basic POJO to represent an individual statistic
 * 
 * @author Gethin James
 * @since 5.0
 */
public class StatsResultStat {
    
    private final String name;
    private final Long sum;
    private final Long count;
    private final Long min;
    private final Long max;
    private final Long mean;
    
    public StatsResultStat(String name, Long sum, Long count, Long min, Long max, Long mean)
    {
        super();
        this.name = name;
        this.sum = sum;
        this.count = count;
        this.min = min;
        this.max = max;
        this.mean = mean;
    }

    public String getName()
    {
        return this.name;
    }

    public Long getSum()
    {
        return this.sum;
    }

    public Long getCount()
    {
        return this.count;
    }

    public Long getMin()
    {
        return this.min;
    }

    public Long getMax()
    {
        return this.max;
    }

    public Long getMean()
    {
        return this.mean;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Stat [name=").append(this.name).append(", sum=").append(this.sum)
                    .append(", count=").append(this.count).append(", min=").append(this.min)
                    .append(", max=").append(this.max).append(", mean=").append(this.mean)
                    .append("]");
        return builder.toString();
    }

}