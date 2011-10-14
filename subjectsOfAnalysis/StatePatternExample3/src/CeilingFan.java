
/* Store, Caller */
public class CeilingFan {
	StateController controller;
	
	public CeilingFan() {
		controller = new StateController();
	}
	
	public void changeSpeed() {
		controller.getCurrentState().changeSpeed(controller);
	}

}
