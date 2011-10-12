
/* Creator, Injector */
public class LowSpeed implements State {
	public void changeSpeed(CeilingFan fan) {
		fan.setState(new MediumSpeed());
		System.out.println("medium speed");
	}

}
