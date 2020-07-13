/*
    Automatic elytra replacement with chestplace
    Copyright (C) 2020 Szum123321

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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