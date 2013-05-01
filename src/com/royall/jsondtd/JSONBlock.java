package com.royall.jsondtd;

public class JSONBlock {

	private JSONBlock parent = null;
	
	private Object block;
	
	public JSONBlock() {
		block = null;
	}
	
	public JSONBlock(Object _block) {
		block = _block;
	}
	
	public JSONBlock(Object _block, JSONBlock _parent) {
		this(_block);
		parent = _parent;
	}
	
	public void putBlock(Object _o) {
		block = _o;
	}
	
	public Object getBlock() {
		return block;
	}
	
	public JSONBlock getParent() {
		return parent;
	}
	
	public int getDepth() {
		if (parent != null)
			return (parent.getDepth()) + 1;
		else
			return 0;
	}
}
