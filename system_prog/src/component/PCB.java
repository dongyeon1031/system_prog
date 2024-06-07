package component;

import system.CPU.ERegister;

public class PCB {
	public enum PState{
		pCreate("생성"), 
		pReady("준비"), 
		pRunning("실행"), 
		pWaiting("대기"), 
		pTerminated("완료"),
		;
		private String state;
		private PState(String state) {
			this.state = state;
		}
		public String getState() {
			return this.state;
		}
	}
	private final int PID;		// 식별자
	private PState state;		// 현재 프로세스 상태
	private int program_counter;// 다음 실행할 메모리 번지 수 (상대주소) -> 포인터임.
	private int stack_pointer;	// 스택 주소 -> 포인터임.
	private int memory_addr;	// 프로세스의 시작 주소
	private int[] registers;	// 레지스터 상태 기록 (push / pop 했을 때 or 프로세스 중단 후 재실행 할 때(interrupt)
	
	public PCB(int PID) {
		this.PID = PID;
		this.registers = new int[ERegister.values().length];
	}
	
	public void saveRegisterStatus(int[] registers) {
		this.registers = registers.clone();
	}
	
	public int[] getRegisterStatus() {
		return this.registers.clone();
	}
	
	public long getPID() {
		return PID;
	}
	public PState getState() {
		return state;
	}
	public void setState(PState state) {
		this.state = state;
	}
	public int getPC() {
		return program_counter;
	}
	public void setPC(int program_counter) {
		this.program_counter = program_counter;
	}
	public int getMemoryAddress() {
		return memory_addr;
	}
	public void setMemoryAddress(int memory_info) {
		this.memory_addr = memory_info;
	}
	public int getSP() {
		return stack_pointer;
	}
	public void setSP(int stack_pointer) {
		this.stack_pointer = stack_pointer;
	}
	
}
