package com.milaboratory.core.io.sequence.fastq;

import com.milaboratory.core.io.CompressionType;
import com.milaboratory.core.io.sequence.*;
import com.milaboratory.core.sequence.UnsafeFactory;
import com.milaboratory.util.CanReportProgress;
import com.milaboratory.util.CountingInputStream;
import com.milaboratory.util.SmartProgressReporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */

public final class SingleFastqReader implements SingleReader, CanReportProgress {
    public static final int DEFAULT_BUFFER_SIZE = 524288;
    private static final byte DELIMITER = '\n';
    /**
     * Used to estimate progress
     */
    private long totalSize;
    /**
     * Used to estimate progress
     */
    private long readBytes;
    private final boolean lazyReads;
    private final QualityFormat format;
    private final CompressionType compressionType;
    private final CountingInputStream countingInputStream;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final InputStream inputStream;
    private final int bufferSize;
    private byte[] buffer;
    private int currentBufferSize;
    private int pointer;
    private int descriptionBegin, sequenceBegin, sequenceEnd, qualityBegin, qualityEnd;
    private long idCounter;


    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param file      file with reads
     * @param lazyReads allow lazy initialization of single reads
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(String file, boolean lazyReads) throws IOException {
        this(new FileInputStream(file), null, CompressionType.detectCompressionType(file),
                true, DEFAULT_BUFFER_SIZE, lazyReads);
    }

    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param file file with reads
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(String file) throws IOException {
        this(file, true);
    }

    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param file file with reads
     * @param ct   type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(String file, CompressionType ct) throws IOException {
        this(new FileInputStream(file), null, ct, true, DEFAULT_BUFFER_SIZE, true);
    }

    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param file   file with reads
     * @param format read quality encoding format
     * @param ct     type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(String file, QualityFormat format, CompressionType ct) throws IOException {
        this(new FileInputStream(file), format, ct, format == null, DEFAULT_BUFFER_SIZE, true);
    }

    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param file      file with reads
     * @param lazyReads allow lazy initialization of single reads
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(File file, boolean lazyReads) throws IOException {
        this(new FileInputStream(file), null, CompressionType.detectCompressionType(file),
                true, DEFAULT_BUFFER_SIZE, lazyReads);
    }

    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param file file with reads
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(File file) throws IOException {
        this(file, true);
    }


    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param file file with reads
     * @param ct   type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(File file, CompressionType ct) throws IOException {
        this(new FileInputStream(file), null, ct, true, DEFAULT_BUFFER_SIZE, true);
    }

    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param file   file with reads
     * @param format read quality encoding format
     * @param ct     type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(File file, QualityFormat format, CompressionType ct) throws IOException {
        this(new FileInputStream(file), format, ct, format == null, DEFAULT_BUFFER_SIZE, true);
    }

    /**
     * Creates a {@link SingleRead} stream from a FASTQ stream with single-end reads data
     *
     * @param stream stream with reads
     * @param ct     type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(InputStream stream, CompressionType ct) throws IOException {
        this(stream, null, ct, true, DEFAULT_BUFFER_SIZE, true);
    }

    /**
     * Creates a {@link SingleRead} stream from a FASTQ files with single-end read data
     *
     * @param stream stream with reads
     * @param format read quality encoding format
     * @param ct     type of compression (NONE, GZIP, etc)
     * @throws IOException in case there is problem with reading from files
     */
    public SingleFastqReader(InputStream stream, QualityFormat format, CompressionType ct) throws IOException {
        this(stream, format, ct, false, DEFAULT_BUFFER_SIZE, true);
    }

    /**
     * Creates a {@link SingleFastqReader} stream from a FASTQ files with single-end read data
     *
     * @param stream             stream with reads
     * @param format             read quality encoding format, if {@code guessQualityFormat} is true this value is used
     *                           as a default format
     * @param ct                 type of compression (NONE, GZIP, etc)
     * @param guessQualityFormat if true reader will try to guess quality string format, if guess fails {@code format}
     *                           will be used as a default quality string format, if {@code format==null} exception will
     *                           be thrown
     * @param bufferSize         size of buffer
     * @param lazyReads          specifies whether created reads should be lazy initialized
     * @throws java.io.IOException
     */
    public SingleFastqReader(InputStream stream, QualityFormat format, CompressionType ct,
                             boolean guessQualityFormat, int bufferSize, boolean lazyReads) throws IOException {
        this.bufferSize = bufferSize;
        this.compressionType = ct;
        this.lazyReads = lazyReads;
        //Check for null
        if (stream == null)
            throw new NullPointerException();

        if (stream instanceof FileInputStream)
            totalSize = ((FileInputStream) stream).getChannel().size();
        else
            totalSize = -1L;

        countingInputStream = new CountingInputStream(stream);
        //Initialization
        //Wrapping stream if un-compression needed
        inputStream = ct.createInputStream(countingInputStream, Math.max(bufferSize / 2, 2048));


        //Guessing quality format
        if (guessQualityFormat) {
            fillBuffer(DEFAULT_BUFFER_SIZE);
            QualityFormat f = guessFormat(); //Buffer minus ~ one read.
            pointer = 0;

            if (f != null)
                format = f;
        }

        if (format == null)
            if (guessQualityFormat)
                throw new RuntimeException("Format guess failed.");
            else
                throw new NullPointerException();

        this.format = format;
    }

    public SingleFastqReader setTotalSize(long totalSize) {
        this.totalSize = totalSize;
        return this;
    }

    @Override
    public double getProgress() {
        return totalSize == -1 ? Double.NaN : (1.0 * countingInputStream.getBytesRead() / totalSize);
    }

    @Override
    public boolean isFinished() {
        return closed.get();
    }

    @Override
    public synchronized SingleRead take() {
        if (closed.get())
            return null;


        try {
            if (!nextRecord(true))
                return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long id = idCounter++;
        if (lazyReads)
            return SingleReadLazy.createSingleRead(format,
                    id,
                    buffer,
                    descriptionBegin,
                    (short) (sequenceBegin - descriptionBegin),
                    (short) (qualityBegin - descriptionBegin),
                    (short) (sequenceEnd - sequenceBegin),
                    (short) (sequenceBegin - descriptionBegin - 1));
        else
            return new SingleReadImpl(id,
                    UnsafeFactory.fastqParse(buffer, sequenceBegin, qualityBegin, sequenceEnd - sequenceBegin, format.getOffset(), id),
                    new String(buffer, descriptionBegin, sequenceBegin - descriptionBegin - 1));
    }

    private void fillBuffer(int size) throws IOException {
        if (closed.get())
            return;
        byte[] newBuffer;
        if (lazyReads)
            newBuffer = new byte[size];//if lazy reads, we can not overwrite buffer content!
        else {
            if (buffer == null)
                buffer = new byte[size];
            if (buffer.length != size) //needed after automatic quality format guessing to shrink buffer size
                newBuffer = new byte[size];
            else
                newBuffer = buffer;
        }
        if (buffer != null) //buffer == null after initialization (in case of non lazy reads)
            System.arraycopy(buffer, pointer, newBuffer, 0, currentBufferSize - pointer);
        int readBytes = inputStream.read(newBuffer, currentBufferSize - pointer, newBuffer.length - currentBufferSize + pointer);
        currentBufferSize = (readBytes == -1 ? 0 : readBytes) + currentBufferSize - pointer;
        pointer = 0;
        buffer = newBuffer;
        if (readBytes == -1)
            close();
    }

    private boolean nextRecord(boolean refillBuffer) throws IOException {
        int pass = -1; //number of tries to fillBuffer

        while (true) {
            ++pass;
            if (pass == 2)// tried to fill buffer 2 times
                if (closed.get()) //no more data in file (close was invoked by fillBuffer)
                    throw new IllegalFileFormatException("Unexpected end of file.");
                else //buffer is smaller than length of record (seq + qual + descript )!
                    throw new IllegalFileFormatException("Too small buffer.");

            if (buffer == null) { //only after initialization
                if (!refillBuffer)
                    throw new RuntimeException();
                fillBuffer(bufferSize);
                continue;
            }

            if (currentBufferSize == 0)
                return false; //empty buffer (EOF reached)

            if (currentBufferSize == pointer) //all data in buffer is already processed
                if (refillBuffer) {
                    fillBuffer(bufferSize);
                    continue;
                } else return false;

            if (buffer[pointer] != '@') // fastq specification
                throw new IllegalFileFormatException();

            //standard fastq reading:

            int pointer = this.pointer;
            ++pointer;
            descriptionBegin = pointer;
            for (; pointer < currentBufferSize && buffer[pointer] != DELIMITER; ++pointer) ;
            if (pointer == buffer.length) {
                if (refillBuffer) {
                    fillBuffer(bufferSize);
                    continue;
                } else return false;
            }
            sequenceBegin = ++pointer;
            for (; pointer < currentBufferSize && buffer[pointer] != DELIMITER; ++pointer) ;
            if (pointer == buffer.length) {
                if (refillBuffer) {
                    fillBuffer(bufferSize);
                    continue;
                } else return false;
            }
            sequenceEnd = pointer++;
            if (pointer == buffer.length) {
                if (refillBuffer) {
                    fillBuffer(bufferSize);
                    continue;
                } else return false;
            }

            if (buffer[pointer] != '+')
                throw new IllegalFileFormatException();

            for (; pointer < currentBufferSize && buffer[pointer] != DELIMITER; ++pointer) ;
            if (pointer == buffer.length) {
                if (refillBuffer) {
                    fillBuffer(bufferSize);
                    continue;
                } else return false;
            }
            qualityBegin = ++pointer;

            for (; pointer < currentBufferSize && buffer[pointer] != DELIMITER; ++pointer) ;
            if (pointer == buffer.length) {
                if (refillBuffer) {
                    fillBuffer(bufferSize);
                    continue;
                } else return false;
            }
            qualityEnd = pointer;
            if (qualityEnd - qualityBegin != sequenceEnd - sequenceBegin)
                throw new IllegalFileFormatException("Quality and sequence have different sizes.");

            this.pointer = pointer + 1;
            return true;
        }
    }

    /**
     * Closes the output port
     */
    @Override
    public void close() {
        if (!closed.compareAndSet(false, true))
            return;

        //is synchronized with itself and _next calls,
        //so no synchronization on inner reader is needed
        try {
            synchronized (inputStream) {
                inputStream.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    private QualityFormat guessFormat() throws IOException {
        boolean signal33 = false, signal64 = false;
        int k, chr;

        while (nextRecord(false)) {

            for (k = qualityBegin; k < qualityEnd; ++k) {
                chr = (int) buffer[k];
                signal33 |= (chr - 64) < QualityFormat.Phred64.getMinValue();
                signal64 |= (chr - 33) > QualityFormat.Phred33.getMaxValue();
            }
        }
        //The file has bad format.
        //If any of formats is applicable file contains out of range values in any way.
        if (signal33 && signal64)
            return null;

        if (signal33)
            return QualityFormat.Phred33;
        if (signal64)
            return QualityFormat.Phred64;

        return null;
    }
}
