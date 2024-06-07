package IOdevice;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import system.Memory;

public class Monitor extends JFrame{
	private static final long serialVersionUID = 1L;
	//association
	Memory memory;
	
	//component
//	private String information = "";
	private int buffer; //일단 버퍼를 integer 하나로 만들자. 
	private Console console;

    public Monitor(Memory memory, Keyboard keyboard) {
    	this.memory = memory;
    	this.buffer = 0;
    	
        setTitle("Monitor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        
        this.console = new Console(this, keyboard);
        this.add(new JScrollPane(console));
        this.setVisible(true);
    }
    
    public void run() {
    	if(memory.interrupted) {
    		this.buffer = this.memory.load(Memory.PRINT_INTERRUPT);
    		this.console.write(Integer.toString(this.buffer));
    		memory.interrupted = false;
    	}
    }
    private class Console extends JPanel{
    	// TUI console
		private static final long serialVersionUID = 1L;
    	//associate
		private Monitor monitor;
		private Keyboard keyboard;
		//components
		JTextArea textArea;
		public Console(Monitor monitor, Keyboard keyboard) {
			super(new BorderLayout());
			this.monitor = monitor;
			this.keyboard = keyboard;
			
	        JLabel label = new JLabel("Consol");
	        label.setHorizontalAlignment(SwingConstants.CENTER); // 텍스트 중앙 정렬
	        this.add(label, BorderLayout.NORTH);
	        
	        textArea = new JTextArea();
	        textArea.addKeyListener(new ConsolKeyboardListener());
	        this.add(new JScrollPane(textArea), BorderLayout.CENTER);
		}
		public void write(String text) {
			this.textArea.setText(this.textArea.getText()+text+"\n");
		}
	    private class ConsolKeyboardListener extends KeyAdapter{
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					int index = textArea.getText().lastIndexOf('\n')+1;
					if(!textArea.getText().substring(index).isEmpty()) {
						keyboard.interrupt(textArea.getText().substring(index));
					}
				}
			}
	    }
	    
    }
//    public static void main(String[] args) {
//    	Monitor m = new Monitor();
//    }
}
