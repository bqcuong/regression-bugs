package com.milaboratory.core.io.sequence.fasta;

import cc.redberry.pipe.OutputPortCloseable;
import com.milaboratory.core.io.sequence.IllegalFileFormatException;
import com.milaboratory.core.io.sequence.SingleRead;
import com.milaboratory.core.io.sequence.SingleReadImpl;
import com.milaboratory.core.io.sequence.SingleReader;
import com.milaboratory.core.sequence.*;
import com.milaboratory.util.Bit2Array;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.milaboratory.core.sequence.NucleotideSequence.ALPHABET;

/**
 * FASTA reader for nucleotide sequences.
 *
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class FastaReader implements SingleReader, OutputPortCloseable<SingleRead> {
    static final byte DEFAULT_WILDCARD = NucleotideAlphabet.A;
    private final AtomicBoolean closed = new AtomicBoolean(false);
    //lets read line by line
    private BufferedReader reader;
    private final boolean withWildcards;
    private String bufferedLine;
    private long id;

    /**
     * Creates reader from the specified input stream.
     *
     * @param inputStream   input stream
     * @param withWildcards if {@code true}, then for each wildcard a
     *                      uniformly distributed nucleotide will be generated from the set of nucleotides
     *                      corresponding to this wildcard.
     */
    public FastaReader(InputStream inputStream, boolean withWildcards) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.withWildcards = withWildcards;
    }

    /**
     * Creates reader from the specified file.
     *
     * @param file          file
     * @param withWildcards if {@code true}, then for each wildcard a
     *                      uniformly distributed nucleotide will be generated from the set of nucleotides
     *                      corresponding to this wildcard.
     */
    public FastaReader(String file, boolean withWildcards) throws FileNotFoundException {
        this(new FileInputStream(file), withWildcards);
    }

    /**
     * Creates reader from the specified file.
     *
     * @param file          file
     * @param withWildcards if {@code true}, then for each wildcard a
     *                      uniformly distributed nucleotide will be generated from the set of nucleotides
     *                      corresponding to this wildcard.
     */
    public FastaReader(File file, boolean withWildcards) throws FileNotFoundException {
        this(new FileInputStream(file), withWildcards);
    }

    @Override
    public synchronized SingleRead take() {
        Item item;
        try {
            item = nextItem();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (item == null)
            return null;

        SingleRead read = new SingleReadImpl(id, getSequenceWithQuality(item.sequence), item.description);
        ++id;//not move upper! id is used in #getSequenceWithQuality(...)
        return read;
    }

    private NSequenceWithQuality getSequenceWithQuality(String sequence) {
        byte[] qualityData = new byte[sequence.length()];
        Arrays.fill(qualityData, SequenceQuality.GOOD_QUALITY_VALUE);

        Bit2Array sequenceData = new Bit2Array(sequence.length());
        byte nucleotide;
        char symbol;
        for (int i = 0; i < sequence.length(); ++i) {
            symbol = sequence.charAt(i);
            nucleotide = ALPHABET.codeFromSymbol(symbol);
            if (nucleotide == -1) //wildChard
            {
                if (withWildcards) {
                    WildcardSymbol wildcard = ALPHABET.getWildcardFor(symbol);
                    if (wildcard == null)
                        throw new IllegalFileFormatException("Unknown wildcard: " + symbol + ".");
                    nucleotide = wildcard.getUniformlyDistributedSymbol(id);
                } else
                    nucleotide = DEFAULT_WILDCARD;
                qualityData[i] = SequenceQuality.BAD_QUALITY_VALUE;
            }
            sequenceData.set(i, nucleotide);
        }

        return new NSequenceWithQuality(new NucleotideSequence(sequenceData),
                new SequenceQuality(qualityData));
    }

    private Item nextItem() throws IOException {
        String description;
        if (bufferedLine != null)
            description = bufferedLine;
        else {
            description = reader.readLine();
            if (description == null)
                return null;
            if (description.charAt(0) != '>')
                throw new IllegalFileFormatException("Wrong FASTA format.");
        }
        StringBuilder sequence = new StringBuilder();
        String line;
        while (true) {
            line = reader.readLine();
            if (line == null)
                break;
            if (!line.isEmpty() && line.charAt(0) == '>')
                break;
            sequence.append(line);
        }
        bufferedLine = line;
        return new Item(description.substring(1), sequence.toString());
    }

    /**
     * Closes the reader
     */
    @Override
    public void close() {
        if (!closed.compareAndSet(false, true))
            return;

        //is synchronized with itself and _next calls,
        //so no synchronization on innerReader is needed
        try {
            synchronized (reader) {
                reader.close();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static final class Item {
        final String description;
        final String sequence;

        private Item(String description, String sequence) {
            this.description = description;
            this.sequence = sequence;
        }
    }
}
