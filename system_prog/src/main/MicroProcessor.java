package main;

import IOdevice.Keyboard;
import IOdevice.Monitor;
import OS.OperationSystem;
import system.Bus;
import system.CPU;
import system.DisplayPanel;
import system.Memory;

public class MicroProcessor {
	//attributes
	public boolean bPowerOn;
	//components
	private Bus bus;
	private CPU cpu;
	private Memory memory;
	private OperationSystem os;
	private Keyboard keyboard;
	private Monitor monitor;
	private DisplayPanel displayPanel;
	
	public CPU getCPU(){
		return this.cpu;
	}
	public Memory getMemory() {
		return this.memory;
	}
	
	public MicroProcessor() {
		//attributes
		this.bPowerOn = true;
		//component
		// hardware
		this.bus = new Bus();
		this.cpu = new CPU();
		this.memory = new Memory();
		// i/o device
		this.keyboard = new Keyboard(this.memory);
		this.monitor = new Monitor(this.memory, this.keyboard);
		// os
		this.os = new OperationSystem(this.cpu, this.memory);
		
		//associations = 연결
		this.bus.associate(this.memory);
		this.cpu.associate(this.bus);
	}
	public void initialize() {
		this.bus.initialize();
		this.cpu.initialize();
		this.memory.initialize();
	}

	public void run() throws Exception {
		this.os.loadProgram(System.getProperty("user.dir")+ "/binary/a.out.txt");
		
//		while(bPowerOn) {
//			this.monitor.run();
//			if(!this.cpu.run()) {
//				this.bPowerOn = false;
//			}
//		}
		this.displayPanel = new DisplayPanel(this);
		
	}

	public void finish() {
		
	}

}
