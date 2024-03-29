// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.common.util.CPlatform;
import goryachev.common.util.GlobalSettings;
import goryachev.fx.CssLoader;
import java.io.File;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * MP3 Player Application.
 */
public class MP3PlayerApp extends Application
{
	public static void main(String[] args) 
	{
		// init logging
		Log.initConsoleForDebug();
		Log.getRoot().info();
		
		File settings = new File(CPlatform.getSettingsFolder(), "MP3Player/settings.conf");
		GlobalSettings.setFileProvider(settings);
		
		Application.launch(MP3PlayerApp.class, args);
	}
	
	
	public void start(Stage s) throws Exception
	{
		CssLoader.setStyles(Styles::new);
		
		MainWindow w = new MainWindow();
		w.open();
		w.initialize();
	}
}
