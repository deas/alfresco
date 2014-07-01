package org.alfresco.service.cmr.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class StatsProcessorTest
{

    @Test
    public void testProcessMap()
    {
        StatsProcessorUsingMap processor = new StatsProcessorUsingMap(mappingData());
        StatsResultSet input = testData();
        StatsResultSet result = processor.process(input);
        assertNotNull(result);
        List<StatsResultStat> statsResults= result.getStats();
        assertEquals(5 , statsResults.size());
        assertEquals("Princess" , statsResults.get(0).getName());
        assertEquals("George" , statsResults.get(1).getName()); //if it doesn't find the result then return the original
        assertEquals("Queen" , statsResults.get(2).getName());
        assertEquals("Prince" , statsResults.get(3).getName());
        assertEquals("King" , statsResults.get(4).getName());
    }

    private StatsResultSet testData()
    {
        List<StatsResultStat> stats = new ArrayList<>();
        stats.add(new StatsResultStat("Kate", 1l, 2l, 3l, 4l, 5l));
        stats.add(new StatsResultStat("George", 1l, 2l, 3l, 4l, 5l));
        stats.add(new StatsResultStat("Liz", 1l, 2l, 3l, 4l, 5l));
        stats.add(new StatsResultStat("William", 1l, 2l, 3l, 4l, 5l));
        stats.add(new StatsResultStat("Charles", 1l, 2l, 3l, 4l, 5l));
 
        StatsResultSetImpl res = new StatsResultSetImpl( 2l, 3l, 4l, 5l,stats);
        return res;
            
    }

    private Map<String, String> mappingData()
    {
        HashMap<String, String> data = new HashMap<>();
        data.put("Charles", "King");
        data.put("Liz", "Queen");
        data.put("William", "Prince");
        data.put("Kate", "Princess");
        return data;
    }

    public static class StatsResultSetImpl implements StatsResultSet {

        private Long numberFound;
        private Long sum;
        private Long max;
        private Long mean;
        private List<StatsResultStat> stats;

        public StatsResultSetImpl(Long numberFound, Long sum, Long max, Long mean,
                    List<StatsResultStat> stats)
        {
            super();
            this.numberFound = numberFound;
            this.sum = sum;
            this.max = max;
            this.mean = mean;
            this.stats = stats;
        }
        public long getNumberFound()
        {
            return this.numberFound;
        }
        public Long getSum()
        {
            return this.sum;
        }
        public Long getMax()
        {
            return this.max;
        }
        public Long getMean()
        {
            return this.mean;
        }
        public List<StatsResultStat> getStats()
        {
            return this.stats;
        }

    }
}
