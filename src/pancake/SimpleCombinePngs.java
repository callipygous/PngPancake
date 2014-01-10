package pancake;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Owner
 * Date: 1/9/14
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleCombinePngs{

    private final int desiredCellWidth;
    private final int desiredCellHeight;

    private final int destImageWidth;
    private final int destImageHeight;

    private final int numCols;
    private final int numRows;

    private final File outputFile;
    private final List<File> argbImages;

    private final int fillColor;


    public SimpleCombinePngs(Pair<Integer, Integer> sourceImageSize, Pair<Integer, Integer> dimensions,
                             int rgba, List<File> argbImages, File outputFile ) {
        desiredCellWidth  = sourceImageSize._1;
        desiredCellHeight = sourceImageSize._2;

        numCols = dimensions._1;
        numRows = dimensions._2;

        destImageWidth  = dimensions._1 * desiredCellWidth;
        destImageHeight = dimensions._2 * desiredCellHeight;

        this.fillColor = rgba;

        this.outputFile = outputFile;
        this.argbImages = argbImages;
    }

    public void combine() throws IOException {
        final BufferedImage destImage = new BufferedImage( destImageWidth, destImageHeight, BufferedImage.TYPE_INT_ARGB );
        final Graphics2D dest2D = destImage.createGraphics();

        for( int i = 0; i < argbImages.size(); i++ ) {
            final int rowIndex = i / numCols;
            final int colIndex = i % numCols;

            //IMPORTANT: RATHER THAN GIVE AN EASY WAY TO READ INTO THE BUFFEREDIMAGE JAVA OF COURSE READS INTO A NEW IMAGE
            //EACH TIME, GOING TO JUST USE THE TIME CONSUMING METHOD SINCE I HAVE NO NEED FOR EFFICIENCY AT THE MOMENT
            final BufferedImage srcImage = ImageIO.read( argbImages.get(i) );
            printImage( rowIndex, colIndex, srcImage, destImage, dest2D);
        }

        ImageIO.write( destImage, "png", outputFile );
    }


    private void printImage( int row, int col, final BufferedImage srcImage, final BufferedImage destImage, Graphics2D dest2D ) {
        if( srcImage.getWidth() > desiredCellWidth || srcImage.getHeight() > desiredCellHeight ) {
            throw new RuntimeException("Iimage size too great: (" + srcImage.getWidth() + ", " + srcImage.getHeight() + ")");
        }

        int startX = col * desiredCellWidth;
        int startY = row * desiredCellHeight;
        int endX = startX + desiredCellWidth;
        int endY = startY + desiredCellHeight;


        if( srcImage.getWidth() < desiredCellWidth ) {

            final int missingWidth = desiredCellWidth - srcImage.getWidth();
            final int leftMissingWidth = (int) Math.floor( missingWidth / 2 );
            final int rightMissingWidth = missingWidth - leftMissingWidth;

            //fill in the area to the left and the right of the image with the fill color
            fillRect( destImage, fillColor, startX, startY, leftMissingWidth, desiredCellHeight );
            fillRect( destImage, fillColor, startX + srcImage.getWidth(), startY, rightMissingWidth, desiredCellHeight );

            startX += leftMissingWidth;
        }

        if( srcImage.getHeight() < desiredCellHeight) {
            //fill in the area to the top and bottom of the image with the fill color
            final int missingHeight = desiredCellHeight - srcImage.getHeight();
            final int topMissingHeight  = (int) Math.floor( missingHeight / 2 );
            final int bottomMissingHeight = missingHeight - topMissingHeight;

            fillRect( destImage, fillColor, startX, startY, srcImage.getWidth(), topMissingHeight );
            fillRect( destImage, fillColor, startX, startY + srcImage.getHeight(), srcImage.getWidth(), bottomMissingHeight );

            startY += topMissingHeight;
        }

        dest2D.drawImage( srcImage, startX, startY,  null);
    }

    private void fillRect( BufferedImage image, int fillColor, int startX, int startY, int width, int height ) {
        int destX = startX + width;
        int destY = startY + height;

        for(int j = startY; j < destY; j++ ) {
            for( int i = startX; i < destX; i++ ) {
                image.setRGB( i, j, fillColor );
            }
        }
    }
}
