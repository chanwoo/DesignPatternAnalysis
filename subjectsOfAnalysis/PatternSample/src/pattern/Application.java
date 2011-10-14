package pattern;

public class Application {
	public static void main(String[] args) {
		CompositeFigure d1 = new CompositeFigure();
		CompositeFigure d2 = new CompositeFigure();
		//CompositeFigure d3 = new CompositeFigure();
		Rectangle r1 = new Rectangle();
		//Rectangle r2 = new Rectangle();
		d2.add(r1);
		d1.add(r1);
		//d1.add(r2);
		d2.add(d1);
		d1.op();
		d1.addListener(d2);
		//d1.addListener(d3);
		r1.trigger();
	}

}