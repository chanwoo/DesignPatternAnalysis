
/* Creator, Injector */
public class Off implements State {
	public void changeSpeed(CeilingFan fan) {
		fan.currentStateList.add(new LowSpeed());
		System.out.println("low speed");
	}

}
