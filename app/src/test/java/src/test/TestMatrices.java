package src.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import src.traj.Matrix;
import src.traj.Matrix2d;
import src.traj.Matrix3d;

public class TestMatrices
{
    @Test
    void testSetter()
    {
        Matrix2d matrix = new Matrix2d();
        System.out.println("Initial: \n" + matrix.toString());

        /*Expected result*/
        double[] row1 = {0, 0};
        double[] row2 = {0, 10};
        Matrix2d result = new Matrix2d(row1, row2);

        matrix.set(1,1,10);
        System.out.println("After: \n" + matrix.toString());

        assertTrue(matrix.equals(result));
    }

    @Test
    void testGetter()
    {
        double[] row1 = {23, 55, 0};
        double[] row2 = {3, 11, 10};
        double[] row3 = {7, 17, 5};
        Matrix3d matrix = new Matrix3d(row1, row2, row3);
        System.out.println("Matrix: \n" + matrix.toString());

        assertEquals(matrix.get(0,0), 23);
        assertEquals(matrix.get(0,1), 55);
        assertEquals(matrix.get(0,2), 0);
        assertEquals(matrix.get(1,0), 3);
        assertEquals(matrix.get(1,1), 11);
        assertEquals(matrix.get(1,2), 10);
        assertEquals(matrix.get(2,0), 7);
        assertEquals(matrix.get(2,1), 17);
        assertEquals(matrix.get(2,2), 5);
    }

    @Test
    void testDeterminant2D()
    {
        double[] row1 = {1, 4};
        double[] row2 = {6, 0};
        Matrix2d matrix = new Matrix2d(row1, row2);
        System.out.println("Matrix: \n" + matrix.toString());

        double determinant = matrix.determinant();
        System.out.println("determinant: "+ determinant);
    }

    @Test
    void testDeterminant3D()
    {
        double[] row1 = {1, 2, 3};
        double[] row2 = {0, 1, 4};
        double[] row3 = {5, 6, 0};
        Matrix3d matrix = new Matrix3d(row1, row2, row3);
        System.out.println("Matrix: \n" + matrix.toString());

        double determinant = matrix.calculateDeterminant();
        System.out.println("Determinant: " + determinant);

        assertEquals(determinant, 1);
    }

    @Test
    void testIsInvertable()
    {
        double[] row1 = {1, 2, 3};
        double[] row2 = {0, 1, 4};
        double[] row3 = {5, 6, 0};
        Matrix3d matrix = new Matrix3d(row1, row2, row3);
        System.out.println("Matrix: \n" + matrix.toString());

        assertTrue(matrix.determineIfInvertable());
    }

    @Test
    void testTranspose()
    {
        double[] row1 = {-24, 20, -5};
        double[] row2 = {18, -15, 4};
        double[] row3 = {5, -4, 1};
        Matrix3d matrix = new Matrix3d(row1, row2, row3);
        System.out.println("Matrix: \n" + matrix.toString());

        Matrix3d transpose = matrix.matrixTranspose();
        System.out.println("Transpose: \n" + transpose.toString());

        /*Correct output matrix*/
        double[] row1Output = {-24, 18, 5};
        double[] row2Output = {20, -15, -4};
        double[] row3Output = {-5, 4, 1};
        Matrix3d accurateTranspose = new Matrix3d(row1Output, row2Output, row3Output);
        System.out.println("Accurate transpose: \n" + accurateTranspose.toString());

        assertTrue(transpose.equals(accurateTranspose));

    }
}
