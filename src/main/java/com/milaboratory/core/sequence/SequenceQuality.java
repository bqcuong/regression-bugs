package com.milaboratory.core.sequence;

import com.milaboratory.core.Range;
import com.milaboratory.core.io.sequence.fastq.QualityFormat;
import com.milaboratory.core.io.sequence.fastq.WrongQualityFormat;
import com.milaboratory.util.ArraysUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class SequenceQuality implements Serializable {
    private static final long serialVersionUID = 1L;
    private final byte[] data;

    /**
     * Creates a phred sequence quality from a Sanger formatted quality string (33 based).
     *
     * @param string
     */
    public SequenceQuality(String string) {
        this(string, 33);
    }

    /**
     * Creates a phred sequence quality from a string formatted with corresponding offset.
     *
     * @param string string
     */
    public SequenceQuality(String string, int offset) {
        this.data = string.getBytes();
        for (int i = this.data.length - 1; i >= 0; --i)
            this.data[i] -= offset;
    }

    /**
     * Creates a phred sequence quality from a string formatted with corresponding offset.
     *
     * @param string string
     */
    public SequenceQuality(String string, QualityFormat format) {
        this(string, format.getOffset());
    }


    /**
     * Creates quality object from raw quality score values.
     *
     * @param data raw quality score values
     */
    public SequenceQuality(byte[] data) {
        this.data = data.clone();
    }

    /**
     * Constructor for factory method.
     */
    SequenceQuality(byte[] data, boolean unsafe) {
        assert unsafe;
        this.data = data;
    }


    public byte[] getInnerData() {
        return data.clone();
    }

    /**
     * Get the log10 of probability of error (e.g. nucleotide substitution) at given sequence point
     *
     * @param position positioninate in sequence
     * @return log10 of probability of error
     */
    public float log10ProbabilityOfErrorAt(int position) {
        return -((float) data[position]) / 10;
    }

    /**
     * Get probability of error (e.g. nucleotide substitution) at given sequence point
     *
     * @param position positioninate in sequence
     * @return probability of error
     */
    public float probabilityOfErrorAt(int position) {
        return (float) Math.pow(10.0, -(data[position]) / 10);
    }

    /**
     * Get the raw sequence quality value (in binary format) at given sequence point
     *
     * @param position positioninate in sequence
     * @return raw sequence quality value
     */
    public byte value(int position) {
        return data[position];
    }

    /**
     * Returns the worst sequence quality value
     *
     * @return worst sequence quality value
     */
    public byte minValue() {
        byte min = Byte.MAX_VALUE;
        for (byte b : data)
            if (b < min)
                min = b;
        return min;
    }

    /**
     * Gets quality values in reverse order
     *
     * @return quality values in reverse order
     */
    public SequenceQuality reverse() {
        return new SequenceQuality(reverseCopy(data), true);
    }

    /**
     * Returns substring of current quality scores line.
     *
     * @param from inclusive
     * @param to   exclusive
     * @return substring of current quality scores line
     */
    public SequenceQuality getRange(int from, int to) {
        return getRange(new Range(from, to));
    }

    /**
     * Returns substring of current quality scores line.
     *
     * @param range range
     * @return substring of current quality scores line
     */
    public SequenceQuality getRange(Range range) {
        byte[] rdata = Arrays.copyOfRange(data, range.getLower(), range.getUpper());
        if (range.isReverse())
            ArraysUtils.reverse(rdata);
        return new SequenceQuality(rdata, true);
    }

    /**
     * Returns size of quality array
     *
     * @return size of quality array
     */
    public int size() {
        return data.length;
    }

    /**
     * Encodes current quality line with given offset. Common values for offset are 33 and 64.
     *
     * @param offset offset
     * @return bytes encoded quality values
     */
    public void encodeTo(QualityFormat format, byte[] buffer, int offset) {
        byte vo = format.getOffset();
        for (int i = 0; i < data.length; ++i)
            buffer[offset++] = (byte) (data[i] + vo);
    }

    /**
     * Encodes current quality line with given offset. Common values for offset are 33 and 64.
     *
     * @param offset offset
     * @return bytes encoded quality values
     */
    public byte[] encode(int offset) {
        if (offset < 0 || offset > 70)
            throw new IllegalArgumentException();

        byte[] copy = new byte[data.length];
        for (int i = copy.length - 1; i >= 0; --i)
            copy[i] += data[i] + offset;
        return copy;
    }

    /**
     * Encodes current quality line with given format. Common values for offset are 33 and 64.
     *
     * @param format quality format with offset
     * @return bytes encoded quality values
     */
    public byte[] encode(QualityFormat format) {
        return encode(format.getOffset());
    }

    /**
     * Encodes current quality line with given offset. Common values for offset are 33 and 64.
     *
     * @param offset offset
     * @return encoded quality values
     */
    public String encodeToString(int offset) {
        return new String(encode(offset));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data) * 31 + 17;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequenceQuality that = (SequenceQuality) o;
        return Arrays.equals(data, that.data);
    }

    @Override
    public String toString() {
        return encodeToString(33);
    }

    /**
     * Creates a phred sequence quality containing only given values of quality.
     *
     * @param qualityValue value to fill the quality values with
     * @param length       size of quality string
     */
    public static SequenceQuality getUniformQuality(byte qualityValue, int length) {
        byte[] data = new byte[length];
        Arrays.fill(data, qualityValue);
        return new SequenceQuality(data, true);
    }

    /******************
     * STATIC METHODS
     *****************/

    /**
     * Helper method.
     */
    private static byte[] reverseCopy(byte[] quality) {
        byte[] newData = new byte[quality.length];
        int reverseposition = quality.length - 1;
        for (int position = 0; position < quality.length; ++position, --reverseposition)
            //reverseposition = quality.length - 1 - position;
            newData[position] = quality[reverseposition];

        assert reverseposition == -1;

        return newData;
    }

    /**
     * Factory method for the SequenceQualityPhred object. It performs all necessary range checks if required.
     *
     * @param format format of encoded quality values
     * @param data   byte with encoded quality values
     * @param from   starting position in {@code data}
     * @param length number of bytes to parse
     * @param check  determines whether range check is required
     * @return quality line object
     * @throws WrongQualityFormat if encoded value are out of range and checking is enabled
     */
    public static SequenceQuality create(QualityFormat format, byte[] data, int from, int length, boolean check) {
        if (from + length >= data.length || from < 0 || length < 0)
            throw new IllegalArgumentException();
        //For performance
        final byte valueOffset = format.getOffset(),
                minValue = format.getMinValue(),
                maxValue = format.getMaxValue();
        byte[] res = new byte[length];
        int pointer = from;
        for (int i = 0; i < length; i++) {
            res[i] = (byte) (data[pointer++] - valueOffset);

            if (check &&
                    (res[i] < minValue || res[i] > maxValue))
                throw new WrongQualityFormat(((char) (data[i])) + " [" + res[i] + "]");
        }
        return new SequenceQuality(res, true);
    }

    /**
     * Factory method for the SequenceQualityPhred object. It performs all necessary range checks if required.
     *
     * @param format format of encoded quality values
     * @param data   byte with encoded quality values
     * @param check  determines whether range check is required
     * @return quality line object
     * @throws WrongQualityFormat if encoded value are out of range and checking is enabled
     */
    public static SequenceQuality create(QualityFormat format, byte[] data, boolean check) {
        return create(format, data, 0, data.length, check);
    }

    public static void arraycopy(SequenceQuality src, int srcPos, byte[] dest, int destPos, int length) {
        System.arraycopy(src.data, srcPos, dest, destPos, length);
    }

}
