package com.milaboratory.core.alignment.blast;

import com.milaboratory.core.io.sequence.fasta.FastaWriter;
import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.Sequence;
import com.milaboratory.util.RandomUtil;
import com.milaboratory.util.TempFileManager;

import java.nio.file.Path;
import java.util.List;

public final class BlastDBBuilder {
    private static final String RECORD_PREFIX = "RECORD";
    private static Path blastDbFolder = null;

    private static synchronized Path getTmpDBPath() {
        if (blastDbFolder == null)
            blastDbFolder = TempFileManager.getTempDir().toPath();
        return blastDbFolder;
    }

    public static String getId(int id) {
        return RECORD_PREFIX + id;
    }

    public static <S extends Sequence<S>> BlastDB build(List<S> sequences) {
        return build(sequences, null, true);
    }

    //TODO caching etc..
    private static <S extends Sequence<S>> BlastDB build(List<S> sequences, Path path, boolean tmp) {
        if (sequences.isEmpty())
            throw new IllegalArgumentException("No records.");

        if (path == null)
            path = getTmpDBPath();

        Alphabet<S> alphabet = sequences.get(0).getAlphabet();

        try {
            String name = RandomUtil.getThreadLocalRandomData().nextHexString(40);
            String fullName = path.resolve(name).toString();
            Process proc = Blast.getProcessBuilder(Blast.CMD_MAKEBLASTDB, "-dbtype", Blast.toBlastAlphabet(alphabet),
                    "-out", fullName, "-title", name).start();
            FastaWriter<S> writer = new FastaWriter<>(proc.getOutputStream(), FastaWriter.DEFAULT_MAX_LENGTH);
            for (int i = 0; i < sequences.size(); i++)
                writer.write(getId(i), sequences.get(i));
            writer.close();
            if (proc.waitFor() != 0)
                throw new RuntimeException("Something goes wrong.");
            return BlastDB.get(fullName, tmp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
