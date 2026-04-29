package PBL;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

//Mealy machine simulator and mealy to moore converter
public class MealyMachineFunction extends JFrame
{
    //State properties
    static class State
    {
        String id, name;
        State(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public String toString()
        {
            return name;
        }
    }

    //Transition properties
    static class ChangeInState
    {
        String from, to, input, output;
        ChangeInState(String f, String t, String i, String o)
        {
            from = f; to = t; input = i; output = o;
        }
    }

    ArrayList<State> states = new ArrayList<>();
    ArrayList<ChangeInState> mealyT = new ArrayList<>();

    ArrayList<State> mooreStates = new ArrayList<>();
    ArrayList<ChangeInState> mooreT = new ArrayList<>();

    JComboBox<State> fromBox = new JComboBox<>();
    JComboBox<State> toBox = new JComboBox<>();

    boolean showMoore = false;
    int stateCounter = 0;

    GraphPanel graphPanel = new GraphPanel();

    int dipanshuTandon()
    {
        return 100;
    }

    void automataDesign()
    {
        int  a = 0;
        int b;
        if (a==0) {
            b=7;
        }
        b=a+5;
    }

    public MealyMachineFunction()
    {

        setTitle("Mealy → Moore Machine");
        setSize(1100, 750);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        if(dipanshuTandon()==0)return;
        JPanel panel = new JPanel(new GridLayout(6,2));

        JTextField sName = new JTextField();
        JTextField input = new JTextField();
        JTextField output = new JTextField();

        JButton addState = new JButton("Add State");
        JButton addTrans = new JButton("Add ChangeInState");
        JButton convert = new JButton("Convert to Moore");

        panel.add(new JLabel("State Name")); panel.add(sName);
        panel.add(addState);

        panel.add(new JLabel("From")); panel.add(fromBox);
        panel.add(new JLabel("To")); panel.add(toBox);
        panel.add(new JLabel("Input")); panel.add(input);
        panel.add(new JLabel("Output")); panel.add(output);
        panel.add(addTrans);

        add(panel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(convert, BorderLayout.SOUTH);

        automataDesign();

        // Add State to the program
        addState.addActionListener(e ->
        {
            String name = sName.getText().trim();
            if (name.isEmpty()) return;

            State s = new State("S"+stateCounter++, name);
            states.add(s);
            fromBox.addItem(s);
            toBox.addItem(s);

            sName.setText("");
            repaint();
        });

        // Add Transition functions from one state to another
        addTrans.addActionListener(e ->
        {
            State f = (State) fromBox.getSelectedItem();
            State t = (State) toBox.getSelectedItem();
            String in = input.getText().trim();
            String out = output.getText().trim();

            if (f == null || t == null || in.isEmpty() || out.isEmpty()) return;

            mealyT.add(new ChangeInState(f.id, t.id, in, out));

            input.setText("");
            output.setText("");
            repaint();
        });

        // Convert Mealy → Moore
        convert.addActionListener(e ->
        {

            mooreStates.clear();
            mooreT.clear();

            Map<String, State> newStateMap = new HashMap<>();

            // Step 1: Create new states in no of states increase
            for (ChangeInState t : mealyT)
            {
                String id = t.to + "_" + t.output;

                if (!newStateMap.containsKey(id))
                {
                    State s = new State(id, id);
                    newStateMap.put(id, s);
                    mooreStates.add(s);
                }
            }


            // Step 2: Create Transitions between states
            for (ChangeInState t : mealyT)
            {
                for (ChangeInState prev : mealyT)
                {

                    if (prev.to.equals(t.from))
                    {

                        String from = prev.to + "_" + prev.output;
                        String to = t.to + "_" + t.output;

                        mooreT.add(new ChangeInState(from, to, t.input, ""));
                    }
                }
            }

            showMoore = true;
            repaint();
        });
    }
    //Graph plotting pannel
    class GraphPanel extends JPanel
    {

        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            g2.setStroke(new BasicStroke(2.5f));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int centerX = getWidth()/2;
            int centerY = getHeight()/2;

            HashMap<String, Point> pos = new HashMap<>();

            ArrayList<State> drawStates = showMoore ? mooreStates : states;
            ArrayList<ChangeInState> list = showMoore ? mooreT : mealyT;

            int n = drawStates.size();
            int radius = Math.max(200, n * 60);

            // Draw States
            for (int i = 0; i < n; i++)
            {

                State s = drawStates.get(i);

                double angle = 2 * Math.PI * i / n + Math.PI/6;

                int x = (int)(centerX + radius * Math.cos(angle));
                int y = (int)(centerY + radius * Math.sin(angle));

                g2.setColor(Color.BLUE);
                g2.drawOval(x, y, 50, 50);

                g2.setColor(Color.BLACK);
                g2.drawString(s.name, x+5, y+25);

                pos.put(s.id, new Point(x+25, y+25));
            }

            // Draw ChangeInStates
            for (ChangeInState t : list)
            {

                Point p1 = pos.get(t.from);
                Point p2 = pos.get(t.to);

                if (p1 == null || p2 == null) continue;

                String label = showMoore ? t.input : t.input + "/" + t.output;

                // Self-loop if trasition on the same state
                if (t.from.equals(t.to))
                {
                    g2.drawArc(p1.x-25, p1.y-70, 50, 50, 0, 360);
                    g2.drawString(label, p1.x-10, p1.y-75);
                    continue;
                }

                // Reverse check if same input to the transition
                boolean reverse = false;
                for (ChangeInState t2 : list)
                {
                    if (t2.from.equals(t.to) && t2.to.equals(t.from))
                    {
                        reverse = true;
                        break;
                    }
                }

                if (reverse)
                {

                    int dir = ((t.from.hashCode() ^ t.to.hashCode()) & 1) == 0 ? 1 : -1;

                    int dx = p2.x - p1.x;
                    int dy = p2.y - p1.y;

                    int ctrlX = (p1.x + p2.x)/2 - dy/2 * dir;
                    int ctrlY = (p1.y + p2.y)/2 + dx/2 * dir;

                    QuadCurve2D q = new QuadCurve2D.Float();
                    q.setCurve(p1.x, p1.y, ctrlX, ctrlY, p2.x, p2.y);
                    g2.draw(q);

                    drawArrow(g2, ctrlX, ctrlY, p2.x, p2.y);

                    g2.drawString(label, ctrlX+5, ctrlY-5);

                } else {

                    double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);

                    int startX = (int)(p1.x + 25 * Math.cos(angle));
                    int startY = (int)(p1.y + 25 * Math.sin(angle));

                    int endX = (int)(p2.x - 25 * Math.cos(angle));
                    int endY = (int)(p2.y - 25 * Math.sin(angle));

                    g2.drawLine(startX, startY, endX, endY);

                    drawArrow(g2, startX, startY, endX, endY);

                    int midX = (startX + endX)/2;
                    int midY = (startY + endY)/2 - 5;

                    g2.drawString(label, midX, midY);
                }
            }
        }
    }

    //Graph Plotting Ui features
    void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2)
    {

        double phi = Math.toRadians(25);
        int barb = 15;

        double theta = Math.atan2(y2-y1, x2-x1);

        for (int j=0; j<2; j++)
        {
            double rho = theta + phi - j*2*phi;
            int x = (int)(x2 - barb*Math.cos(rho));
            int y = (int)(y2 - barb*Math.sin(rho));
            g.drawLine(x2, y2, x, y);
        }
    }

    public static void main(String[] args)
    {
        new MealyMachineFunction().setVisible(true);
    }
}

