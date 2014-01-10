package pancake;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileFinder extends SimpleFileVisitor<Path> {

    private final List<String> errors;
    private final List<String> warnings;
    private final PathMatcher matcher;

    private final List<Path> paths;
    private final FileSystem fileSystem;
    private final Path root;

    public FileFinder( FileSystem fileSystem, Path root,final PathMatcher matcher,
                       final List<String> errors, final List<String> warnings ) {
        this.fileSystem = fileSystem;
        this.root = root;
        this.matcher = matcher;
        this.errors   = errors;
        this.warnings = warnings;
        this.paths = new ArrayList<Path>();
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs ) {
        final Path relativeFile =  root.relativize(file);
        if( matcher.matches( relativeFile ) ) {
            paths.add( file );
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file, IOException exc ) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exc.printStackTrace( pw );

        errors.add("Couldn't open file:" + file.toAbsolutePath() + ".\n The following exception occured:" +  sw.toString() );
        return FileVisitResult.TERMINATE;
    }


    public static List<Path> findFiles( final FileSystem fileSystem, final Path root, final PathMatcher pathMatcher,
                                        final List<String> errors, final List<String> warnings ) {

        final FileFinder fileFinder = new FileFinder( fileSystem, root, pathMatcher, errors, warnings );
        try {
            Files.walkFileTree(root, fileFinder);

        } catch ( IOException exc )  {
            errors.add("Excecption occured while searching for file: " + Util.exceptionToString(exc) );
            return Collections.emptyList();
        }
        return new ArrayList<Path>(fileFinder.paths);
    }

    public static List<Path> findFiles( final PathMatcher pathMatcher, final List<String> errors, final List<String> warnings ) {
        final FileSystem fileSystem = FileSystems.getDefault();
        final Path root = fileSystem.getPath(System.getProperty("user.dir"));
        return findFiles( fileSystem, root, pathMatcher, errors, warnings );
    }
}
