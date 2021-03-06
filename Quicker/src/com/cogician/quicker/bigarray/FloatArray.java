package com.cogician.quicker.bigarray;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.cogician.quicker.Checker;
import com.cogician.quicker.Quicker;

/**
 * <p>
 * Float array is type of {@linkplain BigArray} of a long length, accessed by index of <b>long</b> type, extension for
 * traditional array. Its component is float. For example:
 * 
 * <pre>
 * FloatArray array = new FloatArray(1024 * 1024 * 1024);
 * array.set(0L, 1);
 * array.get(0L);
 * ...
 * </pre>
 * </p>
 *
 * @author Fred Suvn
 * @version 0.0.0, 2016-03-17T14:43:39+08:00
 * @since 0.0.0, 2016-03-17T14:43:39+08:00
 * @see BigArray
 */
public class FloatArray extends BigArray implements Iterable<Float>, Cloneable {

    private static final long serialVersionUID = 1L;

    private static Data createData(long length, int[] dimensions, float initial, boolean lazy) {
        if (1 == dimensions.length) {
            return new OfOneDimension((int)length, initial);
        } else if (2 == dimensions.length) {
            return new OfTwoDimensions(length, dimensions, initial, lazy);
        } else {
            return new OfMultiDimensions(length, dimensions, initial, lazy);
        }
    }

    private FloatArray(Data data) {
        this.data = data;
    }

    /**
     * <p>
     * Constructs with specified length.
     * </p>
     * 
     * @param length
     *            specified length
     * @throws IllegalArgumentException
     *             if specified length < 0
     * @since 0.0.0
     */
    public FloatArray(long length) throws IllegalArgumentException {
        this(length, (float)0);
    }

    /**
     * <p>
     * Constructs with specified length and initial value.
     * </p>
     * 
     * @param length
     *            specified length
     * @param initial
     *            initial value
     * @throws IllegalArgumentException
     *             if specified length < 0
     * @since 0.0.0
     */
    public FloatArray(long length, float initial) throws IllegalArgumentException {
        this(length, BigArray.BLOCK_SIZE, initial, true);
    }

    /**
     * <p>
     * Constructs with specified length, specified block size, initial value and whether this instance is lazy.
     * </p>
     * 
     * @param length
     *            specified length
     * @param blockSize
     *            specified block size
     * @param initial
     *            initial value
     * @param lazy
     *            whether this instance is lazy
     * @throws IllegalArgumentException
     *             if specified length < 0 or specified block size <= 0
     * @since 0.0.0
     */
    public FloatArray(long length, int blockSize, float initial, boolean lazy) throws IllegalArgumentException {
        Checker.checkLength(length);
        Checker.checkPositive(blockSize);
        this.data = createData(length, BigArrayUtil.caculateDimensions(length, blockSize), initial, lazy);
    }

    /**
     * <p>
     * Constructs a big array wraps specified array.
     * </p>
     * 
     * @param array
     *            specified array
     * @throws NullPointerException
     *             if specified array is null
     * @since 0.0.0
     */
    public FloatArray(float[] array) throws NullPointerException {
        Checker.checkNull(array);
        this.data = new ArrayWrapper(array, 0, array.length);
    }

    /**
     * <p>
     * Constructs a big array wraps specified array between from index inclusive and to index exclusive.
     * </p>
     * 
     * @param array
     *            specified array
     * @param from
     *            from index inclusive
     * @param to
     *            to index exclusive
     * @throws NullPointerException
     *             if specified array is null
     * @throws IllegalArgumentException
     *             if from index > to index
     * @throws IndexOutOfBoundsException
     *             if index out of bounds
     * @since 0.0.0
     */
    public FloatArray(float[] array, int from, int to)
            throws NullPointerException, IllegalArgumentException, IndexOutOfBoundsException {
        Checker.checkNull(array);
        Checker.checkRangeIndexes(from, to, array.length);
        this.data = new ArrayWrapper(array, from, to);
    }

    private final Data data;

    @Override
    public long length() {
        return data.length();
    }

    /**
     * <p>
     * Gets value at specified index.
     * </p>
     * 
     * @param index
     *            specified index in bounds
     * @return value at specified index
     * @throws IndexOutOfBoundsException
     *             if out of bounds
     * @since 0.0.0
     */
    public float get(int index) throws IndexOutOfBoundsException {
        return data.get(index);
    }

    /**
     * <p>
     * Sets value at specified index.
     * </p>
     * 
     * @param index
     *            index specified index in bounds
     * @param value
     *            value to be set
     * @throws IndexOutOfBoundsException
     *             if out of bounds
     * @since 0.0.0
     */
    public void set(int index, float value) throws IndexOutOfBoundsException {
        data.set(index, value);
    }

    /**
     * <p>
     * Gets value at specified index.
     * </p>
     * 
     * @param index
     *            specified index in bounds
     * @return value at specified index
     * @throws IndexOutOfBoundsException
     *             if out of bounds
     * @since 0.0.0
     */
    public float get(long index) throws IndexOutOfBoundsException {
        return data.get(index);
    }

    /**
     * <p>
     * Sets value at specified index.
     * </p>
     * 
     * @param index
     *            index specified index in bounds
     * @param value
     *            value to be set
     * @throws IndexOutOfBoundsException
     *             if out of bounds
     * @since 0.0.0
     */
    public void set(long index, float value) throws IndexOutOfBoundsException {
        data.set(index, value);
    }

    /**
     * <p>
     * Fills elements of this array with specified value.
     * </p>
     * 
     * @param value
     *            specified value
     * @since 0.0.0
     */
    public void fill(float value) {
        data.fill(value);
    }

    /**
     * <p>
     * Copies data of this array into destination array. Copied length is minimum length of two array.
     * </p>
     *
     * @param dest
     *            destination array, not null
     * @throws NullPointerException
     *             if destination array is null
     * @since 0.0.0
     */
    public void copy(FloatArray dest) throws NullPointerException {
        copy(0L, dest, 0L, Math.min(length(), dest.length()));
    }

    /**
     * <p>
     * Copies data of this array into destination array.
     * </p>
     *
     * @param srcPos
     *            source start index in bounds
     * @param dest
     *            destination array, not null
     * @param destPos
     *            destination start index in bounds
     * @param length
     *            specified length, >= 0
     * @throws NullPointerException
     *             if destination array is null
     * @throws IndexOutOfBoundsException
     *             if indexes out of bounds
     * @throws IllegalArgumentException
     *             if specified length is negative
     *
     * @since 0.0.0
     */
    public void copy(long srcPos, FloatArray dest, long destPos, long length)
            throws NullPointerException, IndexOutOfBoundsException {
        Checker.checkIndex(srcPos, length());
        Checker.checkIndex(destPos, length());
        Checker.checkLength(length);
        data.copy(srcPos, dest.data, destPos, length);
    }

    /**
     * <p>
     * Clones this array, all settings and options inherited.
     * </p>
     * 
     * @return duplication of this array
     * @since 0.0.0
     */
    @Override
    public FloatArray clone() {
        return new FloatArray(data.clone());
    }

    /**
     * <p>
     * COnverts this array to an object array. If length of this array is greater than {@linkplain Integer#MAX_VALUE},
     * return first {@linkplain Integer#MAX_VALUE} elements.
     * </p>
     * 
     * @return an object array
     * @since 0.0.0
     */
    public float[] toArray() {
        return data.toArray();
    }

    /**
     * <p>
     * Returns an iterator to traverse this array.
     * </p>
     * 
     * @return an iterator to traverse this array
     * @since 0.0.0
     */
    @Override
    public Iterator<Float> iterator() {
        return new FloatArrayIterator(this);
    }

    /**
     * <p>
     * Returns a spliterator to traverse this array.
     * </p>
     *
     * @return a spliterator to traverse this array
     * @since 0.0.0
     */
    public Spliterator<Float> spliterator() {
        return new FloatArraySpliterator(this, 0, length());
    }

    /**
     * <p>
     * Returns a stream to traverse this array.
     * </p>
     *
     * @return a stream to traverse this array
     * @since 0.0.0
     */
    public Stream<Float> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    private static class FloatArrayIterator implements Iterator<Float> {

        private FloatArray array;

        private long cur = 0;

        public FloatArrayIterator(FloatArray array) {
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return cur < array.length();
        }

        @Override
        public Float next() {
            if (cur >= array.length()) {
                throw new NoSuchElementException();
            }
            return array.get(cur++);
        }
    }

    private static class FloatArraySpliterator implements Spliterator<Float> {

        private FloatArray array;

        private long end;

        private long cur;

        public FloatArraySpliterator(FloatArray array, long start, long end) {
            this.array = array;
            this.cur = start;
            this.end = end;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Float> action) {
            if (cur >= array.length()) {
                return false;
            }
            Quicker.require(action).accept(array.get(cur++));
            return true;
        }

        @Override
        public Spliterator<Float> trySplit() {
            if (end - cur > BigArray.UNSPLITERATED_SIZE) {
                return null;
            }
            long newStart = (end - cur) / 2;
            this.cur = newStart;
            return new FloatArraySpliterator(array, cur, newStart);
        }

        @Override
        public long estimateSize() {
            return end - cur;
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }

    }

    private static abstract class Data implements Serializable, Cloneable {

        private static final long serialVersionUID = 1L;

        public abstract int[] getDimensions();

        public abstract long length();

        public float get(int index) throws IndexOutOfBoundsException {
            return get((long)index);
        }

        public void set(int index, float value) throws IndexOutOfBoundsException {
            set((long)index, value);
        }

        public abstract float get(long index) throws IndexOutOfBoundsException;

        public abstract void set(long index, float value) throws IndexOutOfBoundsException;

        public abstract void fill(float value);

        public void copy(long srcPos, Data dest, long destPos, long length)
                throws NullPointerException, IndexOutOfBoundsException {
            if (length == 0) {
                return;
            }
            Checker.checkRangeIndexes(srcPos, srcPos + length, length());
            Checker.checkRangeIndexes(destPos, destPos + length, dest.length());
            int[] dimensions = getDimensions();
            int[] destDimensions = dest.getDimensions();
            int[] srcMultiIndexes = new int[dimensions.length];
            int[] destMultiIndexes = new int[destDimensions.length];
            int sWide = dimensions[dimensions.length - 1];
            int dWide = destDimensions[destDimensions.length - 1];
            for (long i = 0; i < length;) {
                BigArrayUtil.mapIndexes(srcPos + i, dimensions, srcMultiIndexes);
                float[] sr = (float[])BigArrayUtil.getLastDimension(getBackedArray(), srcMultiIndexes);
                int ys = (int)((srcPos + i) % sWide);
                BigArrayUtil.mapIndexes(destPos + i, destDimensions, destMultiIndexes);
                float[] dr = (float[])BigArrayUtil.getLastDimension(dest.getBackedArray(), destMultiIndexes);
                int yd = (int)((destPos + i) % dWide);
                int copyLength = Math.min(sWide - ys, dWide - yd);
                copyLength = (int)Math.min((long)copyLength, length - i);
                if (sr != null) {
                    if (dr == null) {
                        // One-dimensional backed array is never null.
                        dr = (float[])BigArrayUtil.alloc(dest.getBackedArray(), float.class, destDimensions,
                                destMultiIndexes);
                    }
                    System.arraycopy(sr, ys, dr, yd, copyLength);
                }
                i += copyLength;
            }
        }

        public abstract Data clone();

        public abstract float[] toArray();

        public abstract Object getBackedArray();
    }

    private static class ArrayWrapper extends Data {

        private static final long serialVersionUID = 1L;

        private final float[] source;

        private int from;

        private int length;

        public ArrayWrapper(float[] source, int from, int to) {
            this.source = source;
            this.from = from;
            this.length = to - from;
        }

        @Override
        public int[] getDimensions() {
            return null;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public float get(int index) throws IndexOutOfBoundsException {
            return source[index + from];
        }

        @Override
        public void set(int index, float value) throws IndexOutOfBoundsException {
            source[index + from] = value;
        }

        @Override
        public float get(long index) throws IndexOutOfBoundsException {
            return source[(int)index + from];
        }

        @Override
        public void set(long index, float value) throws IndexOutOfBoundsException {
            source[(int)index + from] = value;
        }

        @Override
        public void fill(float value) {
            Arrays.fill(source, from, from + length, value);
        }

        @Override
        public void copy(long srcPos, Data dest, long destPos, long length)
                throws NullPointerException, IndexOutOfBoundsException {
            if (dest instanceof OfOneDimension) {
                if (length == 0) {
                    return;
                }
                Checker.checkRangeIndexes(srcPos, srcPos + length, length());
                Checker.checkRangeIndexes(destPos, destPos + length, ((OfOneDimension)dest).source.length);
                System.arraycopy(source, (int)srcPos + from, ((OfOneDimension)dest).source, (int)destPos, (int)length);
            } else if (dest instanceof ArrayWrapper) {
                if (length == 0) {
                    return;
                }
                Checker.checkRangeIndexes(srcPos, srcPos + length, length());
                Checker.checkRangeIndexes(destPos, destPos + length, dest.length());
                System.arraycopy(source, (int)srcPos + from, ((ArrayWrapper)dest).source,
                        (int)destPos + ((ArrayWrapper)dest).from, (int)length);
            } else {
                super.copy(srcPos, dest, destPos, length);
            }
        }

        @Override
        public Data clone() {
            return new OfOneDimension(this);
        }

        @Override
        public float[] toArray() {
            float[] ar = (float[])Array.newInstance(float.class, length);
            System.arraycopy(source, from, ar, 0, length);
            return ar;
        }

        @Override
        public Object getBackedArray() {
            return source;
        }
    }

    /**
     * <p>
     * One dimension Implementation.
     * </p>
     *
     * @author Fred Suvn
     * @version 0.0.0, 2016-03-18T15:52:34+08:00
     * @since 0.0.0, 2016-03-18T15:52:34+08:00
     */
    private static class OfOneDimension extends Data {

        private static final long serialVersionUID = 1L;

        private final float[] source;

        private final int[] dimensions;

        public OfOneDimension(int length, float initial) {
            source = new float[length];
            if (0 != initial) {
                Arrays.fill(source, initial);
            }
            this.dimensions = new int[]{(int)source.length};
        }

        public OfOneDimension(OfOneDimension copyFrom) {
            this.source = copyFrom.source.clone();
            this.dimensions = new int[]{(int)source.length};
        }

        public OfOneDimension(Data copyFrom) {
            this((int)copyFrom.length(), (float)0);
            copyFrom.copy(0, this, 0, (int)copyFrom.length());
        }

        @Override
        public int[] getDimensions() {
            return dimensions;
        }

        @Override
        public long length() {
            return source.length;
        }

        @Override
        public float get(int index) throws IndexOutOfBoundsException {
            return source[index];
        }

        @Override
        public void set(int index, float value) throws IndexOutOfBoundsException {
            source[index] = value;
        }

        @Override
        public float get(long index) throws IndexOutOfBoundsException {
            return source[(int)index];
        }

        @Override
        public void set(long index, float value) throws IndexOutOfBoundsException {
            source[(int)index] = value;
        }

        @Override
        public void fill(float value) {
            Arrays.fill(source, value);
        }

        @Override
        public void copy(long srcPos, Data dest, long destPos, long length)
                throws NullPointerException, IndexOutOfBoundsException {
            if (dest instanceof OfOneDimension) {
                if (length == 0) {
                    return;
                }
                Checker.checkRangeIndexes(srcPos, srcPos + length, source.length);
                Checker.checkRangeIndexes(destPos, destPos + length, ((OfOneDimension)dest).source.length);
                System.arraycopy(source, (int)srcPos, ((OfOneDimension)dest).source, (int)destPos, (int)length);
            } else if (dest instanceof ArrayWrapper) {
                if (length == 0) {
                    return;
                }
                Checker.checkRangeIndexes(srcPos, srcPos + length, length());
                Checker.checkRangeIndexes(destPos, destPos + length, dest.length());
                System.arraycopy(source, (int)srcPos, ((ArrayWrapper)dest).source,
                        (int)destPos + ((ArrayWrapper)dest).from, (int)length);
            } else {
                super.copy(srcPos, dest, destPos, length);
            }
        }

        @Override
        public Data clone() {
            return new OfOneDimension(this);
        }

        @Override
        public float[] toArray() {
            float[] ar = (float[])Array.newInstance(float.class, source.length);
            System.arraycopy(source, 0, ar, 0, source.length);
            return ar;
        }

        @Override
        public Object getBackedArray() {
            return source;
        }
    }

    /**
     * <p>
     * Abstract two dimensions implementation.
     * </p>
     *
     * @author Fred Suvn
     * @version 0.0.0, 2016-03-22T10:18:37+08:00
     * @since 0.0.0, 2016-03-22T10:18:37+08:00
     */
    private static class OfTwoDimensions extends Data {

        private static final long serialVersionUID = 1L;

        private final long length;

        private final int[] dimensions;

        private final float[][] source;

        private float filler;

        private boolean lazy;

        public OfTwoDimensions(long length, int[] dimensions, float initial, boolean lazy) {
            this.length = length;
            this.dimensions = dimensions;
            this.lazy = lazy;
            if (lazy) {
                source = new float[dimensions[0]][];
            } else {
                source = new float[dimensions[0]][dimensions[1]];
            }
            fill(initial);
        }

        public OfTwoDimensions(OfTwoDimensions copyFrom) {
            this(copyFrom.length, copyFrom.dimensions, copyFrom.filler, copyFrom.lazy);
            copyFrom.copy(0, this, 0, length);
        }

        @Override
        public int[] getDimensions() {
            return dimensions;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public float get(long index) throws IndexOutOfBoundsException {
            Checker.checkIndex(index, length);
            int x = (int)(index / dimensions[1]);
            return null == source[x] ? filler : source[x][(int)(index % dimensions[1])];
        }

        @Override
        public void set(long index, float value) throws IndexOutOfBoundsException {
            Checker.checkIndex(index, length);
            int x = (int)(index / dimensions[1]);
            if (source[x] == null) {
                if (value == filler) {
                    return;
                } else {
                    source[x] = new float[dimensions[1]];
                    if (0 != filler) {
                        Arrays.fill(source[x], filler);
                    }
                }
            }
            int y = (int)(index % dimensions[1]);
            source[x][y] = value;
        }

        @Override
        public void fill(float value) {
            this.filler = value;
            for (int i = 0; i < source.length; i++) {
                fillBlock(i, value);
            }
        }

        private void fillBlock(int block, float value) {
            if (null == source[block]) {
                if (this.filler == value) {
                    return;
                }
                source[block] = new float[dimensions[1]];
            }
            Arrays.fill(source[block], value);
        }

        @Override
        public void copy(long srcPos, Data dest, long destPos, long length)
                throws NullPointerException, IndexOutOfBoundsException {
            if (dest instanceof OfTwoDimensions) {
                if (length == 0) {
                    return;
                }
                Checker.checkRangeIndexes(srcPos, srcPos + length, length());
                Checker.checkRangeIndexes(destPos, destPos + length, dest.length());
                OfTwoDimensions d = (OfTwoDimensions)dest;
                for (long i = 0; i < length;) {
                    int xs = (int)((srcPos + i) / dimensions[1]);
                    int ys = (int)((srcPos + i) % dimensions[1]);
                    int xd = (int)((destPos + i) / d.dimensions[1]);
                    int yd = (int)((destPos + i) % d.dimensions[1]);
                    int copyLength = Math.min(dimensions[1] - ys, d.dimensions[1] - yd);
                    copyLength = (int)Math.min((long)copyLength, length - i);
                    if (source[xs] != null) {
                        if (d.source[xd] == null) {
                            d.source[xd] = new float[d.dimensions[1]];
                        }
                        System.arraycopy(source[xs], ys, d.source[xd], yd, copyLength);
                    }
                    i += copyLength;
                }
            } else {
                super.copy(srcPos, dest, destPos, length);
            }
        }

        @Override
        public Data clone() {
            return new OfTwoDimensions(this);
        }

        @Override
        public float[] toArray() {
            return new OfOneDimension(this).toArray();
        }

        @Override
        public Object getBackedArray() {
            return source;
        }
    }

    /**
     * <p>
     * Multi-dimensions implementation.
     * </p>
     *
     * @author Fred Suvn
     * @version 0.0.0, 2016-03-18T15:52:54+08:00
     * @since 0.0.0, 2016-03-18T15:52:54+08:00
     */
    private static class OfMultiDimensions extends Data {

        private static final long serialVersionUID = 1L;

        private final long length;

        private final int[] dimensions;

        private final Object source;

        private float filler;

        private boolean lazy;

        public OfMultiDimensions(long length, int[] dimensions, float initial, boolean lazy) {
            this.length = length;
            this.dimensions = dimensions;
            this.lazy = lazy;
            if (lazy) {
                source = new Object[dimensions[0]];
            } else {
                source = Array.newInstance(float.class, dimensions);
            }
            fill(initial);
        }

        public OfMultiDimensions(OfMultiDimensions copyFrom) {
            this(copyFrom.length, copyFrom.dimensions, copyFrom.filler, copyFrom.lazy);
            copyFrom.copy(0, this, 0, length);
        }

        @Override
        public int[] getDimensions() {
            return dimensions;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public float get(long index) throws IndexOutOfBoundsException {
            Checker.checkIndex(index, length);
            int[] indexes = new int[dimensions.length];
            BigArrayUtil.mapMultiIndexes(index, dimensions, indexes);
            float[] array = (float[])BigArrayUtil.getLastDimension(source, indexes);
            if (array == null) {
                return filler;
            } else {
                return array[indexes[indexes.length - 1]];
            }
        }

        @Override
        public void set(long index, float value) throws IndexOutOfBoundsException {
            Checker.checkIndex(index, length);
            int[] indexes = new int[dimensions.length];
            BigArrayUtil.mapMultiIndexes(index, dimensions, indexes);
            float[] array = (float[])BigArrayUtil.getLastDimension(source, indexes);
            if (array == null) {
                if (value == filler) {
                    return;
                } else {
                    array = (float[])BigArrayUtil.alloc(source, float.class, dimensions, indexes);
                    if (0 != filler) {
                        Arrays.fill(array, filler);
                    }
                }
            }
            array[indexes[indexes.length - 1]] = value;
        }

        @Override
        public void fill(float value) {
            this.filler = value;
            fillMultiArray((Object[])source, value, 0);
        }

        private void fillMultiArray(Object[] array, float value, int dimension) {
            if (dimension == dimensions.length - 2) {
                for (int i = 0; i < array.length; i++) {
                    if (null == array[i]) {
                        if (this.filler == value) {
                            continue;
                        }
                        array[i] = new float[dimension + 1];
                    }
                    Arrays.fill((float[])array[i], value);
                }
            } else {
                for (int i = 0; i < array.length; i++) {
                    if (null == array[i]) {
                        if (this.filler == value) {
                            continue;
                        }
                        array[i] = new Object[dimension + 1];
                    }
                    fillMultiArray((Object[])array[i], value, dimension + 1);
                }
            }
        }

        @Override
        public Data clone() {
            return new OfMultiDimensions(this);
        }

        @Override
        public float[] toArray() {
            return new OfOneDimension(this).toArray();
        }

        @Override
        public Object getBackedArray() {
            return source;
        }
    }
}
