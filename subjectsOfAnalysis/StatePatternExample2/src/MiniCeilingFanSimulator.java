
public class MiniCeilingFanSimulator {

	public static void main(String[] args) {
		CeilingFan fan = new CeilingFan();
		fan.changeSpeed();
		fan.changeSpeed();
		fan.changeSpeed();
		fan.changeSpeed();
		fan.changeSpeed();
		fan.changeSpeed();
		String s = fan.addString("a", "b");
		double pi = CeilingFan.myPi;
		State state = CeilingFan.highState;
	}

}

