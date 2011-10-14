
/* Creator, Injector */
public class LowSpeed implements State {
	public void changeSpeed(StateController controller) {
		controller.decideNextState(this);
		System.out.println("medium speed");
	}

}
