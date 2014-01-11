package pancake;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionParser {

    private final FileSystem fileSystem;

    private File outputFile;

    private int imgWidth;
    private int imgHeight;

    private int red;
    private int green;
    private int blue;
    private int alpha;

    private HashMap<String, List<File>> patternToFiles;

    private final String imgSizeRegexStr = "(\\d+)x(\\d+)";

    private final String threeDigit = "\\s*(\\d{1,3})\\s*";
    private final String argbRegexStr =
            "\\s*\\(" + threeDigit + "," + threeDigit + "," + threeDigit + "," + threeDigit + "\\)\\s*";

    public File outputFile() { return outputFile; }

    public int width()  { return imgWidth;  }
    public int height() { return imgHeight; }

    public int red()   { return red;   }
    public int green() { return green; }
    public int blue()  { return blue;  }
    public int alpha() { return alpha; }


    private OptionParser() {
        fileSystem = FileSystems.getDefault();
    }

    public Map<String, List<File>> getFiles() {
        return Collections.unmodifiableMap(patternToFiles);
    }

    public String getUsage() {
        return  "Usage: <width>x<height> (a, r, g, b) outputFilePath [path;pattern]\n" +
                "e.g.  \"640x480\" \"(255, 255, 255, 255)\" ./out.png \"../images/;MyAnim*.png\" \"../images/;MyOtherAnim*.png\"";
    }

    //Width images should be resized to, PixelsxHeight In Pixels, 8bit (r,g,b,a) fill color, files/file filters
    //e.g. "640x480" "(255, 255, 255, 255)" "../images/MyAnim*" "../images/MyOtherAnim*"
    public void parse(final String [] args, List<String> errors, List<String> warnings ) {
        if( args.length < 4 ) {
            errors.add( "Too few arguments! " + getUsage() );
        } else {
            extractSize( args[0], errors, warnings );
            extractargb( args[1], errors, warnings );
            extractDestination( args[2], errors, warnings );
            extractFiles( Arrays.copyOfRange( args, 3, args.length), errors, warnings );
        }
    }

    public static OptionParser parseArgs( final String [] args, List<String> errors, List<String> warnings ) {
        final OptionParser parser = new OptionParser();
        parser.parse( args, errors, warnings );
        return parser;
    }

    private void extractSize( final String sizeArg, final List<String> errors, final List<String> warnings ) {
        final Matcher sizeMatcher = Pattern.compile(imgSizeRegexStr).matcher( sizeArg );
        if( sizeMatcher.find() ) {
            imgWidth  = Integer.parseInt( sizeMatcher.group( 1 ) );
            imgHeight = Integer.parseInt( sizeMatcher.group( 2 ) );
        } else {
            errors.add("Could not parse image size!");
        }
    }

    private void checkColor( int value, String name, final List<String> errors ) {
        if( value < 0 ) {
            errors.add( name + "( " + value + " ) is to small!" );
        } else if( value > 255 ) {
            errors.add( name + "( " + value + " ) is to big!");
        }
    }

    private void extractargb( final String argbArg, final List<String> errors, final List<String> warnings ) {
        final Matcher argbMatcher = Pattern.compile( argbRegexStr ).matcher( argbArg );
        if( argbMatcher.find() ) {
            alpha = Integer.parseInt( argbMatcher.group(1) );
            red   = Integer.parseInt(argbMatcher.group(2));
            green = Integer.parseInt( argbMatcher.group(3) );
            blue  = Integer.parseInt( argbMatcher.group(4) );
        } else {
            errors.add("Could not parse argb!");
        }

        checkColor( red,   "red",   errors );
        checkColor( green, "green", errors );
        checkColor( blue,  "blue",  errors );
        checkColor( alpha, "alpha", errors );
    }

    private void extractDestination( final String destPath, final List<String> errors, final List<String> warnings ) {
        outputFile = new File( destPath );
    }

    private void extractFiles( final String [] filePatterns, final List<String> errors, final List<String> warnings ) {
        patternToFiles = new LinkedHashMap<>();

        final Comparator<File> alphaIntFileComparator = new AlphaIntFileNameComparator();

        for(final String filePattern : filePatterns ) {
            //note ; will also stop windows from interpreting the argument as a file and allow you to use *
            if( !filePattern.contains(";") ) {
                errors.add("Invalid file pattern( " + filePattern + " ).  Patterns must be <path>;<globPattern>");
                return;
            }

            final String [] tokens = filePattern.split(";");
            if( tokens.length > 2 ) {
                errors.add("Invalid file pattern( " + filePattern + " )  Multiple ; characters.  Patterns must be <path>;<globPattern>");
                return;
            }

            final Path root = fileSystem.getPath( tokens[0] );
            final PathMatcher pathMatcher = fileSystem.getPathMatcher( "glob:" + tokens[1] );
            final List<Path> filePaths = FileFinder.findFiles( fileSystem, root, pathMatcher, errors, warnings );
            if( filePaths.isEmpty() ) {
                warnings.add( "Found no files for pattern: " + filePattern );
            } else {
                final List<File> files = new ArrayList<>();
                for(final Path filePath : filePaths ) {
                    files.add( filePath.toFile() );
                }
                Collections.sort( files, alphaIntFileComparator );
                patternToFiles.put( filePattern, files );
            }
        }

    }
}
