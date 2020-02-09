package auc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class ReadList {
    public static final int TP = 0;

    public static final int FP = 1;

    public static final int FN = 2;

    public static final int TN = 3;

    public static ClassSort[] convertList(LinkedList<ClassSort> paramLinkedList) {
        ClassSort[] arrayOfClassSort = new ClassSort[paramLinkedList.size()];
        for (byte b = 0; b < arrayOfClassSort.length; b++)
            arrayOfClassSort[b] = paramLinkedList.removeFirst();
        Arrays.sort((Object[])arrayOfClassSort);
        return arrayOfClassSort;
    }

    public static Confusion accuracyScoreAllSplits(ClassSort[] paramArrayOfClassSort, int paramInt1, int paramInt2) {
        Arrays.sort((Object[])paramArrayOfClassSort);
        for (int i = paramArrayOfClassSort.length - 1; i >= paramArrayOfClassSort.length - 20; i--);
        Confusion confusion = new Confusion(paramInt1, paramInt2);
        int j = 0;
        double d1 = paramArrayOfClassSort[paramArrayOfClassSort.length - 1].getProb();
        int k = paramArrayOfClassSort[paramArrayOfClassSort.length - 1].getClassification();
        double d2 = 0.0D;
        double d3 = 0.0D;
        double d4 = 0.0D;
        double[] arrayOfDouble = new double[paramArrayOfClassSort.length];
        int[] arrayOfInt = new int[paramArrayOfClassSort.length];
        for (byte b = 0; b < paramArrayOfClassSort.length; b++) {
            arrayOfDouble[b] = paramArrayOfClassSort[b].getProb();
            arrayOfInt[b] = paramArrayOfClassSort[b].getClassification();
        }
        LinkedList linkedList = new LinkedList();
        for (int m = paramArrayOfClassSort.length - 2; m >= 0; m--) {
            int n = paramArrayOfClassSort[m].getClassification();
            double d = paramArrayOfClassSort[m].getProb();
            if (k == 1 && 0 == n) {
                if (paramArrayOfClassSort[m + 1].getProb() <= d)
                    if (paramArrayOfClassSort[m + 1].getProb() <= d)
                        System.out.println("Bad");
                int[] arrayOfInt1 = fastAccuracy(arrayOfDouble, arrayOfInt, d1);
                confusion.addPoint(arrayOfInt1[0], arrayOfInt1[1]);
            }
            j += n;
            d1 = d;
            k = n;
        }
        return confusion;
    }

    public static int[] fastAccuracy(double[] paramArrayOfdouble, int[] paramArrayOfint, double paramDouble) {
        int[] arrayOfInt = new int[4];
        byte b;
        for (b = 0; b < arrayOfInt.length; b++)
            arrayOfInt[b] = 0;
        for (b = 0; b < paramArrayOfdouble.length; b++) {
            if (paramArrayOfdouble[b] >= paramDouble) {
                if (paramArrayOfint[b] == 1) {
                    arrayOfInt[0] = arrayOfInt[0] + 1;
                } else {
                    arrayOfInt[1] = arrayOfInt[1] + 1;
                }
            } else if (paramArrayOfint[b] == 1) {
                arrayOfInt[2] = arrayOfInt[2] + 1;
            } else {
                arrayOfInt[3] = arrayOfInt[3] + 1;
            }
        }
        return arrayOfInt;
    }

    public static Confusion readFile(String paramString1, String paramString2) {
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        LinkedList<ClassSort> linkedList = new LinkedList();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(paramString1)));
            while (bufferedReader.ready()) {
                String str = bufferedReader.readLine();
                if (AUCCalculator.DEBUG);
                StringTokenizer stringTokenizer = new StringTokenizer(str, "\t ,");
                try {
                    double d1 = Double.parseDouble(stringTokenizer.nextToken());
                    int j = Integer.parseInt(stringTokenizer.nextToken());
                    linkedList.add(new ClassSort(d1, j));
                    if (AUCCalculator.DEBUG);
                    if (AUCCalculator.DEBUG);
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
        ClassSort[] arrayOfClassSort = convertList(linkedList);
        ArrayList<PNPoint> arrayList = new ArrayList();
        double d = arrayOfClassSort[arrayOfClassSort.length - 1].getProb();
        if (arrayOfClassSort[arrayOfClassSort.length - 1].getClassification() == 1) {
            b1++;
        } else {
            b2++;
        }
        b3++;
        for (int i = arrayOfClassSort.length - 2; i >= 0; i--) {
            double d1 = arrayOfClassSort[i].getProb();
            int j = arrayOfClassSort[i].getClassification();
            System.out.println(d1 + " " + j);
            if (d1 != d)
                arrayList.add(new PNPoint(b1, b2));
            d = d1;
            if (j == 1) {
                b1++;
            } else {
                b2++;
            }
            b3++;
        }
        arrayList.add(new PNPoint(b1, b2));
        Confusion confusion = new Confusion(b1, b2);
        for (PNPoint pNPoint : arrayList)
            confusion.addPoint(pNPoint.getPos(), pNPoint.getNeg());
        confusion.sort();
        confusion.interpolate();
        return confusion;
    }
}
