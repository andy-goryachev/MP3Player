// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.common.util.GlobalSettings;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxButton;
import goryachev.fx.FxDump;
import goryachev.fx.FxWindow;
import goryachev.mp3player.db.MusicDB;
import goryachev.mp3player.util.Utils;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.Duration;


/**
 * Main Player Window.
 */
public class MainWindow extends FxWindow
{
	protected static final Log log = Log.get("MainWindow");
	protected static final String CURRENT_TRACK = "CURRENT_TRACK";
	public static final CssStyle BUTTON_PANE = new CssStyle("MainWindow_BUTTON_PANE");
	public static final CssStyle MAIN_PANE = new CssStyle("MainWindow_MAIN_PANE");
	public static final CssStyle INFO_PANE = new CssStyle("MainWindow_INFO_PANE");
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
		setTitle("MP3 Player");
		FxDump.attach(this); // FIX

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
		
		FxButton playButton = new FxButton("", this::togglePlay);
		FxButton jumpButton = new FxButton("", this::jump);
		FxButton prevAlbumButton = new FxButton("", this::prevAlbum);
		FxButton prevTrackButton = new FxButton("", this::prevTrack);
		FxButton nextTrackButton = new FxButton("", this::nextTrack);
		FxButton nextAlbumButton = new FxButton("", this::nextAlbum);

		timeSlider = new Slider();
		timeSlider.valueProperty().addListener((x) ->
		{
			handleSliderMoved();
		});
		
		int w0 = 60;
		int w1 = 30;
		int gp = 4;
		
		CPane tp = new CPane();
		INFO_PANE.set(tp);
		Stop[] stops =
		{
			new Stop(0, Color.gray(0.85)),
			new Stop(1, Color.gray(0.95))
		};
		tp.setBackground(FX.background(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops))); // FIX
		tp.setPadding(gp);
		tp.setHGap(10);
		tp.setVGap(gp);
		tp.addRows
		(
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.PREF,
			CPane.FILL
		);
		tp.addColumns
		(
			w0 + w0 + gp,
			CPane.FILL
		);
		tp.add(0, 0, 1, 5, artField);
		tp.add(1, 0, titleField);
		tp.add(1, 1, albumField);
		tp.add(1, 2, artistField);
		tp.add(1, 3, yearField);
				
		CPane bp = new CPane();
		BUTTON_PANE.set(bp);
		bp.setPadding(gp);
		bp.setHGap(gp);
		bp.setVGap(gp);
		bp.addRows
		(
			CPane.FILL,
			CPane.PREF,
			w1
		);
		bp.addColumns
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
		bp.add(0, 0, 1, 3, playButton);
		bp.add(1, 0, 1, 3, jumpButton);
		//bp.add(4, 0, trackField);
		bp.add(6, 1, 2, 1, trackField);
		bp.add(4, 1, timeField);
		bp.add(5, 1, durationField);
		bp.add(2, 2, prevAlbumButton);
		bp.add(3, 2, prevTrackButton);
		bp.add(4, 2, 2, 1, timeSlider);
		bp.add(6, 2, nextTrackButton);
		bp.add(7, 2, nextAlbumButton);
		bp.setBackground(FX.background(Color.gray(0.8)));
		
		CPane mp = new CPane();
		MAIN_PANE.set(mp);
		mp.addRows
		(
			w0 + w0 + gp + gp + gp,
			w0 + gp + gp
		);
		mp.addColumns
		(
			CPane.FILL
		);
		mp.add(0, 0, tp);
		mp.add(0, 1, bp);
		
		setCenter(mp);
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
		// TODO track from history
	}
	
	
	public void nextAlbum()
	{
		Track t = database.nextAlbum(currentTrack);
		play(t);
	}
	
	
	public void prevTrack()
	{
		Track t = database.prevTrack(currentTrack);
		play(t);
	}
	
	
	public void nextTrack()
	{
		Track t = database.nextTrack(currentTrack);
		play(t);
	}
	
	
	protected void play(Track t)
	{
		log.info(t);
		
		int ix = t.getIndex();
		GlobalSettings.setInt(CURRENT_TRACK, ix);
		
		int num = t.getTrackIndex() + 1;
		trackField.setText(num + "/" + t.getAlbumTrackCount());
		
		titleField.setText(t.getTitle());
		albumField.setText(t.getAlbumName());
		artistField.setText(t.getArtist());
		yearField.setText(t.getYear());
		
		File f = t.getFile();
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
			File f = Dirs.getDataFile();
			MusicDB db = MusicDB.load(musicDir, f);
			if(db == null)
			{
				// old track info is invalid
				GlobalSettings.setInt(CURRENT_TRACK, -1);
				
				// FIX scanning should not happen in the FX thread!
				// TODO scan dialog
				db = MusicDB.scan(musicDir);
				db.save(f);
			}
			
			setDB(db);
			
			// if current track  exists, play it
			int ix = GlobalSettings.getInt(CURRENT_TRACK, -1);
			if(ix >= 0)
			{
				Track t = db.getTrack(ix);
				if(t != null)
				{
					// TODO from specific position?
					play(t);
					return;
				}
			}
			
			// otherwise, random track
			jump();				
			return;
		}
	}
}
