public class T {
	I a, b;
	public static I publicStaticFieldOfT = new C();
	public I publicFieldOfT;
	
	public static I staticMethodA () {
		return new A();
	}
	
	public T() {
		a = null;
		b = new B();
		publicFieldOfT = new D();
	}
	
	public void setA(I o) {
		a = o;
	}

	public I inout(I arg1, I arg2) {
	
		setA(arg1);
		
		setA(b);
		
		I dummy = arg2;
		
		callI(dummy);
		
		I newB = new C();
		b = newB;
		
		I result = methodD();
		
		a = b;
		
		methodD();
		
		staticMethodA();
		
		T.staticMethodA();
		
		a = T.publicStaticFieldOfT;
		
		a = publicStaticFieldOfT;
		
		b = T.staticMethodA();
		
		return a;
		
	}

	public I methodD() {
		return new D();
	}

	public void callI(I o) {
		o.foo();
	}
	
	public class U {
		I u1, u2;
		
		public U() {
			u1 = T.staticMethodA();
			u2 = T.staticMethodA();
		}
		
		public I inner() {
			u1 = u2;
			
			u1 = methodD();
			
			return a;
		}
		
		public I inner2() {
			U u = new U();
			u.inner();
			return inner();
		}
		
	}

}
