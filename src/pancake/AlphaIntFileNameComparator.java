package pancake;

import java.io.File;
import java.util.Comparator;

public class AlphaIntFileNameComparator implements Comparator<File> {
    final String intRegex = "\\d+";

    @Override
    public int compare(File f1, File f2) {
        final String str1 = f1.getName();
        final String str2 = f2.getName();
        
        System.out.println("\n\n Comparing: " +  str1 + " , " + str2 );
        int maxLength = Math.min( str1.length(), str2.length());

        int nextChar1 = 0;
        int nextChar2 = 0;

        while( nextChar1 < str1.length() && nextChar2 < str2.length() ) {
            char c1 = str1.charAt(nextChar1);
            char c2 = str2.charAt(nextChar2);

            if( Character.isDigit( c1 ) && Character.isDigit( c2 ) ) {
                int acc1 = Character.getNumericValue( c1 );

                while( ++nextChar1 < str1.length() && Character.isDigit( c1 = str1.charAt( nextChar1 ) ) ) {
                    acc1 = acc1 * 10 + Character.getNumericValue( c1 );
                }

                int acc2 = Character.getNumericValue( c2 );

                while( ++nextChar2 < str2.length() && Character.isDigit( c2 = str2.charAt( nextChar2 ) ) ) {
                    acc2 = acc2 * 10 + Character.getNumericValue( c2 );
                }


                if( acc1 < acc2 ) {
                    return -1;
                } else if( acc1 > acc2 ) {
                    return 1;
                } //else continue

            } else {
                if( c1 < c2 ) {
                    return -1;
                } else if( c1 > c2 ) {
                    return 1;
                } else {
                    ++nextChar1;
                    ++nextChar2;
                }
            }
        }

        return 0;
    }
}
