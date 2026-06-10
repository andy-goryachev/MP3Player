// Copyright © 2023-2026 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.test.TF;
import goryachev.common.test.Test;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
import java.nio.charset.Charset;


/**
 * Tests CyrillicDetector.
 */
public class TestCyrillicDetector
{
	public static void main(String[] args) throws Exception
	{
		TF.run();
	}
	
	
	@Test
	public void test()
	{
		t("Ìàëåíüêàÿ õîçÿéêà", "Маленькая хозяйка"); // windows-1251
		t("éÎÓÔÒÕÍÅÎÔÁÌØÎÙÅ", "Инструментальные"); // KOI8-R
		t("ÎÍÀ ÁÛËÀ ×ÈÑÒÀ ÊÀÊ ÑÍÅÃ ÇÈÌÎÉ", "ОНА БЫЛА ЧИСТА КАК СНЕГ ЗИМОЙ"); // windows-1251
		t("ß æèâîé ìåõàíèçì", "Я живой механизм"); // windows-1251
		t("Áåëàÿ ãâàðäèÿ", "");
		t("×¸ðíàÿ Âîäà Êðûëüÿ Ground Beat", "");
		t("ÁÀËËÀÄÀ Î ÊÍÈÆÍÛÕ ÄÅÒßÕ", "");
	}
	
	
	protected void t(String input, String expected)
	{
		byte[] b = input.getBytes(CKit.CHARSET_8859_1);
		CyrillicDetector d = new CyrillicDetector();
		d.update(b, 0, b.length);
		Charset cs = d.guessCharset();
		D.print(cs);
		if(cs != null)
		{
			D.print(new String(b, cs));
		}
	}
}
