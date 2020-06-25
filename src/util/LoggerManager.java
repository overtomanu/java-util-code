package util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerManager
{
	static private ConsoleHandler	consoleHandler;
	static private FileHandler		fileTxt;
	static private FileHandler		fileHTML;
	static public Level				logLevel	= Level.ALL;

	static
	{
		try
		{
			fileTxt = new FileHandler("Logging.txt");
			fileHTML = new FileHandler("Logging.html");
			consoleHandler = new ConsoleHandler();
		}
		catch (SecurityException e)
		{
			System.out.println("Unable to set fileHandler for logger\n" + e.getMessage());
		}
		catch (IOException e)
		{
			System.out.println("Unable to set fileHandler for logger\n" + e.getMessage());
		}
	}

	static public Logger getLogger(String name)
	{
		// Create Logger
		Logger logger = Logger.getLogger(name);
		logger.setLevel(logLevel);
		logger.setUseParentHandlers(false);

		// Create txt Formatter
		fileTxt.setFormatter(new SimpleFormatter());
		fileTxt.setLevel(logger.getLevel());

		fileHTML.setFormatter(new MyHtmlFormatter());
		fileHTML.setLevel(logger.getLevel());
		logger.addHandler(fileHTML);
		logger.addHandler(fileTxt);
		logger.addHandler(consoleHandler);
		return logger;
	}

	public static Level getJavaLogLevelObject(String level)
	{

		if (level.equalsIgnoreCase("INFO"))
			return Level.INFO;
		else if (level.equalsIgnoreCase("ALL"))
			return Level.ALL;
		else if (level.equalsIgnoreCase("FINE"))
			return Level.FINE;
		else if (level.equalsIgnoreCase("FINEST"))
			return Level.FINEST;
		else if (level.equalsIgnoreCase("OFF"))
			return Level.OFF;
		else if (level.equalsIgnoreCase("SEVERE"))
			return Level.SEVERE;
		else if (level.equalsIgnoreCase("WARNING"))
			return Level.WARNING;
		else
			return Level.INFO;
	}

	public static void setLogLevel(Level logLevel)
	{
		LoggerManager.logLevel = logLevel;
	}

	public static class MyHtmlFormatter extends Formatter
	{
		@Override
		public String format(final LogRecord rec)
		{
			final StringBuffer buf = new StringBuffer(1000);
			buf.append("<tr>");
			buf.append("<td>");
			if (rec.getLevel().intValue() >= Level.WARNING.intValue())
			{
				buf.append("<b>");
				buf.append(rec.getLevel());
				buf.append("</b>");
				buf.append("</td>");
				buf.append("<td>");
				buf.append("<b>");
				buf.append(this.calcDate(rec.getMillis()));
				buf.append("</b>");
				buf.append("</td>");
				buf.append("<td>");
				buf.append("<strong>");
				buf.append("<big>");
				buf.append(this.formatMessage(rec));
				buf.append("</big>");
				buf.append("</strong>");
				buf.append("</td>");
				buf.append("</tr>\n");
			}
			else
			{
				buf.append(rec.getLevel());
				buf.append("</td>");
				buf.append("<td>");
				buf.append(this.calcDate(rec.getMillis()));
				buf.append("</td>");
				buf.append("<td>");
				buf.append(this.formatMessage(rec));
				buf.append("</td>");
				buf.append("</tr>\n");
			}
			return buf.toString();
		}

		private String calcDate(final long millisecs)
		{
			final SimpleDateFormat date_format = new SimpleDateFormat(
					"MMM dd,yyyy HH:mm");
			final Date resultdate = new Date(millisecs);
			return date_format.format(resultdate);
		}

		@Override
		public String getHead(final Handler h)
		{
			return "<HTML>\n<HEAD><title>Copier Log</title>\nLog Record [" + new Date()
					+ "]\n</HEAD>\n<BODY>\n<PRE>\n" + "<table border>\n  "
					+ "<tr><th><big>Severity</big></th><th><big>Time</big></th><th><big>Log Message</big></th></tr>\n";
		}

		@Override
		public String getTail(final Handler h)
		{
			return "</table>\n  </PRE></BODY>\n</HTML>\n";
		}
	}
}