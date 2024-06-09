package OS;

import java.util.Scanner;

import component.PCB;
import component.PCB.PState;
import system.CPU;
import system.Memory;

public class Kernal {
	// PCB 생성
	// 메모리 공간 확보
	
	//attribute
	public static final int KDY_FILE_MAGIC_NUMBER = 0x7F4B4459;
	
	// association
	private Memory memory;
	private CPU cpu;
	
	//construtor
	public Kernal() {
	}
	
	public PCB createPCB(Scanner scanner) throws Exception {
		int file_magic_number = Integer.parseInt(scanner.next(), 16);
		if(file_magic_number != KDY_FILE_MAGIC_NUMBER) {
			throw new Exception();
		}
		PCB pcb = new PCB(memory.getPCBid());
		pcb.setState(PState.pCreate);
		int heap = Integer.parseInt(scanner.next(), 16);
		int stack = Integer.parseInt(scanner.next(), 16);
		int data = Integer.parseInt(scanner.next(), 16);
		int code = Integer.parseInt(scanner.next(), 16);
		
		pcb.setMemoryAddress(this.memory.allocate(heap+stack+data+code)); // 세그먼트의 크기만큼 메모리 할당.
		pcb.setPC(heap+stack+data); // code segment의 0번째부터 시작하게 설정. (프로세스 내의 상대주소)
		pcb.setSP(heap+stack);	//stack pointer의 프로세스 내 상대주소 (아래로 증가하니까 stack의 젤 윗 주소)
		// 현재 segment 순서	: heap - stack - data - code (stack 증가 방향이 메모라 주소 높은 곳에서 낮은 곳으로 진행)
		this.memory.addPCB(pcb);
		
		this.cpu.setBase(pcb.getMemoryAddress());		// 시작 주소
		this.cpu.setLimit(heap+stack+data+code);		// 사이즈
		
		return pcb;
	}

	public void boot(CPU cpu, Memory memory) {
		this.memory = memory;
		this.cpu = cpu;
	}
}
