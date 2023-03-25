// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.fx.CPane;
import goryachev.fx.FxButton;
import goryachev.fx.FxWindow;
import goryachev.mp3player.cm.MusicRepo;
import goryachev.mp3player.util.Utils;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;


/**
 * Main Player Window.
 */
public class MainWindow extends FxWindow
{
	protected static final Log log = Log.get("MainWindow");
	protected final MusicRepo repo;
	protected final Label trackField;
	protected final Label timeField;
	protected final Label durationField;
	protected final Slider timeSlider;
	private MediaPlayer player;
	private TrackInfo currentTrack;
	

	
	public MainWindow(MusicRepo r)
	{
		super("MainWindow");
		this.repo = r;
		setTitle("Player");
		
		trackField = new Label("1/1");
		trackField.setId("trackField");
		
		timeField = new Label("00:00");
		timeField.setId("timeField");
		
		durationField = new Label("00:00");
		durationField.setAlignment(Pos.CENTER_RIGHT);
		durationField.setId("durationField");

		timeSlider = new Slider();
		timeSlider.valueProperty().addListener((x) ->
		{
			handleSliderMoved();
		});
		
		int w0 = 70;
		int w1 = 35;
		
		CPane p = new CPane();
		p.setPadding(5);
		p.setGaps(5);
		p.addRows
		(
			CPane.FILL,
			CPane.PREF,
			CPane.PREF,
			w1
		);
		p.addColumns
		(
			w0,
			w0,
			w1,
			w1,
			CPane.FILL,
			CPane.PREF,
			w1,
			w1
		);
		p.add(4, 1, trackField);
		p.add(0, 1, 1, 3, new FxButton("Play", this::togglePlay));
		p.add(1, 1, 1, 3, new FxButton("Jump", this::jump));
		p.add(4, 2, timeField);
		p.add(5, 2, durationField);
		p.add(2, 3, new FxButton("|<<", this::prevAlbum));
		p.add(3, 3, new FxButton("|<", this::prevTrack));
		p.add(4, 3, 2, 1, timeSlider);
		p.add(6, 3, new FxButton(">|", this::nextTrack));
		p.add(7, 3, new FxButton(">>|", this::nextAlbum));
		setCenter(p);
		setSize(550, 200);
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
		
		AlbumInfo a = t.getAlbum();
		int trackNum = t.getIndex() + 1;
		trackField.setText(trackNum + "/" + a.getTrackCount());
		
		Media media = new Media(f.toURI().toString());

		if(player != null)
		{
			player.dispose();
		}
		
		MediaPlayer p = new MediaPlayer(media);
		p.currentTimeProperty().addListener((s,pr,c) ->
		{
			Duration time = player.getCurrentTime();
			timeField.setText(Utils.formatTime(time));
			
			Duration duration = player.getTotalDuration();
			durationField.setText(Utils.formatTime(duration));
					
			timeSlider.setValue(time.toMillis() / duration.toMillis() * 100);
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
