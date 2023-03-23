// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package research.media;
import goryachev.common.util.D;
import goryachev.fx.CPane;
import goryachev.fx.FxButton;
import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


/**
 * MP3Player.
 */
public class MP3Player extends Application
{
	private MediaPlayer player;
	
	
	public static void main(String[] args) 
	{
		Application.launch(MP3Player.class, args);
	}
	
	
	public void start(Stage stage) throws Exception
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
		
		String path = "D:/Music/Russian/А/Полина Агуреева/d2/Полина Агуреева - В этой роще берёзовой.mp3";
		Media media = new Media(new File(path).toURI().toString());
		D.print(media.getMetadata());
		
		player = new MediaPlayer(media);
		player.play();
		player.currentTimeProperty().addListener((s,pr,c) ->
		{
			D.print(c, Platform.isFxApplicationThread());
			timeSlider.setValue(player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis() * 100);
		});
//		MediaView v = new MediaView();
//		v.setMediaPlayer(player);
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
