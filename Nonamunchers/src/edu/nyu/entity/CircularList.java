package edu.nyu.entity;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class CircularList <E> {

	List<E> list;

	Iterator<E> it;

	public CircularList(List<E> list) {
		this.list = list;
		it = list.iterator();
	}

	public E next() {
		if (!it.hasNext()) {
			it = list.iterator();
		}
	    return it.next();
	}

	public E next(E elem) {
		Iterator<E> iterator = list.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().equals(elem)) {
				E result = null;
				if (iterator.hasNext()) {
					result = iterator.next();
				} else {
					iterator = list.iterator();
					result = iterator.next();
				}
				it = iterator;
				return result;
			}
		}
		throw new NoSuchElementException();
	}

	public int size() {
		return list.size();
	}

	public boolean insert(E elem, E elem1, E elem2) {
		if (next(elem1).equals(elem2)) {
			int index = list.indexOf(elem2);
			list.add(index, elem);
			return true;
		} else if (next(elem2).equals(elem1)) {
			int index = list.indexOf(elem1);
			list.add(index, elem);
			return true;
		}
		return false;
	}

	public boolean insert(E elem, E elem1) {
		int i = list.indexOf(elem1);
		if (i < 0) {
			return false;
		}
		list.add(i, elem);
		return true;
	}

	public boolean contains(E elem) {
		return list.contains(elem);
	}


	public boolean remove(E elem) {
		return list.remove(elem);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CircularList<E> other = (CircularList<E>) obj;
		if (size() != other.size()) {
			return false;
		}
		int count = 0;
		if (size() == 1) {
			if (!next().equals(other.next())) {
				return false;
			}
		} else if (size() > 1) {
			E objBegin = other.next();
			E objSecond = other.next();
			E thisBegin = next(objBegin);
			if (thisBegin != null && thisBegin.equals(objSecond)) {
				count = 2;
				while (count < other.size()) {
					count++;
					if (!next().equals(other.next())) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		
		return true;
	}

	public List<E> getList() {
		return Collections.unmodifiableList(list);
	}

	@Override
	public String toString() {
		//return "CircularList [list=" + list + "]";
		StringBuffer sb = new StringBuffer();
		for (E e : list) {
			sb.append(e.toString().charAt(0));
		}
		return sb.toString();
	}
	
	public String toString(E elem) {
		if (elem == null) {
			return toString();
		}
		int index = list.indexOf(elem);
		if (index >= 0) {
			List<E> newList = new LinkedList<E>();
			int count = list.size();
			while (count > 0) {
				newList.add(list.get(index));

				if (index < list.size() - 1) {
					index++;
				} else {
					index = 0;
				}
				count--;
			}
		    //return "CircularList [list=" + newList + "]";
			StringBuffer sb = new StringBuffer();
			for (E e : newList) {
				sb.append(e.toString().charAt(0));
			}
			return sb.toString();
		}
		return null;
	}

	public static void main(String[] args) {
		List<String> list = new LinkedList<String>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		List<String> list1 = new LinkedList<String>();
		list1.add("aaa");

		list1.add("ccc");
		list1.add("bbb");






		CircularList<String> c1 = new CircularList<String>(list);
		CircularList<String> c2 = new CircularList<String>(list1);
		System.out.println(c1.equals(c2));

	}

}
