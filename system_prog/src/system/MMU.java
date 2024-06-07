package system;

import constants.EDeviceId;

public class MMU {
	// memory management unit
	
	// association
	private Bus bus;

	public void associate(Bus bus) {
		// cpu를 motherboard에 장착하는 행위
		// 실제는 각 data / memory 등 여러 버스에 직접 MAR / MBR등을 연결해 여러 부분을 연결한다.
		this.bus = bus;
	}
	public int load(EDeviceId eDeviceid, int MAR, int base, int limit) throws Exception {
		if(MAR > limit || MAR < 0) {
			System.out.println(MAR+" "+limit);
			throw new Exception();	// 할당된 메모리 공간 외에 접근을 시도한 경우 -> 프로그램 강제 종료
		}
//		System.out.println(MAR+" "+Integer.toHexString(this.bus.load(eDeviceid, MAR+base)));
		return this.bus.load(eDeviceid, MAR+base);
	}
	public boolean store(EDeviceId eDeviceid, int MAR, int MBR, int base, int limit) throws Exception {
		if(MAR > limit || MAR < 0) {
			throw new Exception();	// 할당된 메모리 공간 외에 접근을 시도한 경우
		}
		return this.bus.store(eDeviceid, MAR + base, MBR);
	}
}