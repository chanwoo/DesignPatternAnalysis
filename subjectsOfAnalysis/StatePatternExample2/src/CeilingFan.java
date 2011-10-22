import java.util.ArrayList;


/* Store, Caller */
public class CeilingFan {
	ArrayList<State> currentStateList;
	static final double myPi = 3.14;
	static State highState = new HighSpeed();
	
	public CeilingFan() {
		State s = new Off();
		currentStateList.add(s);
	}
	
	public void setState(State s) {
		currentStateList.add(s);
	}
	
	public void changeSpeed() {
		State state = getState();
		state.changeSpeed(this);
	}
	
	private State getState() {
		return currentStateList.get(currentStateList.size() - 1);
	}
	
	public String addString(String a, String b) {
		return a + b;
	}
	
	private void testNull() {
		Object a;
		a = null;
	}

}
