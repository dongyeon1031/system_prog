package system;

import java.util.Vector;

import constants.EDeviceId;

public class PCIBus {
	// association
	private Memory memory;

	public PCIBus() {

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

	public Vector<Long> load(EDeviceId eDeviceid, int startAddress, int size) {
		if(eDeviceid == EDeviceId.eMemory) {
			return new Vector<>(this.memory.loadSection(startAddress, size));
		}
		return null;
	}

//	public boolean store(EDeviceId eDeviceid, ) {	// 메모리에 쓴다 = 그래픽 렌더링 작업 = 해당 x
//		if(eDeviceid == EDeviceId.eMemory) {
//			if (MAR == Memory.SCAN_INTERRUPT) {
//				System.out.println("enter bus mbr: "+MBR+" "+MAR);
//			}
//			return this.memory.store(MAR, MBR);
//		}
//		return false;
//	}
}
