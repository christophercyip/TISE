package TimeIndependentSchrodinger;
import java.util.*;
import javax.swing.*;
public class Function
{
  ///// Constants /////
	static float pi = (float) 3.141592653589793;
	static float h = (float) 6.62606957; // e-34
	static float m = (float) 9.10938188; // e-31
	static float c = (float) 1.60217646e-19;
	static float delta_x = (float) 5e-13;
	static int n;
	
	///// Fields /////
	float[] xValues;
	float[] yValues;
	float max;

	///// Constructor /////
	public Function(float[] xVal, float[] yVal)
	{
		xValues = xVal.clone();
		yValues = yVal.clone();
		max = 0;
		for (int i = 0; i < yValues.length; i++)
			if (Math.abs(yValues[i]) > max)
				max = Math.abs(yValues[i]);
	}

	public static void main(String[] args) throws InterruptedException
	{
		// X-value initialization
		float[] x = new float[10001];
		for (int i = 0; i <= 5000; i++)
		{
			x[5000 + i] = i * delta_x;
			x[5000 - i] = -i * delta_x;
		}
		
		// Take Console Input
		Scanner s = new Scanner(System.in);
		System.out.println("0: Paddleball\n1: Harmonic\n2: Finite well\n3: Bounded ramp\n4: Uneven well");
		int id = s.nextInt();
		System.out.println("Energy level:");
		n = s.nextInt();
		
		// Potential initialization
		float[] V = new float[10001];
		for (int i = 0; i <= 10000; i++)
		{
			switch (id)
			{
				case 0: // "Paddleball" Potential
					if (x[i] < 0)
						V[i] = 2 * x[i] * x[i];
					else
						V[i] = 5000 * c;
					break;
				case 1: // Harmonic Oscillator Potential
					V[i] = 2 * x[i] * x[i];
					break;
				case 2: // Finite Well Potential
					if (Math.abs(x[i]) < 5e-10)
						V[i] = 0;
					else
						V[i] = 100 * c;
					break;
				case 3: // Bounded Ramp Potential
					if (Math.abs(x[i]) < 8e-10)
						V[i] = (float) ((x[i] + 8e-10) * 3e-9);
					else
						V[i] = 100 * c;
					break;
				case 4: // Uneven Potential Well
					if (Math.abs(x[i]) > 5e-10)
						V[i] = 100 * c;
					else if (x[i] < 0)
						V[i] = 0;
					else
						V[i] = 10 * c;
					break;
			}
		}
		
		// Find the appropriate energy value
		float E = find(x, V);
		float[] psi = calculate(x, V, E);
		trim_and_scale(psi);
		
		// Probability calculation
		float[] P = new float[10001];
		for (int i = 0; i <= 10000; i++)
			P[i] = psi[i] * psi[i];

		// Plot the functions
		GraphFrame frame = new GraphFrame("Schrodinger Solver", 1000, 660);
		Function potential = new Function(x, V);
		Function wavefunction = new Function(x, psi);
		Function probability = new Function(x, P);
		GraphComponent all_functions = new GraphComponent(potential, wavefunction, probability, E);
		frame.add(all_functions);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static float[] calculate(float[] x, float[] V, float E)
	{
		// Initial Conditions
		float[] psi = new float[10001];
		psi[0] = 0;
		psi[1] = (float) 1e-15;

		// Calculation (The exponents are hard-coded due to potential issues with rounding down to zero)
		for (int i = 2; i <= 10000; i++)
			psi[i] = 
				(float) (2*psi[i-1] - psi[i-2] - (pi*pi*m*delta_x*delta_x) * (8e37) / (h*h) * (E - V[i]) * psi[i-1]);
		return psi;
	}
	
	public static float find(float[] x, float[] V)
	{
		// Find a suitable upper bound via successive doubling
		float low = 0;
		float high = c;
		while (true)
		{
			float[] psi_high = calculate(x, V, high);
			int bumps = find_bumps(psi_high);
			if (bumps > n || (bumps == n && ((n % 2 == 0) == (find_unbounded_sign(psi_high)))))
				break;
			high *= 2;
		}

		// Perform a binary search
		float last_low = low, last_high = high;
		boolean change = true;
		while (change)
		{
			float middle = (low + high) / 2;
			float[] psi_middle = calculate(x, V, middle);
			int bumps = find_bumps(psi_middle);
			if (bumps > n || (bumps == n && ((n % 2 == 0) == (find_unbounded_sign(psi_middle)))))
				high = middle;
			else
				low = middle;
			change = (low != last_low) || (high != last_high);
			last_low = low;
			last_high = high;
		}
		return low;
	}

	public static void trim_and_scale(float[] psi)
	{
		// Trim the final wavefunction
		try
		{
			float[] trimmed = truncate(psi);
			for (int i = 0; i < psi.length; i++)
				psi[i] = trimmed[i];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Bounded energy state with " + n + " bumps does not exist for given potential.");
			System.exit(0);
		}
		
		// Scale the wavefunction so that the maximum is 1
		float max = 0;
		for (int i = 0; i <= 10000; i++)
			if (psi[i] > max)
				max = psi[i];
		for (int i = 0; i <= 10000; i++)
			psi[i] /= max;
	}
	
	// Find which direction the wavefunction diverges to infinity (positive or negative)
	public static boolean find_unbounded_sign(float[] a)
	{
		for (int i = 0; i < a.length; i++)
			if (Double.isNaN(a[i]))
				return a[i-1] > 0;
		return a[a.length - 1] > 0;
	}

	// Find how many "bumps" the wavefunction has in the bounded region
	public static int find_bumps(float[] a)
	{
		int bumps = 0;
		boolean inc = a[1] > a[0];
		for (int i = 1; i < a.length && !Double.isNaN(a[i]); i++)
		{
			if (inc != (a[i] > a[i-1]) && inc == (a[i] > 0))
			{
				bumps++;
				inc = !inc;
			}
		}
		return bumps;
	}

	// Set all values in the "infinite end" to zero
	public static float[] truncate(float[] a)
	{
		float[] new_psi = a.clone();
		boolean pos = false, inc = false, con = false;
		for (int i = a.length - 1; pos != con || pos == inc || Double.isNaN(a[i]); i--)
		{
			pos = a[i] > 0;
			inc = a[i] > a[i-1];
			con = (a[i] - a[i-1]) > (a[i-1] - a[i-2]);
			new_psi[i] = 0;
		}
		return new_psi;
	}
}
