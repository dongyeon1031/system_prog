package OS;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import IOdevice.VGA;
import component.PCB;
import system.CPU;
import system.Memory;

public class OperationSystem {
	//component
	private Kernal kernal;
	private Loader loader;
	//associate
	private CPU cpu;
	private Memory memory;
	private VGA vga;
	public OperationSystem() {
		this.kernal = new Kernal();
		this.loader = new Loader();
	}
	public void boot(CPU cpu, Memory memory, VGA vga) {
		this.cpu = cpu;
		this.memory = memory;
		this.vga = vga;
		this.kernal.boot(cpu, memory);
		this.loader.boot(cpu, memory, vga);
	}
	public boolean loadProgram(String filePath) throws Exception {
		try(Scanner scanner = new Scanner(new File(filePath))){
			PCB pcb = this.kernal.createPCB(scanner);
			if(pcb == null) {
				// interrupt로 console에 찍어야 함.
				System.out.println("wrong file id");
			}
			this.loader.load(scanner, pcb);
			return true;
		} catch (IOException e) {
			// interrupt로 console에 찍어야 함.
        	System.out.println("a.out does not exist");
            return false;
        }
	}
}
