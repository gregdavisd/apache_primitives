/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections.primitives;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * An {@link BooleanList} backed by an array of <code>boolean</code>s. This
 * implementation supports all optional methods.
 *
 * @since Commons Primitives 1.1
 * @version $Revision$ $Date$
 */
public class ArrayBooleanList extends RandomAccessBooleanList
 implements Serializable {

 static final long serialVersionUID = 1L;

 // constructors
 //-------------------------------------------------------------------------
 /**
  * Construct an empty list with the default initial capacity.
  */
 public ArrayBooleanList() {
  this(8);
 }

 /**
  * Construct an empty list with the given initial capacity.
  *
  * @param initialCapacity
  * @throws IllegalArgumentException when <i>initialCapacity</i> is negative
  */
 public ArrayBooleanList(int initialCapacity) {
  if (initialCapacity < 0) {
   throw new IllegalArgumentException("capacity " + initialCapacity);
  }
  _data = new boolean[initialCapacity];
  _size = 0;
 }

 /**
  * Constructs a list containing the elements of the given collection, in the
  * order they are returned by that collection's iterator.
  *
  * @see ArrayBooleanList#addAll(BooleanCollection)
  * @param that the non-<code>null</code> collection of <code>boolean</code>s to
  * add
  * @throws NullPointerException if <i>that</i> is <code>null</code>
  */
 public ArrayBooleanList(BooleanCollection that) {
  this(that.size());
  addAll(that);
 }

 /**
  * Constructs a list by copying the specified array.
  *
  * @param array the array to initialize the collection with
  * @throws NullPointerException if the array is <code>null</code>
  */
 public ArrayBooleanList(boolean[] array) {
  this(array.length);
  System.arraycopy(array, 0, _data, 0, array.length);
  _size = array.length;
 }

 // BooleanList methods
 //-------------------------------------------------------------------------
 @Override
 public boolean get(int index) {
  checkRange(index);
  return _data[index];
 }

 @Override
 public int size() {
  return _size;
 }

 /**
  * Removes the element at the specified position in (optional operation). Any
  * subsequent elements are shifted to the left, subtracting one from their
  * indices. Returns the element that was removed.
  *
  * @param index the index of the element to remove
  * @return the value of the element that was removed
  *
  * @throws UnsupportedOperationException when this operation is not supported
  * @throws IndexOutOfBoundsException if the specified index is out of range
  */
 @Override
 public boolean removeElementAt(int index) {
  checkRange(index);
  incrModCount();
  boolean oldval = _data[index];
  int numtomove = _size - index - 1;
  if (numtomove > 0) {
   System.arraycopy(_data, index + 1, _data, index, numtomove);
  }
  _size--;
  return oldval;
 }

 /**
  * Replaces the element at the specified position in me with the specified
  * element (optional operation).
  *
  * @param index the index of the element to change
  * @param element the value to be stored at the specified position
  * @return the value previously stored at the specified position
  *
  * @throws UnsupportedOperationException when this operation is not supported
  * @throws IndexOutOfBoundsException if the specified index is out of range
  */
 @Override
 public boolean set(int index, boolean element) {
  checkRange(index);
  incrModCount();
  boolean oldval = _data[index];
  _data[index] = element;
  return oldval;
 }

 /**
  * Inserts the specified element at the specified position (optional
  * operation). Shifts the element currently at that position (if any) and any
  * subsequent elements to the right, increasing their indices.
  *
  * @param index the index at which to insert the element
  * @param element the value to insert
  *
  * @throws UnsupportedOperationException when this operation is not supported
  * @throws IllegalArgumentException if some aspect of the specified element
  * prevents it from being added to me
  * @throws IndexOutOfBoundsException if the specified index is out of range
  */
 @Override
 public void add(int index, boolean element) {
  checkRangeIncludingEndpoint(index);
  incrModCount();
  ensureCapacity(_size + 1);
  int numtomove = _size - index;
  System.arraycopy(_data, index, _data, index + 1, numtomove);
  _data[index] = element;
  _size++;
 }

 @Override
 public void clear() {
  incrModCount();
  _size = 0;
 }

 @Override
 public boolean addAll(BooleanCollection collection) {
  return addAll(size(), collection);
 }

 @Override
 public boolean addAll(int index, BooleanCollection collection) {
  if (collection.size() == 0) {
   return false;
  }
  checkRangeIncludingEndpoint(index);
  incrModCount();
  ensureCapacity(_size + collection.size());
  if (index != _size) {
   // Need to move some elements
   System.arraycopy(_data, index, _data, index + collection.size(), _size
    - index);
  }
  for (BooleanIterator it = collection.iterator(); it.hasNext();) {
   _data[index] = it.next();
   index++;
  }
  _size += collection.size();
  return true;
 }

 // capacity methods
 //-------------------------------------------------------------------------
 /**
  * Increases my capacity, if necessary, to ensure that I can hold at least the
  * number of elements specified by the minimum capacity argument without
  * growing.
  *
  * @param mincap
  */
 public void ensureCapacity(int mincap) {
  incrModCount();
  if (mincap > _data.length) {
   int newcap = (_data.length * 3) / 2 + 1;
   boolean[] olddata = _data;
   _data = new boolean[newcap < mincap ? mincap : newcap];
   System.arraycopy(olddata, 0, _data, 0, _size);
  }
 }

 /**
  * Reduce my capacity, if necessary, to match my current {@link #size size}.
  */
 public void trimToSize() {
  incrModCount();
  if (_size < _data.length) {
   boolean[] olddata = _data;
   _data = new boolean[_size];
   System.arraycopy(olddata, 0, _data, 0, _size);
  }
 }

 // private methods
 //-------------------------------------------------------------------------
 private void writeObject(ObjectOutputStream out) throws IOException {
  out.defaultWriteObject();
  out.writeInt(_data.length);
  for (int i = 0; i < _size; i++) {
   out.writeBoolean(_data[i]);
  }
 }

 private void readObject(ObjectInputStream in) throws IOException,
  ClassNotFoundException {
  in.defaultReadObject();
  _data = new boolean[in.readInt()];
  for (int i = 0; i < _size; i++) {
   _data[i] = in.readBoolean();
  }
 }

 private void checkRange(int index) {
  if (index < 0 || index >= _size) {
   throw new IndexOutOfBoundsException(
    "Should be at least 0 and less than " + _size + ", found " + index);
  }
 }

 private void checkRangeIncludingEndpoint(int index) {
  if (index < 0 || index > _size) {
   throw new IndexOutOfBoundsException(
    "Should be at least 0 and at most " + _size + ", found " + index);
  }
 }

 /**
  * Returns the array containing all of my elements and clears this list. The
  * length of the returned array will be equal to my {@link #size size}.
  * <p>
  * The returned array is independent of the list. After calling this function
  * this list will contain no elements.
  * <p>
  * When I guarantee the order in which elements are returned by an
  * {@link #iterator iterator}, the returned array will contain elements in the
  * same order.
  *
  * @return an array containing all the elements
  */
 public boolean[] toBackedArray() {
  trimToSize();
  boolean[] old_data = _data;
  _size = 0;
  _data = new boolean[0];
  return old_data;
 }

 // attributes
 //-------------------------------------------------------------------------
 private transient boolean[] _data = null;
 private int _size = 0;
}
