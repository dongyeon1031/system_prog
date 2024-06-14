package system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import main.MicroProcessor;
import system.CPU.EOperationSelectSignal;
import system.CPU.ERegister;

public class DisplayPanel extends JFrame{
	private static final long serialVersionUID = 1L;
	
	//associate
	private MicroProcessor microProcessor;
	//component
	private RegisterLabel[] registerLabels;
	private JPanel mainPanel;
	
	private MemoryPanel memoryTable;
	private JPanel registerPanel;
	private JPanel universalRegisterPanel;
	private ALUToolBar aluToolbar;
	//attribute
	
	private JButton nextButton;
	public DisplayPanel(MicroProcessor microProcessor) {
		this.microProcessor = microProcessor;
		this.setTitle("MCU architecture");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 600);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		double width = screenSize.width * 0.6;
		double height = screenSize.height * 0.6;
		this.setSize((int) width, (int) height);
		
		double x = (screenSize.width - width) * 0.5;
		double y = (screenSize.height - height) * 0.5;
		this.setLocation((int) x, (int) y);
		
		mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        
        this.aluToolbar = new ALUToolBar();
        this.mainPanel.add(this.aluToolbar, BorderLayout.NORTH);
        
        JPanel memoryPanel = new JPanel(new BorderLayout());
        mainPanel.add(memoryPanel, BorderLayout.EAST);
        
        memoryTable = new MemoryPanel();	//memory랑 매핑 -> stack 앞에 20개랑 data / code 이렇게 보여주기
        // -> SP / BP는 어떻게?
        memoryTable.setBackground(new Color(245, 245, 220)); // light beige color
        memoryTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        memoryTable.setPreferredSize(new Dimension(300, getHeight()));
        memoryPanel.add(memoryTable);
        
        nextButton = new JButton("다음 명령어 수행");
        nextButton.addActionListener(new ActionHanlder());
        memoryPanel.add(nextButton, BorderLayout.NORTH);
        
        registerLabels = new RegisterLabel[ERegister.values().length];

        for(int i=0; i<ERegister.values().length; i++) {
        	registerLabels[i] =  new RegisterLabel(ERegister.values()[i], SwingConstants.LEFT);
        }
        
        
        registerPanel = new JPanel(new GridLayout(4, 2));
        mainPanel.add(registerPanel, BorderLayout.CENTER);
        
        JPanel mmuPanel = new JPanel(new GridLayout(2,1));
        this.addRegister(mmuPanel, ERegister.eBase, getForeground());
        this.addRegister(mmuPanel, ERegister.eLimit, getForeground());
        
        // Registers and control panels
        Color lightGreen = new Color(144, 238, 144);
        Color lightPurple = new Color(216, 191, 216);
        Color lightGray = new Color(202, 222, 104);
        addRegister(registerPanel, ERegister.ePC, lightGreen); // 
        addRegister(registerPanel, ERegister.eMAR, lightGreen); // 
        addRegister(registerPanel, ERegister.eIR, lightGreen); // 
        addRegister(registerPanel, ERegister.eMBR, lightGreen); // 
        addRegister(registerPanel, ERegister.eSP, lightPurple); // 
        addRegister(registerPanel, ERegister.eStatus, lightGray); // 
        addRegister(registerPanel,  ERegister.eBP, lightPurple); // 
//        addRegister(registerPanel, ERegister.eLimit, lightGray); //
        registerPanel.add(mmuPanel);
        
        universalRegisterPanel = new JPanel(new GridLayout(9, 1));
        universalRegisterPanel.setPreferredSize(new Dimension(200, getHeight())); // Increase the size of the left panel
        mainPanel.add(universalRegisterPanel, BorderLayout.WEST);
        
        Color lightBlue = new Color(173, 216, 230);
        for (int i = 0; i <= 8; i++) { // 각 레지스터랑 매핑
            addRegister(universalRegisterPanel, ERegister.values()[i], lightBlue); // light blue
        }
        
        setVisible(true);
        this.updateUI();
	}
	
    private void addRegister(JPanel panel, ERegister register, Color color) {
    	RegisterLabel registerLabel = registerLabels[register.ordinal()];
        registerLabel.setOpaque(true);
        registerLabel.setBackground(color);
        registerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), 
                BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Add margins
        panel.add(registerLabel);
    }
	private class ActionHanlder implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if(!microProcessor.getCPU().run()) {
					microProcessor.bPowerOn = false;
				}
				microProcessor.getVGA().run();
				updateUI();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		
	}
	
	private void updateUI() {
		long[] registerState = this.microProcessor.getCPU().getState();
		for(int i = 0 ; i<registerState.length; i++) {
			this.registerLabels[i].setText(String.format("%08x", registerState[i]));
		}
//		System.out.println(registerState[ERegister.eBP.ordinal()]);
		this.memoryTable.setInstruction(this.microProcessor.getMemory().getState(), 
				(registerState[ERegister.eBP.ordinal()]+registerState[ERegister.eBase.ordinal()]),
				(registerState[ERegister.eSP.ordinal()]+registerState[ERegister.eBase.ordinal()]),
				(registerState[ERegister.ePC.ordinal()]+registerState[ERegister.eBase.ordinal()]));
		this.aluToolbar.setInfo(this.microProcessor.getCPU().getALUState());
	}
	
	private class RegisterLabel extends JLabel{
		private static final long serialVersionUID = 1L;
		private ERegister mappingRegister;
		public RegisterLabel(ERegister r, int horizontalAlignment) {
			super(r.name(), horizontalAlignment);
			this.mappingRegister = r;
		}
		@Override
		public void setText(String text) {
			if(this.mappingRegister == null) {
				super.setText(text);
			}else {
				super.setText(this.mappingRegister.name()+" : "+text);	
			}
		}
	}
	
	private class MemoryPanel extends JScrollPane{
		private static final long serialVersionUID = 1L;
		private MemoryTable jTable;
		private DefaultTableModel model;
		private String sp;
		private String pc;
		private String bp;
		public MemoryPanel() {
			String[] title = {"address", "instruction"};
			this.model = new DefaultTableModel(null, title);
			this.jTable = new MemoryTable();
			//associate
			this.jTable.setModel(model);
			this.setViewportView(jTable);
			
		}
		public void setInstruction(long[][] instructions, long bp, long sp, long pc) {
			this.model.setRowCount(0);
			for(int i = 0; i < instructions.length; i++) {
				String[] row = {Long.toHexString(instructions[i][0]), String.format("%08x", instructions[i][1])};
				this.model.addRow(row);
			}
			this.bp = Long.toHexString(bp);
			this.sp = Long.toHexString(sp);
			this.pc = Long.toHexString(pc);
			
		}
		
		private class MemoryTable extends JTable{
	        private static final long serialVersionUID = 1L;
	        @Override
	        public boolean isCellEditable(int row, int column) {
	        	// 행 편집이 불가능하게 설정
	            return false;
	        }
			@Override
	        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
	            Component c = super.prepareRenderer(renderer, row, column);
	            String address = (String) this.getModel().getValueAt(row, 0);
	            
	            if(address.equals(bp)) {
	            	c.setBackground(Color.magenta);
	            }if(address.equals(pc)) {
	        		c.setBackground(Color.cyan);
	        	}else if(address.equals(sp)){
	        		c.setBackground(Color.LIGHT_GRAY);
	        	}else if(this.isRowSelected(row)) {
	            	c.setBackground(getSelectionBackground());
	            }else {	
	            	c.setBackground(Color.WHITE);
	            }
	            return c;
	        }
		}
	}
	
	private class ALUToolBar extends JToolBar{
		private static final long serialVersionUID = 1L;
		private JLabel infoLabel;
		private final String aluInfo = "ALU state : ";
		public ALUToolBar() {
			this.infoLabel = new JLabel(aluInfo);
			this.add(infoLabel);
		}
		public void setInfo(EOperationSelectSignal signal) {
			if(signal != null) {
				this.infoLabel.setText(aluInfo + signal.name());	
			}else {
				this.infoLabel.setText(aluInfo + "none");
			}
		}
	}
	
}
