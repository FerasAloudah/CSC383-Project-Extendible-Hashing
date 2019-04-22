package core;

import java.util.List;

public interface Map<K extends Comparable<K>, T> {

	boolean empty();
	boolean full();
	T retrieve();
	void update(T e);
	boolean find(K key);
	boolean insert(K key, T data);
	boolean remove(K key);
	List<T> toList();

}
