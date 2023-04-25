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
import goryachev.mp3player.cm.ContentManagerWindow;
import goryachev.mp3player.db.MusicDB;
import goryachev.mp3player.util.MSlider;
import goryachev.mp3player.util.Utils;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
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
	protected static final String CURRENT_TRACK = "CURRENT_TRACK";
	public static final CssStyle BUTTON_PANE = new CssStyle("MainWindow_BUTTON_PANE");
	public static final CssStyle MAIN_PANE = new CssStyle("MainWindow_MAIN_PANE");
	public static final CssStyle INFO_PANE = new CssStyle("MainWindow_INFO_PANE");
	protected MusicDB db;
	protected final CoverArtLabel artField;
	protected final Label titleField;
	protected final Label albumField;
	protected final Label artistField;
	protected final Label yearField;
	protected final Label trackField;
	protected final Label timeField;
	protected final Label durationField;
	protected final MSlider timeSlider;
	protected final FxButton playButton;
	protected final FxButton jumpButton;
	protected final FxButton prevAlbumButton;
	protected final FxButton prevTrackButton;
	protected final FxButton nextTrackButton;
	protected final FxButton nextAlbumButton;
	protected final FxButton cmButton;
	private MediaPlayer player;
	private Track currentTrack;

	
	public MainWindow()
	{
		super("MainWindow");
		setTitle("MP3 Player");
		FxDump.attach(this); // FIX

		artField = new CoverArtLabel();
		artField.setId("artField");

		titleField = new Label();
		titleField.setId("titleField");
		titleField.setStyle("-fx-font-weight:bold; -fx-font-size:125%;");
		
		albumField = new Label();
		albumField.setId("albumField");
		
		artistField = new Label();
		artistField.setId("artistField");
		
		yearField = new Label();
		yearField.setId("yearField");
		
		trackField = new Label();
		trackField.setId("trackField");
//		trackField.setAlignment(Pos.CENTER_RIGHT);
		
		timeField = new Label();
		timeField.setId("timeField");
		
		durationField = new Label();
		durationField.setAlignment(Pos.CENTER_RIGHT);
		durationField.setId("durationField");
		
		int w0 = 60;
		int w1 = 30;
		int gp = 4;
		
		playButton = new FxButton(Icons.play(), this::togglePlay);
		jumpButton = new FxButton(Icons.jump(), this::jump);
		prevAlbumButton = new FxButton(Icons.prevAlbum(), this::prevAlbum);
		prevTrackButton = new FxButton(Icons.prevTrack(), this::prevTrack);
		nextTrackButton = new FxButton(Icons.nextTrack(), this::nextTrack);
		nextAlbumButton = new FxButton(Icons.nextAlbum(), this::nextAlbum);
		cmButton = new FxButton(Icons.contentManager(), this::openContentManager);

		timeSlider = new MSlider();
		timeSlider.valueProperty().addListener((x) ->
		{
			handleSliderMoved();
		});
				
		CPane tp = new CPane();
		INFO_PANE.set(tp);
		tp.setPadding(gp);
		tp.setHGap(10);
		//tp.setVGap(gp);
		tp.addRows
		(
			CPane.PREF,
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
		tp.add(0, 0, 1, 6, artField);
		tp.add(1, 0, titleField);
		tp.add(1, 1, albumField);
		tp.add(1, 2, artistField);
		tp.add(1, 3, yearField);
		tp.add(1, 4, trackField); // still, not a good place for this label
				
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
			w1,
			w1
		);
		bp.add(0, 0, 1, 3, playButton);
		bp.add(1, 0, 1, 3, jumpButton);
//		bp.add(2, 0, 2, 2, new FxButton(""));
//		bp.add(6, 0, 2, 2, new FxButton(""));
		bp.add(4, 1, timeField);
		bp.add(5, 1, durationField);
		bp.add(2, 2, prevAlbumButton);
		bp.add(3, 2, prevTrackButton);
		bp.add(4, 2, 2, 1, timeSlider);
		bp.add(6, 2, nextTrackButton);
		bp.add(7, 2, nextAlbumButton);
		bp.add(8, 2, cmButton);
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
				player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue()));
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
		Track t = db.randomJump();
		play(t);
	}
	
	
	public void prevAlbum()
	{
		Track t = db.fromHistory(currentTrack);
		play(t);
	}
	
	
	public void nextAlbum()
	{
		Track t = db.nextAlbum(currentTrack);
		play(t);
	}
	
	
	public void prevTrack()
	{
		Track t = db.prevTrack(currentTrack);
		play(t);
	}
	
	
	public void nextTrack()
	{
		Track t = db.nextTrack(currentTrack);
		play(t);
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
		db = d;
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
			File dbDir = Dirs.getDatabaseDirectory();
			MusicDB db = MusicDB.load(musicDir, dbDir);
			if(db == null)
			{
				// old track info is invalid
				GlobalSettings.setInt(CURRENT_TRACK, -1);
				
				// FIX scanning should not happen in the FX thread!
				// TODO scan dialog
				db = MusicDB.scan(musicDir, dbDir);
				db.save();
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
	
	
	protected void openContentManager()
	{
		ContentManagerWindow.openAlbum(currentTrack);
	}
	
	
	protected void play(Track t)
	{
		log.info(t);
		
		db.addToHistory(t);
		
		// FIX remove
		{
			playButton.setGraphic(Icons.play());
			jumpButton.setGraphic(Icons.jump());
			prevAlbumButton.setGraphic(Icons.prevAlbum());
			prevTrackButton.setGraphic(Icons.prevTrack());
			nextTrackButton.setGraphic(Icons.nextTrack());
			nextAlbumButton.setGraphic(Icons.nextAlbum());
		}
		
		int ix = t.getIndex();
		GlobalSettings.setInt(CURRENT_TRACK, ix);
		
		trackField.setText(t.getNumber() + "/" + t.getAlbumTrackCount());
		titleField.textProperty().bind(t.titleProperty());
		albumField.textProperty().bind(t.albumProperty());
		artistField.textProperty().bind(t.artistProperty());
		yearField.textProperty().bind(t.yearProperty());
		
		File f = t.getFile();
		// TODO if file not found, tell musicDB that a re-scan is required
		
		Image im = t.getCoverArt();
		artField.setImage(im);
		
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
					
			timeSlider.setValue(time.toMillis() / duration.toMillis());
		});
		p.setOnEndOfMedia(() ->
		{
			nextTrack();
		});
		currentTrack = t;
		p.play();
		player = p;
	}
}
