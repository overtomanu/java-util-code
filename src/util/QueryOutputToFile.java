package util;

import java.io.CharArrayWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryOutputToFile
{
	public static Logger log = Logger.getLogger(QueryOutputToFile.class.getName());
	public static final Charset UTF16 = Charset.forName("UTF-16");
	
	public static boolean queryOutputTofile(Connection con, String query, Writer fileWriter)
	{
		if (con != null)
		{
			Statement statement = null;
			ResultSet rs = null;
			try
			{
				String Query = query;
				statement = con.createStatement();
				rs = statement.executeQuery(Query);
				ResultSetMetaData resultSetMetaData = rs.getMetaData();
				int colCount = resultSetMetaData.getColumnCount();
				Map<String, Integer> colInfo = new LinkedHashMap<String, Integer>();
				for (int i = 0; i < colCount; i++)
				{
					colInfo.put(resultSetMetaData.getColumnName(i + 1).toUpperCase(), resultSetMetaData.getColumnType(i + 1));
				}
				//return colInfo;
				
				
				// write column names first
				StringBuffer line = new StringBuffer();
				
				for (Map.Entry<String, Integer> entry : colInfo.entrySet())
				{
					line.append("\"" + entry.getKey().trim() + "\",");
				}
				writeLine(fileWriter, line);
				// write table data
				List<String> columns = new ArrayList<String>(colInfo.keySet());
				
				while (rs.next())
				{
					line = new StringBuffer();
					for (int i = 1; i <= colInfo.size(); i++)
					{
						switch (colInfo.get(columns.get(i - 1)))
						{
							case java.sql.Types.INTEGER:
							case java.sql.Types.NUMERIC:
							case java.sql.Types.BIGINT:
							case java.sql.Types.DECIMAL:
							case java.sql.Types.DOUBLE:
							case java.sql.Types.FLOAT:
							case java.sql.Types.TINYINT:
							case java.sql.Types.SMALLINT:
							case java.sql.Types.BIT:
							case java.sql.Types.BOOLEAN:
								String s = rs.getString(i);
								if (s != null && s.length() > 0)
									line.append("\"" + s + "\",");
								else
									line.append(",");
								break;

							case java.sql.Types.DATE:
								Date date = rs.getDate(i);
								if (date != null)
								{
									line.append("\"" + date.getTime() + "\",");
								}
								else
									line.append(",");
								break;

							case java.sql.Types.TIMESTAMP:
								Timestamp ts = rs.getTimestamp(i);
								if (ts != null)
									line.append("\"" + ts.getTime() + "\",");
								else
									line.append(",");
								break;

							case java.sql.Types.TIME:
								Time t = rs.getTime(i);
								if (t != null)
									line.append("\"" + t.getTime() + "\",");
								else
									line.append(",");
								break;

							case java.sql.Types.BINARY:
								byte[] ba = rs.getBytes(i);
								if (ba != null)
								{
									line.append("\"" + StringUtil.encodeForCSVStorage(new String(ba,UTF16)) + "\",");
								}
								else
									line.append(",");
								break;

							case java.sql.Types.VARBINARY:
								byte[] vba = rs.getBytes(i);
								if (vba != null)
								{
									line.append("\"" + StringUtil.encodeForCSVStorage(new String(vba,UTF16)) + "\",");
								}
								else
									line.append(",");
								break;

							case java.sql.Types.LONGVARBINARY:
								InputStream is = rs.getBinaryStream(i);
								if (is != null)
								{
									Reader streamReader = new InputStreamReader(is, UTF16);
									line.append("\"" + StringUtil.encodeForCSVStorage(IOUtil.ReaderToString(streamReader)) + "\",");
								}
								else
								{
									line.append(",");
								}
								break;

							case java.sql.Types.BLOB:
								Blob blob = rs.getBlob(i);

								if (blob != null)
								{
									try
									{
										
										InputStream blobIs = blob.getBinaryStream();
										Reader streamReader = new InputStreamReader(blobIs, UTF16);
										line.append("\"" + StringUtil.encodeForCSVStorage(IOUtil.ReaderToString(streamReader)) + "\",");
									}
									finally
									{
										// blob.free();
										System.gc();
									}
								}
								else
								{
									line.append(",");
								}

								break;

							case java.sql.Types.LONGVARCHAR:
								Reader cs = rs.getCharacterStream(i);
								if (cs != null)
								{
									
									line.append("\"" + StringUtil.encodeForCSVStorage(IOUtil.ReaderToString(cs)) + "\",");
								}
								else
								{
									line.append(",");
								}
								break;

							case java.sql.Types.CLOB:
								Clob clob = rs.getClob(i);
								try
								{
									if (clob != null)
									{
										
										Reader clobIs = clob.getCharacterStream();
										
										line.append("\"" + StringUtil.encodeForCSVStorage(IOUtil.ReaderToString(clobIs)) + "\",");
									}
									else
									{
										line.append(",");
									}
								}
								finally
								{
									// clob.free();
									System.gc();
								}
								break;

							case java.sql.Types.CHAR:
							case java.sql.Types.VARCHAR:
							case java.sql.Types.OTHER:
							default:
								String ds = rs.getString(i);
								if (ds != null && ds.length() > 0)
									line.append("\"" + StringUtil.encodeForCSVStorage(rs.getString(i)) + "\",");
								else
									line.append(",");
								break;
						}
					}
					writeLine(fileWriter, line);
				}
				
				return true;
			}
			catch (SQLException e)
			{
				log.log(Level.SEVERE, "QueryOutputToFile SQLException occured", e);
			}
			catch (IOException e)
			{
				log.log(Level.SEVERE, "QueryOutputToFile IOException occured", e);
			}
			finally
			{
				try
				{
					if (fileWriter != null)
						fileWriter.close();
				}
				catch (IOException e)
				{
					log.log(Level.SEVERE, "QueryOutputToFile IOException-2 occured", e);
				}
				try
				{
						if (statement != null)
						{
							statement.close();
						}
				}
				catch (SQLException e)
				{
					log.log(Level.SEVERE, "QueryOutputToFile SQLException-2 occured", e);
				}
				try
				{
					if (rs != null)
					{
						rs.close();
					}
				}
				catch (SQLException e)
				{
					log.log(Level.SEVERE, "QueryOutputToFile SQLException-3 occured", e);
				}
			}
		}
		return false;
	}
	
	private static void writeLine(Writer bw, StringBuffer line) throws IOException
	{
		if (line.charAt(line.length() - 1) == ',')
			line.deleteCharAt(line.length() - 1);
		bw.write(line.toString()+"\n");;
		bw.flush();
	}
	
}

class StringUtil
{
	public static String encodeForCSVStorage(String string)
	{
		String encodedString = string.replaceAll("\r\n", "#LINEFEED_NEWLINE#")
										.replaceAll("\n", "#NEWLINE#")
										.replaceAll("\r", "#LINEFEED#")
										.replaceAll("\"", "\"\"");
		return encodedString;
	}
}
