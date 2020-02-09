package auc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class AUCCalculator {
    private static String fileName;

    private static String fileType;

    private static double posCount;

    private static double negCount;

    private static double minRecall = 0.0D;

    public static boolean DEBUG = false;

    public static void main(String[] paramArrayOfString) {
        Confusion confusion;
        readArgs(paramArrayOfString);
        if (fileType.equalsIgnoreCase("list")) {
            confusion = ReadList.readFile(fileName, fileType);
        } else {
            confusion = readFile(fileName, fileType, posCount, negCount);
        }
        confusion.writePRFile(fileName + ".pr");
        confusion.writeStandardPRFile(fileName + ".spr");
        confusion.writeROCFile(fileName + ".roc");
        confusion.calculateAUCPR(minRecall);
        confusion.calculateAUCROC();
    }

    public static void readArgs(String[] paramArrayOfString) {
        fileName = "";
        byte b = 2;
        try {
            fileName = paramArrayOfString[0];
            fileType = paramArrayOfString[1];
            if (!fileType.equalsIgnoreCase("PR") && !fileType.equalsIgnoreCase("ROC") && !fileType.equalsIgnoreCase("list"))
                throw new NoSuchElementException();
            if (fileType.equalsIgnoreCase("PR") || fileType.equalsIgnoreCase("ROC")) {
                posCount = Double.parseDouble(paramArrayOfString[2]);
                negCount = Double.parseDouble(paramArrayOfString[3]);
                b = 4;
                if (posCount < 1.0D || negCount < 1.0D)
                    throw new NumberFormatException();
            }
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            System.err.println("ERROR: Missing Arguments - exiting...");
            System.err.println("Usage:\njava AUCCalculator <fileName> <fileType> <posCount*> <negCount*> <minRecall**>");
            System.err.println("   posCount and negCount required if fileType ROC or PR");
            System.err.println("   minRecall always optional");
            System.exit(-1);
        } catch (NumberFormatException numberFormatException) {
            System.err.println("ERROR: Incorrect Count arguments, must be positive numbers - exiting...");
            System.err.println("Usage:\njava AUCCalculator <fileName> <posCount*> <negCount*> <minRecall**>");
            System.err.println("   posCount and negCount required if fileType ROC or PR");
            System.err.println("   minRecall always optional");
            System.exit(-1);
        } catch (NoSuchElementException noSuchElementException) {
            System.err.println("ERROR: Incorrect fileType, must be ROC, PR, LIST - exiting...");
            System.err.println("Usage:\njava AUCCalculator <fileName> <posCount*> <negCount*> <minRecall**>");
            System.err.println("   posCount and negCount required if fileType ROC or PR");
            System.err.println("   minRecall always optional");
            System.exit(-1);
        }
        try {
            minRecall = Double.parseDouble(paramArrayOfString[b]);
            if (minRecall < 0.0D || minRecall > 1.0D)
                throw new NumberFormatException();
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {

        } catch (NumberFormatException numberFormatException) {
            System.err.println("ERROR: Incorrect minRecall argument, must be positive between 0 and 1 - exiting...");
            System.err.println("Usage:\njava AUCCalculator <fileName> <posCount*> <negCount*> <minRecall**>");
            System.err.println("   posCount and negCount required if fileType ROC or PR");
            System.err.println("   minRecall always optional");
            System.exit(-1);
        }
    }

    public static Confusion readFile(String paramString1, String paramString2, double paramDouble1, double paramDouble2) {
        if (DEBUG)
            System.out.println("--- Reading in " + paramString2 + " File: " + paramString1 + " ---");
        Confusion confusion = new Confusion(paramDouble1, paramDouble2);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(paramString1)));
            if (!paramString2.equals("PR") && !paramString2.equals("ROC") && !paramString2.equals("pr") && !paramString2.equals("roc"))
                throw new NoSuchElementException();
            while (bufferedReader.ready()) {
                String str = bufferedReader.readLine();
                if (DEBUG)
                    System.out.println(str);
                StringTokenizer stringTokenizer = new StringTokenizer(str, "\t ,");
                try {
                    double d1 = Double.parseDouble(stringTokenizer.nextToken());
                    double d2 = Double.parseDouble(stringTokenizer.nextToken());
                    if (DEBUG)
                        System.out.println(d1 + "\t" + d2);
                    if (paramString2.equals("PR")) {
                        confusion.addPRPoint(d1, d2);
                    } else {
                        confusion.addROCPoint(d1, d2);
                    }
                    if (DEBUG)
                        System.out.println("End of Line");
                } catch (NumberFormatException numberFormatException) {
                    System.err.println("...skipping bad input line (bad numbers)");
                } catch (NoSuchElementException noSuchElementException) {
                    System.err.println("...skipping bad input line (missing data)");
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("ERROR: File " + paramString1 + " not found - exiting...");
            System.exit(-1);
        } catch (NoSuchElementException noSuchElementException) {
            System.err.println("...incorrect fileType argument, either PR or ROC - exiting");
            System.exit(-1);
        } catch (IOException iOException) {
            System.err.println("ERROR: IO Exception in file " + paramString1 + " - exiting...");
            System.exit(-1);
        }
        confusion.sort();
        confusion.interpolate();
        return confusion;
    }

    public static Confusion readArrays(int[] paramArrayOfint, double[] paramArrayOfdouble) {
        if (paramArrayOfint.length != paramArrayOfdouble.length || paramArrayOfint.length == 0) {
            System.err.println(paramArrayOfint.length + " " + paramArrayOfdouble.length);
            System.err.println("ERROR: incorrect array lengths - exiting");
            System.exit(-1);
        }
        double d1 = 0.0D;
        double d2 = 0.0D;
        for (byte b1 = 0; b1 < paramArrayOfint.length; b1++) {
            if (paramArrayOfint[b1] == 0) {
                d2++;
            } else if (paramArrayOfint[b1] == 1) {
                d1++;
            } else {
                System.err.println("ERROR: example not 0 or 1 - exiting");
                System.exit(-1);
            }
        }
        Confusion confusion = new Confusion(d1, d2);
        double d3 = 0.0D;
        double d4 = 0.0D;
        if (paramArrayOfint[0] == 0) {
            d4++;
        } else if (paramArrayOfint[0] == 1) {
            d3++;
        } else {
            System.err.println("ERROR: example not 0 or 1 - exiting");
            System.exit(-1);
        }
        for (byte b2 = 1; b2 < paramArrayOfdouble.length; b2++) {
            if (paramArrayOfdouble[b2] != paramArrayOfdouble[b2 - 1])
                try {
                    confusion.addPoint(d3, d4);
                } catch (NumberFormatException numberFormatException) {
                    System.err.println("...skipping bad input line (bad numbers)");
                }
            if (paramArrayOfint[b2] == 0) {
                d4++;
            } else if (paramArrayOfint[b2] == 1) {
                d3++;
            } else {
                System.err.println("ERROR: example not 0 or 1 - exiting");
                System.exit(-1);
            }
        }
        confusion.addPoint(d3, d4);
        confusion.sort();
        confusion.interpolate();
        return confusion;
    }
}
