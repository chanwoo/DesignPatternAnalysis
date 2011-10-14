
/* Creator, Injector */
public class Off implements State {
	public void changeSpeed(StateController controller) {
		controller.decideNextState(this);
		System.out.println("low speed");
	}

}
