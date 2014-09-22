import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

public class Runner {
	private static String destDir;
	private static boolean row = false, // defaults to --bottom
						   top = false,
						   seed = false;
	private static Integer[][] colorArr;
	private static final Random rand = new Random();
	private static int p1, p2, width, height;

	public static void main(String[] args) {
		String source = null;
		
		// Parse command line arguments
		if (args.length > 0) {
			source = args[0];
			for (int i = 1; i < args.length; i++) {
				switch(args[i].toLowerCase()) {
					case "--top":
						row = false;
						top = true;
						break;
					case "--right":
						row = true;
						top = false;
						break;
					case "--bottom":
						row = false;
						top = false;
						break;
					case "--left":
						row = true;
						top = true;						
						break;
					case "--seed":
						rand.setSeed(new Date().getTime());
						seed = true;
						break;
					default:
						failWithError("Command line argument " + args[i] + " unrecognized. \nCurrently supported arguments: --top --right --bottom --left --seed");
				}
			}
		} else {
			failWithError("No input file specified.");
		}
		
		// Read file & initiate class variables
		File sourceFile = new File(source);
		BufferedImage img = null;
		
		try {
			img = ImageIO.read(sourceFile);
		} catch (IOException e) {
			failWithError("Input file " + source + " not found.");
		}
		createDir(sourceFile);

		height = img.getHeight();
		width = img.getWidth();
		p1 = row ? height : width;
		p2 = row ? width : height;
		colorArr = row ? new Integer[height][width] : new Integer[width][height];		
		
		for (int i = 0; i < p1; i++) {
			for (int j = 0; j < p2; j++) {
				colorArr[i][j] = row ? img.getRGB(j, i) : img.getRGB(i, j);
			}
		}

		arraysSort();
	}

	private static void failWithError(String msg) {
		System.err.println(msg);
		System.exit(1);
	}
	
	// Picks the topmost/leftmost pixel to be sorted
	private static int getFirst(Integer[] colorArr) {
		int start;
		if (top) {
			start = 0;
		} else {
			start = getEnd(colorArr);
		}
		return start;
	}
	
	// Picks the bottommost/rightmost pixel to be sorted
	private static int getSecond(Integer[] colorArr) {
		int end;
		if (top) {
			end = getEnd(colorArr);
		} else {
			end = p2;
		}
		return end;
	}

	// Picks the pixel where sorting will end
	private static int getEnd(Integer[] colorArr) {
		if (seed) {
			return rand.nextInt(row ? width : height);
		} else {
			int n = Arrays.binarySearch(colorArr, -16777216);
			if (n == -1) {
				// Rule of thirds: "an image should be imagined as divided into nine equal parts by two 
				// equally spaced horizontal lines and two equally spaced vertical lines, and that important 
				// compositional elements should be placed along these lines or their intersections"
				// - http://en.wikipedia.org/wiki/Rule_of_thirds
				int start = rand.nextInt(p2 / 3);
				n = rand.nextInt(p2 / 3) + start;
			}
			return n;
		}
	}

	// Creates a directory for the results in the same directory as the source file
	private static void createDir(File sourceFile) {
		try {
			destDir = sourceFile.getParent() + File.separator + "PSResults" + new Date().getTime();
			if (!(new File(destDir).mkdir())) {
				System.out.println("Could not create directory " + destDir + ". It may already exist.");
			}
		} catch(SecurityException e) {
			failWithError("Directory could not be created.");
		}
	}

	private static void arraysSort() {
		for (int i = 0; i < p1; i++) {
			Arrays.sort(colorArr[i], getFirst(colorArr[i]), getSecond(colorArr[i]), (top) ? null : Collections.reverseOrder());
		}
		savePicture("0.png");
	}

	private static void savePicture(String fileName) {
		BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Must do this to get the right format ImageIO.write wants
		// TODO: replace with something prettier and more efficient
		for(int i = 0; i < p1; i++) {
			for(int j = 0; j < p2; j++) {
				if (row) {
					resultImage.setRGB(j, i, colorArr[i][j]);
				} else {
					resultImage.setRGB(i, j, colorArr[i][j]);
				}
			}
		}
		
		String path = destDir + File.separator + fileName;
		File outputfile = new File(path);

	    try {
	    	ImageIO.write(resultImage,"png",outputfile);
			System.out.println("File " + path + " created.");
	    } catch (IOException e) {
			failWithError("File" + path + " could not be created.");
		}
	}
}
