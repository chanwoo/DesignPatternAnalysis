
/* Creator, Injector */
public class HighSpeed implements State {
	public void changeSpeed(CeilingFan fan) {
		fan.setState(new Off());
		System.out.println("off");
	}

}
