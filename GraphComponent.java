package TimeIndependentSchrodinger;
import java.awt.*;
import javax.swing.*;
@SuppressWarnings("serial")
public class GraphComponent extends JComponent
{
  ///// Fields /////
	Function V;
	Function psi;
	Function P;
	float E;

	///// Constructor /////
	public GraphComponent(Function f1, Function f2, Function f3, float energy)
	{
		V = f1;
		psi = f2;
		P = f3;
		E = energy;
	}

	public void paintComponent(Graphics g)
	{
		// Scale all the graphs accordingly
		float range = Math.min(V.max, 100 * Function.c);
		float scale1 = (float) (1 / range);
		float scale2 = (float) (0.9 / psi.max);
		float scale3 = (float) (0.9 / P.max);
		
		// Draw all horizontal gridlines
		g.setColor(Color.gray);
		for (int i = 0; i < 100 && (i * Function.c) < range; i += 10)
		{
			int x = (int) (190 * (i * Function.c) / range);
			g.drawLine(0, 220 - x, 1000, 220 - x);
		}
		for (int i = 250; i < 440; i += 15)
			g.drawLine(0, i, 1000, i);
		for (int i = 470; i < 660; i += 15)
			g.drawLine(0, i, 1000, i);

		// Potential graphing
		g.setColor(Color.green);
		for (int i = 0; i < V.xValues.length; i++)
		{
			int y = 220 + (int) (scale1 * -190 * (V.yValues[i]));
			if (scale1 * Math.abs(V.yValues[i]) < 1)
				g.drawLine(i / 10, 220, i / 10, y);
			else
				g.drawLine(i / 10, 220, i / 10, 30);
		}
		
		// Wavefunction graphing
		g.setColor(Color.red);
		for (int i = 0; i < psi.xValues.length; i++)
		{
			int y = 340 + (int) (scale2 * -100 * (psi.yValues[i]));
			if (scale2 * Math.abs(psi.yValues[i]) < 1)
				g.drawLine(i / 10, 340, i / 10, y);
		}
		
		// Probability graphing
		g.setColor(Color.orange);
		for (int i = 0; i < P.xValues.length; i++)
		{
			int y = 660 + (int) (scale3 * -200 * (P.yValues[i]));
			if (scale3 * Math.abs(P.yValues[i]) < 1)
				g.drawLine(i / 10, 660, i / 10, y);
		}

		// Show text and draw boundaries
		g.setColor(Color.black);
		g.drawString("Energy: " + (E / Function.c) + " eV   for   n = " + Function.n, 10, 25);
		g.drawString("Wavefunction", 10, 245);
		g.drawString("Probability", 10, 465);
		g.drawLine(0, 229, 1000, 229);
		g.drawLine(0, 230, 1000, 230);
		g.drawLine(0, 231, 1000, 231);
		g.drawLine(0, 449, 1000, 449);
		g.drawLine(0, 450, 1000, 450);
		g.drawLine(0, 451, 1000, 451);
		g.drawLine(0, 220, 1000, 220);
		g.drawLine(0, 340, 1000, 340);
		g.drawLine(0, 660, 1000, 660);
		
		// Draw a line indicating the energy level
		g.setColor(Color.blue);
		int energy_level = (int) (220 - 190 * scale1 * E);
		g.drawLine(0, energy_level, 1000, energy_level);
		
		// Draw classical turning points
		g.setColor(Color.black);
		boolean pos = E > V.yValues[0];
		for (int i = 0; i < V.xValues.length; i++)
		{
			if (pos != (E > V.yValues[i]))
			{
				pos = !pos;
				for (int y = 30; y < 660; y += 4)
					g.drawLine(i / 10, y, i / 10, y + 1);
			}
		}
	}
}
