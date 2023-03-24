// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.fx.CPane;
import goryachev.fx.FxWindow;


/**
 * Main Player Window.
 */
public class MainWindow extends FxWindow
{
	public MainWindow()
	{
		super("MainWindow");
		
		CPane p = new CPane();
		p.addRows
		(
			CPane.FILL,
			CPane.PREF,
			CPane.PREF
		);
		p.addColumns
		(
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.FILL,
			CPane.PREF
		);
		setCenter(p);
		setSize(500, 250);
	}
}
