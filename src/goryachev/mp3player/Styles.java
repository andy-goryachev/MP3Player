// Copyright © 2016-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.fx.CommonStyles;
import goryachev.fx.FxStyleSheet;
import goryachev.fx.Theme;
import goryachev.fx.internal.CssTools;
import goryachev.mp3player.cm.AlbumPane;
import goryachev.mp3player.util.CoverArtLabel;
import goryachev.mp3player.util.MSlider;
import javafx.scene.paint.Color;


/**
 * Application style sheet.
 */
public class Styles
	extends FxStyleSheet
{
	public Styles()
	{
		Theme theme = Theme.current();
		Color buttonPanel = Color.gray(0.82);
		Color bottomInfo = Color.gray(1.0);
		Color lightBorder = Color.gray(0.9);
		Color darkBorder = Color.gray(0.5);

		add
		(
			new CommonStyles(),
			
			selector(MainWindow.INFO_PANE).defines
			(
				prop("-fx-background-color", "linear-gradient(to bottom, " + CssTools.toColor(buttonPanel) + ", " + CssTools.toColor(bottomInfo) + ")")
			),
			selector(MainWindow.BUTTON_PANE).defines
			(
				prop("-fx-background-color", buttonPanel)
			),
			selector(MainWindow.MAIN_PANE, ".button").defines
			(
			    prop("-fx-background-color", CssTools.toColor(darkBorder) + ", " + CssTools.toColor(lightBorder) + ", " + CssTools.toColor(buttonPanel)),
			    prop("-fx-background-insets", "0, 1, 2"),
			    prop("-fx-background-radius", "3px, 2px, 0px"),
			    prop("-fx-padding", 0)
			),
			selector(MainWindow.MAIN_PANE, MainWindow.ARMED).defines
			(
			    prop("-fx-background-color", CssTools.toColor(darkBorder) + ", " + CssTools.toColor(lightBorder) + ", " + CssTools.toColor(Color.rgb(255, 172, 172)))
			),
			selector(MainWindow.MAIN_PANE, ".button:hover").defines
			(
				prop("-fx-background-color", CssTools.toColor(darkBorder) + ", " + CssTools.toColor(lightBorder) + ", white")
			),
			selector(MainWindow.MAIN_PANE, ".button:armed").defines
			(
				//prop("-fx-background-color", CssTools.toColor(darkBorder) + ", " + CssTools.toColor(lightBorder) + ", yellow"),
				prop("-fx-translate-x", "1px"),
			    prop("-fx-translate-y", "1px")
			),
			// offset
			selector(MainWindow.INFO_PANE, "#album, #artist, #year, #track").defines
			(
				padding(0, 0, 0, 4),
				prop("-fx-fill", "red")
			),
			// slider
			selector(CoverArtLabel.ART).defines
			(
				effect("dropshadow(gaussian, gray, 10, 0, 2, 2)")
			),
			selector(MSlider.TRACK).defines
			(
			    prop("-fx-background-color", CssTools.toColor(darkBorder) + ", " + CssTools.toColor(lightBorder) + ", " + CssTools.toColor(buttonPanel)),
			    prop("-fx-background-insets", "0, 1, 2"),
			    prop("-fx-background-radius", "3px, 2px, 0px"),
			    prop("-fx-padding", 0)
			),
			selector(MSlider.TRACK, ":hover").defines
			(
				prop("-fx-background-color", CssTools.toColor(darkBorder) + ", " + CssTools.toColor(lightBorder) + ", white")
			),
			selector(MSlider.THUMB).defines
			(
			    prop("-fx-background-color", CssTools.toColor(darkBorder) + ", " + CssTools.toColor(lightBorder) + ", " + CssTools.toColor(Color.LIGHTGREEN)),
			    prop("-fx-background-insets", "0, 1, 2"),
			    prop("-fx-background-radius", "0px, 0px, 0px"),
			    prop("-fx-padding", 0)
			),
			selector(AlbumPane.CURRENT_TRACK).defines
			(
				fontWeight("bold")
			),
			""
		);
	}
}
