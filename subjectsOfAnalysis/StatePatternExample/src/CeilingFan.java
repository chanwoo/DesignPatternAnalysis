
/* Store, Caller */
public class CeilingFan {
	State current_state;
	
	public CeilingFan() {
		current_state = new Off();
	}
	
	public void setState(State s) {
		current_state = s;
	}
	
	public void changeSpeed() {
		getState().changeSpeed(this);
	}
	
	public State getState() {
		return current_state;
	}

}
