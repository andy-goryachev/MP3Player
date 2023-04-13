// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.test.TF;
import goryachev.common.test.Test;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
import java.nio.charset.Charset;


/**
 * Tests RussianConverter.
 */
public class TestRussianConverter
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
	}


	protected void t(String input, String expected)
	{
		Charset[] charsets =
		{
			Charset.forName("Cp1251"),
			Charset.forName("KOI8-R"),
			Charset.forName("cp866"),
			Charset.forName("ISO-8859-5"),
			CKit.CHARSET_UTF8,
			CKit.CHARSET_ASCII
		};
		
		byte[] b = input.getBytes(CKit.CHARSET_8859_1);
		
		for(Charset cs: charsets)
		{
			String s = new String(b, cs);
			D.print(cs, s);
		}
	}
}
