package com.milaboratory.core.alignment;

import com.milaboratory.core.sequence.Alphabet;
import com.milaboratory.core.sequence.AminoAcidSequence;
import com.milaboratory.core.sequence.IncompleteAminoAcidSequence;

import java.io.IOException;
import java.io.InputStream;

import static com.milaboratory.core.alignment.ScoringMatrixIO.readAABlastMatrix;

/**
 * BLASTMatrix - enum of available BLAST AminoAcid substitution matrices
 */
public enum BLASTMatrix {
    BLOSUM45, BLOSUM50, BLOSUM62, BLOSUM80, BLOSUM90, PAM30, PAM70, PAM250;
    private volatile int[] iMatrix = null, matrix = null;

    public int[] getMatrix(Alphabet alphabet) {
        if (alphabet == AminoAcidSequence.ALPHABET) {
            if (matrix == null) {
                synchronized (this) {
                    if (matrix == null) {
                        try (InputStream stream = BLASTMatrix.class.getClassLoader().getResourceAsStream("matrices/" + this.name())) {
                            matrix = readAABlastMatrix(stream, AminoAcidSequence.ALPHABET, '_');
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            return matrix;
        } else if (alphabet == IncompleteAminoAcidSequence.ALPHABET) {
            if (iMatrix == null) {
                synchronized (this) {
                    if (iMatrix == null) {
                        try (InputStream stream = BLASTMatrix.class.getClassLoader().getResourceAsStream("matrices/" + this.name())) {
                            iMatrix = readAABlastMatrix(stream, IncompleteAminoAcidSequence.ALPHABET,
                                    '_', '.');
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            return iMatrix;
        } else
            throw new IllegalArgumentException("Unknown alphabet.");
    }
}
