package util;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;

public class IOUtil
{
	protected static final Logger	log	= LoggerManager.getLogger(IOUtil.class.getName());

	public static void InputToOutputstream(InputStream is,OutputStream os) throws IOException
	{
		try
		{
			long freespace = Runtime.getRuntime().freeMemory() * 1 / 10;
			int buffersize = freespace > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) freespace;
			int offset = 0;
			int numRead = 0;
			byte[] bytes = new byte[buffersize];

			while ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
			{
				offset += numRead;

				if (offset >= buffersize)
				{
					os.write(bytes);
					os.flush();
					log.finer("Wrote " + offset + " bytes to file");
					bytes = new byte[buffersize];
					offset = 0;
				}
			}
			if (offset > 0)
			{
				os.write(bytes, 0, offset);
				os.flush();
				log.finer("Wrote " + offset + " bytes to file");
			}
		}
		finally
		{
			os.close();
			is.close();
			System.gc();
		}
	}
	
	public static byte[] InputStreamToBytes(InputStream is) throws IOException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try
		{
			long freespace = Runtime.getRuntime().freeMemory() * 1 / 10;
			int buffersize = freespace > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) freespace;
			int offset = 0;
			int numRead = 0;
			byte[] bytes = new byte[buffersize];

			while ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
			{
				offset += numRead;

				if (offset >= buffersize)
				{
					os.write(bytes);
					os.flush();
					log.finer("Wrote " + offset + " bytes to byte array");
					bytes = new byte[buffersize];
					offset = 0;
				}
			}
			if (offset > 0)
			{
				os.write(bytes, 0, offset);
				os.flush();
				log.finer("Wrote " + offset + " bytes to byte array");
			}
			return os.toByteArray();
		}
		finally
		{
			os.close();
			is.close();
			System.gc();
		}
	}

	public static void ReaderToWriter(Reader reader,Writer writer) throws IOException
	{
		try
		{
			long freespace = Runtime.getRuntime().freeMemory() * 1 / 10;
			//java chars occupy 2 byte
			int buffersize = freespace > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) freespace / 2;
			int offset = 0;
			int numRead = 0;
			char[] chars = new char[buffersize];
			while ((numRead = reader.read(chars, offset, chars.length - offset)) >= 0)
			{
				offset += numRead;

				if (offset >= buffersize)
				{
					writer.write(chars);
					writer.flush();
					log.finer("Wrote " + offset + " characters to file");
					chars = new char[buffersize];
					offset = 0;
				}
			}
			if (offset > 0)
			{
				writer.write(chars, 0, offset);
				writer.flush();
				log.finer("Wrote " + offset + " characters to file");
			}
		}
		finally
		{
			writer.close();
			reader.close();
			//System.gc();
		}
	}
	
	public static String ReaderToString(Reader reader) throws IOException
	{
		CharArrayWriter cw = new CharArrayWriter();
		try
		{
			long freespace = Runtime.getRuntime().freeMemory() * 1 / 10;
			//java chars occupy 2 byte
			int buffersize = freespace > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) freespace / 2;
			int offset = 0;
			int numRead = 0;
			char[] chars = new char[buffersize];
			while ((numRead = reader.read(chars, offset, chars.length - offset)) >= 0)
			{
				offset += numRead;

				if (offset >= buffersize)
				{
					cw.write(chars);
					chars = new char[buffersize];
					offset = 0;
				}
			}
			if (offset > 0)
			{
				cw.write(chars, 0, offset);
			}
			String result = cw.toString();
			return result;
		}
		finally
		{
			cw.close();
			reader.close();
			//System.gc();
		}
	}
	
	public static String bytesToHex(byte[] bytes) 
	{
		if(bytes!=null && bytes.length > 0)
		{
		    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		    char[] hexChars = new char[bytes.length * 2];
		    int v;
		    for ( int j = 0; j < bytes.length; j++ ) {
		        v = bytes[j] & 0xFF;
		        hexChars[j * 2] = hexArray[v >>> 4];
		        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		    }
		    return new String(hexChars);
		}
		return null;
	}

	public static Long countCharacters(Reader r) throws IOException
	{
		int count = 0;
		while (r.read() != -1)
		{
			count++;
		}
		return Long.valueOf(count);
	}
	
	public static String getString(Reader r) throws IOException
	{
		String content="";
		int charInt = 0;
		while ((charInt = r.read()) != -1)
		{
			content+=(char)charInt;
		}
		return content;
	}
	
	public static String getHexString(File file) throws FileNotFoundException, IOException
	{
		if(file == null)
			return null;
		
		byte[] fileBytes = InputStreamToBytes(new FileInputStream(file));
		if(fileBytes!=null && fileBytes.length > 0)
			return bytesToHex(fileBytes);
		else
			return null;
	}
	
	public static BufferedWriter getWriter(String fileName, String charset) throws IOException
	{
		BufferedWriter writeTofile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), charset));
		return writeTofile;
	}

	public static void writeLine(BufferedWriter bw, String line) throws IOException
	{
		bw.write(line);
		bw.newLine();
		bw.flush();
	}
}
