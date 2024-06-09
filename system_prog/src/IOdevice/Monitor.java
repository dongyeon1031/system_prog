package IOdevice;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;

import system.Device;

public class Monitor extends JFrame implements Device{
	private static final long serialVersionUID = 1L;
	//association
	
	//component
//	private String information = "";
	private Vector<Integer> buffer; //일단 버퍼를 integer 하나로 만들자.
	private JLabel text;
//	private Console console;

    public Monitor() {
    	this.buffer = new Vector<>();
    	
        setTitle("Monitor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        
//        this.console = new Console(this, keyboard);
//        this.add(new JScrollPane(console));
        this.text = new JLabel();
        this.add(text);
        this.setVisible(true);
    }
    
//    public void run() {
//    	if(memory.interrupted) {
//    		this.buffer = this.memory.load(Memory.PRINT_INTERRUPT);
//    		this.console.write(Integer.toString(this.buffer));
//    		memory.interrupted = false;
//    	}
//    }
    public void getSignal(Vector<Integer> signal) {
    	// 원래는 전기 신호를 받아서 이걸 출력해야 하는데 지금은 그냥 숫자 출력
    	this.buffer = signal;
    	String str = "";
    	for(int b : this.buffer) {
    		if(b != -1) {
    			str += Integer.toString(b);
    		}
    	}
    	this.text.setText(str);
    }
//    private class Console extends JPanel{
//    	// TUI console
//		private static final long serialVersionUID = 1L;
//    	//associate
//		private Monitor monitor;
//		private Keyboard keyboard;
//		//components
//		JTextArea textArea;
//		public Console(Monitor monitor, Keyboard keyboard) {
//			super(new BorderLayout());
//			this.monitor = monitor;
//			this.keyboard = keyboard;
//			
//	        JLabel label = new JLabel("Consol");
//	        label.setHorizontalAlignment(SwingConstants.CENTER); // 텍스트 중앙 정렬
//	        this.add(label, BorderLayout.NORTH);
//	        
//	        textArea = new JTextArea();
//	        textArea.addKeyListener(new ConsolKeyboardListener());
//	        this.add(new JScrollPane(textArea), BorderLayout.CENTER);
//		}
//		public void write(String text) {
//			this.textArea.setText(this.textArea.getText()+text+"\n");
//		}
//	    private class ConsolKeyboardListener extends KeyAdapter{
//			@Override
//			public void keyPressed(KeyEvent e) {
//				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//					int index = textArea.getText().lastIndexOf('\n')+1;
//					if(!textArea.getText().substring(index).isEmpty()) {
//						keyboard.interrupt(textArea.getText().substring(index));
//					}
//				}
//			}
//	    }
//	    
//    }
	@Override
	public boolean initialize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean finish() {
		// TODO Auto-generated method stub
		return false;
	}
}
