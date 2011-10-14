
public class StateController {
	private State currentState;
	
	public StateController() {
		currentState = new Off();
	}
	
	public void decideNextState(State previousState) {
		if (previousState instanceof Off) {
			currentState = new LowSpeed();
		}
		else if (previousState instanceof LowSpeed) {
			currentState = new MediumSpeed();
		}
		else if (previousState instanceof MediumSpeed) {
			currentState = new HighSpeed();
		}
		else if (previousState instanceof HighSpeed) {
			currentState = new Off();
		}
	}
	
	public State getCurrentState() {
		return currentState;
	}

}
