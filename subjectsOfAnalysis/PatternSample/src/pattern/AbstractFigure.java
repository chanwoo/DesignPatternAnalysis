package pattern;
public abstract class AbstractFigure implements Figure {
	private FigureEventListener _listener;

	public  void addListener(FigureEventListener l) { 
		_listener = FigureEventMulticaster.add(listener(), l);	
	}
	
	public FigureEventListener listener() { 
		return _listener; 
	}
	
	public void trigger() {
		if ( listener() != null )	
			listener().update(new FigureEvent(this));
	}
}