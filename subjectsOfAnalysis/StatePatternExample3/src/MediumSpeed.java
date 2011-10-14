
/* Creator, Injector */
public class MediumSpeed implements State {
	public void changeSpeed(StateController controller) {
		controller.decideNextState(this);
		System.out.println("high speed");
	}

}
