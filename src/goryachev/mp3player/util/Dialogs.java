// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player.util;


/**
 * Dialogs.
 * 
 * TODO move to fx
 */
public class Dialogs
{
	public static void openErrorDialog(Object parent, String title, String text)
	{
		MessageDialog d = new MessageDialog(parent);
		d.setTitle(title);
		d.setMessage(text);
		d.buttonPane().addButton("OK", d::close);
		d.open();
	}
}
