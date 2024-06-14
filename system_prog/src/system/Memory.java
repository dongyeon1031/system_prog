package system;

import java.util.List;
import java.util.Vector;

import component.PCB;
import component.PCB.PState;

public class Memory implements Device {
	// attribute
//	public static final int PRINT_INTERRUPT = 0;
//	public static final int SCAN_INTERRUPT = 1;
	private PCB currentPCB;
	
	// component
	public Vector<Long> memories;
	private Vector<PCB> process_table;
//	public boolean interrupted = false;

	public Memory() {
		this.memories = new Vector<>();
		this.process_table = new Vector<>();
	}

	public boolean initialize() {
		return true;
	}

	public long load(long MAR) {
//		if (MAR == SCAN_INTERRUPT) {
//			this.interrupted = true;
//			while (this.interrupted) {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		return this.memories.get((int) MAR);
	}
	public List<Long> loadSection(int MAR, int size) {
		return this.memories.subList(MAR, size);
	}

	public boolean store(long MAR, long MBR) {
		this.memories.set((int)MAR, MBR);
//		if (MAR == PRINT_INTERRUPT) {
//			this.interrupted = true;
//		}
		return true;
	}
	

//	public void interrupt() {
//		this.interrupted = true;
//	}

	public boolean finish() {
		return true;
	}

	public void run() {

	}

	public int allocate(int size) {
		// @input : 할당할 메모리의 크기
		// @output : 할당한 메모리의 시작 주소
		int start_point = this.memories.size();
		for (int i = 0; i < size; i++) {
			this.memories.add((long) -1);
		}
		return start_point;
	}
	public int getPCBid() {
		return this.process_table.size();
	}
	public boolean addPCB(PCB pcb) {
		return this.process_table.add(pcb);
	}
	public void runProcess(PCB pcb) {
		if(this.currentPCB != null) {
			this.currentPCB.setState(PState.pWaiting);
		}
		this.currentPCB = pcb;
		this.currentPCB.setState(PState.pRunning);
	}
	
	public long[][] getState() {
		int stack = this.currentPCB.getSP() + this.currentPCB.getMemoryAddress() - 15;	// stack은 15개정도 보여주기
		long[][] retArray = new long[this.memories.size()-stack][2];
		int k = 0;
		for(int i = stack; i < this.memories.size();i++) {
			retArray[k][0] = i;
			retArray[k][1] = this.memories.get(i);
			k++;
		}
		return retArray;
	}
}
