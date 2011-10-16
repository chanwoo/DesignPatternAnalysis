/* Store, Caller */
public class CeilingFan {
	State currentState;

	public CeilingFan() {
		currentState = new Off();
	}

	public void changeSpeed() {
		currentState.changeSpeed();
	}

	public class Off implements State {
		State nextState;

		public Off() {
			nextState = new On(this);
		}

		public void changeSpeed() {
			currentState = nextState;
			System.out.println("off");
		}

	}
	
	public class On implements State {
		State nextState;

		public On(State aNextState) {
			nextState = aNextState;
		}

		public void changeSpeed() {
			currentState = nextState;
			System.out.println("on");
		}
	}

}
