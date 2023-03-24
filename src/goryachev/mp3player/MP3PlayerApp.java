// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.mp3player.cm.MusicRepo;
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
		
		// try and catch, show swing dialog if failed to launch
		// check database, show set up dialog if not found
		// setup: source directory, db directory
		String dir = "D:/Music";
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
