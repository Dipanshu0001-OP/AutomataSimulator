package PBL;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

//Moore machine functioning and Moore to Meley machine conversion
public class MooreMachineFunction extends JFrame
{

    //states definition
    static class State
    {
        String id, name, output;
        State(String id, String n, String o) {
            this.id = id; name = n; output = o;
        }
        public String toString() {
            return name + "/" + output;
        }
    }

    //trasition definition
    static class Transition
    {
        String from, to, input, output;
        Transition(String f, String t, String i, String o) {
            from = f; to = t; input = i; output = o;
        }
    }

    ArrayList<State> states = new ArrayList<>();
    ArrayList<Transition> mooreT = new ArrayList<>();
    ArrayList<Transition> mealyT = new ArrayList<>();

    JComboBox<State> fromBox = new JComboBox<>();
    JComboBox<State> toBox = new JComboBox<>();

    boolean showMealy = false;
    int stateCounter = 0;

    GraphPanel graphPanel = new GraphPanel();

    //Moore machine function
    public MooreMachineFunction()
    {

        setTitle("Moore → Mealy Machine");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6,2));

        JTextField sName = new JTextField();
        JTextField sOut = new JTextField();
        JTextField input = new JTextField();

        JButton addState = new JButton("Add State");
        JButton addTrans = new JButton("Add Transition");
        JButton convert = new JButton("Convert to Mealy");

        panel.add(new JLabel("State Name")); panel.add(sName);
        panel.add(new JLabel("State Output")); panel.add(sOut);
        panel.add(addState);

        panel.add(new JLabel("From")); panel.add(fromBox);
        panel.add(new JLabel("To")); panel.add(toBox);
        panel.add(new JLabel("Input")); panel.add(input);
        panel.add(addTrans);

        add(panel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(convert, BorderLayout.SOUTH);

        addState.addActionListener(e -> {
            String name = sName.getText().trim();
            String out = sOut.getText().trim();

            if (name.isEmpty() || out.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter valid state!");
                return;
            }

            String id = "S" + stateCounter++;
            State s = new State(id, name, out);
            states.add(s);

            fromBox.addItem(s);
            toBox.addItem(s);

            sName.setText("");
            sOut.setText("");

            repaint();
        });

        addTrans.addActionListener(e ->
        {
            State f = (State) fromBox.getSelectedItem();
            State t = (State) toBox.getSelectedItem();
            String in = input.getText().trim();

            if (f == null || t == null || in.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }

            mooreT.add(new Transition(f.id, t.id, in, ""));
            input.setText("");
            repaint();
        });

        convert.addActionListener(e ->
        {
            mealyT.clear();

            for (Transition t : mooreT) {
                String out = getOutput(t.to);
                mealyT.add(new Transition(t.from, t.to, t.input, out));
            }

            showMealy = true;
            repaint();
        });
    }

    String getOutput(String id)
    {
        for (State s : states)
            if (s.id.equals(id)) return s.output;
        return "";
    }

    class GraphPanel extends JPanel
    {
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));

            int centerX = getWidth()/2;
            int centerY = getHeight()/2;

            Set<String> drawStates = new HashSet<>();

            if (showMealy) {
                for (Transition t : mealyT) {
                    drawStates.add(t.from);
                    drawStates.add(t.to);
                }
            } else {
                for (State s : states) drawStates.add(s.id);
            }

            int n = drawStates.size();
            int radius = Math.max(200, n * 60);

            HashMap<String, Point> pos = new HashMap<>();

            int i = 0;
            for (String id : drawStates) {

                double angle = 2 * Math.PI * i / n + Math.PI/6;

                int x = (int)(centerX + radius * Math.cos(angle));
                int y = (int)(centerY + radius * Math.sin(angle));

                y += (i % 2 == 0) ? 25 : -25;

                g2.drawOval(x, y, 50, 50);

                String label = id;

                if (!showMealy) {
                    for (State s : states)
                        if (s.id.equals(id))
                            label = s.name + "/" + s.output;
                }

                g2.drawString(label, x+5, y+25);

                pos.put(id, new Point(x+25, y+25));
                i++;
            }

            ArrayList<Transition> list = showMealy ? mealyT : mooreT;

            for (Transition t : list)
            {

                Point p1 = pos.get(t.from);
                Point p2 = pos.get(t.to);

                if (p1 == null || p2 == null) continue;

                String label = showMealy ? t.input + "/" + t.output : t.input;

                // Self loop
                if (t.from.equals(t.to)) {
                    g2.drawArc(p1.x-25, p1.y-70, 50, 50, 0, 360);
                    g2.drawString(label, p1.x-10, p1.y-75);
                    continue;
                }

                // Check reverse
                boolean reverse = false;
                for (Transition t2 : list) {
                    if (t2.from.equals(t.to) && t2.to.equals(t.from)) {
                        reverse = true;
                        break;
                    }
                }

                if (reverse) {

                    int dir = ((t.from.hashCode() ^ t.to.hashCode()) & 1) == 0 ? 1 : -1;

                    int dx = p2.x - p1.x;
                    int dy = p2.y - p1.y;

                    int ctrlX = (p1.x + p2.x)/2 - dy/2 * dir;
                    int ctrlY = (p1.y + p2.y)/2 + dx/2 * dir;

                    QuadCurve2D q = new QuadCurve2D.Float();
                    q.setCurve(p1.x, p1.y, ctrlX, ctrlY, p2.x, p2.y);
                    g2.draw(q);

                    drawArrow(g2, ctrlX, ctrlY, p2.x, p2.y);
                    g2.drawString(label, ctrlX + 5, ctrlY - 5);

                } else {
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    drawArrow(g2, p1.x, p1.y, p2.x, p2.y);

                    int midX = (p1.x + p2.x)/2;
                    int midY = (p1.y + p2.y)/2 - 5;

                    g2.drawString(label, midX, midY);
                }
            }
        }
    }

    //addition of arrow to the transition lines
    void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        double phi = Math.toRadians(25);
        int barb = 15;

        double theta = Math.atan2(y2-y1, x2-x1);

        for (int j=0; j<2; j++) {
            double rho = theta + phi - j*2*phi;
            int x = (int)(x2 - barb*Math.cos(rho));
            int y = (int)(y2 - barb*Math.sin(rho));
            g.drawLine(x2, y2, x, y);
        }
    }

    public static void main(String[] args) {
        new MooreMachineFunction().setVisible(true);
    }
}
