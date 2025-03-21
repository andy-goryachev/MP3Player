// Copyright © 2020-2025 Andy Goryachev <andy@goryachev.com>
package goryachev.fx;
import goryachev.common.util.IDisconnectable;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


/**
 * A Change Listener that calls a callback when any of the registered properties change.
 * This class allows for disconnecting the listeners from all the registered properties.
 */
public class FxChangeListener
	implements ChangeListener, IDisconnectable
{
	private final Runnable callback;
	private final CopyOnWriteArrayList<ObservableValue> properties = new CopyOnWriteArrayList<>();
	private boolean enabled = true;
	
	
	public FxChangeListener(Runnable callback)
	{
		this.callback = callback;
	}
	

	public void listen(ObservableValue<?> p)
	{
		if(p != null)
		{
			properties.add(p);
			p.addListener(this);
		}
	}
	
	
	public void listen(ObservableValue<?> ... props)
	{
		for(ObservableValue<?> p: props)
		{
			listen(p);
		}
	}
	
	
	@Override
	public void disconnect()
	{
		for(ObservableValue p: properties)
		{
			p.removeListener(this);
		}
	}
	
	
	public void enable()
	{
		setEnabled(true);
	}
	
	
	public void disable()
	{
		setEnabled(true);
	}
	
	
	public void setEnabled(boolean on)
	{
		enabled = on;
	}
	
	
	public boolean isEnabled()
	{
		return enabled;
	}


	@Override
	public void changed(ObservableValue src, Object prev, Object curr)
	{
		fire();
	}
	
	
	public void fire()
	{
		if(enabled)
		{
			invokeCallback();
		}
	}
	
	
	protected void invokeCallback()
	{
		callback.run();
	}
}
