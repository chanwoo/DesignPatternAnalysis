public class Target {
	InterfaceType fieldA, fieldB;
	
	public Target() {
		fieldA = new ConcreteInterfaceType1();
		fieldB = new ConcreteInterfaceType2();
	}

	public InterfaceType methodB(InterfaceType o) {
		InterfaceType fieldValue = fieldA;
		methodC(fieldValue);
		fieldB = fieldValue;
		InterfaceType result = methodA();
		
		InterfaceType a = new ConcreteInterfaceType2();
		InterfaceType b = new ConcreteInterfaceType2();
		
		return result;
	}

	public ConcreteInterfaceType3 methodA() {
		return new ConcreteInterfaceType3();
	}

	public void methodC(InterfaceType o) {
		o.foo();
	}
}
