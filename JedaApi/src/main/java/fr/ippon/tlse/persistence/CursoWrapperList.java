package fr.ippon.tlse.persistence;

import java.util.Iterator;
import java.util.List;

public class CursoWrapperList<E> implements CursoWrapper<E> {

	private Iterator<E>	it;

	private int			count;

	public CursoWrapperList(List<E> lstValue) {
		it = lstValue.iterator();
		count = lstValue.size();
	}

	@Override
	public boolean hasNext() {
		return it.hasNext();
	}

	@Override
	public E next() {
		return it.next();
	}

	@Override
	public int count() {
		return count;
	}

}
