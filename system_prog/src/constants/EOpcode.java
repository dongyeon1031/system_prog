package constants;

public enum EOpcode {
	eHalt("halt"), eLoad("load"), eStore("store"), eAdd("add"), eAnd("add"), 
	eJump("jmp"), eZero("zero"), eBz("bz"), eCompare("cmp"), eNot("not"), 
	 eShr("shr"), eMove("move"), eMovec("moveC"), ePush("push"), ePop("pop"), 
	 eInterrupt("irq"), eLoadr("loadr"), eStorer("storer")//	loadr = 레지스터가 가리키는 주소를 load
	;

	private final String inst;

	private EOpcode(String s) {
		this.inst = s;
	}

	public final String getInst() {
		return this.inst;
	}
}
