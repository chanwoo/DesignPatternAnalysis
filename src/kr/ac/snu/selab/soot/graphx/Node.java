package kr.ac.snu.selab.soot.graphx;

public abstract class Node<T> {
	protected T element;

	public Node(T element) {
		this.element = element;
	}

	public T getElement() {
		return element;
	}

	public abstract String toXML();

	public String key() {
		return element.toString();
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject.getClass() != getClass())
			return false;

		@SuppressWarnings("unchecked")
		Node<T> compare = (Node<T>) anObject;
		return (element.equals(compare.element));
	}
}
