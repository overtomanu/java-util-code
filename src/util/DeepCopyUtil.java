package util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DeepCopyUtil
{
	//TODO deepcopy Map
	
	public static <A> A deepCopyArrayAndCollectionShallowElement(A sourceObj)
	{
		return deepCopyArrayAndCollection(sourceObj, false);
	}
	
	@SuppressWarnings(
	{
		"rawtypes", "unchecked"
	})
	public static <A> A deepCopyArrayAndCollection(A sourceObj,boolean cloneLeafElements)
	{
		printlog("deepCopyArrayAndCollection entry, param" + sourceObj);
		if (sourceObj != null)
		{
			
			try
			{
				Class srcClazz = sourceObj.getClass();

				if (sourceObj instanceof Collection)
				{
					A copy = null;
					Collection copyCol=null;
					try 
					{
						copy =(A) srcClazz.getDeclaredConstructor(null).newInstance();
						copyCol = (Collection) copy;
					}
					catch (NoSuchMethodException e) {
						copy = (A) srcClazz.getMethod("clone", null).invoke(sourceObj,null);
						copyCol = (Collection) copy;
						copyCol.clear();
					}
					
					for (Object element : (Collection) sourceObj)
					{
						copyCol.add(
								deepCopyArrayAndCollection(element,cloneLeafElements));
					}
					return copy;
				}
				if (srcClazz.isArray())
				{
					A copy = (A) Array.newInstance(srcClazz.getComponentType(),
							Array.getLength(sourceObj));
					for (int i = 0; i < Array.getLength(sourceObj); i++)
					{
						Array.set(copy, i, deepCopyArrayAndCollection(
								Array.get(sourceObj, i),cloneLeafElements));
					}
					return copy;
				}
				// default case - any object
				if(cloneLeafElements && sourceObj instanceof Cloneable)
				{
					try
					{
						A copy = (A) srcClazz.getMethod("clone", null).invoke(sourceObj,
								null);
						return copy;
					}
					catch (Exception e1)
					{

					}
				}
			}
			catch (IllegalAccessException e){e.printStackTrace();}
			catch (IllegalArgumentException e){e.printStackTrace();}
			catch (InvocationTargetException e){e.printStackTrace();}
			catch (NoSuchMethodException e){e.printStackTrace();}
			catch (SecurityException e){e.printStackTrace();}
			catch (InstantiationException e){e.printStackTrace();}
			finally
			{
				printlog("deepCopyArrayAndCollection exit, param" + sourceObj);
			}
			
		}
		return sourceObj;
	}

	private static void printlog(String logRecord)
	{
		//System.out.println(logRecord);
	}

	public static String deepToString(Object srcObject)
	{
		printlog("deepToString entry, param" + srcObject);
		if (srcObject != null)
		{
			Class srcClazz = srcObject.getClass();
			if (srcObject instanceof Collection)
			{
				String result = "[";
				for (Object element : (Collection) srcObject)
				{
					result += deepToString(element) + ",";
				}
				if (result.endsWith(","))
				{
					result = result.substring(0, result.length() - 1);
				}
				result+="]";
				return result;
			}
			if (srcClazz.isArray())
			{
				String result = "[";
				for (int i = 0; i < Array.getLength(srcObject); i++)
				{
					result += deepToString(Array.get(srcObject, i)) + ",";
				}
				if (result.endsWith(","))
				{
					result = result.substring(0, result.length() - 1);
				}
				result+="]";
				return result;
			}
			// default case - any object
			return srcObject.toString();
		}
		return null;
	}

	public static void main(String[] args)
	{
		int[][] testArr = new int[][]
		{
			{
				1, 2, 3
			},
			{
				4, 5, 6
			},
			{
				7, 8, 9
			}
		};
		int[][] copy = deepCopyArrayAndCollectionShallowElement(testArr);
		// int[][] copy = testArr.clone();
		copy[1][1]=999;
		System.out.println("Copy Array:");
		System.out.println(deepToString(copy));
		System.out.println("source Array:");
		System.out.println(deepToString(testArr));
		/*for(int i=0;i<testArr.length;i++) {
			for (int j=0;j<testArr[i].length;j++) {
				System.out.print(testArr[i][j]+",");
			}
			System.out.println();
		}*/
		/*List<List<Integer>> srcList = new ArrayList<>() {
		{
			add(Arrays.asList(3,2,1));
			add(Arrays.asList(4,5,6,7,8));
			add(Arrays.asList(9,10,11,12,13));
			add(new ArrayList<>());
		}};
		List<List<Integer>> copyList = deepCopyArrayAndCollectionShallowElement(srcList);
		copyList.get(3).add(999);
		System.out.println("Copy List:");
		System.out.println(deepToString(copyList));
		System.out.println("source List:");
		System.out.println(deepToString(srcList));*/
		
		List srcList2 = new ArrayList() {{
			add(new ArrayList() {{add(1);add(2);add(3);}});
			add(new int[] {3,4,5,6});
			add(new Integer[] {7,8,9,10,11});
		}};
		List copyList2 = deepCopyArrayAndCollectionShallowElement(srcList2);
		((List)copyList2.get(0)).add(999);
		((Integer[])copyList2.get(2))[2]=8888;
		System.out.println("Copy List:");
		System.out.println(deepToString(copyList2));
		System.out.println("source List:");
		System.out.println(deepToString(srcList2));
	}
}
