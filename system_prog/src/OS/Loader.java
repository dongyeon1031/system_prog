package OS;

import java.util.Scanner;

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
	public Loader(CPU cpu, Memory memory) {
		this.memory = memory;
		this.cpu = cpu;
	}
    public boolean load(Scanner scanner, PCB pcb) {
    	int pc = pcb.getPC(); 
    	this.cpu.setPC(pc);
    	int sp = pcb.getSP();
    	this.cpu.setSP(sp);
    	
    	for(int i=pc ; ; i++) {
    		int instruction = Integer.parseInt(scanner.next(), 16);
    		memory.store(i+pcb.getMemoryAddress(), instruction);	// 이건 절대주소로 할당해야 할듯? -> 실제 프로세스 주소로
    		if(!scanner.hasNext()) {
    			break;
    		}
    	}
    	pcb.setState(PState.pReady);
    	
    	this.memory.runProcess(pcb); //일단 여기서 실행하자.
        
        return true;
    }
}