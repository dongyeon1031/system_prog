package main;

import java.io.FileNotFoundException;

import assembler.Assembler;

public class Main {
	private Assembler assembler;
	private MicroProcessor microprocessor;
	public Main() {
		this.microprocessor = new MicroProcessor();
		this.assembler = new Assembler();
	}

	private void initialize() throws FileNotFoundException {
		this.microprocessor.initialize();
		this.assembler.initialize();
	}
	
	private void run() throws Exception {
		this.assembler.run();
		this.microprocessor.run();
	}
	
	private void finish() {
		this.microprocessor.finish();
		this.assembler.finish();
	}
	
	static public void main(String[] args) throws Exception {
		Main main = new Main();
		main.initialize();
		main.run();
		main.finish();
	}
}
