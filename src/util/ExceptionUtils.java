package util;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExceptionUtils
{
	public static void divide(int a, int b)
	{
		if (b == 0)
		{
			ArithmeticException ae = new ArithmeticException("top layer");
			ae.initCause(new IOException("cause"));
			throw ae;
		}
		else
		{
			System.out.println(a / b);
		}
	}

	public static void main(String[] args)
	{
		try
		{
			divide(5, 0);
		}
		catch (ArithmeticException ae)
		{
			System.out.println(getFullExceptionDetail(ae));
		}
	}

	@SuppressWarnings("unchecked")
	public static String getFullExceptionDetail(Throwable throwable)
	{
		StringBuilder fullDetails = new StringBuilder();
		@SuppressWarnings("rawtypes")
		List list = new ArrayList();
		boolean loopContinue = true;
		while (loopContinue)
		{
			list.add(throwable);
			fullDetails.append(getExceptionDetail(throwable));
			throwable = getCause(throwable, list);
			/*if don't want detailed cause methods, remove above line
			and uncomment below line*/

			// throwable = throwable.getCause();
			if(throwable != null && list.contains(throwable) == false)
			{
				fullDetails.append("\nCaused by:\n");
				fullDetails.append("----------\n");
			}
			else
			{
				loopContinue=false;
			}
		}
		return fullDetails.toString();
	}

	public static String getExceptionDetail(Throwable throwable)
	{
		StringBuilder details = new StringBuilder();
		if (throwable != null)
		{
			details.append("Exception:\n" + throwable.getClass().getName()
					+ "\nMessage:\n" + throwable.getMessage() + "\nStack Trace :\n"
					+ getStackTrace(throwable));
		}
		return details.toString();
	}

	public static String getStackTrace(Throwable throwable)
	{
		StringBuilder stacktrace = new StringBuilder();
		stacktrace.append("");
		if (throwable != null)
		{

			StackTraceElement[] stackTrace;
			for (int length = (stackTrace = throwable
					.getStackTrace()).length, i = 0; i < length; ++i)
			{
				final StackTraceElement ste = stackTrace[i];
				stacktrace.append(ste.toString() + "\n");
			}

		}
		return stacktrace.toString();
	}

	public static Throwable getCause(Throwable throwable)
	{
		return getCause(throwable, null);
	}

	@SuppressWarnings("rawtypes")
	public static Throwable getCause(Throwable throwable, List exceptionChain)
	{
		String[] methodNames =
		{
			"getCause", "getNextException", "getTargetException", "getException",
			"getSourceException", "getRootCause", "getCausedByException", "getNested",
			"getLinkedException", "getNestedException", "getLinkedCause", "getThrowable",
		};
		if (throwable == null)
		{
			return null;
		}
		Throwable cause = getCauseUsingWellKnownTypes(throwable);
		if (cause == null || (exceptionChain != null && exceptionChain.contains(cause)))
		{

			for (int i = 0; i < methodNames.length; i++)
			{
				String methodName = methodNames[i];
				if (methodName != null)
				{
					cause = getCauseUsingMethodName(throwable, methodName);
					if (cause != null && (exceptionChain == null
							|| !exceptionChain.contains(cause)))
					{
						break;
					}
				}
			}

			if (cause == null
					|| (exceptionChain != null && exceptionChain.contains(cause)))
			{
				cause = getCauseUsingFieldName(throwable, "detail");
			}
		}
		return cause;
	}

	private static Throwable getCauseUsingFieldName(Throwable throwable, String fieldName)
	{
		Field field = null;
		try
		{
			field = throwable.getClass().getField(fieldName);
		}
		catch (NoSuchFieldException ignored)
		{
			// exception ignored
		}
		catch (SecurityException ignored)
		{
			// exception ignored
		}

		if (field != null && Throwable.class.isAssignableFrom(field.getType()))
		{
			try
			{
				return (Throwable) field.get(throwable);
			}
			catch (IllegalAccessException ignored)
			{
				// exception ignored
			}
			catch (IllegalArgumentException ignored)
			{
				// exception ignored
			}
		}
		return null;
	}

	private static Throwable getCauseUsingWellKnownTypes(Throwable throwable)
	{
		if (throwable instanceof SQLException)
		{
			return ((SQLException) throwable).getNextException();
		}
		else if (throwable instanceof InvocationTargetException)
		{
			return ((InvocationTargetException) throwable).getTargetException();
		}
		else
		{
			return throwable.getCause();
		}
	}

	private static Throwable getCauseUsingMethodName(Throwable throwable,
			String methodName)
	{
		Method method = null;
		try
		{
			method = throwable.getClass().getMethod(methodName, null);
		}
		catch (NoSuchMethodException ignored)
		{
			// exception ignored
		}
		catch (SecurityException ignored)
		{
			// exception ignored
		}

		if (method != null && Throwable.class.isAssignableFrom(method.getReturnType()))
		{
			try
			{
				return (Throwable) method.invoke(throwable, new Object[0]);
			}
			catch (IllegalAccessException ignored)
			{
				// exception ignored
			}
			catch (IllegalArgumentException ignored)
			{
				// exception ignored
			}
			catch (InvocationTargetException ignored)
			{
				// exception ignored
			}
		}
		return null;
	}
}
