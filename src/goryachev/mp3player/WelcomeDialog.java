// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.util.CKit;
import goryachev.common.util.Parsers;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxAction;
import goryachev.fx.FxButton;
import goryachev.fx.FxDialog;
import goryachev.mp3player.util.Dialogs;
import java.io.File;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


/**
 * Welcome Dialog.
 */
public class WelcomeDialog extends FxDialog
{
	protected final TextField musicDir;
	protected final TextField dataDir;
	protected final FxAction commitAction = new FxAction(this::commit);
	
	
	public WelcomeDialog(Object parent)
	{
		super(parent, "WelcomeDialog");
		setTitle("File Locations");
		
		musicDir = new TextField();
		
		dataDir = new TextField();
		
		CPane p = new CPane();
		p.setPadding(10);
		p.setHGap(5);
		p.setVGap(5);
		p.addRows
		(
			CPane.FILL,
			CPane.PREF,
			CPane.PREF,
			20,
			CPane.PREF,
			CPane.PREF
		);
		p.addColumns
		(
			CPane.PREF,
			CPane.FILL,
			CPane.PREF
		);
		int r = 0;
		p.add(1, r, 2, 1, new Label("Info here...")); // TODO
		r++;
		p.add(0, r, FX.label("Music directory:", Pos.CENTER_RIGHT));
		p.add(1, r, musicDir);
		p.add(2, r, new FxButton("Browse"));
		r++;
		p.add(1, r, 2, 1, new Label("about music dir...")); // TODO
		r++;
		p.add(0, r, FX.label("Data directory:", Pos.CENTER_RIGHT));
		p.add(1, r, dataDir);
		p.add(2, r, new FxButton("Browse"));
		r++;
		p.add(1, r, 2, 1, new Label("about data dir...")); // TODO
		
		setCenter(p);
		buttonPane().setPadding(0, 10, 10, 10);
		buttonPane().fill();
		buttonPane().addButton("Exit", this::exit);
		buttonPane().addButton("OK", commitAction, FxButton.AFFIRM);
		setSize(600, 300);
	}
	
	
	protected void commit()
	{
		String s = musicDir.getText();
		if(CKit.isBlank(s))
		{
			Dialogs.openErrorDialog(this, "Music Directory", "Please enter the location of your music.");
			return;
		}
		
		File f = new File(s);
		if(!f.exists() || !f.isDirectory())
		{
			Dialogs.openErrorDialog(this, "Music Directory", "Please enter a valid folder.");
			return;
		}
		
		Dirs.setMusicDirectory(f);
		
		// TODO check for: exists, isDir
		// TODO store, launch scanner
		close();
	}
	
	
	protected void exit()
	{
		close();
		Platform.exit();
	}
}
