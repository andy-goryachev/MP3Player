// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.common.util.D;
import goryachev.fx.CPane;
import goryachev.fx.FxButton;
import goryachev.mp3player.cm.MusicRepo;
import java.io.File;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


/**
 * MP3 Player Application.
 */
public class MP3PlayerApp extends Application
{
	private static MusicRepo repo;
	private MediaPlayer player;
	
	
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
		w.jump();
		// TODO play saved track or jump
	}
	
	
	// FIX
	public void start2(Stage stage) throws Exception
	{
		FxButton playButton = new FxButton("Play", this::playPause);

		Slider timeSlider = new Slider();
		timeSlider.valueProperty().addListener((x) ->
		{
			if(timeSlider.isPressed())
			{
				player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
			}
		});
		
		CPane p = new CPane();
		p.setBottom(new HBox(playButton, timeSlider));
		
		Scene sc = new Scene(p);
		stage.setScene(sc);
		stage.setWidth(300);
		stage.setHeight(200);
		stage.show();
		
		// ha, does not play ogg
		String path = "user.data/sample.ogg";
		Media media = new Media(new File(path).toURI().toString());
		D.print(media.getMetadata());
		
		player = new MediaPlayer(media);
		player.play();
		player.currentTimeProperty().addListener((s,pr,c) ->
		{
			timeSlider.setValue(player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis() * 100);
		});
	}
	
	
	protected void playPause()
	{
		MediaPlayer.Status st = player.getStatus();
		switch(st)
		{
		case PLAYING:
			player.pause();
			break;
		case PAUSED:
			player.play();
			break;
		}
	}
}
