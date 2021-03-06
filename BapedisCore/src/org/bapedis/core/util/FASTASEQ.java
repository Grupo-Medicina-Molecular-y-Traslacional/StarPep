/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.project.ProjectManager;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.io.BufferedReaderBytesRead;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class FASTASEQ {

    protected static int MAX_LENGTH = -1;
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public static List<ProteinSequence> load(String fasta) throws Exception{
        return load(new StringReader(fasta));
    }

    public static List<ProteinSequence> load(File inputFile) throws Exception {
        FileInputStream inStream = new FileInputStream(inputFile);
        InputStreamReader isr = new InputStreamReader(inStream);
        return load(isr);
    }

    public static List<ProteinSequence> load(Reader reader) throws Exception {
        try {
            BufferedReaderBytesRead br = new BufferedReaderBytesRead(reader);
            GenericFastaHeaderParser headerParser = new GenericFastaHeaderParser<>();
            ProteinSequenceCreator sequenceCreator = new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet());
            List<ProteinSequence> entries = new LinkedList<>();
            String header = "";
            long sequenceIndex = 0;
            StringBuilder sb = new StringBuilder();
            long fileIndex = br.getBytesRead();
            String line = br.readLine();
            if (line.charAt(0) != '>') {
                throw new IOException(NbBundle.getMessage(FASTASEQ.class, "FASTAfile.invalidFirstLine"));
            }

            boolean keepGoing = true;
            do {
                line = line.trim(); // nice to have but probably not needed
                if (line.length() != 0) {
                    if (line.startsWith(">")) {//start of new fasta record
                        if (sb.length() > 0) {//i.e. if there is already a sequence before
                            //    logger.debug("Sequence index=" + sequenceIndex);                                
                            if (MAX_LENGTH > 0 && sb.length() > MAX_LENGTH) {
                                throw new Exception(NbBundle.getMessage(FASTASEQ.class, "FASTAfile.invalidSeqLength", header, MAX_LENGTH));
                            }

                            try {
                                @SuppressWarnings("unchecked")
                                ProteinSequence sequence = (ProteinSequence) sequenceCreator.getSequence(sb.toString(), sequenceIndex);
                                headerParser.parseHeader(header, sequence);
                                entries.add(sequence);
                            } catch (CompoundNotFoundException e) {
                                throw new CompoundNotFoundException(NbBundle.getMessage(FASTASEQ.class, "FASTAfile.compoundNotFound", header, e.getMessage()));
                            }

                            sb.setLength(0); //this is faster, better memory utilization (same buffer)
                        }
                        header = line.substring(1);
                    } else if (line.startsWith(";")) {
                    } else {
                        //mark the start of the sequence with the fileIndex before the line was read
                        if (sb.length() == 0) {
                            sequenceIndex = fileIndex;
                        }
                        sb.append(line);
                    }
                }
                fileIndex = br.getBytesRead();
                line = br.readLine();
                if (line == null) {//i.e. EOF
                    String seq = sb.toString();
                    if (seq.length() == 0) {
                        pc.reportMsg("warning: Can't parse sequence. Got sequence of length 0!. Sequence index: " + sequenceIndex, pc.getCurrentWorkspace());
                        pc.reportMsg("header: " + header, pc.getCurrentWorkspace());
                    }
                    //    logger.debug("Sequence index=" + sequenceIndex + " " + fileIndex );
                    if (MAX_LENGTH > 0 && sb.length() > MAX_LENGTH) {
                        throw new Exception(NbBundle.getMessage(FASTASEQ.class, "FASTAfile.invalidSeqLength", header, MAX_LENGTH));
                    }

                    try {
                        @SuppressWarnings("unchecked")
                        ProteinSequence sequence = (ProteinSequence) sequenceCreator.getSequence(seq, sequenceIndex);
                        headerParser.parseHeader(header, sequence);
                        entries.add(sequence);
                    } catch (Exception e) {
                        throw new CompoundNotFoundException(NbBundle.getMessage(FASTASEQ.class, "FASTAfile.compoundNotFound", header, e.getMessage()));
                    }
                    keepGoing = false;
                }
            } while (keepGoing);

            return entries;
        } catch (CompoundNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new Exception(NbBundle.getMessage(FASTASEQ.class, "FASTAfile.badInput", ex.getMessage()));
        }
    }
    
    public static String asFASTA(List<ProteinSequence> entries) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FastaWriterHelper.writeProteinSequence(baos, entries);
        return baos.toString();
    }
}
