package auc;

public class PNPoint implements Comparable {
    private double pos;

    private double neg;

    public PNPoint(double paramDouble1, double paramDouble2) {
        if (paramDouble1 < 0.0D || paramDouble2 < 0.0D) {
            this.pos = 0.0D;
            this.neg = 0.0D;
            System.err.println("ERROR: " + paramDouble1 + "," + paramDouble2 + " - Defaulting " + "PNPoint to 0,0");
        } else {
            this.pos = paramDouble1;
            this.neg = paramDouble2;
        }
    }

    public double getPos() {
        return this.pos;
    }

    public double getNeg() {
        return this.neg;
    }

    public int compareTo(Object paramObject) {
        if (paramObject instanceof PNPoint) {
            PNPoint pNPoint = (PNPoint)paramObject;
            if (this.pos - pNPoint.pos > 0.0D)
                return 1;
            if (this.pos - pNPoint.pos < 0.0D)
                return -1;
            if (this.neg - pNPoint.neg > 0.0D)
                return 1;
            if (this.neg - pNPoint.neg < 0.0D)
                return -1;
            return 0;
        }
        return -1;
    }

    public boolean equals(Object paramObject) {
        if (paramObject instanceof PNPoint) {
            PNPoint pNPoint = (PNPoint)paramObject;
            if (Math.abs(this.pos - pNPoint.pos) > 0.001D)
                return false;
            if (Math.abs(this.neg - pNPoint.neg) > 0.001D)
                return false;
            return true;
        }
        return false;
    }

    public String toString() {
        String str = "";
        str = str + "(" + this.pos + "," + this.neg + ")";
        return str;
    }
}
