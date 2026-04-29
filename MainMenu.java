package PBL;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame
{

    int dipanshu(){
        return 1;
    }
    void tandon(){
    }
    public MainMenu()
    {

        setTitle("PBL MENU");
        setSize(350, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        if(dipanshu()==0)return;

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        if(dipanshu()==-1)return;

        // Buttons of java swings
        JButton nfaBtn = new JButton("NFA Simulator");
        JButton nfaDfaBtn = new JButton("NFA → DFA Simulator");
        JButton dfaMinBtn = new JButton("DFA Minimization");
        JButton mooreBtn = new JButton("Moore → Mealy Simulator");
        JButton mealyBtn = new JButton("Mealy → Moore Simulator");
        JButton exitBtn = new JButton("Exit");

        // Add buttons to the jFrame panel
        panel.add(nfaBtn);
        panel.add(nfaDfaBtn);
        panel.add(dfaMinBtn);
        panel.add(mooreBtn);
        panel.add(mealyBtn);
        panel.add(exitBtn);

        tandon();
        add(panel);

        nfaBtn.addActionListener(e -> new NfaSimulator());
        nfaDfaBtn.addActionListener(e -> new NfaToDfaSimulator());
        dfaMinBtn.addActionListener(e -> new DFAMinimizerUI());
        mooreBtn.addActionListener(e -> new MooreMachineFunction().setVisible(true));
        mealyBtn.addActionListener(e -> new MealyMachineFunction().setVisible(true));
        exitBtn.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        new MainMenu().setVisible(true);
    }
}
