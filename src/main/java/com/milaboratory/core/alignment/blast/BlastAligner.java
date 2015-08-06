package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.Range;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.mutations.Mutations;
import com.milaboratory.core.mutations.MutationsUtil;
import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public final class BlastAligner<S extends Sequence<S>> implements AutoCloseable {
    private static final String outfmt = "7 btop sstart send qstart qend score bitscore sseqid qseq sseq";
    private final Process process;
    private final PrintStream outputStream;
    private final int batchSize;
    private boolean closed = false;

    public BlastAligner(int batchSize, String blast, String... args) throws IOException {
        this.batchSize = batchSize;
        String[] args0 = new String[args.length + 3];
        args0[0] = blast;
        args0[1] = "-outfmt";
        args0[2] = outfmt;
        System.arraycopy(args, 0, args0, 3, args.length);
        ProcessBuilder pb = new ProcessBuilder(args0);
        pb.redirectErrorStream(false);
        pb.environment().put("BATCH_SIZE", Integer.toString(batchSize));
        this.process = pb.start();
        this.outputStream = new PrintStream(process.getOutputStream());
        this.outputStream.println(">");
    }

    @SuppressWarnings("unchecked")
    public BlastAlignmentResult<S>[] align(final S[] sequences) throws Exception {
        if (sequences.length > batchSize)
            throw new IllegalArgumentException("sequences.length > batchSize");
        if (closed)
            throw new IllegalStateException("Closed.");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (S s : sequences)
                    outputStream.println(s.toString() + "\n>");
                outputStream.flush();
                if (sequences.length < batchSize) {
                    closed = true;
                    outputStream.close();
                }
            }
        });
        t.start();
        String line;
        int i = 0;
        final BlastAlignmentResult<S>[] results = new BlastAlignmentResult[sequences.length];
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (i < sequences.length) {
            final List<BlastAlignmentHit<S>> hits = new ArrayList<>();
            int num = -1, done = 0;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.contains("hits found")) {
                        num = parseInt(line.replace("#", "").replace("hits found", "").trim());
                        if (num == 0)
                            break;
                    } else if (num != -1 && !line.startsWith("#")) {
                        hits.add(parseLine(line, sequences[i].getAlphabet()));
                        if (++done == num)
                            break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            results[i++] = new BlastAlignmentResult<>(hits);
        }
        if (closed)
            close();

        t.join();
        return results;
    }

    @Override
    public void close() throws Exception {
        closed = true;
        outputStream.close();
        process.getInputStream().close();
        process.waitFor();
    }

    private static <S extends Sequence<S>> BlastAlignmentHit<S> parseLine(String line, Alphabet<S> alphabet) {
        String[] fields = line.split("\t");
        int i = 0;
        //btop sstart send qstart qend score sseqid qseq sseq
        String btop = fields[i++],
                sstart = fields[i++],
                send = fields[i++],
                qstart = fields[i++],
                qend = fields[i++],
                score = fields[i++],
                bitscore = fields[i++],
                sseqid = fields[i++],
                qseq = fields[i++].replace("-", ""),
                sseq = fields[i++].replace("-", "");

        Mutations<S> mutations = new Mutations<>(alphabet, MutationsUtil.btopDecode(btop, alphabet));
        Alignment<S> alignment = new Alignment<>(alphabet.parse(sseq), mutations,
                new Range(0, sseq.length()), new Range(parseInt(qstart) - 1, parseInt(qend)),
                Float.parseFloat(bitscore));
        return new BlastAlignmentHit<>(sseqid, -1, alignment);
    }
}
