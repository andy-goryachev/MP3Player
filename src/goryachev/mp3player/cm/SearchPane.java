// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.cm;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;


/**
 * Search Pane.
 */
public class SearchPane extends CPane
{
	public static final CssStyle PANE = new CssStyle("SearchPane_PANE");
	protected final TextField queryField;
	
	
	public SearchPane()
	{
		PANE.set(this);
		setHGap(5);
		setPadding(10);
		
		queryField = new TextField();
		
		addColumns
		(
			CPane.PREF,
			CPane.FILL
		);
		addRows
		(
			CPane.PREF
		);
		add(0, 0, FX.label("Find:", Pos.CENTER_RIGHT));
		add(1, 0, queryField);
	}
}
