package system;

import constants.EDeviceId;

public class Bus implements Device{
	//association
	private Memory memory;
	
	public Bus() {
		
	}
	public void associate(Memory memory) {
		this.memory = memory;
	}
	public boolean initialize() {
		return true;
	}

	public boolean finish() {
		return true;
	}
	public long load(EDeviceId eDeviceid, long MAR) {
		if(eDeviceid == EDeviceId.eMemory) {
			return this.memory.load(MAR);
		}
		return 0;
	}
	public boolean store(EDeviceId eDeviceid, long MAR, long MBR) {
		if(eDeviceid == EDeviceId.eMemory) {
//			if (MAR == Memory.SCAN_INTERRUPT) {
//				System.out.println("enter bus mbr: "+MBR+" "+MAR);
//			}
			return this.memory.store(MAR, MBR);
		}
		return false;
	}
}
