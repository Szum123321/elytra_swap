package net.szum123321.elytra_swap.data;

import net.minecraft.util.Pair;

public class MutablePair <A, B> {
	private A first;
	private B last;

	public MutablePair(A first, B last) {
		this.first = first;
		this.last = last;
	}

	public A getFirst() {
		return this.first;
	}

	public B getLast() {
		return this.last;
	}

	public void setFirst(A first) {
		this.first = first;
	}

	public void setLast(B last) {
		this.last = last;
	}

	public Pair<A, B> toImmutable() {
		return new Pair<>(first, last);
	}
}