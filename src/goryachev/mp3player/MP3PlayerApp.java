// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.common.util.CPlatform;
import goryachev.common.util.GlobalSettings;
import goryachev.mp3player.cm.MusicRepo;
import java.io.File;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * MP3 Player Application.
 */
public class MP3PlayerApp extends Application
{
	private static MusicRepo repo;
	
	
	public static void main(String[] args) 
	{
		// init logging
		Log.initConsoleForDebug();
		
		File settings = new File(CPlatform.getSettingsFolder(), "MP3Player/settings.conf");
		GlobalSettings.setFileProvider(settings);
		
		File repoDir = new File(CPlatform.getSettingsFolder(), "MP3Player");
		
		// try and catch, show swing dialog if failed to launch
		// check database, show set up dialog if not found
		// setup: source directory, db directory
		String dir = "D:/Music/Western";
		repo = MusicRepo.load(dir);
		
		Application.launch(MP3PlayerApp.class, args);
	}
	
	
	public void start(Stage s) throws Exception
	{
		MainWindow w = new MainWindow(repo);
		w.open();
		// TODO play saved track or jump
		w.jump();
	}
}
