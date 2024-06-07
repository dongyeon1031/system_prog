package component;

import java.util.Vector;

import assembler.symbolTable.EType;
import assembler.symbolTable.Token;
import constants.EOpcode;
import system.CPU;
import system.CPU.ERegister;

public class InstructionMappingTable {
	public static Vector<String> makeAdd(Token op0, Token op1) throws Exception{
		Vector<String> instructions = new Vector<>();
		
		if(op0.getType() == EType.eRegister) { // 더할 대상이 레지스터인 경우
			int r0 = getRegisterNum(op0);
			int r1 = getNextRegisterNum(r0);
			switch(op1.getType()) { // 더할 값별로 처리
			case eRegister: // add
				instructions.add(makeAdd(r0, getRegisterNum(op1)));
				break;
			case eVariable: // load -> add
				instructions.add(makePush(r1));
				instructions.add(makeLoad(r1, op1.getOffset()));
				instructions.add(makeAdd(r0,r1));
				instructions.add(makePop(r1));
				break;
			case eConstant: // moveC -> add
				instructions.add(makePush(r1));
				instructions.add(makeMoveC(r1, op1.getInitialValue()));
				instructions.add(makeAdd(r0, r1));
				instructions.add(makePop(r1));
				break;
			case eAddress:
				r1 = getNextRegisterNum(r0);
				int r2 = getNextRegisterNum(r1);
				instructions.add(makePush(r1));
				instructions.add(makePush(r2));
				instructions.add(makeMove(r1, getRegisterNum(op1)));	// 레지스터 값 저장
				
				int constant = op1.getInitialValue();
				if(constant < 0) {
					constant *= -1;
					instructions.add(makeMoveC(r2, constant));	// 상수(더하거나 뺄 값) 저장
					instructions.add(makeNot(r2));		// r2 = ~r2;
					int r3 = getNextRegisterNum(r2);
					instructions.add(makePush(r3));
					
					instructions.add(makeMoveC(r3, 1));
					instructions.add(makeAdd(r2, r3));	// r2 += 1;
					instructions.add(makeAdd(r1, r2));	// r1 += r2; (뺄셈)
					
					instructions.add(makePop(r3));
				}else {
					instructions.add(makeMoveC(r2, constant));	// 상수(더하거나 뺄 값) 저장
					instructions.add(makeAdd(r1, r2));	// 최종 주소	
				}
				
				instructions.add(makeLoadR(r2, r1)); 	// 최종 주소로 load
				instructions.add(makeAdd(r0, r2));
				instructions.add(makePop(r1));
				instructions.add(makePop(r2));
				break;
			default: 
				throw new Exception();
			}
		}else if(op0.getType() == EType.eVariable) { // 더할 대상이 변수인 경우
			switch(op1.getType()) { // 더할 값별로 처리
			case eRegister: // load -> add -> store
				int r1 = getRegisterNum(op1);
				int r0 = getNextRegisterNum(r1);
				instructions.add(makePush(r0));
				instructions.add(makeLoad(r0, op0.getOffset()));
				instructions.add(makeAdd(r0, r1));
				instructions.add(makeStore(op0.getOffset(), r0));
				instructions.add(makePop(r0));
				break;
			case eVariable: // load -> load -> add -> store
				instructions.add(makePush(0));
				instructions.add(makePush(1));
				instructions.add(makeLoad(0,op0.getOffset()));
				instructions.add(makeLoad(1,op1.getOffset()));
				instructions.add(makeAdd(0,1));
				instructions.add(makeStore(op0.getOffset(),0));
				instructions.add(makePop(0));
				instructions.add(makePop(1));
				break;
			case eConstant: // load -> addc -> store
				instructions.add(makePush(0));
				instructions.add(makePush(1));
				instructions.add(makeLoad(0,op0.getOffset()));
				instructions.add(makeMoveC(1, op1.getInitialValue()));
				instructions.add(makeAdd(0, 1));
				instructions.add(makeStore(op0.getOffset(), 0));
				instructions.add(makePop(0));
				instructions.add(makePop(1));
				break;
			default: 
				throw new Exception();
			}
		}else { // 더할 대상이 레지스터가 아닌 경우
			// 일단 불가능.
			// 메모리에 더하려는 경우 add -> store 로 할지 고민해보기
			throw new Exception();
		}
		return instructions;
	}
	public static Vector<String> makeMove(Token op0, Token op1) throws Exception{
		Vector<String> instructions = new Vector<>();
		
		if(op0.getType() == EType.eRegister) { // 옮길 곳이 레지스터인 경우
			switch(op1.getType()) { // 옮길 대상별로 처리
			case eRegister: // move
				instructions.add(makeMove(getRegisterNum(op0), getRegisterNum(op1)));
				break;
			case eVariable: // load
				instructions.add(makeLoad(getRegisterNum(op0), op1.getOffset()));
				break;
			case eConstant: // moveC
				instructions.add(makeMoveC(getRegisterNum(op0), op1.getInitialValue()));
				break;
			case eAddress: // address to register 
				int r0 = getRegisterNum(op0);
				int r1 = getNextRegisterNum(r0);
				int r2 = getNextRegisterNum(r1);
				instructions.add(makePush(r1));
				instructions.add(makePush(r2));
				instructions.add(makeMove(r1, getRegisterNum(op1)));
				
				int constant = op1.getInitialValue();
				if(constant < 0) {	// 음수인 경우 (보수 연산)
					constant *= -1;
					instructions.add(makeMoveC(r2, constant));
					instructions.add(makeNot(r2));
					
					int r3 = getNextRegisterNum(r2);
					instructions.add(makePush(r3));
					
					instructions.add(makeMoveC(r3, 1));
					instructions.add(makeAdd(r2, r3));	// r2 += 1;
					instructions.add(makeAdd(r1, r2));	// r1 += r2; (뺄셈)
					
					instructions.add(makePop(r3));
				}else {				// 양수인 경우
					instructions.add(makeMoveC(r2, constant));
					instructions.add(makeAdd(r1,r2));	
				}
				
				instructions.add(makeLoadR(r0, r1));
				instructions.add(makePop(r1));
				instructions.add(makePop(r2));
				break;
			default: 
				throw new Exception();
			}
		}else if(op0.getType() == EType.eVariable){ // 옮길 곳이 메모리인 경우
			switch(op1.getType()) {
			case eRegister: // store
				instructions.add(makeStore(op0.getOffset(), getRegisterNum(op1)));
				break;
			case eVariable: // variable to variable -> load하고 store
				instructions.add(makePush(0));
				instructions.add(makeLoad(0, op1.getOffset()));
				instructions.add(makeStore(op0.getOffset(), 0));
				instructions.add(makePop(0));
				break;
			case eConstant: // constant to variable -> moveC하고 store
				instructions.add(makePush(0));
				instructions.add(makeMoveC(0, op1.getInitialValue()));
				instructions.add(makeStore(op0.getOffset(), 0));
				instructions.add(makePop(0));
				break;
			default: 
				throw new Exception();
			}
		}else {
			throw new Exception();
		}
		return instructions;
	}
	public static Vector<String> makeCmp(Token op0, Token op1) throws Exception{
		Vector<String> instructions = new Vector<>();
		
		int r0;
		// r1이 register type이면 그 다음 레지스터 번호로 r0 세팅
		if(op1.getType() == EType.eRegister) {
			r0 = getNextRegisterNum(getRegisterNum(op1));
		}else {
			r0 = 0;
		}
		switch(op0.getType()) {
		case eRegister: // register인 경우
			r0 = getRegisterNum(op0);
			break;
		case eVariable: // variable인 경우 -> load
			instructions.add(makePush(r0));
			instructions.add(makeLoad(r0, op0.getOffset()));
			break;
		case eConstant: // constant인 경우 -> moveC
			instructions.add(makePush(r0));
			instructions.add(makeMoveC(r0, op0.getInitialValue()));
			break;
		default: 
			throw new Exception();
		}

		// operand 1의 타입별 처리
		int r1;
		// r10이 register type이면 그 다음 레지스터 번호로 r0 세팅
		if(op0.getType() == EType.eRegister) {
			r1 = getNextRegisterNum(getRegisterNum(op0));
		}else {
			r1 = 0;
		}
		switch(op1.getType()) { // operand 0의 타입별 처리
		case eRegister: // register인 경우
			r1 = getRegisterNum(op1);
			break;
		case eVariable: // variable인 경우 -> load
			r1 = 1;
			instructions.add(makePush(r1));
			instructions.add(makeLoad(r1, op1.getOffset()));
			break;
		case eConstant: // constant인 경우 -> moveC
			r1 = 1;
			instructions.add(makePush(r1));
			instructions.add(makeMoveC(r1, op1.getInitialValue()));
			break;        
		default: 
			throw new Exception();
		}
		instructions.add(makeCmp(r0, r1));
		
		if(op0.getType() != EType.eRegister){
			instructions.add(makePop(r0));
		}
		if(op1.getType() != EType.eRegister) {
			instructions.add(makePop(r1));
		}
		return instructions;
	}
	public static Vector<String> makeJmp(Token op0){
		Vector<String> instructions = new Vector<>();
		instructions.add(makeJmp(op0.getOffset()));
		return instructions;
	}
	public static Vector<String> makeGe(Token op0){
		Vector<String> instructions = new Vector<>();
		String instruction_zero = getOpcode(EOpcode.eZero.getInst());
		instruction_zero += String.format("%06X", op0.getOffset());
		instructions.add(instruction_zero);
		
		String instruction_notbz = makeNot(ERegister.eStatus.ordinal());
		instructions.add(instruction_notbz);
		
		String instruction_nobz = getOpcode(EOpcode.eBz.getInst());
		instruction_nobz += String.format("%06X", op0.getOffset());
		instructions.add(instruction_nobz);
		instructions.add(instruction_notbz);
		return instructions;
	}
	public static Vector<String> makeInt(Token op0){
		Vector<String> instructions = new Vector<>();
		String instruction_jmp = getOpcode(EOpcode.eInterrupt.getInst());
		instruction_jmp += String.format("%06X", getRegisterNum(op0));
		instructions.add(instruction_jmp);
		return instructions;
	}
	public static Vector<String> makePush(Token op0){
		Vector<String> instructions = new Vector<>();
		instructions.add(makePush(getRegisterNum(op0)));
		return instructions;
	}
	public static Vector<String> makePop(Token op0){
		Vector<String> instructions = new Vector<>();
		instructions.add(makePop(getRegisterNum(op0)));
		return instructions;
	}
	public static Vector<String> makeHalt(){
		Vector<String> instructions = new Vector<>();
		instructions.add(getOpcode(EOpcode.eHalt.getInst())+"000000");
		return instructions;
	}
	public static Vector<String> makeRet(){
		// 반환주소 stack에서 pop해서 pc에 저장하기
		Vector<String> instructions = new Vector<>();
		instructions.add(makePop(CPU.ERegister.ePC.ordinal()));
		return instructions;
	}
	
	public static Vector<String> makeCall(Token op0, int address) {
		// stack에 반환 주소 push하고 jump
		Vector<String> instructions = new Vector<>();
		instructions.add(makeMoveC(8, address+3));	// 현재 시작 주소 + 명령어 line 수를 더해서 다음 명령어 시작점을 push한다.
		instructions.add(makePush(8));
		
		instructions.add(makeJmp(op0.getOffset()));
		return instructions;
	}

	
	/*
	 * 코드 반복 제거 고민
	 * 
	 * 1. operand가 차지할 비트 수에 따라 나눈다.
	 * ex_	r to r -> 2 / 2
	 * 		r to m -> 2 / 4
	 * 2. opcode를 파라미터로 받는다.
	 */
	private static String makeAdd(int register0Num, int register1Num) {
		String instruction = getOpcode(EOpcode.eAdd.getInst());
		instruction += String.format("%02X", register0Num);
		instruction += String.format("%02X", register1Num);
		instruction += "00";
		return instruction;
	}
	private static String makeMove(int register0Num, int register1Num) {
		String instruction = getOpcode(EOpcode.eMove.getInst());
		instruction += String.format("%02X", register0Num);
		instruction += String.format("%02X", register1Num);
		instruction += "00";
		return instruction;
	}
	private static String makeLoad(int registerNum, int offset) {
		String instruction = getOpcode(EOpcode.eLoad.getInst());
		instruction += String.format("%02X", registerNum);
		instruction += String.format("%04X", offset);	// 변수 offset 정의부터 해야한다.
		return instruction;
	}
	private static String makeLoadR(int register0Num, int register1Num) {
		String instruction = getOpcode(EOpcode.eLoadr.getInst());
		instruction += String.format("%02X", register0Num);
		instruction += String.format("%02X", register1Num);
		instruction += "00";
		return instruction;
	}
	private static String makeMoveC(int registerNum, int constValue) {
		String instruction = getOpcode(EOpcode.eMovec.getInst());
		instruction += String.format("%02X", registerNum);
		instruction += String.format("%04X", constValue);
		return instruction;
	}
	private static String makeStore(int offset, int registerNum) {
		String instruction = getOpcode(EOpcode.eStore.getInst());
		instruction += String.format("%04X", offset);
		instruction += String.format("%02X", registerNum);
		return instruction;
	}
	private static String makeCmp(int register0Num, int register1Num) {
		String instruction = getOpcode(EOpcode.eCompare.getInst());
		instruction += String.format("%02X", register0Num);
		instruction += String.format("%02X", register1Num);
		instruction += "00";
		return instruction;
	}
	private static String makePush(int registerNum) {
		String instruction = getOpcode(EOpcode.ePush.getInst());
		instruction += String.format("%02X", registerNum);
		instruction += "0000";
		return instruction;
				
	}
	private static String makePop(int registerNum) {
		String instruction = getOpcode(EOpcode.ePop.getInst());
		instruction += String.format("%02X", registerNum);
		instruction += "0000";
		return instruction;
	}
	private static String makeNot(int registerNum) {
		String instruction = getOpcode(EOpcode.eNot.getInst());
		instruction += String.format("%02X", registerNum);
		instruction += "0000";
		return instruction;
	}
	public static String makeJmp(int offset){
		String instruction = getOpcode(EOpcode.eJump.getInst());
		instruction += String.format("%06X", offset);
		return instruction;
	}
	
	
	private static String getOpcode(String inst) {
		for (EOpcode i : EOpcode.values()) {
			if (i.getInst().equals(inst)) {
				return String.format("%02X", i.ordinal());
			}
		}
		return null;
	}
	public static int getRegisterNum(Token token) {
		int num = -1;
		String name = token.getName();
		try {
			num = Integer.parseInt(name.substring(1));
		}catch(NumberFormatException e) {
			if(name.equals(ERegister.eAh.getName())) {
				num = CPU.ERegister.eAh.ordinal();
			}else if(name.equals(ERegister.eAl.getName())) {
				num = CPU.ERegister.eAl.ordinal();
			}else if(name.equals(ERegister.eSP.getName())) {
				num = CPU.ERegister.eSP.ordinal();
			}else if(name.equals(ERegister.eBP.getName())) {
				num = CPU.ERegister.eBP.ordinal();
			}else if(name.equals(ERegister.ePC.getName())) {
				num = CPU.ERegister.ePC.ordinal();
			}
		}
		return num;
	}
	private static int getNextRegisterNum(int registerNum) {
		return (registerNum + 1) % 8;
	}
}
