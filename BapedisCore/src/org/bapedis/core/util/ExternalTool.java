package org.bapedis.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ExternalTool
{

	public ExternalTool(){

	}

	public String get(final String processName, String[] command)
	{
		return get(processName, command, null);
	}

	public String get(final String processName, String[] command, String[] env)
	{
		try
		{
			File tmp = File.createTempFile("outfile", "out");
			run(processName, command, tmp, true, env);
			return OSUtil.readStringFromFile(tmp.getAbsolutePath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public Process run(final String processName, String[] command)
	{
		return run(processName, command, null, true);
	}

	public Process run(final String processName, String command[], File stdOutfile, boolean wait)
	{
		return run(processName, command, stdOutfile, wait, null);
	}

	public Process run(final String processName, String command[], File stdOutfile, boolean wait, String env[])
	{
		return run(processName, command, stdOutfile, wait, env, null);
	}

	protected void stdout(String s)
	{
		System.out.println(s);
	}

	protected void stderr(String s)
	{
		System.err.println(s);
	}

	StringBuffer errorOut = new StringBuffer();

	public String getErrorOut()
	{
		return errorOut.toString();
	}

	public Process run(final String processName, String[] cmd, File stdOutfile, boolean wait, String env[],
			File workingDirectory)
	{
		if (stdOutfile != null && wait == false)
			throw new IllegalStateException("illegal param combination");

		try
		{
			final File tmpStdOutfile = stdOutfile != null ? new File(stdOutfile + ".tmp") : null;
			// final long starttime = new Date().getTime();
			final Process child;

			if (env == null && workingDirectory == null)
				child = Runtime.getRuntime().exec(cmd);
			else if (env != null && workingDirectory == null)
				child = Runtime.getRuntime().exec(cmd, env);
			else
				child = Runtime.getRuntime().exec(cmd, env, workingDirectory);

			Thread th = null;
			th = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{

						BufferedReader buffy = new BufferedReader(new InputStreamReader(child.getInputStream()));
						PrintStream print = null;
						if (tmpStdOutfile != null)
							print = new PrintStream(tmpStdOutfile);
						while (true)
						{
							String s = buffy.readLine();
							if (s != null)
								if (tmpStdOutfile != null)
									print.println(s);
								else
									stdout(s);
							else
								break;
						}
						buffy.close();
						if (tmpStdOutfile != null)
							print.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			Thread thError = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						BufferedReader buffy = new BufferedReader(new InputStreamReader(child.getErrorStream()));
						// Status.INFO.println();
						while (true)
						{
							String s = buffy.readLine();
							if (s != null)
							{
								errorOut.append(s);
								errorOut.append("\n");
								stderr(s);
							}
							else
								break;
						}
						buffy.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});

			if (th != null)
				th.start();
			thError.start();

			if (wait)
			{
				child.waitFor();
				while (th.isAlive() || thError.isAlive())
				{
					try
					{
						Thread.sleep(50);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				if (child.exitValue() != 0)
					throw new ExternalToolError(processName + " exited with error: " + child.exitValue(),
							getErrorOut(), null);
				if (tmpStdOutfile != null && !OSUtil.robustRenameTo(tmpStdOutfile, stdOutfile))
					throw new Error("cannot rename tmp file");
			}
			return child;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static class ExternalToolError extends Error
	{
		private String errorOut;

		public ExternalToolError(String msg, String errorOut, Throwable cause)
		{
			super(msg, cause);
			this.errorOut = errorOut;
		}

		public String getErrorOut()
		{
			return errorOut;
		}
	}
}