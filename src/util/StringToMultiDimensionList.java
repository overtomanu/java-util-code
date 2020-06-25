package util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class StringToMultiDimensionList
{
	@SuppressWarnings({
		"unchecked", "rawtypes"
	})
	public static List strToList(Object objToBeTokenized, List<String> tokens,int depth,int mode)
	{
		List returnObj=null;
		if(depth<tokens.size())
		{
			//System.out.println("Tokenizing "+objToBeTokenized);
			if(objToBeTokenized instanceof String)
			{
				String[] strTokens = ((String)objToBeTokenized).split(Pattern.quote(tokens.get(depth)));
				List<String> strTokenList = new ArrayList<>(Arrays.asList(strTokens));
				returnObj=strTokenList;
			}
			else if (objToBeTokenized instanceof List)
			{
				List objTokenList = (List)objToBeTokenized;
				for(int i=0;i<objTokenList.size();i++)
				{
					Object objItemToTokenize = objTokenList.remove(i);
					objTokenList.add(i, strToList(objItemToTokenize, tokens, depth, 1));
				}
				returnObj=objTokenList;
			}
			else
			{
				
			}
			if(mode==0)
			{
				returnObj=strToList(returnObj, tokens, depth+1, 0);
			}
			return returnObj;
		}
		else
		{
			return ((List)objToBeTokenized);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static String multiDimensionalListHTML(List listToBeHTML)
	{
		String returnString="";
		returnString+="<table  border=\"1\">";
		//System.out.println("Called function for "+listToBeHTML);
		if(listToBeHTML!=null && listToBeHTML.size()>0)
		{
			boolean containsListChild=false;
			for(Object listItem:listToBeHTML)
			{
				if(listItem instanceof List)
				{
					containsListChild=true;
					break;
				}
			}
			if(!containsListChild)
			{
				returnString+="<tr>";
				for(Object listItem:listToBeHTML)
				{
					returnString+="<td>"+listItem.toString()+"</td>";
				}
				returnString+="</tr>";
			}
			else
			{
				for(Object listItem:listToBeHTML)
				{
					if(listItem instanceof List)
					{
						returnString+="<tr>";
						for(Object listItemSItem:(List)listItem)
						{
							if(listItemSItem instanceof List)
							{
								returnString+="<td>"+multiDimensionalListHTML((List)listItemSItem)+"</td>";
							}
							else
							{
								returnString+="<td>"+listItemSItem.toString()+"</td>";
							}
						}
						returnString+="</tr>";
					}
					else
					{
						returnString+="<tr><td>"+listItem.toString()+"</td></tr>";
					}
				}
			}
		}
		returnString+="</table>";
		return returnString;
	}
	
	@SuppressWarnings({
		"rawtypes"
	})
	public static void main(String[] args)
	{
		String str = "";
		str="11|12|13||21|22|23||31|32|33";
		//Table of Lists OR List of Tables problem in 3 level nested lists
		//str="111|112|113||121|122|123||131|132|133|||211|212|213||221|222|223||231|232|233";
		//str="1111|1112|1113||1121|1122|1123||1131|1132|1133|||1211|1212|1213||1221|1222|1223||1231|1232|1233||||2111|2112|2113||2121|2122|2123||2131|2132|2133|||2211|2212|2213||2221|2222|2223||2231|2232|2233";
		//str="11111|11112|11113||11121|11122|11123||11131|11132|11133|||11211|11212|11213||11221|11222|11223||11231|11232|11233||||12111|12112|12113||12121|12122|12123||12131|12132|12133|||12211|12212|12213||12221|12222|12223||12231|12232|12233|||||21111|21112|21113||21121|21122|21123||21131|21132|21133|||21211|21212|21213||21221|21222|21223||21231|21232|21233||||22111|22112|22113||22121|22122|22123||22131|22132|22133|||22211|22212|22213||22221|22222|22223||22231|22232|22233";
		str="111111|111112|111113||111121|111122|111123||111131|111132|111133|||111211|111212|111213||111221|111222|111223||111231|111232|111233||||112111|112112|112113||112121|112122|112123||112131|112132|112133|||112211|112212|112213||112221|112222|112223||112231|112232|112233|||||121111|121112|121113||121121|121122|121123||121131|121132|121133|||121211|121212|121213||121221|121222|121223||121231|121232|121233||||122111|122112|122113||122121|122122|122123||122131|122132|122133|||122211|122212|122213||122221|122222|122223||122231|122232|122233||||||211111|211112|211113||211121|211122|211123||211131|211132|211133|||211211|211212|211213||211221|211222|211223||211231|211232|211233||||212111|212112|212113||212121|212122|212123||212131|212132|212133|||212211|212212|212213||212221|212222|212223||212231|212232|212233|||||221111|221112|221113||221121|221122|221123||221131|221132|221133|||221211|221212|221213||221221|221222|221223||221231|221232|221233||||222111|222112|222113||222121|222122|222123||222131|222132|222133|||222211|222212|222213||222221|222222|222223||222231|222232|222233";
		
		List test = null;
		test = strToList(str,Arrays.asList("||", "|") , 0, 0);
		//test = strToList(str,Arrays.asList("|||", "||", "|") , 0, 0);
		//test = strToList(str,Arrays.asList("||||", "|||", "||", "|") , 0, 0);
		test = strToList(str,Arrays.asList("|||||", "||||", "|||", "||", "|") , 0, 0);
		test = strToList(str,Arrays.asList("||||||", "|||||", "||||", "|||", "||", "|") , 0, 0);
		System.out.println(test);
		
		System.out.println(multiDimensionalListHTML(test));
	}
}
