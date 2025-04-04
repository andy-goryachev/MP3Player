// Copyright © 2008-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.common.util.platform;
import goryachev.common.util.CPlatform;
import java.io.File;


public class CPlatformUnix
	extends CPlatform
{
	@Override
	protected File getSettingsFolderPrivate()
	{
		return new File(getUserHome(), "." + SETTINGS_FOLDER);
	}
}
