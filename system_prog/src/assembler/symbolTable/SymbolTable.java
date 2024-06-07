package assembler.symbolTable;

import java.util.HashSet;

public class SymbolTable {
	private HashSet<Token> symbol_table;
	
	public SymbolTable() {
		this.symbol_table = new HashSet<>();
	}
	public Token addSymbol(Token token) {
		for(Token t : this.symbol_table) {
			if(t.getName().equals(token.getName())) {
				this.symbol_table.remove(t);
				this.symbol_table.add(token);
				return t;
			}
		}
		this.symbol_table.add(token);
		return token;
	}
	public Token getSymbol(String name) {
		for(Token t : this.symbol_table) {
			if(t.getName().equals(name)) {
				return t;
			}
		}
		return null;
	}
	
	public HashSet<Token> getSymbolTable(){
		return this.symbol_table;
	}
}
