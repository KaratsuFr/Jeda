package fr.ippon.tlse.persistence;

import java.util.Iterator;

public interface CursoWrapper<E> extends Iterator<E> {
	int count();

}
