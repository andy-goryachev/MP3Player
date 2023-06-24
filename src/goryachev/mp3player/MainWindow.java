// Copyright © 2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.common.log.Log;
import goryachev.common.util.CPlatform;
import goryachev.common.util.GlobalSettings;
import goryachev.fx.CPane;
import goryachev.fx.CssStyle;
import goryachev.fx.FX;
import goryachev.fx.FxButton;
import goryachev.fx.FxDump;
import goryachev.fx.FxWindow;
import goryachev.mp3player.cm.ContentManagerWindow;
import goryachev.mp3player.db.MusicDB;
import goryachev.mp3player.util.CoverArtLabel;
import goryachev.mp3player.util.MSlider;
import goryachev.mp3player.util.Utils;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Window;
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
		titleField.setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS);
		titleField.setId("titleField");
		titleField.setStyle("-fx-font-weight:bold; -fx-font-size:125%;");
		
		albumField = new Label();
		albumField.setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS);
		albumField.setId("albumField");
		
		artistField = new Label();
		artistField.setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS);
		artistField.setId("artistField");
		
		yearField = new Label();
		yearField.setTextOverrun(OverrunStyle.CENTER_WORD_ELLIPSIS);
		yearField.setId("yearField");
		
		trackField = new Label();
		trackField.setId("trackField");
		
		timeField = new Label();
		timeField.setId("timeField");
		
		durationField = new Label();
		durationField.setAlignment(Pos.CENTER_RIGHT);
		durationField.setId("durationField");
		
		int w0 = 50;
		int w1 = 25;
		int gp = 4;
		
		playButton = new FxButton(Icons.play(true), this::togglePlay);
		jumpButton = new FxButton(Icons.jump(), this::jump);
		prevAlbumButton = new FxButton(Icons.prevAlbum(), this::prevAlbum);
		prevTrackButton = new FxButton(Icons.prevTrack(), this::prevTrack);
		nextTrackButton = new FxButton(Icons.nextTrack(), this::nextTrack);
		nextAlbumButton = new FxButton(Icons.nextAlbum(), this::nextAlbum);
		cmButton = new FxButton(Icons.contentManager(), this::openContentManager);
		
		timeSlider = new MSlider();
		
		timeSlider.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleSliderClick);
				
		CPane tp = new CPane();
		INFO_PANE.set(tp);
		if(CPlatform.isMac())
		{
			tp.setPadding(gp, gp + gp);	
		}
		else
		{
			tp.setPadding(gp);
		}
		tp.setHGap(10);
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
		if(CPlatform.isMac())
		{
			bp.setPadding(gp, gp + gp, gp + gp, gp + gp);
		}
		else
		{
			bp.setPadding(gp);
		}
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


	protected void handleSliderClick(MouseEvent ev)
	{
		if(player != null)
		{
			double v = timeSlider.getValueForX(ev.getX());
			Duration d = player.getMedia().getDuration().multiply(v);
			player.seek(d);
			play();
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
				// pause
				player.pause();
				playButton.setGraphic(Icons.play(false));
				break;
			case PAUSED:
				// play
				playButton.setGraphic(Icons.play(true));
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
				// TODO probably that's why Platform.exit() does not
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
			
			log.info("Music dir: %s", musicDir);
			log.info("DB dir: %s", dbDir);
			
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
	
	
	public void play(Track t)
	{
		log.info(t);
		
		File f = t.getFile();
		if(!f.exists())
		{
			rescanRequired();
			return;
		}
		// TODO if file not found, tell musicDB that a re-scan is required

		String uri = f.toURI().toString();
		Media media;
		try
		{
			media = new Media(uri);
		}
		catch(Exception e)
		{
			log.error(e);
			rescanRequired();
			return;
		}
		
		db.addToHistory(t);
		
		if(Icons.DEBUG)
		{
			jumpButton.setGraphic(Icons.jump());
			prevAlbumButton.setGraphic(Icons.prevAlbum());
			prevTrackButton.setGraphic(Icons.prevTrack());
			nextTrackButton.setGraphic(Icons.nextTrack());
			nextAlbumButton.setGraphic(Icons.nextAlbum());
			cmButton.setGraphic(Icons.contentManager());
		}
		
		int ix = t.getIndex();
		GlobalSettings.setInt(CURRENT_TRACK, ix);
		
		trackField.setText(t.getNumber() + " / " + t.getAlbumTrackCount());
		titleField.textProperty().bind(t.titleProperty());
		albumField.textProperty().bind(t.albumProperty());
		artistField.textProperty().bind(t.artistProperty());
		yearField.textProperty().bind(t.yearProperty());
				
		Image im = t.getCoverArt();
		artField.setImage(im);

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
	
	
	public static void playTrack(Track t)
	{
		for(Window w: Window.getWindows())
		{
			if(w instanceof MainWindow mw)
			{
				mw.play(t);
				return;
			}
		}
	}
	
	
	protected void rescanRequired()
	{
		// TODO dialog? flag?
	}
}
