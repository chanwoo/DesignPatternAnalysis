package pattern;
public class FigureEvent {
	Figure _src;
	public FigureEvent(Figure a) { _src = a; }
	public Figure getSrc() { return _src; }
}