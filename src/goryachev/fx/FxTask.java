// Copyright © 2017-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;
import goryachev.common.log.Log;
import goryachev.common.util.CTask;
import javafx.application.Platform;


/**
 * FX CTask.
 */
public class FxTask<T>
	extends CTask<T>
{
	public FxTask()
	{
	}
	
	
	@Override
	protected void handleSuccess(T result)
	{
		if(onSuccess != null)
		{
			Platform.runLater(() -> super.handleSuccess(result));
		}
	}
	
	
	@Override
	protected void handleError(Throwable e)
	{
		if(onError == null)
		{
			log.error(e);
		}
		else
		{
			Platform.runLater(() -> super.handleError(e));
		}
	}
	
	
	@Override
	protected void handleFinish()
	{
		if(onFinish != null)
		{
			Platform.runLater(() -> super.handleFinish());
		}
	}
}
