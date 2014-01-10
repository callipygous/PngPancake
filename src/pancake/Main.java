package pancake;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main( final String args[] ) throws IOException {
        final List<String> errors   = new ArrayList<String>();
        final List<String> warnings = new ArrayList<String>();
        OptionParser options = OptionParser.parseArgs( args, errors, warnings );

        printMessages( "Errors:",  errors,    System.err );
        printMessages( "Warnings:", warnings, System.out );

        if( !errors.isEmpty() ) {
            System.exit(1);
        }

        Pair<Integer, Integer> imageSize = new Pair<>( options.width(), options.height() );
        int argb = Util.toArgb( options.alpha(), options.red(), options.green(), options.blue() );
        System.out.println("Size: " + options.width() + "x" + options.height() );
        System.out.println("Argb: " + options.alpha() + "," +
                                      options.red()   + "," +
                                      options.green()  + "," +
                                      options.blue() );

        int totalFiles = 0;
        int firstFile = 0;
        final List<File> allFiles = new ArrayList<File>();
        for( Map.Entry<String, List<File>> entry : options.getFiles().entrySet() ) {
            final String name      = entry.getKey();
            final List<File> files = entry.getValue();
            final int lastFile = firstFile + files.size() - 1;
            System.out.println( name + ": " + firstFile + " -> " + lastFile );
            firstFile = lastFile + 1;
            totalFiles += files.size();
            allFiles.addAll( entry.getValue() );
        }

        Pair<Integer, Integer> dimensions = Util.centralFactors( totalFiles );
        System.out.println("Total files: " + totalFiles );
        System.out.println("Dimensions: "  + dimensions );
        System.out.println("Output File: " + options.outputFile().getAbsolutePath() );

        SimpleCombinePngs combinePngs = new SimpleCombinePngs(imageSize, dimensions, argb, allFiles, options.outputFile() );
        combinePngs.combine();
    }

    public static void printMessages(final String heading, final List<String> messages, PrintStream ps ) {

        if( !messages.isEmpty() ) {
            ps.println( heading );
            for( final String msg : messages ) {
                ps.println( msg );
            }
            ps.println();
        }
    }
}
