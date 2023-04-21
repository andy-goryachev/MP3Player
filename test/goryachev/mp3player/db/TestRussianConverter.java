// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.test.TF;
import goryachev.common.test.Test;
import goryachev.common.util.CKit;
import goryachev.common.util.D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Locale;
import javax.imageio.ImageIO;


/**
 * Tests RussianConverter.
 */
public class TestRussianConverter
{
	private static final Charset[] CHARSETS =
	{
		Charset.forName("Cp1251"),
		Charset.forName("KOI8-R"),
		Charset.forName("cp866"),
		Charset.forName("ISO-8859-5")
	};
	

	public static void main(String[] args) throws Exception
	{
		TF.run();
	}
	
	
	//@Test
	public void test()
	{
		t("Ìàëåíüêàÿ õîçÿéêà", "Маленькая хозяйка"); // windows-1251
		t("éÎÓÔÒÕÍÅÎÔÁÌØÎÙÅ", "Инструментальные"); // KOI8-R
		t("ÎÍÀ ÁÛËÀ ×ÈÑÒÀ ÊÀÊ ÑÍÅÃ ÇÈÌÎÉ", "ОНА БЫЛА ЧИСТА КАК СНЕГ ЗИМОЙ"); // windows-1251
		t("ß æèâîé ìåõàíèçì", "Я живой механизм"); // windows-1251 
	}


	protected void t(String input, String expected)
	{
		byte[] b = input.getBytes(CKit.CHARSET_8859_1);
		
		for(Charset cs: CHARSETS)
		{
			String s = new String(b, cs);
			D.print(cs, s);
		}
	}
	
	
	@Test
	public void generateFrequencyMaps() throws Exception
	{
		String text = CKit.readString(new File("D:/Vault/Books/Russian/Brodsky/Brodsky - Poetry.txt"));
		
		for(Charset cs: CHARSETS)
		{
			{
				byte[] b = text.getBytes(cs);
				TransitionMap m = computeTransitions(b);
				File out = new File("test.out/freq_" + cs + ".png");
				m.generateImage(out);
			}
			
			// upper
			{
				byte[] b = text.toUpperCase(Locale.ROOT).getBytes(cs);
				TransitionMap m = computeTransitions(b);
				File out = new File("test.out/UPPERCASE_freq_" + cs + ".png");
				m.generateImage(out);
			}
			
			// lower
			{
				byte[] b = text.toLowerCase(Locale.ROOT).getBytes(cs);
				TransitionMap m = computeTransitions(b);
				File out = new File("test.out/LOWERCASE_freq_" + cs + ".png");
				m.generateImage(out);
			}
		}
	}


	private TransitionMap computeTransitions(byte[] bytes)
	{
		TransitionMap m = new TransitionMap();
		byte prev = bytes[0];
		for(int i=1; i<bytes.length; i++)
		{
			byte b = bytes[i];
			m.add(prev, b);
			prev = b;
		}
		return m;
	}
	
	
	//
	
	
	protected static class TransitionMap
	{
		private final long[] freq = new long[256 * 256];
		private long max;
		
		
		public TransitionMap()
		{
		}
		
		
		protected int index(int prev, int next)
		{
			return  (prev & 0xff) + ((next & 0xff) << 8);
		}

		
		public void add(byte prev, byte next)
		{
			int ix = index(prev, next);
			long v = ++freq[ix];
			if(max < v)
			{
				max = v;
			}
		}
		
		
		public void generateImage(File file) throws Exception
		{
			int black = Color.BLACK.getRGB();
			int blue = Color.BLUE.getRGB();
			int green = Color.GREEN.getRGB();
			int yellow = Color.YELLOW.getRGB();
			int white = Color.WHITE.getRGB();
			double mx = max;
			
			BufferedImage im = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
			for(int y = 0; y < 256; y++)
			{
				for(int x = 0; x < 256; x++)
				{
					int ix = index(x, y);
					long v = freq[ix];
					double f = v / mx;
					
					int c;
					if(v == 0)
					{
						c = black;
					}
//					else if(f < 0.0001)
//					{
//						c = blue;
//					}
//					else if(f < 0.001)
//					{
//						c = green;
//					}
//					else
//					{
//						c = yellow;
//					}
					else
					{
						c = white;
					}
					im.setRGB(x, y, c);
				}
			}

			ImageIO.write(im, "PNG", file);
		}
	}
}
