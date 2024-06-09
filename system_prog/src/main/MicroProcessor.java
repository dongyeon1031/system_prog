package main;

import IOdevice.Monitor;
import IOdevice.VGA;
import OS.OperationSystem;
import system.Bus;
import system.CPU;
import system.DisplayPanel;
import system.Memory;
import system.PCIBus;

public class MicroProcessor {
	//attributes
	public boolean bPowerOn;
	//components
	private Bus bus;
	private CPU cpu;
	private Memory memory;
	private OperationSystem os;
	private PCIBus pciBus;
	private VGA vga;
	private Monitor monitor;

	private DisplayPanel displayPanel;
	
	public CPU getCPU(){
		return this.cpu;
	}
	public Memory getMemory() {
		return this.memory;
	}
	public VGA getVGA() {
		return this.vga;
	}
	
	public MicroProcessor() {
		//attributes
		this.bPowerOn = true;
		//component
		// hardware
		this.bus = new Bus();
		this.cpu = new CPU();
		this.memory = new Memory();
		this.pciBus = new PCIBus();
		this.vga = new VGA();
		// i/o device
		this.monitor = new Monitor();
		// os
		this.os = new OperationSystem();
		
		//associations = 연결
		this.bus.associate(this.memory);
		this.cpu.associate(this.bus);
		this.pciBus.associate(this.memory);
		this.vga.associate(this.pciBus);
		this.vga.connect(this.monitor);
		
		this.os.boot(cpu, memory, vga);
	}
	public void initialize() {
		this.bus.initialize();
		this.cpu.initialize();
		this.memory.initialize();
	}

	public void run() throws Exception {
		this.os.loadProgram(System.getProperty("user.dir")+ "/binary/a.out.txt");
		
//		while(bPowerOn) {
//			if(!this.cpu.run()) {
//				this.bPowerOn = false;
//			}
//			this.vga.run();
//		}
		this.displayPanel = new DisplayPanel(this);
		
	}

	public void finish() {
		
	}


}
