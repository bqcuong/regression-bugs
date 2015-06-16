package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.Range;
import com.milaboratory.core.alignment.Alignment;
import com.milaboratory.core.alignment.BatchAligner;
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
public final class BlastAligner<S extends Sequence<S>> implements BatchAligner<S>, AutoCloseable {
    private static final String outfmt = "7 btop sstart send qstart qend score sseqid qseq sseq";
    private final Process process;
    private final PrintStream outputStream;
    private final Alphabet<S> alphabet;

    public BlastAligner(Alphabet<S> alphabet, String[] args) throws IOException {
        this.alphabet = alphabet;
        ProcessBuilder pb = new ProcessBuilder("blastn", "-db", "/Users/poslavsky/Projects/milab/blast/16SMicrobial", "-outfmt", outfmt);
        pb.redirectErrorStream(false);
        pb.environment().put("BATCH_SIZE", "1");
        this.process = pb.start();
        this.outputStream = new PrintStream(process.getOutputStream());
        this.outputStream.println(">");
    }

    @Override
    public BlastAlignmentResult<S> align(final S sequence) {
        outputStream.println(sequence.toString() + "\n>");
        outputStream.flush();

        String line;
        int num = -1, done = 0;

        final List<BlastAlignmentHit<S>> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        try {
            while ((line = reader.readLine()) != null) {
                if (line.contains("hits found"))
                    num = parseInt(line.replace("#", "").replace("hits found", "").trim());
                else if (num != -1 && !line.startsWith("#")) {
                    result.add(parseLine(line, alphabet));
                    if (++done == num)
                        break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new BlastAlignmentResult<>(result);
    }

    @Override
    public void close() throws Exception {
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
                sseqid = fields[i++],
                qseq = fields[i++].replace("-", ""),
                sseq = fields[i++].replace("-", "");

        Mutations<S> mutations = new Mutations<S>(alphabet, MutationsUtil.btopDecode(btop, alphabet));
        //.move(parseInt(sstart));


        Alignment<S> alignment = new Alignment<>(alphabet.parse(sseq), mutations,
                new Range(0, sseq.length()), new Range(parseInt(qstart) - 1, parseInt(qend)),
                Float.parseFloat(score));
        return new BlastAlignmentHit<>(sseqid, -1, alignment);
    }
}
