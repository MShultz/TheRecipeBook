package rstead.bgoff.mshultz.therecipebook;

/**
 * Created by Ben Goff on 5/2/2017.
 */

public class Fraction {
    private int denominator;
    private int wholeNumber;
    private int numerator;

    public int getWholeNumber() {
        return wholeNumber;
    }

    public void setWholeNumber(int wholeNumber) {
        this.wholeNumber = wholeNumber;
    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    public Fraction(int whole, int numerator, int denominator) {
        setWholeNumber(whole);
        setNumerator(numerator);
        setDenominator(denominator);
    }

    public Fraction(String data){
        data = data.trim();
        int wholeNum = 0;
        int numerator = 0;
        int denominator = 1;
        if(data.contains(" ")){
            String[] wholeSplit = data.split(" ");
            wholeNum = Integer.parseInt(wholeSplit[0].trim());
            String[] fracSplit = wholeSplit[1].trim().split("/");
            numerator = Integer.parseInt(fracSplit[0].trim());
            denominator = Integer.parseInt(fracSplit[1].trim());
        }else{
            if(data.contains("/")){
                String[] fracSplit = data.trim().split("/");
                numerator = Integer.parseInt(fracSplit[0].trim());
                denominator = Integer.parseInt(fracSplit[1].trim());
            }else{
                wholeNum = Integer.parseInt(data.trim());
            }
        }
        setWholeNumber(wholeNum);
        setNumerator(numerator);
        setDenominator(denominator);
    }

    public void reduce() {
        int gcd = gcd(getNumerator(), getDenominator());
        setNumerator(getNumerator() / gcd);
        setDenominator(getDenominator() / gcd);
    }

    public void makeProper() {
        int newWholeNumber = getNumerator() / getDenominator();
        int newNumerator = getNumerator() - (newWholeNumber * getDenominator());
        setWholeNumber(getWholeNumber() + newWholeNumber);
        setNumerator(newNumerator);
    }

    public void makeImproper() {
        int numToAdd = getWholeNumber() * getDenominator();
        setWholeNumber(0);
        setNumerator(getNumerator() + numToAdd);
    }

    public void simplify() {
        this.reduce();
        this.makeProper();
    }

    private static int gcd(int m, int n) {
        //Implement Euclid's recursive algo for finding the greatest common divisor
        m = Math.abs(m);
        n = Math.abs(n);
        int gcd = 0;
        if (m == 0) {
            gcd = n;
        } else if (n == 0) {
            gcd = m;
        } else if (m > n) {
            gcd = gcd(m % n, n);
        } else if (m < n) {
            gcd = gcd(m, n % m);
        }
        return gcd;
    }

    public void scaleFraction(double scalar){
        scalar = Math.floor(scalar * 100) / 100;
        int wholeNum = (int)scalar;
        int numerator = (int)((scalar - wholeNum) * 100);
        Fraction scaleFrac = new Fraction((int)scalar, numerator, 100);
        Fraction f = Fraction.multiply(this, scaleFrac);
        this.setWholeNumber(f.getWholeNumber());
        this.setNumerator(f.getNumerator());
        this.setDenominator(f.getDenominator());
    }

    @Override
    public String toString() {
        String s = "";
        if (getWholeNumber() != 0) {
            s += getWholeNumber();
        }
        if (getNumerator() != 0) {
            if (getWholeNumber() != 0) {
                s += " ";
            }
            s += getNumerator();
            if (getDenominator() != 1) {
                s += "/" + getDenominator();
            }
        }
        if (getWholeNumber() == 0 && getNumerator() == 0) {
            s = "0";
        }
        return s;
    }

    public static Fraction multiply(Fraction a, Fraction b) {
        Fraction first = new Fraction(a.getWholeNumber(), a.getNumerator(), a.getDenominator());
        Fraction second = new Fraction(b.getWholeNumber(), b.getNumerator(), b.getDenominator());

        first.makeImproper();
        second.makeImproper();

        int newNumerator = first.getNumerator() * second.getNumerator();
        int newDenominator = first.getDenominator() * second.getDenominator();

        Fraction f = new Fraction(0, newNumerator, newDenominator);

        f.simplify();
        return f;
    }

}