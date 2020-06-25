package util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeUtil
{
	private static Map<String, Long>	timeTotalMap		= new HashMap<String, Long>();
	private static Map<String, Long>	timeTotalCounterMap	= new HashMap<String, Long>();
	private Map<String, Long>			startTimeMap	= null;
	private Map<String, Long>			endTimeMap		= null;
	private boolean 					totalAdded		= false; 	
	
	public static Long getTotalTimeMillis(String timingId)
	{
		return timeTotalMap.get(timingId);
	}
	
	public static Long getAvgTimeMillis(String timingId) throws Exception
	{
		Long totalTime = getTotalTimeMillis(timingId);
		Long count = timeTotalCounterMap.get(timingId);
		if(totalTime==null || count==null || count==0)
		{
			throw new Exception("Timing Info not available");
		}
		return totalTime/count;
	}
	
	public static List<String> getTotalTimeTimingIds()
	{
		List<String> timingIdList = new ArrayList<String>();
		for(Map.Entry<String, Long> entry:timeTotalMap.entrySet())
		{
			timingIdList.add(entry.getKey());
		}
		return timingIdList;
	}

	public TimeUtil()
	{
		super();
		startTimeMap = new HashMap<String, Long>();
		endTimeMap = new HashMap<String, Long>();
	}

	public void setStartTime(String timingId)
	{
		startTimeMap.put(timingId, System.currentTimeMillis());
	}

	public Long getExecutionTimeMillis(String timingId) throws Exception
	{
		Long currentTimeMillis = System.currentTimeMillis();
		Long startTime = startTimeMap.get(timingId);
		if(startTime==null)
		{
			Exception startTimeNotSet = new Exception("Start time not set for "+timingId);
			throw startTimeNotSet;
		}
		
		Long endTime = endTimeMap.get(timingId);
		if(endTime==null)
		{
			endTime=currentTimeMillis;
			endTimeMap.put(timingId, endTime);
		}
		
		return (endTime - startTime);
	}

	public Long getExecutionTimeInSecond(String timingId) throws Exception
	{
		Long endTimeMillis = getExecutionTimeMillis(timingId);
		if (endTimeMillis != null)
		{
			return endTimeMillis / 1000;
		}
		// default, should not happen
		return 0L;
	}

	public void addTotal()
	{
		if(!totalAdded)
		{
			for(Map.Entry<String, Long> entry:startTimeMap.entrySet())
			{
				String timingId = entry.getKey();
				Long totalTime = timeTotalMap.get(timingId);
				if(totalTime==null)
				{
					totalTime=0L;
				}
				
				Long startTime = entry.getValue();
				Long endTime = endTimeMap.get(timingId);
				
				if(startTime==null || endTime==null)
				{
					continue;
				}
				
				timeTotalMap.put(timingId, totalTime+endTime-startTime);
				Long count = timeTotalCounterMap.get(timingId);
				timeTotalCounterMap.put(timingId, (count==null?0L:count)+1);
			}
			totalAdded=true;
		}
	}
	
	public List<String> getTimingIds()
	{
		List<String> timingIdList = new ArrayList<String>();
		for(Map.Entry<String, Long> entry:endTimeMap.entrySet())
		{
			timingIdList.add(entry.getKey());
		}
		return timingIdList;
	}
	
	@Override
	public String toString()
	{
		String toStr="";
		for(Map.Entry<String, Long> entry:startTimeMap.entrySet())
		{
			String timingId = entry.getKey();
			Long startTime = entry.getValue();
			Long endTime = endTimeMap.get(timingId);
			
			if(startTime==null || endTime==null)
			{
				continue;
			}
			
			toStr+=timingId+"="+(endTime-startTime)+"||";
		}
		return toStr;
	}
	
	public static void main(String[] args)
	{
		for(int i=1;i<4;i++)
		{
			TimeUtil timeUtilObj = new TimeUtil();
			timeUtilObj.setStartTime("first");
			try
			{
				Thread.sleep(i*250);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				System.out.println(i+"-first:"+timeUtilObj.getExecutionTimeMillis("first"));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			timeUtilObj.setStartTime("second");
			try
			{
				Thread.sleep(i*500);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				System.out.println(i+"-second:"+timeUtilObj.getExecutionTimeMillis("second"));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			timeUtilObj.addTotal();
			System.out.println(timeUtilObj.toString());
		}
		for(String timingId:TimeUtil.getTotalTimeTimingIds())
		{
			try
			{
				System.out.println(timingId+" Avg timing="+TimeUtil.getAvgTimeMillis(timingId));
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
