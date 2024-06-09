package OS;

import java.util.Scanner;

import IOdevice.VGA;
import component.PCB;
import component.PCB.PState;
import system.CPU;
import system.Memory;

public class Loader {
	/*
	 * loader의 역할
	 * 
	 * 프로그램을 메모리에 배치하고 실행을 준비한다.
	 * -> 메모리 매핑
	 */
	// association
	private Memory memory;
	private CPU cpu;
	public Loader() {
	}
    public boolean load(Scanner scanner, PCB pcb) {
    	int pc = pcb.getPC(); 
    	this.cpu.setPC(pc);
    	int sp = pcb.getSP();
    	this.cpu.setSP(sp);
    	
    	for(int i=pc ; ; i++) {
    		int instruction = Integer.parseInt(scanner.next(), 16);
    		memory.store(i+pcb.getMemoryAddress(), instruction);	// 실제 프로세스 주소로 할당
    		if(!scanner.hasNext()) {
    			break;
    		}
    	}
    	pcb.setState(PState.pReady);
    	
    	this.memory.runProcess(pcb); //일단 여기서 실행하자.
        
        return true;
    }
	public void boot(CPU cpu, Memory memory, VGA vga) {
		// VGA가 접근할 메모리 주소를 할당한다.
		this.memory = memory;
		this.cpu = cpu;
		vga.load(this.memory.allocate(VGA.TEXT_MODE_MEMORY_SIZE));
		
	}
}