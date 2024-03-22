// Copyright © 2016-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;
import goryachev.common.util.FH;
import javafx.scene.Node;


/**
 * CSS Style.
 * 
 * Usage example:
 * <pre>
 * public static final CssStyle EXAMPLE = new CssStyle();
 * ...
 * {
 *     Pane pane = new Pane();
 *     EXAMPLE.set(pane);
 * }
 * <pre>
 */
public class CssStyle
{
	private String name;
	private static long seq;


	public CssStyle(String name)
	{
		this.name = generateName(name);
	}
	
	
	public CssStyle()
	{
		this.name = generateName(null);
	}
	
	
	private static synchronized String generateName(String name)
	{
		if(CssLoader.dump)
		{
			StackTraceElement s = new Throwable().getStackTrace()[2];
			return s.getClassName() + "-" + s.getLineNumber() + (name == null ? "" : "-" + name);
		}
		else
		{
			return "S" + (seq++); 
		}
	}
	
	
	public boolean equals(Object x)
	{
		if(x == this)
		{
			return true;
		}
		else if(x instanceof CssStyle s)
		{
			return getName().equals(s.getName());
		}
		else
		{
			return false;
		}
	}
	
	
	public int hashCode()
	{
		int h = FH.hash(CssStyle.class);
		h = FH.hash(h, getName());
		return h;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	
	public String toString()
	{
		return name;
	}
	
	
	public void set(Node n)
	{
		n.getStyleClass().add(getName());
	}
}
