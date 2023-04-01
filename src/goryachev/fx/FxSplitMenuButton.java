// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;


/**
 * FxSplitMenuButton.
 */
public class FxSplitMenuButton extends SplitMenuButton
{
	public FxSplitMenuButton(MenuItem ... items)
	{
		super(items);
	}
	
	
	public FxSplitMenuButton()
	{
	}
	
	
	public FxSplitMenuButton(String text)
	{
		setText(text);
	}
	
	
	public FxMenuItem item(String text)
	{
		FxMenuItem m = new FxMenuItem(text);
		getItems().add(m);
		return m;
	}
	
	
	public FxMenu menu(String text)
	{
		FxMenu m = new FxMenu(text);
		getItems().add(m);
		return m;
	}
}
