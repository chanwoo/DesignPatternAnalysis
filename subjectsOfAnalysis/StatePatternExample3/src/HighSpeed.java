
/* Creator, Injector */
public class HighSpeed implements State {
	public void changeSpeed(StateController controller) {
		controller.decideNextState(this);
		System.out.println("off");
	}

}
