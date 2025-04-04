// Copyright © 2016-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.fx.table;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;


/**
 * FxTreeTableCellFactory.
 */
public abstract class FxTreeTableCellFactory<T>
	implements Callback<TreeTableColumn<T,T>, TreeTableCell<T,T>>
{
	@Override
	public abstract TreeTableCell<T,T> call(TreeTableColumn<T,T> col);
}
