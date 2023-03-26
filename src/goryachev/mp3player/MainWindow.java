// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.fx.CPane;
import goryachev.fx.FX;
import goryachev.fx.FxButton;
import goryachev.fx.FxWindow;
import goryachev.mp3player.cm.MusicDB;
import goryachev.mp3player.util.Utils;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;


/**
 * Main Player Window.
 */
public class MainWindow extends FxWindow
{
	protected static final Log log = Log.get("MainWindow");
	protected MusicDB database;
	protected final Label artField;
	protected final Label titleField;
	protected final Label albumField;
	protected final Label artistField;
	protected final Label yearField;
	protected final Label trackField;
	protected final Label timeField;
	protected final Label durationField;
	protected final Slider timeSlider;
	private MediaPlayer player;
	private Track currentTrack;
	

	
	public MainWindow()
	{
		super("MainWindow");
		setTitle("Player");

		artField = new Label();
		artField.setId("artField");
		artField.setBackground(FX.background(Color.GRAY));

		titleField = new Label("Track Name");
		titleField.setId("titleField");
		titleField.setStyle("-fx-font-weight:bold; -fx-font-size:125%;");
		
		albumField = new Label("Album");
		albumField.setId("albumField");
		
		artistField = new Label("Artist");
		artistField.setId("artistField");
		
		yearField = new Label("2023");
		yearField.setId("yearField");
		
		trackField = new Label();
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
		
		CPane fp = new CPane();
		fp.setPadding(0, 5);
		fp.setHGap(2);
		fp.setVGap(2);
		fp.addRows
		(
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF
		);
		fp.addColumns(CPane.FILL);
		fp.add(0, 0, titleField);
		fp.add(0, 1, albumField);
		fp.add(0, 2, artistField);
		fp.add(0, 3, yearField);
		
		int w0 = 60;
		int w1 = 30;
		
		CPane p = new CPane();
		p.setPadding(2);
		p.setHGap(2);
		p.setVGap(2);
		p.addRows
		(
			w0 + w0,
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
		p.add(0, 0, 2, 1, artField);
		p.add(2, 0, 5, 1, fp);
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
		setWidth(550);
		setResizable(false);
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
		Track t = database.randomJump();
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
		Track t = database.nextTrack(currentTrack);
		play(t);
	}
	
	
	public void play(Track t)
	{
		File f = t.getFile();
		log.info(f);
		
		int trackNum = t.getIndex() + 1;
		trackField.setText(trackNum + "/" + t.getAlbumTrackCount());
		
		titleField.setText(t.getTitle());
		albumField.setText(t.getAlbumName());
		artistField.setText(t.getArtist());
		yearField.setText(t.getYear());
		
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
	
	
	protected void setDB(MusicDB d)
	{
		database = d;
	}
	
	
	public void initialize()
	{
		for(;;)
		{
			// check music dir preference.  if not found -> welcome dialog
			File musicDir = Dirs.getMusicDirectory();
			if((musicDir == null) || (!musicDir.exists()) || (!musicDir.isDirectory()))
			{
				new WelcomeDialog(this).open();
				continue;
			}
			
			// load db
			File db = Dirs.getDataFile();
			MusicDB repo = MusicDB.loadData(db);
			if(repo == null)
			{
				// FIX scanning should not happen in the FX thread!
				repo = MusicDB.scan(musicDir);
				repo.store(db);
			}
			
			setDB(repo);
			
			// TODO if current track  exists, play it

			// otherwise, jump
			jump();
			return;
		}
	}
}
