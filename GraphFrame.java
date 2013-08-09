package TimeIndependentSchrodinger;
import javax.swing.*;
@SuppressWarnings("serial")
public class GraphFrame extends JFrame
{
  public GraphFrame(String name, int width, int height)
	{
		setVisible(true);
		setTitle(name);
		setSize(width + 20, height + 40);
	}
}
