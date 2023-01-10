class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	private T[] _storage;
	private int _numElements;

	// Constructor
	@SuppressWarnings("unchecked")
	public HeapImpl () {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	// double the size of the storage array
	@SuppressWarnings("unchecked")
	private void expandStorage() {
		T[] _newStorage = (T[]) new Comparable[_storage.length * 2];
		System.arraycopy(_storage, 0, _newStorage, 0, _storage.length);
		_storage = _newStorage;
	}

	// returns the inputted index's left child's index
	private int getLeftChildIndex(int index) {
		return index * 2 + 1;
	}
	// returns the inputted index's right child's index
	private int getRightChildIndex(int index) {
		return index * 2 + 2;
	}

	// returns the inputted index's parent's index
	private int getParentIndex(int index) {
		return (index-1) / 2;
	}

	// swap the elements at the two indices
	private void swap(int index1, int index2) {
		T temp = _storage[index1];
		_storage[index1] = _storage[index2];
		_storage[index2] = temp;
	}

	// bubble up the element at the given index
	// if the element is greater than its parent, swap them
	// and continue bubbling up
	private void bubbleUp(int index) {
		if (index == 0) {
			return;
		}
		int parentIndex = getParentIndex(index);
		if (_storage[index].compareTo(_storage[parentIndex]) > 0) {
			T temp = _storage[index];
			_storage[index] = _storage[parentIndex];
			_storage[parentIndex] = temp;
			bubbleUp(parentIndex);
		}
	}

	// trickle down the element at the given index
	// if the element is less than its children, swap it with the larger child,
	// then recursively trickle down the child
	void trickleDown (int index) {
		int _leftChild = getLeftChildIndex(index);
		int _rightChild = getRightChildIndex(index);
		/*       0
		       /   \
		      1     2
		     / \   / \
		    3   4 5   6
		    1 = (0*2)+1, 2 = (0*2)+1, 3 = (1*2)+1, 4 = (1*2)+2, ...,
		    n_left = (n_parent*2)+1, n_right = (n_parent*2)+2
		 */
 		int _largeChild = index; // index of the larger child
		if (_leftChild < _numElements && _storage[_leftChild].compareTo(_storage[_largeChild]) > 0) {
			_largeChild = _leftChild;
		}
		if (_rightChild < _numElements && _storage[_rightChild].compareTo(_storage[_largeChild]) > 0) {
			_largeChild = _rightChild;
		}
		if (_largeChild != index) {
			swap(index, _largeChild);
			trickleDown(_largeChild);
		}
	}

	// add an element to the heap
	public void add (T data) {
		if (_numElements == _storage.length) {
			expandStorage();
		}
		_storage[_numElements] = data;
		_numElements++;
		bubbleUp(_numElements-1);
	}

	// remove the first element in the heap
	public T removeFirst () {
		if (_numElements == 0) {
			return null;
		} else {
			T temp = _storage[0];
			_storage[0] = _storage[_numElements - 1];
			_numElements--;
			trickleDown(0);
			return temp;
		}
	}

	// return the number of elements in the heap
	public int size () {
		return _numElements;
	}
}
