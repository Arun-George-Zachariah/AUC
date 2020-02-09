package auc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Vector;

public class Confusion extends Vector<PNPoint> {
    private double totPos;

    private double totNeg;

    public Confusion(double paramDouble1, double paramDouble2) {
        if (paramDouble1 < 1.0D || paramDouble2 < 1.0D) {
            this.totPos = 1.0D;
            this.totNeg = 1.0D;
            System.err.println("ERROR: " + paramDouble1 + "," + paramDouble2 + " - " + "Defaulting Confusion to 1,1");
        } else {
            this.totPos = paramDouble1;
            this.totNeg = paramDouble2;
        }
    }

    public void addPRPoint(double paramDouble1, double paramDouble2) throws NumberFormatException {
        if (paramDouble1 > 1.0D || paramDouble1 < 0.0D || paramDouble2 > 1.0D || paramDouble2 < 0.0D)
            throw new NumberFormatException();
        double d1 = paramDouble1 * this.totPos;
        double d2 = (d1 - paramDouble2 * d1) / paramDouble2;
        PNPoint pNPoint = new PNPoint(d1, d2);
        if (!contains(pNPoint))
            add(pNPoint);
    }

    public void addROCPoint(double paramDouble1, double paramDouble2) throws NumberFormatException {
        if (paramDouble1 > 1.0D || paramDouble1 < 0.0D || paramDouble2 > 1.0D || paramDouble2 < 0.0D)
            throw new NumberFormatException();
        double d1 = paramDouble2 * this.totPos;
        double d2 = paramDouble1 * this.totNeg;
        PNPoint pNPoint = new PNPoint(d1, d2);
        if (!contains(pNPoint))
            add(pNPoint);
    }

    public void addPoint(double paramDouble1, double paramDouble2) throws NumberFormatException {
        if (paramDouble1 < 0.0D || paramDouble1 > this.totPos || paramDouble2 < 0.0D || paramDouble2 > this.totNeg)
            throw new NumberFormatException();
        PNPoint pNPoint = new PNPoint(paramDouble1, paramDouble2);
        if (!contains(pNPoint))
            add(pNPoint);
    }

    public void sort() {
        if (AUCCalculator.DEBUG)
            System.out.println("--- Sorting the datapoints !!! ---");
        if (size() == 0) {
            System.err.println("ERROR: No data to sort....");
            return;
        }
        PNPoint[] arrayOfPNPoint = new PNPoint[size()];
        byte b1 = 0;
        while (size() > 0) {
            arrayOfPNPoint[b1++] = elementAt(0);
            removeElementAt(0);
        }
        Arrays.sort((Object[])arrayOfPNPoint);
        for (byte b2 = 0; b2 < arrayOfPNPoint.length; b2++)
            add(arrayOfPNPoint[b2]);
        PNPoint pNPoint1 = elementAt(0);
        while (pNPoint1.getPos() < 0.001D && pNPoint1.getPos() > -0.001D) {
            removeElementAt(0);
            pNPoint1 = elementAt(0);
        }
        double d = pNPoint1.getNeg() / pNPoint1.getPos();
        PNPoint pNPoint2 = new PNPoint(1.0D, d);
        if (!contains(pNPoint2) && pNPoint1.getPos() > 1.0D)
            insertElementAt(pNPoint2, 0);
        pNPoint2 = new PNPoint(this.totPos, this.totNeg);
        if (!contains(pNPoint2))
            add(pNPoint2);
    }

    public void interpolate() {
        if (AUCCalculator.DEBUG)
            System.out.println("--- Interpolating New Points ---");
        if (size() == 0) {
            System.err.println("ERROR: No data to interpolate....");
            return;
        }
        for (byte b = 0; b < size() - 1; b++) {
            PNPoint pNPoint1 = elementAt(b);
            PNPoint pNPoint2 = elementAt(b + 1);
            double d1 = pNPoint2.getPos() - pNPoint1.getPos();
            double d2 = pNPoint2.getNeg() - pNPoint1.getNeg();
            double d3 = d2 / d1;
            double d4 = pNPoint1.getPos();
            double d5 = pNPoint1.getNeg();
            while (Math.abs(pNPoint1.getPos() - pNPoint2.getPos()) > 1.001D) {
                double d = d5 + (pNPoint1.getPos() - d4 + 1.0D) * d3;
                PNPoint pNPoint = new PNPoint(pNPoint1.getPos() + 1.0D, d);
                insertElementAt(pNPoint, ++b);
                pNPoint1 = pNPoint;
            }
        }
    }

    public double calculateAUCPR(double paramDouble) {
        if (AUCCalculator.DEBUG)
            System.out.println("--- Calculating AUC-PR ---");
        if (paramDouble < 0.0D || paramDouble > 1.0D) {
            System.err.println("ERROR: invalid minRecall, must be between 0 and 1 - returning 0");
            return 0.0D;
        }
        if (size() == 0) {
            System.err.println("ERROR: No data to calculate....");
            return 0.0D;
        }
        double d1 = paramDouble * this.totPos;
        byte b = 0;
        PNPoint pNPoint1 = elementAt(b);
        PNPoint pNPoint2 = null;
        try {
            while (pNPoint1.getPos() < d1) {
                pNPoint2 = pNPoint1;
                pNPoint1 = elementAt(++b);
            }
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            System.out.println("ERROR: minRecall out of bounds - exiting...");
            System.exit(-1);
        }
        double d2 = (pNPoint1.getPos() - d1) / this.totPos;
        double d3 = pNPoint1.getPos() / (pNPoint1.getPos() + pNPoint1.getNeg());
        double d4 = d2 * d3;
        if (pNPoint2 != null) {
            double d5 = pNPoint1.getPos() / this.totPos - pNPoint2.getPos() / this.totPos;
            double d6 = pNPoint1.getPos() / (pNPoint1.getPos() + pNPoint1.getNeg()) - pNPoint2.getPos() / (pNPoint2.getPos() + pNPoint2.getNeg());
            double d7 = d6 / d5;
            double d8 = pNPoint2.getPos() / (pNPoint2.getPos() + pNPoint2.getNeg()) + d7 * (d1 - pNPoint2.getPos()) / this.totPos;
            double d9 = 0.5D * d2 * (d8 - d3);
            d4 += d9;
        }
        d2 = pNPoint1.getPos() / this.totPos;
        for (int i = b + 1; i < size(); i++) {
            PNPoint pNPoint = elementAt(i);
            double d5 = pNPoint.getPos() / this.totPos;
            double d6 = pNPoint.getPos() / (pNPoint.getPos() + pNPoint.getNeg());
            double d7 = (d5 - d2) * d6;
            double d8 = 0.5D * (d5 - d2) * (d3 - d6);
            d4 += d7 + d8;
            pNPoint1 = pNPoint;
            d2 = d5;
            d3 = d6;
        }
        System.out.println("Area Under the Curve for Precision - Recall is " + d4);
        return d4;
    }

    public double calculateAUCROC() {
        if (AUCCalculator.DEBUG)
            System.out.println("--- Calculating AUC-ROC ---");
        if (size() == 0) {
            System.err.println("ERROR: No data to calculate....");
            return 0.0D;
        }
        PNPoint pNPoint = elementAt(0);
        double d1 = pNPoint.getPos() / this.totPos;
        double d2 = pNPoint.getNeg() / this.totNeg;
        double d3 = 0.5D * d1 * d2;
        for (byte b = 1; b < size(); b++) {
            PNPoint pNPoint1 = elementAt(b);
            double d4 = pNPoint1.getPos() / this.totPos;
            double d5 = pNPoint1.getNeg() / this.totNeg;
            double d6 = (d4 - d1) * d5;
            double d7 = 0.5D * (d4 - d1) * (d5 - d2);
            d3 += d6 - d7;
            pNPoint = pNPoint1;
            d1 = d4;
            d2 = d5;
        }
        d3 = 1.0D - d3;
        System.out.println("Area Under the Curve for ROC is " + d3);
        return d3;
    }

    public void writePRFile(String paramString) {
        System.out.println("--- Writing PR file " + paramString + " ---");
        if (size() == 0) {
            System.err.println("ERROR: No data to write....");
            return;
        }
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(new File(paramString)));
            for (byte b = 0; b < size(); b++) {
                PNPoint pNPoint = elementAt(b);
                double d1 = pNPoint.getPos() / this.totPos;
                double d2 = pNPoint.getPos() / (pNPoint.getPos() + pNPoint.getNeg());
                printWriter.println(d1 + "\t" + d2);
            }
            printWriter.close();
        } catch (IOException iOException) {
            System.out.println("ERROR: IO Exception in file " + paramString + " - exiting...");
            System.exit(-1);
        }
    }

    public void writeStandardPRFile(String paramString) {
        System.out.println("--- Writing standardized PR file " + paramString + " ---");
        if (size() == 0) {
            System.err.println("ERROR: No data to write....");
            return;
        }
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(new File(paramString)));
            byte b = 0;
            PNPoint pNPoint1 = null;
            PNPoint pNPoint2 = elementAt(b);
            double d;
            for (d = 1.0D; d <= 100.0D; d++) {
                double d1 = pNPoint2.getPos() / this.totPos;
                double d2 = -1.0D;
                if (d / 100.0D <= d1) {
                    if (pNPoint1 == null) {
                        d2 = pNPoint2.getPos() / (pNPoint2.getPos() + pNPoint2.getNeg());
                    } else {
                        double d3 = pNPoint2.getPos() - pNPoint1.getPos();
                        double d4 = pNPoint2.getNeg() - pNPoint1.getNeg();
                        double d5 = d4 / d3;
                        double d6 = d / 100.0D * this.totPos;
                        double d7 = pNPoint1.getNeg() + (d6 - pNPoint1.getPos()) * d5;
                        d2 = d6 / (d6 + d7);
                    }
                    printWriter.println((d / 100.0D) + "\t" + d2);
                } else {
                    do {
                        pNPoint1 = pNPoint2;
                        pNPoint2 = elementAt(++b);
                        d1 = pNPoint2.getPos() / this.totPos;
                    } while (d / 100.0D > d1);
                    double d3 = pNPoint2.getPos() - pNPoint1.getPos();
                    double d4 = pNPoint2.getNeg() - pNPoint1.getNeg();
                    double d5 = d4 / d3;
                    double d6 = d / 100.0D * this.totPos;
                    double d7 = pNPoint1.getNeg() + (d6 - pNPoint1.getPos()) * d5;
                    d2 = d6 / (d6 + d7);
                    printWriter.println((d / 100.0D) + "\t" + d2);
                }
            }
            printWriter.close();
        } catch (IOException iOException) {
            System.out.println("ERROR: IO Exception in file " + paramString + " - exiting...");
            System.exit(-1);
        }
    }

    public void writeROCFile(String paramString) {
        System.out.println("--- Writing ROC file " + paramString + " ---");
        if (size() == 0) {
            System.err.println("ERROR: No data to write....");
            return;
        }
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(new File(paramString)));
            printWriter.println("0\t0");
            for (byte b = 0; b < size(); b++) {
                PNPoint pNPoint = elementAt(b);
                double d1 = pNPoint.getPos() / this.totPos;
                double d2 = pNPoint.getNeg() / this.totNeg;
                printWriter.println(d2 + "\t" + d1);
            }
            printWriter.close();
        } catch (IOException iOException) {
            System.out.println("ERROR: IO Exception in file " + paramString + " - exiting...");
            System.exit(-1);
        }
    }

    public String toString() {
        String str = "";
        str = str + "TotPos: " + this.totPos + ", TotNeg: " + this.totNeg + "\n";
        for (byte b = 0; b < size(); b++)
            str = str + elementAt(b) + "\n";
        return str;
    }
}
