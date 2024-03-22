// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;
import goryachev.fx.FxDialog;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


/**
 * Message Dialog.
 */
public class MessageDialog extends FxDialog
{
	public MessageDialog(Object parent)
	{
		super(parent, "MessageDialog");
		setSize(400, 200);
	}
	
	
	public void setMessage(String text)
	{
		setCenter(new TextFlow(new Text(text)));
	}
}
