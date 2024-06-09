package IOdevice;

import java.util.Vector;

import constants.EDeviceId;
import system.Device;
import system.PCIBus;

public class VGA implements Device{
	//attribute
	public static final int TEXT_MODE_MEMORY_SIZE = 1024; // 4kb = 4096byte -> 1024 * 4byte(integer)
	public static int system_memory_address;
	//component
	private Vector<Integer> vga_memory;
	//association
	private Monitor monitor;
	private PCIBus pciBus;
	// constructor
	public VGA() {
		this.vga_memory = new Vector<>();
	}
	public void load(int s_memory_address) {
		system_memory_address = s_memory_address;
	}
	//method
	public void run() {
		
//		Vector<Integer> memory = ;
//		for(int i=0; i<TEXT_MODE_MEMORY_SIZE; i++) {
//			this.vga_memory.set(i, memory.get(i));
//		}
		this.vga_memory = new Vector<>(this.pciBus.load(EDeviceId.eMemory, system_memory_address, TEXT_MODE_MEMORY_SIZE));
		this.monitor.getSignal(this.vga_memory);
	}
	public void connect(Monitor monitor) {
		this.monitor = monitor;
	}
	public void associate(PCIBus pciBus) {
		this.pciBus = pciBus;
	}
	@Override
	public boolean initialize() {
		return true;
	}
	@Override
	public boolean finish() {
		return true;
	}
}