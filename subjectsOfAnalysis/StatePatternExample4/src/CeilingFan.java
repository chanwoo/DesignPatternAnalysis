/* Store, Caller */
public class CeilingFan {
	State currentState;

	public CeilingFan() {
		currentState = new Off();
	}

	public void changeSpeed() {
		currentState.changeSpeed();
	}

	public class HighSpeed implements State {
		State nextState;

		public HighSpeed() {
			nextState = new Off();
		}

		public void changeSpeed() {
			currentState = nextState;
			System.out.println("off");
		}
	}

	public class LowSpeed implements State {
		State nextState;

		public LowSpeed() {
			nextState = new MediumSpeed();
		}

		public void changeSpeed() {
			currentState = nextState;
			System.out.println("medium speed");
		}
	}

	public class MediumSpeed implements State {
		State nextState;

		public MediumSpeed() {
			nextState = new HighSpeed();
		}

		public void changeSpeed() {
			currentState = nextState;
			System.out.println("high speed");
		}

	}

	public class Off implements State {
		State nextState;

		public Off() {
			nextState = new LowSpeed();
		}

		public void changeSpeed() {
			currentState = nextState;
			System.out.println("low speed");
		}

	}

}
