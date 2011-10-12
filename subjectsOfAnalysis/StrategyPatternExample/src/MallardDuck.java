/* Creator, Injector */
public class MallardDuck extends Duck {
	public MallardDuck() {
		setQuackBehavior(new Quack());
		flyBehavior = new FlyWithWings();
	}
}
