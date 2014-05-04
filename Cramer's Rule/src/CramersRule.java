import java.util.Arrays;

public class CramersRule {
	
	// returns array of values x, y, z, ...  in that order
	public static int[] calculate(int[][] matOriginal) {
		int answers[] = new int[matOriginal[0].length];
		int x, y, z;
		int[][] matX, matY, matZ;
		
		matX = matY = matZ = new int[matOriginal[0].length][matOriginal[0].length];
		
		matX = copyContent(matX, matOriginal, 0);
		matY = copyContent(matY, matOriginal, 1);
		matZ = copyContent(matZ, matOriginal, 2);
		
		System.out.println(Arrays.deepToString(matX));
		System.out.println(Arrays.deepToString(matY));
		System.out.println(Arrays.deepToString(matZ));
		
		determinant()
		
		answers[0] = x;
		answers[1] = y;
		answers[2] = z;
		
		return 
	}
	
	private static int[][] copyContent(int dest[][], int src[][], int indexToSkip) {
		for(int i = 0; i < src[0].length; i++) {
			if(i == indexToSkip)
				continue;
			
			dest[i] = Arrays.copyOfRange(src[i], src[i][0], src[i][src[i].length - 1]);
		}
		
		return dest;
	}
	
	private static int determinant(int[][] num, int[][] den) {
		
	}
}

class Driver {
	public static void main(String args[]) {
		int mat[][] = { {2, -1, -3}, {3, 2, -3}, {1, 3, 1}, {2, -1, 0} };
		
		System.out.println(Arrays.toString(CramersRule.calculate(mat)));
	}
}