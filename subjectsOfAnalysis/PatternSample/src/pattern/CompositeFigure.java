package pattern;
import java.util.Vector;


public  class CompositeFigure extends AbstractFigure implements FigureEventListener {	
    
	protected Vector _components;	

	public CompositeFigure() { 
		_components = new Vector();	
	}
	
	public void  add(Figure f) {
		f.addListener(this);
		_components.add(f);		
	}
	
	public void op() {
		// do something
		for ( int i=0; i<_components.size(); i++){
			Figure a = (Figure)_components.get(i);
			a.op();
		}
	}
	
	public void update(FigureEvent f) {
		System.out.println("update: " + this);
		//update1(f);
		if ( listener() != null ) 
			listener().update(f);
	}	

}