package test.extract.features;

import java.util.ArrayList;

import com.aliasi.sentences.IndoEuropeanSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

/**
 * This code has been integrated from:
 * Implements various readability indexes
 * http://code.google.com/p/panos-ipeirotis/source/browse/trunk/src/com/ipeirotis/readability/?r=2
 * @author Panos Ipeirotis
 */

/** Use SentenceModel to find sentence boundaries in text */
public class SentenceExtractor {

    final TokenizerFactory TOKENIZER_FACTORY = new IndoEuropeanTokenizerFactory();
    final SentenceModel SENTENCE_MODEL = new IndoEuropeanSentenceModel();

    public String[] getSentences(String text) {

        ArrayList<String> tokenList = new ArrayList<String>();
        ArrayList<String> whiteList = new ArrayList<String>();
        Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
        tokenizer.tokenize(tokenList, whiteList);

        String[] tokens = new String[tokenList.size()];
        String[] whites = new String[whiteList.size()];
        tokenList.toArray(tokens);
        whiteList.toArray(whites);
        int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens, whites);

        if (sentenceBoundaries.length < 1) {
            return new String[0];
        }

        String[] result = new String[sentenceBoundaries.length];

        int sentStartTok = 0;
        int sentEndTok = 0;
        for (int i = 0; i < sentenceBoundaries.length; ++i) {
            sentEndTok = sentenceBoundaries[i];
            // System.out.println("SENTENCE "+(i+1)+": ");
            StringBuffer sb = new StringBuffer();
            for (int j = sentStartTok; j <= sentEndTok; j++) {
                sb.append(tokens[j] + whites[j + 1]);
                
                // System.out.print(tokens[j]+whites[j+1]);
            }
            result[i] = sb.toString();
            // System.out.println();
            sentStartTok = sentEndTok + 1;
        }
        return result;
    }
    
}