
/* Creator, Injector */
public class MediumSpeed implements State {
	public void changeSpeed(CeilingFan fan) {
		fan.setState(new HighSpeed());
		System.out.println("high speed");
	}

}
