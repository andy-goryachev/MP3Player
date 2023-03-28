// Copyright © 2016-2023 Andy Goryachev <andy@goryachev.com>
package goryachev.mp3player;
import goryachev.fx.CommonStyles;
import goryachev.fx.FxStyleSheet;
import goryachev.fx.Theme;
import goryachev.fx.internal.CssTools;
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
		Color buttonPanel = Color.gray(0.8);
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
				// "-fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color"),
			    prop("-fx-background-color", CssTools.toColor(darkBorder) + ", " + CssTools.toColor(lightBorder) + ", " + CssTools.toColor(buttonPanel)),
				//prop("-fx-background-color", "blue, yellow, transparent"),
			    prop("-fx-background-insets", "0, 1, 2"),
			    prop("-fx-background-radius", "3px, 2px, 0px"),
			    prop("-fx-padding", 0),
			    prop("-fx-color", "white")
			),
			selector(MainWindow.MAIN_PANE, ".button:hover").defines
			(
//				prop("-fx-color", "red")
				prop("-fx-background-color", "-fx-outer-border, -fx-inner-border, white")
			)
			
			/*
			selector(".table-view .cell").defines
			(
				padding(0)
			),
			selector(".cell").defines
			(
				padding(0)
			),

			selector(".list-cell:odd").defines
			(
				// disable alternative background in list view
			    prop("-fx-background", "-fx-control-inner-background")
			)
			*/
		);
	}
}
