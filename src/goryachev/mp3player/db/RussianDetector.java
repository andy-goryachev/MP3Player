// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.db;
import goryachev.common.util.CKit;
import goryachev.common.util.CSet;
import java.nio.charset.Charset;


/**
 * Russian Charset Detector.
 */
public class RussianDetector implements ICharsetDetector
{
	private static final CSet<String> illegal = initIllegal();
	private static final CSet<String> frequent = initFrequent();
	private static final String ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
	private Stats[] stats;
	
	
	public RussianDetector()
	{
		Charset[] charsets = new Charset[]
		{
			Charset.forName("Cp1251"),
			Charset.forName("KOI8-R"),
			Charset.forName("cp866"),
			Charset.forName("ISO-8859-5"),
			CKit.CHARSET_UTF8,
			CKit.CHARSET_ASCII
		};
		
		stats = new Stats[charsets.length];
		for(int i=0; i<charsets.length; i++)
		{
			Charset cs = charsets[i];
			stats[i] = new Stats(cs);
		}
	}


	/** supply more data for analysis */
	public void update(byte[] bytes, int off, int len)
	{
		for(Stats st: stats)
		{
			try
			{
				process(st, bytes, off, len);
			}
			catch(Exception e)
			{
				st.errors++;
			}
		}
	}
	
	
	/** returns the best guess, or null */
	public Charset guessCharset()
	{
		Stats best = null;
		
		for(Stats s: stats)
		{
			if(isBetter(s, best))
			{
				best = s;
			}
		}
		
		return best == null ? null : best.charset;
	}
	
	
	protected boolean isBetter(Stats s, Stats best)
	{
		if(s.errors > 0)
		{
			return false;
		}
		else if(s.illegal > 0)
		{
			return false;
		}
		else if(s.ctrl > 0)
		{
			return false;
		}
		
		if(best == null)
		{
			if((s.frequent > 0) && (s.russian > 0))
			{
				return true;
			}
			return false;
		}
		
		if(s.frequent > best.frequent)
		{
			return true;
		}
		
		if(s.russian > best.russian)
		{
			return true;
		}
		
		return false;
	}


	protected void process(Stats st, byte[] bytes, int off, int len) throws Exception
	{
		Charset cs = st.charset;
		String text = new String(bytes, off, len, cs);
		st.total = text.length();

		char prev = 0;
		for(char c: text.toCharArray())
		{
			if(isControl(c))
			{
				st.ctrl++;
			}
			else
			{
				char c2 = c;
				c = Character.toLowerCase(c);
				
				if(isLowercaseRussian(c))
				{					
					st.russian++;
					
					if(Character.toUpperCase(c2) == c2)
					{
						st.uppercase++;
					}
					
					if(isLowercaseRussian(prev))
					{
						String s = mkString(prev, c);
						
						if(illegal.contains(s))
						{
							st.illegal++;
						}
						else if(frequent.contains(s))
						{
							st.frequent++;
						}
					}
				}
			}
			
			prev = c;
		}
		
		//D.print(cs, st, text);
	}
	
	
	protected static boolean isControl(char c)
	{
		if(c < 0x20)
		{
			switch(c)
			{
			case '\t':
//			case '\r':
//			case '\n':
				return false;
			}
			return true;
		}
		return false;
	}
	
	
	protected static boolean isLowercaseRussian(char c)
	{
		// https://en.wikipedia.org/wiki/Cyrillic_(Unicode_block)
		if((c >= 0x0430) && (c <= 0x044f))
		{
			return true;
		}
		else if(c == 0x0451)
		{
			return true;
		}
		return false;
	}
	
	
	protected static String mkString(char c1, char c2)
	{
		return new String(new char[] { c1, c2 });
	}
	
	
	private static CSet<String> initIllegal()
	{
		// https://yadro-servis.ru/blog/nevosmosnoe-sochetanie-bukv/
		String s = "ёё|ёщ|ыё|ёу|йэ|гъ|кщ|щф|щз|эщ|щк|гщ|щп|щт|щш|щг|щм|фщ|щл|щд|дщ|ьэ|чц|вй|ёц|ёэ|ёа|йа|шя|шы|ёе|йё|гю|хя|йы|ця|гь|сй|хю|хё|ёи|ёо|яё|ёя|ёь|ёэ|ъж|эё|ъд|цё|уь|щч|чй|шй|шз|ыф|жщ|жш|жц|ыъ|ыэ|ыю|ыь|жй|ыы|жъ|жы|ъш|пй|ъщ|зщ|ъч|ъц|ъу|ъф|ъх|ъъ|ъы|ыо|жя|зй|ъь|ъэ|ыа|нй|еь|цй|ьй|ьл|ьр|пъ|еы|еъ|ьа|шъ|ёы|ёъ|ът|щс|оь|къ|оы|щх|щщ|щъ|щц|кй|оъ|цщ|лъ|мй|шщ|ць|цъ|щй|йь|ъг|иъ|ъб|ъв|ъи|ъй|ъп|ър|ъс|ъо|ън|ък|ъл|ъм|иы|иь|йу|щэ|йы|йъ|щы|щю|щя|ъа|мъ|йй|йж|ьу|гй|эъ|уъ|аь|чъ|хй|тй|чщ|ръ|юъ|фъ|уы|аъ|юь|аы|юы|эь|эы|бй|яь|ьы|ьь|ьъ|яъ|яы|хщ|дй|фй";
		String[] ss = CKit.split(s, '|');
		return new CSet<>(ss);
	}
	
	
	private static CSet<String> initFrequent()
	{
		String s = "ст|то|не|но|на|по|ра|ет|ро|ка|во|ре|ко|ер|ть|ни|ен|ве|ос|ол|пр|от|ли|ор|та|го|ов|ом|те|ле|од|ой|ес|ло|ва|ак|ит|да|ал|ем|за|ла|де|ел|он|ри|ль|ме|ат|ны|ас|ан|ти|мо|тр|ам|че|ск|се|ог|об|ин|до|ав|бе|тв|ей|ки|ви|же|сл|ру|ар|бо|им|пе|вс|кр|чт|ты|из|ед|ми|ок|ад|ил|ис|нн|аз|ма|со|ди|вы|нь|ае|ив|их|ик|хо|ый|тс|ну|ше|ег|сь|па|ев|ус|ду|оч|ут|бы|уд|жи|ча|дн|св|гл|ез|ши|ож|ек|ту|зн|ку|чи|оз|сп|мн|сн|еб|ры|ще|пл|са|эт|лу|му|ах|гр|бу|зд|ий|ое|бр|мы|си|ид|еч|оп|гд|ых|шь|уж|лю|аю|ир|су|др|ум|тн|га|рт|вн|уг|зв|пу|пи|ье|жа|рн|иц|аж|лы|ие|иш|ым|чн|бл|см|ич|еп|ыл|ул|ыв|еж|щи|еш|ба|дв|ее|вр|уч|ца";
		String[] ss = CKit.split(s, '|');
		return new CSet<>(ss);
	}

	
	//
	
	
	protected static class Stats
	{
		public final Charset charset;
		public int total;
		public int ctrl;
		public int russian;
		public int uppercase;
		public int frequent;
		public int illegal;
		public int errors;
		
		
		public Stats(Charset cs)
		{
			charset = cs;
		}
		
		
		public String toString()
		{
			return
				"{ctrl=" + ctrl +
				" russian=" + russian +
				" uppercase=" + uppercase +
				" frequent=" + frequent +
				" illegal=" + illegal +
				" errors=" + errors +
				" total=" + total +
				"}";
		}
	}
}
