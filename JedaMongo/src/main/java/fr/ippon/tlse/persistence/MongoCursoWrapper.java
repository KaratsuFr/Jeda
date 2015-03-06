package fr.ippon.tlse.persistence;

import org.jongo.MongoCursor;

public class MongoCursoWrapper<E> implements CursoWrapper<E> {

	private MongoCursor<E>	cur;

	public MongoCursoWrapper(MongoCursor<E> cur) {
		this.cur = cur;
	}

	@Override
	public boolean hasNext() {
		return cur.hasNext();
	}

	@Override
	public E next() {
		return cur.next();
	}

	@Override
	public int count() {
		return cur.count();
	}
}
