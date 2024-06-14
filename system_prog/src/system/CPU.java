package system;
import java.util.Arrays;

import constants.EDeviceId;
import constants.EOpcode;

public class CPU implements Device {
	public enum EStatus {
		eZero(0xFE, 0x01, 0x01), eNegative(0xfd, 0x02, 0x02);

		private final int nClear;
		private final int nSet;
		private final int nGet;

		private EStatus(int nClear, int nSet, int nGet) {
			this.nClear = nClear;	// 0으로 만들 때
			this.nSet = nSet; 		// 1로 만들 때
			this.nGet = nGet;		// 플래그 비트 순서(위치)
		}

		public int getNClear() {
			return this.nClear;
		}

		public int getNSet() {
			return this.nSet;
		}

		public int getNGet() {
			return this.nGet;
		}
	}

	public enum ERegister {
		eR0("r0"), eR1("r1"), eR2("r2"), eR3("r3"), eR4("r4"), eR5("r5"), eR6("r6"), eR7("r7"), eR8("r8"), 
		ePC("rpc"), eIR("rir"), eMAR("rmar"), eMBR("rmbr"), eStatus("rstatus"), 
		eBase("base"), eLimit("limit"),	// 각 현재 세그먼트의 시작 주소 / 세그먼트의 크기를 저장함. (MMU에서 사용)
		eAh("rah"), eAl("ral"), 		// interrupt용 레지스터 (ah : 처리할 interrupt 종류 저장 / al : 입력값 / 출력값 등 정보 저장) 
		eSP("rsp"), eBP("rbp"),			// 스택 포인터(stack pointer) / 프레임 포인터(base pointer) 저장
		;
		private String name;
		private ERegister(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
	}
	// attribute
	private static final int TERNARY = 3;
	private boolean isPcIncrease;

	// component
	private long registers[] = new long[ERegister.values().length];
	private int opCode;
	private int[] operands;
	private MMU mmu;
	private ALU alu;

	public CPU() {
		System.out.println("hello cpu");
		this.operands = new int[TERNARY];
		// operand를 3개로 쪼개되, 3개를 각각 사용하지 않고, 1/2 혹은 2/1, 1/1와 같이 사용한다.
		this.mmu = new MMU();	// mmu는 cpu 내부 부품이기 때문에 associate로 연결하지 않고 생성자에서 생성한다.
		this.alu = new ALU();	// ALU "
		this.isPcIncrease = true;
	}

	public void associate(Bus bus) {
		// cpu를 motherboard에 장착하는 행위
		// 실제는 각 data / memory 등 여러 버스에 직접 MAR / MBR등을 연결해 여러 부분을 연결한다.
		this.mmu.associate(bus);
	}

	public boolean initialize() {
		return true; // power on self-test
	}

	// method
	private long get(ERegister eRegister) {
		return registers[eRegister.ordinal()];
	}

	private void set(ERegister eRegister, long value) {
		this.registers[eRegister.ordinal()] = value;
	}

	private void setZero(boolean bResult) {
		if (!bResult) {	// 거짓인 경우 0으로 만든다.
			this.registers[ERegister.eStatus.ordinal()] &= EStatus.eZero.getNClear(); // zeroflag 0으로 만들기 -> 이거랑 플래그 위치
																						// constant로 고정하기
		} else {
			this.registers[ERegister.eStatus.ordinal()] |= EStatus.eZero.getNSet(); // zeroflag 1로 만들기
		}
	}

	private void setNegative(boolean bResult) {
		if (!bResult) { // 거짓인 경우 0으로 만든다.
			this.registers[ERegister.eStatus.ordinal()] &= EStatus.eNegative.getNClear(); // zeroflag 0으로 만들기
		} else {
			this.registers[ERegister.eStatus.ordinal()] |= EStatus.eNegative.getNSet(); // zeroflag 1로 만들기
		}
	}
	private boolean getFlagStat(int FlagNum) {
		if((FlagNum & this.registers[ERegister.eStatus.ordinal()]) == 0) { // 해당 flag가 0인 경우
			return false;
		}
		return true;
	}
	
	public void setBase(int address) {
		this.registers[ERegister.eBase.ordinal()] = address;
	}
	public void setLimit(int size) {
		this.registers[ERegister.eLimit.ordinal()] = size;
	}

	// @display method
	private void display(String opcode) {
		System.out.println("*************************************");
		System.out.println("opcode: " + opcode);
		System.out.print("operands: ");
		for (long i : this.operands) {
			System.out.print(String.format("%02x", i) + " ");
		}
		System.out.println("\nR0 : " + String.format("%x",this.registers[ERegister.eR0.ordinal()])
		+"\tR1: " + String.format("%x",this.registers[ERegister.eR1.ordinal()])
		+"\tR2: " + String.format("%x",this.registers[ERegister.eR2.ordinal()]));
		System.out.println("*************************************\n");
	}
	public long[] getState() {
		return this.registers;
	}
	public EOperationSelectSignal getALUState() {
		return this.alu.eOperationSelectSignal;
	}

	// state change method
	private void increasePC() {
		this.registers[ERegister.ePC.ordinal()] += 1;
	}
	public void setPC(long address) {
		this.registers[ERegister.ePC.ordinal()] = address;
	}
	public void setSP(long address) {
		this.registers[ERegister.eSP.ordinal()] = address;
	}
	
	// instructions
	
	//@Memory Operation
	private void load(ERegister Rd, long address) throws Exception {
		// @input : Rd register, memory address
		// @Rule : 주소를 MBR에 저장함. MBR의 값을 Rd register에 저장함.System.out.println(address);
		this.registers[ERegister.eMBR.ordinal()] = this.mmu.load(EDeviceId.eMemory, address, 
				this.registers[ERegister.eBase.ordinal()], this.registers[ERegister.eLimit.ordinal()]);
		this.registers[Rd.ordinal()] = this.registers[ERegister.eMBR.ordinal()];
	}
	private void store(long address, ERegister Rd) throws Exception {
		// @input : memory address, Rd register
		// @Rule : Rd register의 값을 MBR에 옮기고 address를 MAR에 저장한 뒤 메모리에 저장한다.
		this.registers[ERegister.eMAR.ordinal()] = address;
		this.registers[ERegister.eMBR.ordinal()] = this.registers[Rd.ordinal()];
		this.mmu.store(EDeviceId.eMemory, this.registers[ERegister.eMAR.ordinal()],
				this.registers[ERegister.eMBR.ordinal()], this.registers[ERegister.eBase.ordinal()], this.registers[ERegister.eLimit.ordinal()]);
	}
	private void loadr(ERegister Rd, ERegister Rn) throws Exception {
		// @input : Rd register, Rn register
		// @Rule : Rn register이 가리키는(저장한) 메모리 주소를 MBR에 저장함. MBR 값을 Rd에 저장함
		
		this.registers[ERegister.eMBR.ordinal()] = this.mmu.load(EDeviceId.eMemory, this.registers[Rn.ordinal()], 
				this.registers[ERegister.eBase.ordinal()], this.registers[ERegister.eLimit.ordinal()]);
		this.move(Rd, ERegister.eMBR);
	}
	private void storer(ERegister Rd, ERegister Rn) throws Exception {
		// @input : Rd register, Rn register
		// @Rule : Rn register의 값을 MBR에 옮기고 Rd register가 가리키는 메모리 주소를 MAR에 저장한 뒤 메모리에 저장한다.
		this.registers[ERegister.eMAR.ordinal()] = this.registers[Rd.ordinal()];
		this.registers[ERegister.eMBR.ordinal()] = this.registers[Rn.ordinal()];
		this.mmu.store(EDeviceId.eMemory, this.registers[ERegister.eMAR.ordinal()],
				this.registers[ERegister.eMBR.ordinal()], this.registers[ERegister.eBase.ordinal()], this.registers[ERegister.eLimit.ordinal()]);
	}
	
	// @push / pop register
	private void push(ERegister Rd) throws Exception {
		// @input : rd
		// @Rule : stack에 rd 값을 저장하고 stack pointer를 증가시킨다.
		this.registers[ERegister.eSP.ordinal()] --;	// stack pointer 증가 (아래로 증가함.)
		this.move(ERegister.eMAR, ERegister.eSP);
		this.mmu.store(EDeviceId.eMemory, this.registers[ERegister.eMAR.ordinal()],
				this.registers[Rd.ordinal()], this.registers[ERegister.eBase.ordinal()], this.registers[ERegister.eLimit.ordinal()]);
	}
	private void pop(ERegister Rd) throws Exception {
		// @input : rd
		// @Rule : stack 값을 rd에 저장하고 stack pointer를 감소시킨다.
		this.registers[ERegister.eMBR.ordinal()] = this.mmu.load(EDeviceId.eMemory, this.registers[ERegister.eSP.ordinal()],
				this.registers[ERegister.eBase.ordinal()], this.registers[ERegister.eLimit.ordinal()]);
		this.move(Rd, ERegister.eMBR);
		this.registers[ERegister.eSP.ordinal()] ++;	//stack pointer 감소 (위로 감소함)
	}
	
	//@Register Operation
	private void move(ERegister Rd, ERegister Rn) {
		// @input : Rd register, Rn register
		// @Rule : Rn register의 값을 Rd register에 저장함.
		this.registers[Rd.ordinal()] = this.registers[Rn.ordinal()];
	}
	private void movec(ERegister Rd, long constant) {
		// @input : Rd register, constant
		// @Rule : const를 Rd register에 저장함.
		
		this.registers[Rd.ordinal()] = constant;
	}


	//@Flow of control
	private void jump(long address) {
		// @input : address
		// @Rule : PC register를 address로 jump함.
		this.setPC(address);
		this.isPcIncrease = false;
	}
	private void zero(long address) {
		// @input : address
		// @Rule : zero flag 값이 1이면 PC register를 address로 jump함.
		if(this.getFlagStat(EStatus.eZero.getNGet())) {
			this.setPC(address);
			this.isPcIncrease = false;
		}
	}
	
	private void belowZero(long address) {
		// @input : address
		// @Rule : below zero flag 값이 1이면 PC register를 address로 jump함.
		if(this.getFlagStat(EStatus.eNegative.getNGet())) {
			this.setPC(address);
			this.isPcIncrease = false;
		}
	}
	
	//@System Calls
//	private void interrupt(long address, ERegister Rd) {
//		switch(address) {
//		case Memory.SCAN_INTERRUPT:
//			this.set(Rd, this.bus.load(EDeviceId.eMemory, address));
//			break;
//		case Memory.PRINT_INTERRUPT:
//			this.bus.store(EDeviceId.eMemory, address, this.registers[Rd.ordinal()]);
//			break;
//		}
//	}
//	private void halt() {
//		
//	}


	// instruction execution cycle
	private void fetch() throws Exception { // 준비
		this.move(ERegister.eMAR, ERegister.ePC);
		this.set(ERegister.eMBR, mmu.load(EDeviceId.eMemory, get(ERegister.eMAR),
				this.registers[ERegister.eBase.ordinal()], this.registers[ERegister.eLimit.ordinal()]));
		this.move(ERegister.eIR, ERegister.eMBR);
	}

	private void decode() { // 해석
		// opcode 1byte, operand 3byte인 경우
		long instruction = this.get(ERegister.eIR);
		this.opCode = (int) (instruction >> 24); // 1byte만 남기기 = opcode 가져오기
		this.generateOperationSignal();	// opcode 기반으로 연산 선택 신호 만들기
		this.operands[0] = (int) (instruction & 0x00ff0000); // operand 가져오기
		this.operands[0] >>= 16;
		this.operands[1] = (int) (instruction & 0x0000ff00);
		this.operands[1] >>= 8;
		this.operands[2] = (int) (instruction & 0x000000ff);
	}
	private void generateOperationSignal() {
		EOperationSelectSignal eOperationSelectSignal;
		if (opCode == EOpcode.eAdd.ordinal()) { // MBR 값 읽어오기
			eOperationSelectSignal = EOperationSelectSignal.eAddSign;
		}else if (opCode == EOpcode.eAnd.ordinal()) {
			eOperationSelectSignal = EOperationSelectSignal.eAndSign;
		}else if (opCode == EOpcode.eCompare.ordinal()) {
			eOperationSelectSignal = EOperationSelectSignal.eCompareSign;
		}else if (opCode == EOpcode.eNot.ordinal()) {
			eOperationSelectSignal = EOperationSelectSignal.eNotSign;
		}else if (opCode == EOpcode.eShr.ordinal()) {
			eOperationSelectSignal = EOperationSelectSignal.eShearSign;
		}else {
			eOperationSelectSignal = null;
		}
		this.alu.selectOperation(eOperationSelectSignal);
	}

	private boolean execute() throws Exception { // 실행
		if (opCode == EOpcode.eHalt.ordinal()) {
			this.display(EOpcode.values()[opCode].getInst());
			return false;
		} else if (opCode == EOpcode.eLoad.ordinal()) { // MBR 값 읽어오기
			this.load(ERegister.values()[this.operands[0]], this.combineOperand(Arrays.copyOfRange(operands, 1, 3)));
		} else if (opCode == EOpcode.eStore.ordinal()) { // MBR에 값, MAR에 주소 저장하기
			this.store(this.combineOperand(Arrays.copyOfRange(operands, 0, 2)), ERegister.values()[this.operands[2]]);
		}else if (opCode == EOpcode.eJump.ordinal()) {
			this.jump(this.combineOperand(operands));
		} else if(opCode == EOpcode.eZero.ordinal()) {
			this.zero(this.combineOperand(operands));
		} else if(opCode == EOpcode.eBz.ordinal()) {
			this.belowZero(this.combineOperand(operands));
		} else if (opCode == EOpcode.eMove.ordinal()) {
			this.move(ERegister.values()[this.operands[0]], ERegister.values()[this.operands[1]]);
		} else if (opCode == EOpcode.eMovec.ordinal()) {
			this.movec(ERegister.values()[this.operands[0]], this.combineOperand(Arrays.copyOfRange(operands, 1, 3)));
		}else if (opCode == EOpcode.ePush.ordinal()) {
			this.push(ERegister.values()[this.operands[0]]);
		}else if (opCode == EOpcode.ePop.ordinal()) {
			this.pop(ERegister.values()[this.operands[0]]);
//		}else if (opCode == EOpcode.eInterrupt.ordinal()) {
		// interrupt 나중에 생각하기
//			this.interrupt(this.combineOperand(Arrays.copyOfRange(operands, 0, 2)), ERegister.values()[this.operands[2]]);
		}else if(opCode == EOpcode.eLoadr.ordinal()) {
			this.loadr(ERegister.values()[this.operands[0]], ERegister.values()[this.operands[1]]);
		}else if(opCode == EOpcode.eStorer.ordinal()) {
			this.storer(ERegister.values()[this.operands[0]], ERegister.values()[this.operands[1]]);
		}else {
			// ALU한테 연산 시켜버리기
			this.alu.doOperation(ERegister.values()[this.operands[0]], ERegister.values()[this.operands[1]]);
		}
		
		this.display(EOpcode.values()[opCode].getInst());
		if(this.isPcIncrease) {
			this.increasePC();
		}
		this.isPcIncrease = true;
		return true;
	}

	public boolean run() throws Exception {
//		MBR = bus.load(EDeviceId.eMemory, MAR); 
		this.fetch();
		this.decode();
		return this.execute();
	}

	public boolean finish() {
		return true;
	}

	public long combineOperand(int[] operand) {
		// @input : Operand Array
		// @Rule : 인덱스의 역순으로 자릿수를 잡아 합친다.
		// ex) {0x10, 0x11} -> 0x1011
		int retVal = 0;
		int len = operand.length;
		for (int i = 0; i < len; i++) {
			retVal += operand[i] << ((len - i - 1) * 8);
		}
		return retVal;
	}
	
	public enum EOperationSelectSignal{
		//ALU가 수행할 연산 종류를 나타내는 신호 -> opcode는 기계어고 signal은 전기 신호임. (CU가 생성)
		eAddSign, eAndSign, eCompareSign, eNotSign, eShearSign,
	}

	private class ALU{
		private EOperationSelectSignal eOperationSelectSignal;

		public ALU() {
		}
		public void selectOperation(EOperationSelectSignal eOperationSelectSignal) {
			this.eOperationSelectSignal = eOperationSelectSignal;
		}
		public void doOperation(ERegister Rd, ERegister Rn) {
			switch(this.eOperationSelectSignal) {
			case eAddSign:
				this.add(Rd, Rn);
				break;
			case eAndSign:
				this.and(Rd, Rn);
				break;
			case eCompareSign:
				this.compare(Rd, Rn);
				break;
			case eNotSign:
				this.not(Rd);
				break;
			case eShearSign:
				this.shear(Rd);
				break;
			}
		}
		//@Algebraic Operation
		private void add(ERegister Rd, ERegister Rn) {
			// @input : Rd register, Rn register
			// @Rule : Rn register와 Rd register의 합을 Rd register에 저장함.
			registers[Rd.ordinal()] = registers[Rd.ordinal()] + registers[Rn.ordinal()];
		}

		//@Relational Operation
		private void compare(ERegister Rd, ERegister Rn) {
			// @input : Rd register, Rn register
			// @Rule : Rn register와 Rd register의 값이 같다면 zero flag를 0으로 / Rd가 작다면 negative flag를 0으로 설정함.
			setZero(registers[Rd.ordinal()] == registers[Rn.ordinal()]);
			setNegative(registers[Rd.ordinal()] < registers[Rn.ordinal()]);
		}
		private void not(ERegister Rd) {
			// @input : Rd register
			// @Rule : Rd 의 bitwise not 결과를 Rd에 저장함.
			registers[Rd.ordinal()] = ~registers[Rd.ordinal()]; 
		}
		private void shear(ERegister Rd) {
			// @input : Rd register
			// @Rule : Rd 의 bitwise not 결과를 Rd에 저장함.
			registers[Rd.ordinal()] >>= registers[Rd.ordinal()]; 
		}
		
		//@Logical Operation
		private void and(ERegister Rd, ERegister Rn) {
			// @input : Rd register, Rn register
			// @Rule : Rn register와 Rd register의 논리AND 결과를 Rd register에 저장함.
			registers[Rd.ordinal()] = registers[Rd.ordinal()] & registers[Rn.ordinal()];
		}
	}
}
