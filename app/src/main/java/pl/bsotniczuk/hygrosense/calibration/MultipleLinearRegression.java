package pl.bsotniczuk.hygrosense.calibration;

import Jama.Matrix;
import Jama.QRDecomposition;


/**
 *  The {@code MultipleLinearRegression} class performs a multiple linear regression
 *  on an set of <em>N</em> data points using the model
 *  <em>y</em> = &beta;<sub>0</sub> + &beta;<sub>1</sub> <em>x</em><sub>1</sub> + ... +
 &beta;<sub><em>p</em></sub> <em>x<sub>p</sub></em>,
 *  where <em>y</em> is the response (or dependent) variable,
 *  and <em>x</em><sub>1</sub>, <em>x</em><sub>2</sub>, ..., <em>x<sub>p</sub></em>
 *  are the <em>p</em> predictor (or independent) variables.
 *  The parameters &beta;<sub><em>i</em></sub> are chosen to minimize
 *  the sum of squared residuals of the multiple linear regression model.
 *  It also computes the coefficient of determination <em>R</em><sup>2</sup>.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class MultipleLinearRegression {
    private final Matrix beta;  // regression coefficients
    private double sse;         // sum of squared
    private double sst;         // sum of squared

    public MultipleLinearRegression(double[][] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("matrix dimensions don't agree");
        }
        int n = y.length;

        Matrix matrixX = new Matrix(x);
        Matrix matrixY = new Matrix(y, n);

        QRDecomposition qr = new QRDecomposition(matrixX);
        beta = qr.solve(matrixY);

        double sum = 0.0;
        for (int i = 0; i < n; i++)
            sum += y[i];
        double mean = sum / n;

        for (int i = 0; i < n; i++) {
            double dev = y[i] - mean;
            sst += dev*dev;
        }
        Matrix residuals = matrixX.times(beta).minus(matrixY);
        sse = residuals.norm2() * residuals.norm2();
    }

    public double beta(int j) {
        return beta.get(j, 0);
    }

    public double R2() {
        return 1.0 - sse/sst;
    }

}

