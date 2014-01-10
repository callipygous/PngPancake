package pancake;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;

public class Util {

    public static String exceptionToString( Exception exc ) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace( pw );
        return sw.toString();
    }

    //I have made no attempt to optimize this factorization as there will never be enough images for me to care
    public static Pair<Integer, Integer> centralFactors( final int number ) {

        if( number == 1 ) {
            return new Pair<Integer,Integer>(1, 1);
        } else if( number == 2  ) {
            return new Pair<Integer, Integer>(2, 1);
        } else if( number == 3 ) {
            return new Pair<Integer, Integer>(3, 1);
        }

        final int nonPrime;
        BigInteger bi = BigInteger.valueOf( number );
        if( bi.isProbablePrime( number ) ) {
            nonPrime = number - 1;
        } else {
            nonPrime = number;
        }

        double sqrt = Math.sqrt( number );
        int lowFactor    = (int) Math.floor( sqrt );
        int highFactor = (int) Math.ceil( sqrt );

        if( lowFactor == highFactor ) {
            return new Pair<Integer,Integer>( lowFactor, lowFactor );
        }

        while( lowFactor > 0 || highFactor < number ) {
            if( lowFactor > 0 ) {
                final int lowResult = number / lowFactor;
                if( lowFactor * lowResult == number ) {
                    return new Pair<Integer, Integer>( lowFactor, lowResult );
                }
                --lowFactor;
            }

            if( highFactor < number ) {
                final int highResult = number / highFactor;
                if( highFactor * highResult == number ) {
                    return new Pair<Integer, Integer>( highFactor, highResult );
                }
                ++highFactor;
            }
        }

        //number is prime, subtract 1 and try again
        final Pair<Integer, Integer> smallerFactors = centralFactors( number - 1 );
        return new Pair<>( smallerFactors._1, smallerFactors._2 + 1 );
    }

    public static int toArgb( int a, int r, int g, int b ) {
        return (a << 24) + ((r & 0xFF) << 16) + ((g & 0xFF) << 8) + ( b & 0xFF );
    }
}
