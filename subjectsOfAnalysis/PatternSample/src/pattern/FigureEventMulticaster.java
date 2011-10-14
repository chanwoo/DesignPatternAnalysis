package pattern;
public class FigureEventMulticaster implements FigureEventListener {
	FigureEventListener a, b;
	
	protected FigureEventMulticaster(FigureEventListener a, FigureEventListener b) {
		this.a = a; 
		this.b = b;
	}     
	
	public void update(FigureEvent e) {
		System.out.println("multicast: " + a + ", " + b);
		a.update(e);
		b.update(e);
	}
	
	public static FigureEventListener add(FigureEventListener a, FigureEventListener b) {
		if (a == null) return b;
		if (b == null) return a;
		//CompositeFigure.foo();
		return new FigureEventMulticaster(a, b);
	}
}