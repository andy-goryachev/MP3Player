// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.log.Log;
import goryachev.common.test.TF;
import goryachev.common.test.Test;
import goryachev.common.util.D;
import java.io.File;


/**
 * Tests ID3_Info.
 */
public class TestID3_Info
{
	public static void main(String[] args) throws Exception
	{
		Log.initConsoleForDebug();
		TF.run();
	}
	
	
	@Test
	public void test()
	{
		String file = "d:/music/Russian/П/Alla Pugacheva/192kbps/11-Barishnya s krestyanskoy zastavi/15-Uehal ricar moy.mp3";
		t(file);
	}
	
	
	protected void t(String file)
	{
		ID3_Info d = ID3_Info.parseID3(new File(file), () -> new RussianDetector());
		D.print(d);
	}
}
