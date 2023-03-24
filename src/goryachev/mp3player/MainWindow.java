// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.fx.CPane;
import goryachev.fx.FxButton;
import goryachev.fx.FxWindow;
import goryachev.mp3player.cm.MusicRepo;
import java.io.File;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


/**
 * Main Player Window.
 */
public class MainWindow extends FxWindow
{
	protected static final Log log = Log.get("MainWindow");
	protected final MusicRepo repo;
	protected final Slider timeSlider;
	private MediaPlayer player;
	private TrackInfo currentTrack;

	
	public MainWindow(MusicRepo r)
	{
		super("MainWindow");
		this.repo = r;
		setTitle("Player");
		
		timeSlider = new Slider();
		timeSlider.valueProperty().addListener((x) ->
		{
			handleSliderMoved();
			if(timeSlider.isPressed())
			{
				//player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
			}
		});
		
		CPane p = new CPane();
		p.setPadding(5);
		p.setGaps(5);
		p.addRows
		(
			CPane.FILL,
			CPane.PREF,
			CPane.PREF
		);
		p.addColumns
		(
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.FILL,
			CPane.PREF
		);
		p.add(0, 2, new FxButton("Play", this::togglePlay));
		p.add(1, 2, new FxButton("Jump", this::jump));
		p.add(2, 2, new FxButton("|<<", this::prevAlbum));
		p.add(3, 2, new FxButton("|<", this::prevTrack));
		p.add(4, 2, timeSlider);
		p.add(5, 2, new FxButton(">|", this::nextTrack));
		p.add(6, 2, new FxButton(">>|", this::nextAlbum));
		setCenter(p);
		setSize(500, 250);
	}
	
	
	protected void handleSliderMoved()
	{
		if(timeSlider.isPressed())
		{
			if(player != null)
			{
				player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
				play();
			}
		}
	}
	
	
	protected void togglePlay()
	{
		if(player != null)
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
	
	
	public void jump()
	{
		TrackInfo t = repo.randomJump();
		play(t);
	}
	
	
	public void prevAlbum()
	{
		// TODO
	}
	
	
	public void nextAlbum()
	{
		// TODO
	}
	
	
	public void prevTrack()
	{
		// TODO
	}
	
	
	public void nextTrack()
	{
		// TODO
		TrackInfo t = repo.nextTrack(currentTrack);
		play(t);
	}
	
	
	public void play(TrackInfo t)
	{
		File f = t.getFile();
		log.info(f);
		
		Media media = new Media(f.toURI().toString());
				
		if(player != null)
		{
			player.dispose();
		}
		
		MediaPlayer p = new MediaPlayer(media);
		p.currentTimeProperty().addListener((s,pr,c) ->
		{
			timeSlider.setValue(player.getCurrentTime().toMillis() / player.getTotalDuration().toMillis() * 100);
		});
		p.setOnEndOfMedia(() ->
		{
			nextTrack();
		});
		currentTrack = t;
		p.play();
		player = p;
	}
	
	
	public void stop()
	{
		if(player != null)
		{
			player.stop();
		}
	}
	
	
	public void play()
	{
		if(player != null)
		{
			player.play();
		}
	}
}
