package auc;

public class ClassSort implements Comparable {
    private double val;

    private int classification;

    public ClassSort(double paramDouble, int paramInt) {
        this.val = paramDouble;
        this.classification = paramInt;
    }

    public int getClassification() {
        return this.classification;
    }

    public double getProb() {
        return this.val;
    }

    public int compareTo(Object paramObject) {
        double d = ((ClassSort)paramObject).getProb();
        if (this.val < d)
            return -1;
        if (this.val > d)
            return 1;
        int i = ((ClassSort)paramObject).getClassification();
        if (i == this.classification)
            return 0;
        if (this.classification > i)
            return -1;
        return 1;
    }
}