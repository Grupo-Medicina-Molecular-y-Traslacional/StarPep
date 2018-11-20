package org.bapedis.core.util;

import java.util.Map;


public class BinaryLocator
{
	public static boolean locate(Binary... binaries)
	{
		//System.err.println("BinaryLocator > " + ListUtil.toString(binaries));
		Map<String, String> env = System.getenv();
		String pathDirs[];
		if (OSUtil.isWindows())
			pathDirs = System.getenv("PATH").split(";");
		else
			pathDirs = env.get("PATH").split(":");
		//System.err.println("BinaryLocator > " + ArrayUtil.toString(pathDirs));
		boolean allFound = true;
		for (Binary bin : binaries)
		{
			// may already be found in earlier call
			if (bin.isFound())
				continue;

			// check envName
			if (bin.getEnvName() != null)
			{
				//System.err.println("BinaryLocator > " + bin.getEnvName());
				if (env.get(bin.getEnvName()) != null)
					bin.setLocation(env.get(bin.getEnvName()));
				if (bin.isFound())
					continue;
			}

			// check Path
			for (String dir : pathDirs)
			{
				bin.setParent(dir);
				if (bin.isFound())
					break;
			}
			if (bin.isFound())
				continue;

			allFound = false;
		}
		return allFound;
	}
}
