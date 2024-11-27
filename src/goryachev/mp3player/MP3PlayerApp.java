// Copyright © 2023-2024 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.common.util.ASettingsStore;
import goryachev.common.util.CPlatform;
import goryachev.common.util.GlobalSettings;
import goryachev.fx.FxFramework;
import goryachev.fx.settings.FxSettingsSchema;
import java.io.File;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * MP3 Player Application.
 */
public class MP3PlayerApp extends Application
{
	public static final String COPYRIGHT = "copyright © 2024 andy goryachev";
	public static final String VERSION = "2024.1126.1605";

	
	public static void main(String[] args) 
	{
		// init logging
		Log.initConsoleForDebug();
		Log.getRoot().info();
		
		File settings = new File(CPlatform.getSettingsFolder(), "MP3Player/settings.conf");
		GlobalSettings.setFileProvider(settings);
		
		Application.launch(MP3PlayerApp.class, args);
	}
	
	
	@Override
	public void start(Stage s) throws Exception
	{
		FxFramework.setStyleSheet(Styles::new);
		
		ASettingsStore store = GlobalSettings.instance();
		FxFramework.openLayout(new FxSettingsSchema(store)
		{
			@Override
			public Stage createDefaultWindow()
			{
				return new MainWindow();
			}

			@Override
			protected Stage createWindow(String name)
			{
				return new MainWindow();
			}
		});
	}
}
