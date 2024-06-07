package IOdevice;

import java.awt.Component;

import system.Memory;

public class Keyboard extends Component{
	private static final long serialVersionUID = 1L;
	//component
	
	//association
	private Memory memory;
	public Keyboard(Memory memory) {
		this.memory = memory;
//		this.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				information += e.getKeyChar();
//				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//					int index = information.lastIndexOf('\n')+1;
//					interrupt(information.substring(index));
//					System.out.println("enter");
//				}
//			}
//		});
	}
    public void interrupt(String text) {
        try {
        	int val = Integer.parseInt(text);
        	this.memory.store(Memory.SCAN_INTERRUPT, val);
        	this.memory.interrupted = false;
        } catch (NumberFormatException e) {
            
        }
    }
//  public int interrupt() {
//	    Scanner scanner = new Scanner(System.in);
//	    int num = scanner.nextInt();
//	    scanner.close();
//	    return num;
//  }

}
