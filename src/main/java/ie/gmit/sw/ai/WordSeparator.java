package ie.gmit.sw.ai;

import java.io.*;
import java.util.*;
import java.math.BigDecimal;

public class WordSeparator {
    public long N = 1024908267229L;

    public Map<String, Long> dictionary = new HashMap<>();
    public int maximumDictionaryWordLength = 0;

    public WordSeparator() {
    }

    public boolean loadDictionary(String corpus, int termIndex, int countIndex) {
        File file = new File(corpus);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineParts = line.split("\\s+");
                if (lineParts.length >= 2) {
                    String key = lineParts[termIndex];
                    try {
                        long count = Long.parseLong(lineParts[countIndex]);
                        if (key.length() > maximumDictionaryWordLength) {
                            maximumDictionaryWordLength = key.length();
                        }
                        dictionary.put(key, count);
                    } catch (NumberFormatException e) {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Pair<String, BigDecimal> segment(String input) {
        return segment(input.toLowerCase(), maximumDictionaryWordLength);
    }

    public Pair<String, BigDecimal> segment(String input, int maxSegmentationWordLength) {
        int arraySize = Math.min(maxSegmentationWordLength, input.length());
        int arrayWidth = ((input.length() - 1) >> 6) + 1;

        long[][] segmentedSpaceBits = new long[arraySize][arrayWidth];
        BigDecimal[] probabilityLogSum = new BigDecimal[arraySize];
        int circularIndex = -1;

        for (int j = 0; j < input.length(); j++) {
            int spaceLongIndex = (j - 1) >> 6;
            int arrayCopyLength = Math.min(spaceLongIndex + 1, arrayWidth);

            if (j > 0) segmentedSpaceBits[circularIndex][spaceLongIndex] |= (1L << ((j - 1) & 0x3f));

            int imax = Math.min(input.length() - j, maxSegmentationWordLength);
            for (int i = 1; i <= imax; i++) {
                int destinationIndex = ((i + circularIndex) % arraySize);

                String part1 = input.substring(j, j + i);
                BigDecimal probabilityLogPart1 = BigDecimal.ZERO;
                if (dictionary.containsKey(part1)) {
                    probabilityLogPart1 = BigDecimal.valueOf(Math.log10((double) dictionary.get(part1) / (double) N));
                } else {
                    probabilityLogPart1 = BigDecimal.valueOf(Math.log10(10.0 / (N * Math.pow(10.0, part1.length()))));
                }

                if (j == 0) {
                    probabilityLogSum[destinationIndex] = probabilityLogPart1;
                } else if ((i == maxSegmentationWordLength) ||
                        (probabilityLogSum[destinationIndex].compareTo(probabilityLogSum[circularIndex].add(probabilityLogPart1)) < 0)) {
                    System.arraycopy(segmentedSpaceBits[circularIndex], 0, segmentedSpaceBits[destinationIndex], 0, arrayCopyLength);
                    probabilityLogSum[destinationIndex] = probabilityLogSum[circularIndex].add(probabilityLogPart1);
                }
            }

            circularIndex++;
            if (circularIndex == arraySize) circularIndex = 0;
        }

        StringBuilder resultString = new StringBuilder(input.length() * 2);
        int last = -1;
        for (int i = 0; i <= input.length() - 2; i++) {
            if ((segmentedSpaceBits[circularIndex][i >> 6] & (1L << (i & 0x3f))) > 0) {
                resultString.append(input, last + 1, i + 1);
                resultString.append(' ');
                last = i;
            }
        }
        resultString.append(input.substring(last + 1));

        return new Pair<>(resultString.toString(), probabilityLogSum[circularIndex]);
    }

    public static class Pair<K, V> {
        public final K first;
        public final V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }
    }

    public static void main(String[] args) {
        WordSeparator ws = new WordSeparator();
        ws.loadDictionary("C:\\Users\\leduc\\IdeaProjects\\demo1\\PlayfairCipherBreaker\\src\\main\\java\\ie\\gmit\\sw\\ai\\dic.txt", 0, 1);
        System.out.println(ws.segment("Input : " + "THEHOBBITCHAPTERIANUNEXPECTEDPARTYINAHOLEINTHEGROUNDTHERELIVEDAHOBBITNOTANASTYDIRTYWETHOLEFILQLEDWITHTHEENDSOFWORMSANDANOQOZYSMELQLNORYETADRYBARESANDYHOLEWITHNOTHINGINITQTOSITDOWNONORTOEATITWASAHOBQBITHOLEANDTHATMEANSCOMFORTITHADAPERFECTLYROUNDDOORLIKEAPORTHOLEPAINTEDGREQENWITHASHINYYELQLOWBRASQSKNOBINTHEEXACTMIDDLETHEDOOROPENEDONTOATUBESHAPEDHALLQLIKEATUNNELAVERYCOMFORTABLETUNNELWITHOUTSMOKEWITHPANELLEDWALLSANDFLOORSTILEDANDCARPETEDPROVIDEDWITHPOLISHEDCHAIRSANDLOTSANDLOTSOFPEGSFORHATSANDCOATSTHEHOBBITWASFONDOFVISITORSTHETUNNELWOUNDONANDONGOINGFAIRLYBUTNOTQUITESTRAIGHTINTOTHESIDEOFTHEHILLTHEHILQLASALQLTHEPEOPLEFORMANYMILESROUNDCALLEDITANDMANYLITQTLEROUNDDOORSOPENEDOUTOFITFIRSTONONESIDEANDTHENONANOTHERNOGOINGUPSTAIRSFORTHEHOBBITBEDROOMSBATHROQOMSCELLARSPANTRIESLOTSOFTHESEWARDROBESHEHADWHOLEROOMSDEVOTEDTOCLOTHESKITCHENSDININGROQOMSALQLWEREONTHESAMEFLOQORANDINDEQEDONTHESAMEPASSAGETHEBESTROQOMSWEREALQLONTHELEFTHANDSIDEGOINGINFORTHESEWERETHEONLYONESTOHAVEWINDOWSDEQEPSETROUNDWINDOWSLOQOKIN\n").first);
    }
}